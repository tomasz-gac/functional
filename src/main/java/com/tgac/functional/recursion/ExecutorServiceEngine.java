package com.tgac.functional.recursion;

import com.tgac.functional.category.Nothing;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An Engine that uses an ExecutorService to run Recur computations. It supports parallel execution for ForEach nodes.
 */
@SuppressWarnings({"unchecked"})
public final class ExecutorServiceEngine<A> implements Engine<A> {

	private final Recur<A> initialRecur;
	private final ExecutorService executorService; // Shared executor for running tasks.
	private final RecurProcessor<A> recurProcessor;

	private volatile boolean processingStarted = false;
	private volatile boolean cancelled = false;
	private Consumer<? super A> resultConsumer; // The final consumer for the result of the root computation.
	private final CompletableFuture<A> finalResultFuture; // Future that completes with the final result or an exception.

	private final AtomicInteger activeTasksCount = new AtomicInteger(0); // Tracks the number of tasks currently running.
	private final Object cancellationLock = new Object(); // Lock for waiting on tasks to complete after cancellation.

	/**
	 * Base class for the internal execution stack. It holds the state of a computation.
	 */
	static abstract class EngineStack {
		Recur<Object> computation;
		final Deque<Function<Object, Recur<Object>>> continuationStack = new ArrayDeque<>(); // Stack of flatMap functions.
		final boolean isRootStackForEngine;
		ForEachParentStack parent;

		EngineStack(Recur<Object> computation, boolean isRootStackForEngine, ForEachParentStack parent) {
			this.computation = computation;
			this.isRootStackForEngine = isRootStackForEngine;
			this.parent = parent;
		}
	}

	/**
	 * An EngineStack for standard sequential computations (Done, More, FlatMap).
	 */
	static final class ComputationStack extends EngineStack {
		ComputationStack(Recur<Object> computation, boolean isRootStack, ForEachParentStack parent) {
			super(computation, isRootStack, parent);
		}
	}

	/**
	 * An EngineStack that represents a ForEach node that has spawned child computations.
	 * It waits for all its children to complete.
	 */
	static final class ForEachParentStack extends EngineStack {
		final Consumer<Object> itemConsumer; // Consumer for the results of child computations.
		final AtomicInteger forEachChildrenPendingCount;

		ForEachParentStack(Recur.ForEach<Object> computation, EngineStack originalStack) {
			super(computation, originalStack.isRootStackForEngine, originalStack.parent);
			this.continuationStack.addAll(originalStack.continuationStack);
			this.itemConsumer = computation.getSink();
			this.forEachChildrenPendingCount = new AtomicInteger(computation.getOptions().size());
		}
	}

	public ExecutorServiceEngine(Recur<A> initialRecur, ExecutorService executorService) {
		if (initialRecur == null)
			throw new NullPointerException("Initial Recur cannot be null");
		if (executorService == null)
			throw new NullPointerException("ExecutorService cannot be null");

		this.initialRecur = initialRecur;
		this.executorService = executorService;
		this.finalResultFuture = new CompletableFuture<>();
		this.recurProcessor = new RecurProcessor<>(this);
	}

	boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void close() {
		if (!this.cancelled) {
			this.cancelled = true;
			if (!finalResultFuture.isDone()) {
				finalResultFuture.completeExceptionally(new CancellationException("Engine computations cancelled by close()."));
			}
		}

		// Wait for all active tasks to finish before closing.
		synchronized (cancellationLock) {
			long timeoutMillis = 5000;
			long startTime = System.currentTimeMillis();
			while (activeTasksCount.get() > 0) {
				long elapsedTime = System.currentTimeMillis() - startTime;
				if (elapsedTime >= timeoutMillis) {
					break;
				}
				try {
					cancellationLock.wait(Math.max(1, timeoutMillis - elapsedTime));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
	}

	/**
	 * Submits a new computation task to the executor service.
	 */
	void submitEngineTask(EngineStack stack) {
		activeTasksCount.incrementAndGet();
		try {
			executorService.submit(() -> {
				try {
					processRecurComputation(stack);
				} catch (Throwable t) {
					if (!this.cancelled) {
						handleFailure(stack, t);
					} else {
						cleanupParentForCancelledTask(stack);
					}
				} finally {
					int remainingTasks = activeTasksCount.decrementAndGet();
					if (cancelled && remainingTasks == 0) {
						synchronized (cancellationLock) {
							cancellationLock.notifyAll();
						}
					}
				}
			});
		} catch (RejectedExecutionException ree) {
			activeTasksCount.decrementAndGet();
			if (!this.cancelled) {
				handleFailure(stack, new RuntimeException("Task submission rejected for stack " + stack, ree));
			} else if (activeTasksCount.get() == 0) {
				synchronized (cancellationLock) {
					cancellationLock.notifyAll();
				}
			}
		}
	}

	private void startRootProcessing(Consumer<? super A> sink) {
		synchronized (this) {
			if (this.cancelled) {
				if (!finalResultFuture.isDone()) {
					finalResultFuture.completeExceptionally(new CancellationException("Engine was cancelled before processing started."));
				}
				return;
			}
			if (processingStarted) {
				if (this.resultConsumer != sink && sink != null)
					this.resultConsumer = sink;
				return;
			}
			this.resultConsumer = sink;
			ComputationStack rootStack = new ComputationStack((Recur<Object>) this.initialRecur, true, null);
			processingStarted = true;
			submitEngineTask(rootStack);
		}
	}

	/**
	 * Main processing loop for a computation stack. It delegates the actual processing to RecurProcessor.
	 */
	private void processRecurComputation(EngineStack stack) {
		try {
			EngineStack currentStack = stack;
			while (!this.cancelled) {
				EngineStack nextStack = recurProcessor.process(currentStack);
				if (nextStack == null) { // Computation is done or paused
					break;
				}
				currentStack = nextStack;
			}

			if (this.cancelled) {
				cleanupParentForCancelledTask(currentStack);
			}
		} catch (Throwable t) {
			if (!this.cancelled) {
				handleFailure(stack, t);
			} else {
				cleanupParentForCancelledTask(stack);
			}
		}
	}

	/**
	 * Handles the completion of a child computation (part of a ForEach).
	 */
	void handleDoneForChild(EngineStack stack, Object value) {
		ForEachParentStack parentForEachStack = stack.parent;
		if (!this.cancelled && parentForEachStack.itemConsumer != null) {
			parentForEachStack.itemConsumer.accept(value);
		}
		if (parentForEachStack.forEachChildrenPendingCount.decrementAndGet() == 0) {
			// All children are done, so this parent can continue its own computation.
			parentForEachStack.computation = Recur.done(Nothing.nothing());
			if (!this.cancelled) {
				submitEngineTask(parentForEachStack);
			}
		}
	}

	/**
	 * Handles the completion of the root computation.
	 */
	void handleDoneForRoot(EngineStack stack, Object value) {
		if (!this.cancelled && this.resultConsumer != null) {
			try {
				this.resultConsumer.accept((A) value);
			} catch (Exception e) {
				if (!finalResultFuture.isDone()) {
					finalResultFuture.completeExceptionally(e);
				}
				return;
			}
		}
		if (!finalResultFuture.isDone()) {
			finalResultFuture.complete((A) value);
		}
	}

	private void cleanupParentForCancelledTask(EngineStack cancelledChildStack) {
		if (cancelledChildStack == null)
			return;
		if (cancelledChildStack.parent != null) {
			ForEachParentStack parent = cancelledChildStack.parent;
			if (parent.forEachChildrenPendingCount.decrementAndGet() == 0) {
				if (parent.isRootStackForEngine && !finalResultFuture.isDone() && this.cancelled) {
					finalResultFuture.completeExceptionally(new CancellationException("Root ForEach parent completed due to child cancellations."));
				}
			}
		}
		if (cancelledChildStack.isRootStackForEngine && !finalResultFuture.isDone() && this.cancelled) {
			finalResultFuture.completeExceptionally(new CancellationException("Root computation task cancelled."));
		}
	}

	private void handleFailure(EngineStack stack, Throwable t) {
		if (this.cancelled) {
			cleanupParentForCancelledTask(stack);
			return;
		}

		if (!finalResultFuture.isDone()) {
			finalResultFuture.completeExceptionally(t);
		}

		if (stack != null && stack.parent != null) {
			ForEachParentStack parentForEachStack = stack.parent;
			if (parentForEachStack.forEachChildrenPendingCount.decrementAndGet() == 0) {
				parentForEachStack.computation = Recur.done(Nothing.nothing());
				if (!this.cancelled)
					submitEngineTask(parentForEachStack);
			}
		}
	}

	@Override
	public A get() {
		startRootProcessing(null);
		try {
			return finalResultFuture.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			close();
			throw new RuntimeException("Engine.get() interrupted", e);
		} catch (java.util.concurrent.ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof CancellationException)
				throw (CancellationException) cause;
			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;
			if (cause instanceof Error)
				throw (Error) cause;
			throw new RuntimeException("Exception in engine computation", cause);
		}
	}

	@Override
	public void run(Consumer<? super A> sink) {
		startRootProcessing(sink);
		try {
			finalResultFuture.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean step(Consumer<? super A> sink) {
		if (this.cancelled)
			return true;
		synchronized (this) {
			if (!processingStarted)
				startRootProcessing(sink);
		}
		return finalResultFuture.isDone();

	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		if (this.cancelled) {
			return true;
		}
		startRootProcessing(sink);
		if (finalResultFuture.isDone()) {
			return true;
		}
		try {
			finalResultFuture.get(iterations, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException | CancellationException | TimeoutException e) {
			// The future might be done (exceptionally) or not done (timeout).
			// We just proceed to the final check.
		}
		return finalResultFuture.isDone() || this.cancelled;
	}

	@Override
	public Optional<A> run(int iterations) {
		if (this.cancelled)
			return Optional.empty();
		run(iterations, null);

		if (finalResultFuture.isDone()) {
			try {
				return Optional.ofNullable(finalResultFuture.getNow(null));
			} catch (Exception e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
}