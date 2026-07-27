package com.tgac.functional.fibers.schedulers;

// ABOUTME: The single-step interpreter shared by every scheduler: one dispatch over the Fiber ADT.
// ABOUTME: Schedulers are drivers — queues, joins and granularity live there; step semantics live here.

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Source;
import com.tgac.functional.fibers.WorkScope;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

/**
 * Advances one fiber frame by one step. A frame is a fiber under evaluation:
 * the current computation plus the stack of pending continuations, plus the
 * AMBIENT SCOPE the frame's work bills to (null = unowned).
 *
 * <p>Billing is the interpreter's, not the schedulers': a frame born with a
 * scope ticks its start at birth (no gap for a racing seal); on completion
 * the finish ticks and the scope's seal attempt runs as the frame's own tail
 * — same driver, same fairness. {@link Fiber.Scoped} re-owns a subtree
 * within a frame via a restore marker on the continuation stack, and
 * {@link Fiber.Detached} carries the one legal escape: an explicit
 * re-parenting scope, or null for unowned. Exactly-once holds by
 * construction — consumer code never touches the billing doors.
 *
 * The rare events — a frame completing, forking, or detaching a child — are
 * reported through {@link Effects}; the common path (unwrap a deferred,
 * descend into a flatMap, apply a continuation) mutates the frame and
 * allocates nothing.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class FiberStep {

	static final class Frame {
		Fiber<Object> computation;
		WorkScope scope;
		final Deque<Function<Object, Fiber<Object>>> ks = new ArrayDeque<>();

		Frame(Fiber<?> computation) {
			this(computation, null);
		}

		@SuppressWarnings("unchecked")
		Frame(Fiber<?> computation, WorkScope scope) {
			this.computation = (Fiber<Object>) computation;
			this.scope = scope;
			if (scope != null) {
				scope.started();
			}
		}
	}

	/**
	 * Scheduling policy hooks. {@code completed} and {@code forked} mean the
	 * frame yielded control and must leave the run queue; {@code detached}
	 * reports an independent child while the frame itself keeps running —
	 * the child's frame is born with the given scope (null = unowned).
	 */
	interface Effects {
		void completed(Object value);

		/**
		 * The fork always carries at least one option: empty forks are
		 * vacuously complete and never reach the scheduler. Child frames
		 * inherit the forking frame's ambient scope.
		 */
		void forked(Fiber.Forked<Object> fork);

		void detached(Fiber<?> child, WorkScope scope);

		/**
		 * The resume handle for the current frame, bound to its queue entry.
		 * Completing it re-bills {@code owner} (billed-before-unblocked is
		 * internal — no caller can misorder it), hands the frame its result
		 * and re-queues it through the drive's injection boundary.
		 */
		default Await.Waiter<Object> resumeHandle(WorkScope owner) {
			throw new UnsupportedOperationException(
					"Fiber.await is not supported by this scheduler yet");
		}

		/**
		 * The current frame is about to be offered to {@code at} — register it
		 * as held and leave the run queue NOW, before the source can resume it
		 * from another thread.
		 */
		default void suspending(Source<?> at) {
			throw new UnsupportedOperationException(
					"Fiber.await is not supported by this scheduler yet");
		}

		/** The suspend was answered immediately — undo {@link #suspending}. */
		default void suspendCancelled() {
			throw new UnsupportedOperationException(
					"Fiber.await is not supported by this scheduler yet");
		}
	}


	/**
	 * @return true when the frame is still runnable; false when it yielded
	 * 		control through {@link Effects#completed} or {@link Effects#forked}
	 */
	@SuppressWarnings("unchecked")
	static boolean step(Frame frame, Effects effects, StepListener listener) {
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
				if (frame.scope != null) {
					// the finish and the seal attempt run as this frame's tail —
					// same driver steps the cascade and whatever it emits
					WorkScope owner = frame.scope;
					frame.scope = null;
					frame.computation = owner.finished().map(__ -> value);
					return true;
				}
				listener.onCompleted(value);
				effects.completed(value);
				return false;
			}
			Function<Object, Fiber<Object>> k = frame.ks.pollLast();
			frame.computation = k.apply(value);
			return true;
		}
		if (computation instanceof Fiber.Detached) {
			Fiber.Detached<?> detached = (Fiber.Detached<?>) (Fiber<?>) computation;
			frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(Nothing.nothing());
			listener.onDetached(detached.getFiber());
			effects.detached(detached.getFiber(), detached.getScope());
			return true;
		}
		if (computation instanceof Fiber.Awaiting) {
			Fiber.Awaiting<Object> awaiting = (Fiber.Awaiting<Object>) (Fiber<?>) computation;
			Source<Object> source = awaiting.getSource();
			WorkScope owner = frame.scope;
			if (owner != null) {
				// the blocked record lands BEFORE the running pair closes — a
				// racing seal must never see drained counters with no sleeper
				owner.blocked(frame, source.account());
			}
			// hand the frame off BEFORE offering the waiter: once the source
			// holds it, another thread may resume the frame at any moment, so
			// nothing here may touch the frame after a held suspend
			frame.scope = null;
			Await.Waiter<Object> waiter = effects.resumeHandle(owner);
			effects.suspending(source);
			Await.Result<Object> immediate = source.suspend(awaiting.getReady(), waiter);
			if (immediate != null) {
				effects.suspendCancelled();
				if (owner != null) {
					owner.unblocked(frame);
				}
				frame.scope = owner;
				frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(immediate);
				return true;
			}
			// held: the finish tick and seal attempt run as detached work — the
			// still-open pair until it runs only delays a seal, which is sound
			if (owner != null) {
				effects.detached(owner.finished(), null);
			}
			return false;
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
