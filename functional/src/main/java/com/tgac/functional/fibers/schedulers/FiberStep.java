package com.tgac.functional.fibers.schedulers;

// ABOUTME: The single-step interpreter shared by every scheduler: one dispatch over the Fiber ADT.
// ABOUTME: Schedulers are drivers — queues, countdowns and granularity live there; step semantics live here.

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Source;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

/**
 * Advances one fiber frame by one step. A frame is a fiber under evaluation:
 * the current computation plus the stack of pending continuations, plus the
 * AMBIENT SCOPE the frame's work is recorded in (null = unowned).
 *
 * <p>The Scope calls are the interpreter's, not the schedulers': a
 * frame constructed with a scope calls started() at construction (no gap
 * for a racing seal); on completion finished() runs and the seal attempt
 * it returns runs as the frame's own continuation — same scheduler, same
 * fairness. {@link Fiber.Detached} carries the one legal escape from
 * inheritance: an explicit re-parenting scope, or null for unowned.
 * Exactly-once holds by construction — consumer code never calls the
 * Scope methods.
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
		Scope scope;
		final Deque<Function<Object, Fiber<Object>>> ks = new ArrayDeque<>();

		Frame(Fiber<?> computation) {
			this(computation, (Scope) null);
		}

		@SuppressWarnings("unchecked")
		Frame(Fiber<?> computation, Scope scope) {
			this.computation = (Fiber<Object>) computation;
			this.scope = scope;
			if (this.scope != null) {
				this.scope.started();
			}
		}

		/** Step this frame once as {@code entry}, reporting events to {@code effects}. */
		<E> boolean step(E entry, Effects<E> effects, StepListener listener) {
			return FiberStep.step(this, entry, effects, listener);
		}
	}

	/**
	 * Scheduling policy hooks. {@code completed} and {@code forked} mean the
	 * frame yielded control and must leave the run queue; {@code detached}
	 * reports an independent child while the frame itself keeps running —
	 * the child's frame is born with the given scope (null = unowned).
	 */
	interface Effects<E> {
		void completed(E entry, Object value);

		/**
		 * Inject the fork's children as independent frames; the forking
		 * frame continues in the same step, so the children must be queued
		 * before it runs again. Always carries at least one option - empty
		 * forks never reach the scheduler. Child frames inherit the forking
		 * frame's ambient scope.
		 */
		void forked(E entry, Fiber.Forked<Object> fork);

		void detached(E entry, Fiber<?> child, Scope into);

		/**
		 * The resume handle for the current frame, bound to its queue entry.
		 * Completing it calls owner.started() then owner.unblocked(frame) —
		 * that order is internal, no caller can misorder it — hands the frame
		 * its result and re-queues it through the scheduler's injections.
		 */
		default ResumeHandle resumeHandle(E entry, Scope owner) {
			throw new UnsupportedOperationException(
					"Fiber.await is not supported by this scheduler yet");
		}

		/**
		 * The frame is yielding into {@code at}: register it as held and fire
		 * the fork's countdown — BEFORE the offer, so no completion can
		 * outrun the registration. Every await yields; a child fires its
		 * countdown at its first yield.
		 */
		default void suspended(E entry, Source<?> at) {
			throw new UnsupportedOperationException(
					"Fiber.await is not supported by this scheduler yet");
		}
	}


	/**
	 * @return true when the frame is still runnable; false when it yielded
	 * 		control through {@link Effects#completed} or {@link Effects#forked}
	 */
	@SuppressWarnings("unchecked")
	private static <E> boolean step(Frame frame, E entry, Effects<E> effects, StepListener listener) {
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
					Scope owner = frame.scope;
					frame.scope = null;
					frame.computation = owner.finished().map(__ -> value);
					return true;
				}
				listener.onCompleted(value);
				effects.completed(entry, value);
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
			effects.detached(entry, detached.getFiber(), detached.getInto());
			return true;
		}
		if (computation instanceof Fiber.Awaiting) {
			Fiber.Awaiting<Object> awaiting = (Fiber.Awaiting<Object>) (Fiber<?>) computation;
			Source<Object> source = awaiting.getSource();
			Scope owner = frame.scope;
			// AN AWAIT ALWAYS YIELDS. Every record is placed BEFORE the offer,
			// so no completion can outrun the bookkeeping; nothing here may
			// touch the frame after the offer
			frame.scope = null;
			ResumeHandle handle = effects.resumeHandle(entry, owner);
			if (owner != null) {
				if (source.sealOnly() && owner == source.scope()) {
					throw new IllegalStateException(
							"drained its own workforce - a wait for yourself: " + source);
				}
				// the blocked record shields the owner's counters until the
				// resume is billed
				owner.blocked(frame, source.scope(), source.sealOnly());
			}
			effects.suspended(entry, source);
			source.suspend(awaiting.getReady(), handle);
			// the pair closes AFTER the offer: an inline completion's
			// resumed() lands inside this frame's still-open pair, so the
			// counters are never transiently drained
			if (owner != null) {
				effects.detached(entry, owner.finished(), null);
			}
			return false;
		}
		if (computation instanceof Fiber.Forked) {
			Fiber.Forked<Object> fork = (Fiber.Forked<Object>) computation;
			if (fork.getOptions() != null && !fork.getOptions().isEmpty()) {
				listener.onForked(fork);
				effects.forked(entry, fork);
			}
			// fork completes immediately: the children are injected into the
			// ambient scope and the frame continues - a fork is a CONTROL
			// scatter, its completion carries nothing
			frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(Nothing.nothing());
			return true;
		}
		throw new IllegalStateException("Unknown Fiber subclass: " + computation.getClass());
	}
}
