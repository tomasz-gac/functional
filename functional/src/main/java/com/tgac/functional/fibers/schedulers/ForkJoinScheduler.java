package com.tgac.functional.fibers.schedulers;

// ABOUTME: Parallel scheduler: each fiber frame is a ForkJoinTask, forks steal across pool workers.
// ABOUTME: A driver over FiberStep — a join callback threads fork/join and continue-after-join.

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Scheduler;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Runs a fiber tree in parallel on a {@link ForkJoinPool}. Each frame is a
 * task; a {@code Forked} node forks its options onto the pool (work-stealing),
 * and the frame's continuation resumes as a fresh task once the options join.
 *
 * Unlike the sequential schedulers, this one is eager and runs each frame's
 * trampoline uninterrupted — the pool provides interleaving. The computation
 * is complete only when every task has finished, including detached fibers,
 * so results and side effects are fully drained before {@link #get()} returns.
 * As with the executor-based schedulers, {@code run(iterations, sink)} treats
 * {@code iterations} as a millisecond budget, not a step count.
 */
@SuppressWarnings("unchecked")
public final class ForkJoinScheduler<A> implements Scheduler<A> {

	private static final Runnable NO_JOIN = () -> {};
	private static final Consumer<Object> DISCARD = value -> {};

	private final Fiber<A> initialFiber;
	private final ForkJoinPool pool;
	private final CompletableFuture<A> result = new CompletableFuture<>();
	private final AtomicInteger pending = new AtomicInteger(0);

	private volatile boolean started = false;
	private volatile boolean cancelled = false;
	private volatile StepListener stepListener = StepListener.NO_OP;
	private volatile A rootValue;
	private Consumer<? super A> rootSink;

	public ForkJoinScheduler(Fiber<A> initialFiber) {
		this(initialFiber, ForkJoinPool.commonPool());
	}

	public ForkJoinScheduler<A> withListener(StepListener listener) {
		this.stepListener = listener == null ? StepListener.NO_OP : listener;
		return this;
	}

	public ForkJoinScheduler(Fiber<A> initialFiber, ForkJoinPool pool) {
		if (initialFiber == null)
			throw new NullPointerException("Initial Fiber cannot be null");
		if (pool == null)
			throw new NullPointerException("ForkJoinPool cannot be null");
		this.initialFiber = initialFiber;
		this.pool = pool;
	}

	private void start(Consumer<? super A> sink) {
		synchronized (this) {
			if (started) {
				if (sink != null) {
					this.rootSink = sink;
				}
				return;
			}
			this.rootSink = sink;
			this.started = true;
		}
		pending.incrementAndGet();
		pool.execute(new Task(new FiberStep.Frame(initialFiber), this::deliverRoot, NO_JOIN));
	}

	private void deliverRoot(Object value) {
		rootValue = (A) value;
		Consumer<? super A> sink = rootSink;
		if (sink != null) {
			sink.accept((A) value);
		}
	}

	/** The last task to finish completes the result with the root value. */
	private void taskFinished() {
		if (pending.decrementAndGet() == 0 && !result.isDone()) {
			result.complete(rootValue);
		}
	}

	private static Fiber<Object> doneNothing() {
		return (Fiber<Object>) (Fiber<?>) Fiber.done(Nothing.nothing());
	}

	/**
	 * Runs one frame to its next yield. A successor task (fork option,
	 * continuation, or detached child) is always constructed before the
	 * current task's count is released, so {@code pending} reaches zero only
	 * when the whole tree is genuinely done.
	 */
	private final class Task extends RecursiveAction implements FiberStep.Effects {
		private final FiberStep.Frame frame;
		private final Consumer<Object> valueSink;
		private final Runnable joinCallback;

		Task(FiberStep.Frame frame, Consumer<Object> valueSink, Runnable joinCallback) {
			this.frame = frame;
			this.valueSink = valueSink;
			this.joinCallback = joinCallback;
		}

		@Override
		public StepListener listener() {
			return stepListener;
		}

		@Override
		protected void compute() {
			try {
				while (!cancelled && FiberStep.step(frame, this)) {
					// run this frame's trampoline uninterrupted
				}
			} catch (Throwable t) {
				if (!result.isDone()) {
					result.completeExceptionally(t);
				}
			} finally {
				taskFinished();
			}
		}

		@Override
		public void completed(Object value) {
			valueSink.accept(value);
			joinCallback.run();
		}

		@Override
		public void forked(Fiber.Forked<Object> fork) {
			List<Fiber<Object>> options = fork.getOptions();
			Consumer<Object> childSink = fork.getSink();

			// the continuation resumes this frame once every option has joined
			FiberStep.Frame contFrame = frame;
			Consumer<Object> contSink = valueSink;
			Runnable contJoin = joinCallback;
			AtomicInteger latch = new AtomicInteger(options.size());
			Runnable childDone = () -> {
				if (latch.decrementAndGet() == 0) {
					contFrame.computation = doneNothing();
					spawn(new Task(contFrame, contSink, contJoin));
				}
			};

			for (Fiber<Object> option : options) {
				spawn(new Task(new FiberStep.Frame(option), childSink, childDone));
			}
		}

		@Override
		public void detached(Fiber<?> child) {
			// runs independently; its result is discarded, but the tree is not
			// complete until it finishes
			spawn(new Task(new FiberStep.Frame(child), DISCARD, NO_JOIN));
		}

		private void spawn(Task task) {
			pending.incrementAndGet();
			task.fork();
		}
	}

	@Override
	public A get() {
		start(null);
		try {
			return result.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			close();
			throw new RuntimeException("ForkJoinScheduler.get() interrupted", e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;
			if (cause instanceof Error)
				throw (Error) cause;
			throw new RuntimeException("Exception in fiber computation", cause);
		}
	}

	@Override
	public void run(Consumer<? super A> sink) {
		start(sink);
		try {
			result.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			close();
		} catch (ExecutionException e) {
			throw new RuntimeException(e.getCause());
		}
	}

	@Override
	public boolean step(Consumer<? super A> sink) {
		if (cancelled)
			return true;
		start(sink);
		return result.isDone();
	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		if (cancelled)
			return true;
		start(sink);
		if (result.isDone())
			return true;
		try {
			result.get(iterations, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException | CancellationException | TimeoutException e) {
			// completed exceptionally, cancelled, or timed out — fall through to the check
		}
		return result.isDone() || cancelled;
	}

	@Override
	public Optional<A> run(int iterations) {
		if (cancelled)
			return Optional.empty();
		run(iterations, null);
		return result.isDone() ?
				Optional.ofNullable(result.getNow(null)) :
				Optional.empty();
	}

	@Override
	public void close() {
		cancelled = true;
		if (!result.isDone()) {
			result.completeExceptionally(new CancellationException("ForkJoinScheduler cancelled by close()"));
		}
	}
}
