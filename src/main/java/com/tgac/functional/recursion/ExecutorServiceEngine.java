package com.tgac.functional.recursion;

import com.tgac.functional.category.Nothing;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"unchecked"})
public final class ExecutorServiceEngine<A> implements Engine<A> {

	private final Recur<A> initialRecur;
	private final ExecutorService executorService; // Shared executor

	private final Set<EngineStack> pausedForEachParentStacks;

	private volatile boolean processingStarted = false;
	private volatile boolean cancelled = false;
	private Consumer<? super A> rootSink;
	private final CompletableFuture<A> finalResultFuture;

	private final AtomicInteger activeTasksCount = new AtomicInteger(0);
	private final Object cancellationLock = new Object();

	private static class EngineStack {
		Recur<Object> computation;
		final Deque<Function<Object, Recur<Object>>> fs = new ArrayDeque<>();
		final boolean isRootStackForEngine;

		Consumer<Object> forEachItemSinkInternal;
		AtomicInteger forEachChildrenPendingCount;
		EngineStack childOfWhichParentForEachStack;
		private static final AtomicInteger idGenerator = new AtomicInteger(0);
		private final int stackId = idGenerator.incrementAndGet();

		public EngineStack(Recur<Object> initialComputation, boolean isRootStackForEngine) {
			this.computation = initialComputation;
			this.isRootStackForEngine = isRootStackForEngine;
		}

		public void configureAsForEachParent(Recur.ForEach<Object> forEachNode) {
			this.forEachItemSinkInternal = forEachNode.getSink();
			List<Recur<Object>> options = forEachNode.getOptions();
			this.forEachChildrenPendingCount = new AtomicInteger(options != null ? options.size() : 0);
		}

		@Override
		public String toString() {
			return "EngineStack{id=" + stackId + ", isRoot=" + isRootStackForEngine +
					", comp=" + (computation != null ? computation.getClass().getSimpleName() : "null") +
					", fs=" + fs.size() +
					(forEachChildrenPendingCount != null ? ", childrenPending=" + forEachChildrenPendingCount.get() : "") +
					(childOfWhichParentForEachStack != null ? ", parentId=" + childOfWhichParentForEachStack.stackId : "") +
					"}";
		}
	}

	public ExecutorServiceEngine(Recur<A> initialRecur, ExecutorService executorService) {
		if (initialRecur == null)
			throw new NullPointerException("Initial Recur cannot be null");
		if (executorService == null)
			throw new NullPointerException("ExecutorService cannot be null");

		this.initialRecur = initialRecur;
		this.executorService = executorService;
		this.pausedForEachParentStacks = Collections.newSetFromMap(new ConcurrentHashMap<>());
		this.finalResultFuture = new CompletableFuture<>();
	}

