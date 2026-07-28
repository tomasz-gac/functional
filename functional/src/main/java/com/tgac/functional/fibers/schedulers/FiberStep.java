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
 * AMBIENT SCOPE the frame's work is recorded in (null = unowned).
 *
 * <p>The WorkScope calls are the interpreter's, not the schedulers': a
 * frame constructed with a scope calls started() at construction (no gap
 * for a racing seal); on completion finished() runs and the seal attempt
 * it returns runs as the frame's own continuation — same scheduler, same
 * fairness. {@link Fiber.Detached} carries the one legal escape from
 * inheritance: an explicit re-parenting scope, or null for unowned.
 * Exactly-once holds by construction — consumer code never calls the
 * WorkScope methods.
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
		Scope<?> scope;
		final Deque<Function<Object, Fiber<Object>>> ks = new ArrayDeque<>();

		Frame(Fiber<?> computation) {
			this(computation, null);
		}

		@SuppressWarnings("unchecked")
		Frame(Fiber<?> computation, WorkScope scope) {
			this.computation = (Fiber<Object>) computation;
			this.scope = own(scope);
			if (this.scope != null) {
				this.scope.started();
			}
		}

		/** The runtime's Scope is the only admissible WorkScope. */
		private static Scope<?> own(WorkScope scope) {
			if (scope == null) {
				return null;
			}
			if (!(scope instanceof Scope)) {
				throw new IllegalArgumentException(
						"foreign WorkScope implementations are not supported: " + scope.getClass());
			}
			return (Scope<?>) scope;
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
		 * Completing it calls owner.started() then owner.unblocked(frame) —
		 * that order is internal, no caller can misorder it — hands the frame
		 * its result and re-queues it through the scheduler's injections.
		 */
		default Await.Waiter<Object> resumeHandle(Scope<?> owner) {
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
					// finished() and the seal attempt run as this frame's
					// continuation — the same scheduler steps the cascade and
					// whatever it emits
					Scope<?> owner = frame.scope;
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
			Scope<?> owner = frame.scope;
			if (owner != null) {
				// the blocked record lands BEFORE the started/finished pair
				// closes — a racing seal must never see drained counters with
				// no blocked record
				owner.blocked(frame, source.scope());
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
			// held: finished() and the seal attempt run as detached work — the
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
