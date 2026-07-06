package com.tgac.functional.fibers.schedulers;

// ABOUTME: The single-step interpreter shared by every scheduler: one dispatch over the Fiber ADT.
// ABOUTME: Schedulers are drivers — queues, joins and granularity live there; step semantics live here.

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

/**
 * Advances one fiber frame by one step. A frame is a fiber under evaluation:
 * the current computation plus the stack of pending continuations.
 *
 * The rare events — a frame completing, forking, or detaching a child — are
 * reported through {@link Effects}; the common path (unwrap a deferred,
 * descend into a flatMap, apply a continuation) mutates the frame and
 * allocates nothing.
 */
final class FiberStep {

	static final class Frame {
		Fiber<Object> computation;
		final Deque<Function<Object, Fiber<Object>>> ks = new ArrayDeque<>();

		@SuppressWarnings("unchecked")
		Frame(Fiber<?> computation) {
			this.computation = (Fiber<Object>) computation;
		}
	}

	/**
	 * Scheduling policy hooks. {@code completed} and {@code forked} mean the
	 * frame yielded control and must leave the run queue; {@code detached}
	 * reports an independent child while the frame itself keeps running.
	 */
	interface Effects {
		void completed(Object value);

		/**
		 * The fork always carries at least one option: empty forks are
		 * vacuously complete and never reach the scheduler.
		 */
		void forked(Fiber.Forked<Object> fork);

		void detached(Fiber<?> child);

		/** The observer of this driver's steps; {@link StepListener#NO_OP} by default. */
		default StepListener listener() {
			return StepListener.NO_OP;
		}
	}

	private FiberStep() {
	}

	/**
	 * @return true when the frame is still runnable; false when it yielded
	 * 		control through {@link Effects#completed} or {@link Effects#forked}
	 */
	@SuppressWarnings("unchecked")
	static boolean step(Frame frame, Effects effects) {
		StepListener listener = effects.listener();
		Fiber<Object> computation = frame.computation;
		listener.onStep(computation);

		if (computation instanceof Fiber.Deferred) {
			frame.computation = ((Fiber.Deferred<Object>) computation).getRec().get();
			return true;
		}
		if (computation instanceof Fiber.FlatMap) {
			Fiber.FlatMap<Object, Object> flat = (Fiber.FlatMap<Object, Object>) computation;
			frame.ks.addLast(flat.getF());
			frame.computation = flat.getArg();
			return true;
		}
		if (computation instanceof Fiber.Done) {
			Object value = ((Fiber.Done<Object>) computation).getValue();
			if (frame.ks.isEmpty()) {
				listener.onCompleted(value);
				effects.completed(value);
				return false;
			}
			frame.computation = frame.ks.pollLast().apply(value);
			return true;
		}
		if (computation instanceof Fiber.Detached) {
			Fiber<?> child = ((Fiber.Detached<?>) (Fiber<?>) computation).getFiber();
			frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(Nothing.nothing());
			listener.onDetached(child);
			effects.detached(child);
			return true;
		}
		if (computation instanceof Fiber.Forked) {
			Fiber.Forked<Object> fork = (Fiber.Forked<Object>) computation;
			if (fork.getOptions() == null || fork.getOptions().isEmpty()) {
				// forking zero tasks is vacuously complete; the frame continues
				frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(Nothing.nothing());
				return true;
			}
			listener.onForked(fork);
			effects.forked(fork);
			return false;
		}
		throw new IllegalStateException("Unknown Fiber subclass: " + computation.getClass());
	}
}