	@Override
	public void close() {
		if (!this.cancelled) {
			this.cancelled = true;
			if (!finalResultFuture.isDone()) {
				finalResultFuture.completeExceptionally(new CancellationException("Engine computations cancelled by close()."));
			}
		}

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

	private void submitEngineTask(EngineStack stack) {
		activeTasksCount.incrementAndGet();
		try {
			executorService.submit(() -> {
				try {
					processRecurComputation(stack);
				} catch (Throwable t) {
					if (!this.cancelled) {
						handleFailure(stack, t);
					} else {
						cleanupParentForCancelledTask(stack, true);
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
				if (this.rootSink != sink && sink != null)
					this.rootSink = sink;
				return;
			}
			this.rootSink = sink;
			EngineStack rootStack = new EngineStack((Recur<Object>) this.initialRecur, true);
			processingStarted = true;
			submitEngineTask(rootStack);
		}
	}

	private void processRecurComputation(EngineStack stack) {
		String workerName = Thread.currentThread().getName();
		try {
			while (true) {
				if (this.cancelled) {
					cleanupParentForCancelledTask(stack, false);
					return;
				}

				Recur<Object> computation = stack.computation;

				if (computation instanceof Recur.More) {
					stack.computation = ((Recur.More<Object>) computation).getRec().get();
				} else if (computation instanceof Recur.FlatMap) {
					Recur.FlatMap<Object, Object> flatMapNode = (Recur.FlatMap<Object, Object>) computation;
					stack.fs.addLast(flatMapNode.getF());
					stack.computation = flatMapNode.getArg();
				} else if (computation instanceof Recur.Done) {
					Object value = ((Recur.Done<Object>) computation).getValue();
					if (!stack.fs.isEmpty()) {
						stack.computation = stack.fs.pollLast().apply(value);
					} else {
						if (stack.childOfWhichParentForEachStack != null) {
							EngineStack parentForEachStack = stack.childOfWhichParentForEachStack;
							if (!this.cancelled && parentForEachStack.forEachItemSinkInternal != null) {
								parentForEachStack.forEachItemSinkInternal.accept(value);
							}
							if (parentForEachStack.forEachChildrenPendingCount.decrementAndGet() == 0) {
								pausedForEachParentStacks.remove(parentForEachStack);
								parentForEachStack.computation = Recur.done(Nothing.nothing());
								if (!this.cancelled) {
									submitEngineTask(parentForEachStack);
								}
							}
						} else if (stack.isRootStackForEngine) {
							if (!this.cancelled && this.rootSink != null) {
								try {
									this.rootSink.accept((A) value);
								} catch (Exception e) {
									if (!finalResultFuture.isDone())
										finalResultFuture.completeExceptionally(e);
									return;
								}
							}
							if (!finalResultFuture.isDone())
								finalResultFuture.complete((A) value);
						}
						return;
					}
				} else if (computation instanceof Recur.ForEach) {
					Recur.ForEach<Object> forEachNode = (Recur.ForEach<Object>) computation;
					List<Recur<Object>> options = forEachNode.getOptions();

					if (options == null || options.isEmpty()) {
						stack.computation = Recur.done(Nothing.nothing());
					} else {
						stack.configureAsForEachParent(forEachNode);

						if (stack.forEachChildrenPendingCount.get() == 0) {
							stack.computation = Recur.done(Nothing.nothing());
						} else {
							pausedForEachParentStacks.add(stack);
							final EngineStack parentStackForChildren = stack;
							int submittedChildCount = 0;
							for (int i = 0; i < options.size(); i++) {
								if (this.cancelled) {
									int skippedOrFailedCount = options.size() - submittedChildCount;
									if (parentStackForChildren.forEachChildrenPendingCount.addAndGet(-skippedOrFailedCount) == 0) {
										pausedForEachParentStacks.remove(parentStackForChildren);
									}
									break;
								}
								Recur<Object> branchRecur = options.get(i);
								EngineStack childStack = new EngineStack(branchRecur, false);
								childStack.childOfWhichParentForEachStack = parentStackForChildren;
								submitEngineTask(childStack);
								submittedChildCount++;
							}
							return;
						}
					}
				} else {
					IllegalStateException ex = new IllegalStateException("Unknown Recur subclass: " + computation.getClass().getName() + " in stack " + stack);
					if (!this.cancelled)
						handleFailure(stack, ex);
					else
						cleanupParentForCancelledTask(stack, true);
					return;
				}
			}
		} catch (Throwable t) {
			if (!this.cancelled)
				handleFailure(stack, t);
			else {
				cleanupParentForCancelledTask(stack, true);
			}
		}
	}

	private void cleanupParentForCancelledTask(EngineStack cancelledChildStack, boolean isDueToError) {
		if (cancelledChildStack == null)
			return;
		if (cancelledChildStack.childOfWhichParentForEachStack != null) {
			EngineStack parent = cancelledChildStack.childOfWhichParentForEachStack;
			if (parent.forEachChildrenPendingCount.decrementAndGet() == 0) {
				pausedForEachParentStacks.remove(parent);
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
		String workerName = Thread.currentThread().getName();
		if (this.cancelled) {
			cleanupParentForCancelledTask(stack, true);
			return;
		}

		if (!finalResultFuture.isDone()) {
			finalResultFuture.completeExceptionally(t);
		}

		if (stack != null && stack.childOfWhichParentForEachStack != null) {
			EngineStack parentForEachStack = stack.childOfWhichParentForEachStack;
			if (parentForEachStack.forEachChildrenPendingCount.decrementAndGet() == 0) {
				pausedForEachParentStacks.remove(parentForEachStack);
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
		if (this.cancelled)
			return true;
		startRootProcessing(sink);
		for (int i = 0; i < iterations; i++) {
			if (finalResultFuture.isDone() || this.cancelled) {
				return true;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return finalResultFuture.isDone() || this.cancelled;
			}
		}
		return finalResultFuture.isDone() || this.cancelled;
	}

	@Override
	public Optional<A> run(int iterations) {
		if (this.cancelled)
			return Optional.empty();
		final CompletableFuture<A> resultCapture = new CompletableFuture<>();
		run(iterations, result -> {
			if (!resultCapture.isDone()) {
				resultCapture.complete(result);
			}
		});

		if (finalResultFuture.isDone()) {
			try {
				if (finalResultFuture.isCompletedExceptionally()) {
					return Optional.empty();
				}
				return Optional.ofNullable(finalResultFuture.getNow(null));
			} catch (CancellationException e) {
				return Optional.empty();
			} catch (Exception e) {
				return Optional.empty();
			}
		}
		if (resultCapture.isDone()) {
			try {
				if (resultCapture.isCompletedExceptionally())
					return Optional.empty();
				return Optional.ofNullable(resultCapture.getNow(null));
			} catch (Exception e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
}