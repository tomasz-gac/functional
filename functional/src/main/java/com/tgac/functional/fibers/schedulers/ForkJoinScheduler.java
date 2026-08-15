package com.tgac.functional.fibers.schedulers;

// ABOUTME: Parallel scheduler: each fiber frame is a ForkJoinTask, forks steal across pool workers.
// ABOUTME: A driver over Frame — a join callback threads fork/join and continue-after-join.

import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Scheduler;
import com.tgac.functional.fibers.interpreter.AwaitBoundary;
import com.tgac.functional.fibers.interpreter.Frame;
import com.tgac.functional.fibers.interpreter.ResumeHandle;
import com.tgac.functional.fibers.interpreter.Scope;
import com.tgac.functional.fibers.interpreter.StepListener;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Runs a fiber tree in parallel on a {@link ForkJoinPool}. Each frame is a
 * task; a {@code Forked} node forks its options onto the pool (work-stealing),
 * and the forking frame continues at once - fork is a control scatter.
 *
 * Unlike the sequential schedulers, this one is eager and runs each frame's
 * trampoline uninterrupted — the pool provides interleaving. The computation
 * is complete only when every task has finished, including detached fibers,
 * so results and side effects are fully drained before {@link #get()} returns.
 * A pool drive counts no steps: {@code run(iterations, sink)} refuses, and
 * {@link #advance} drives by a poll window instead. Failure is structured —
 * the first throwing frame fails the whole drive and cancels the rest of the
 * tree cooperatively, at each frame's next yield.
 */
@SuppressWarnings("unchecked")
public final class ForkJoinScheduler<A> implements Scheduler<A> {

	private static final Consumer<Object> DISCARD = value -> {
	};

	private final Fiber<A> initialFiber;
	private final ForkJoinPool pool;
	private final CompletableFuture<A> result = new CompletableFuture<>();
	private final AtomicInteger pending = new AtomicInteger(0);
	/**
	 * Bumped on every pending mutation — the stability witness for strand
	 * detection. Atomic so it is MONOTONE: an unchanged reading across a
	 * poll window provably means zero mutations, never a racy overwrite.
	 */
	private final AtomicLong opsEpoch = new AtomicLong();

	private int pendingOp(int delta) {
		opsEpoch.incrementAndGet();
		return delta > 0 ? pending.incrementAndGet() : pending.decrementAndGet();
	}

	/** Frames held by a Channel; each keeps one pending unit open until its resume. */
	private final Map<Frame, Object> outstanding =
			Collections.synchronizedMap(new LinkedHashMap<Frame, Object>());

	private volatile boolean started = false;
	private volatile boolean cancelled = false;
	private volatile StepListener stepListener = StepListener.NO_OP;
	private volatile A rootValue;
	private Consumer<? super A> rootSink;

	public ForkJoinScheduler(Fiber<A> initialFiber) {
		this(initialFiber, ForkJoinPool.commonPool());
	}

	@Override
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
		pendingOp(1);
		pool.execute(new Task(new Frame(initialFiber), this::deliverRoot));
	}

	private void deliverRoot(Object value) {
		rootValue = (A) value;
		Consumer<? super A> sink = rootSink;
		if (sink != null) {
			sink.accept((A) value);
		}
	}

	/**
	 * Fail the whole drive: first failure wins the result, and the winner
	 * cancels the rest of the tree — cooperatively, at each frame's next
	 * yield. A branch's exception is never survivable by its siblings: an
	 * exception is a BUG, not search failure, and silence is the only word
	 * the engine has for the latter.
	 */
	private void fail(Throwable t) {
		if (result.completeExceptionally(t)) {
			cancelled = true;
		}
	}

	/** The last task to finish completes the result with the root value. */
	private void taskFinished() {
		int p = pendingOp(-1);
		if (p == 0 && !result.isDone()) {
			// completing with parked frames is ALWAYS a bug: every held frame
			// keeps a pending unit open, so p == 0 with outstanding entries
			// means a unit was lost - refuse loudly rather than hand back a
			// partial fixpoint
			if (!outstanding.isEmpty()) {
				fail(new IllegalStateException(
						"drive drained with " + outstanding.size()
								+ " frame(s) still parked: "
								+ AwaitBoundary.describePlaces(outstanding.values())));
				return;
			}
			result.complete(rootValue);
			return;
		}
		// STRAND DETECTION DOES NOT LIVE HERE: a one-shot p == size read
		// races wake-storms — a mid-churn snapshot fired falsely under load
		// (the pending audit kept moving long after the refusal). The
		// drivers detect strands with the STABILIZED check below, across
		// poll timeouts.
	}

	/**
	 * The stabilized strand check, called on poll timeouts: refuse only when
	 * two consecutive observations agree AND no pending op landed between
	 * them — a genuinely dead drive, never a mid-churn snapshot. Every held
	 * frame keeps a unit, so a quiet epoch with p == size > 0 means only
	 * parked frames remain and nothing can ever wake them.
	 */
	private volatile long lastEpoch = -1;
	private volatile int lastP = -1;

	private void strandCheckStabilized() {
		int p = pending.get();
		int size = outstanding.size();
		long e = opsEpoch.get();
		boolean candidate = p > 0 && p == size;
		boolean stable = candidate && e == lastEpoch && p == lastP;
		lastEpoch = candidate ? e : -1;
		lastP = candidate ? p : -1;
		if (stable && !result.isDone()) {
			fail(new IllegalStateException(
					"scheduler exhausted: pending=" + p + " outstanding=" + size
							+ " blocked at unsealed sources: "
							+ AwaitBoundary.describePlaces(outstanding.values())));
		}
	}

	/**
	 * Runs one frame to its next yield. A successor task (fork option,
	 * continuation, or detached child) is always constructed before the
	 * current task's count is released, so {@code pending} reaches zero only
	 * when the whole tree is genuinely done.
	 */
	private final class Task extends RecursiveAction implements Frame.Effects<Task> {
		private final Frame frame;
		private final Consumer<Object> valueSink;

		Task(Frame frame, Consumer<Object> valueSink) {
			this.frame = frame;
			this.valueSink = valueSink;
		}

		@Override
		protected void compute() {
			try {
				while (!cancelled && frame.step(this, this, stepListener)) {
					// run this frame's trampoline uninterrupted
				}
			} catch (Throwable t) {
				fail(t);
			} finally {
				taskFinished();
			}
		}

		@Override
		public void completed(Task task, Object value) {
			task.valueSink.accept(value);
		}

		@Override
		public void forked(Task task, List<Frame> children) {
			// children spawn; the forking frame continues in its own compute loop
			for (Frame child : children) {
				task.spawn(new Task(child, DISCARD));
			}
		}

		@Override
		public void detached(Task task, Frame child) {
			// runs independently; its result is discarded, but the tree is not
			// complete until it finishes
			task.spawn(new Task(child, DISCARD));
		}

		@Override
		public ResumeHandle resumeHandle(Task task, Scope owner) {
			Frame contFrame = task.frame;
			Consumer<Object> contSink = task.valueSink;
			return new ResumeHandle(contFrame, owner, () -> {
				// remove-then-spawn-then-release: a mid-flight resume keeps
				// pending strictly above outstanding
				outstanding.remove(contFrame);
				pendingOp(1);
				pool.execute(new Task(contFrame, contSink));
				taskFinished();
			});
		}

		@Override
		public void suspended(Task task, Object at) {
			// the held unit lands BEFORE the map entry so a concurrent
			// strand check can only read p > size, never a false equality
			pendingOp(1);
			outstanding.put(task.frame, at);
		}

		private void spawn(Task task) {
			pendingOp(1);
			// pool.execute, not task.fork(): a fire-and-forget fork onto the
			// worker's local deque was observed LOST under load (a forked
			// task with no task-end in the full pending audit) — the leaked
			// ledger pair behind the deep-chain strand. The submission
			// queue has never dropped one.
			pool.execute(task);
		}
	}

	@Override
	public A get() {
		start(null);
		while (true) {
			try {
				return result.get(64, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {
				// keep waiting, but refuse a stably dead drive
				strandCheckStabilized();
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
		throw new UnsupportedOperationException(
				"a pool drive counts no steps: run(steps) is a stepping-driver "
						+ "contract — use advance() for bounded progress");
	}

	/** The pool's quantum is a poll window, not a step batch. */
	@Override
	public boolean advance(Consumer<? super A> sink) {
		if (cancelled)
			return true;
		start(sink);
		if (result.isDone())
			return true;
		try {
			result.get(64, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (TimeoutException e) {
			// not done yet — keep driving, but refuse a stably dead drive
			strandCheckStabilized();
		} catch (CancellationException e) {
			// closed underneath us — done by decree
		} catch (ExecutionException e) {
			// an exceptional completion is a REFUSAL, not a completion:
			// swallowing it here converted every loud invariant (stranded
			// frames, grow-on-sealed, drained-with-parked-frames) into a
			// silent partial answer stream
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			if (cause instanceof Error) {
				throw (Error) cause;
			}
			throw new RuntimeException(cause);
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
		// cooperative quiesce: in-flight trampolines see the flag at their
		// next yield; only parked holds may remain pending. Two agreeing
		// reads under an unchanged ops epoch certify the interval quiet —
		// the same monotone-counter argument the group seal uses.
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(1);
		long stableEpoch = -1;
		while (System.nanoTime() < deadline) {
			long epoch = opsEpoch.get();
			if (pending.get() == outstanding.size()) {
				if (epoch == stableEpoch) {
					return;
				}
				stableEpoch = epoch;
			} else {
				stableEpoch = -1;
			}
			Thread.yield();
		}
	}
}
