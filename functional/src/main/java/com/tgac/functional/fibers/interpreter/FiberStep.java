package com.tgac.functional.fibers.interpreter;

// ABOUTME: The single-step interpreter shared by every scheduler: one dispatch over the Fiber ADT.
// ABOUTME: Schedulers are drivers — queues, countdowns and granularity live there; step semantics live here.

import com.tgac.functional.algebra.Semilattice;
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
public final class FiberStep {

	public static final class Frame {
		Fiber<?> computation;
		Scope scope;
		final Deque<Function<Object, Fiber<Object>>> ks = new ArrayDeque<>();

		public Frame(Fiber<?> computation) {
			this(computation, (Scope) null);
		}

		@SuppressWarnings("unchecked")
		public Frame(Fiber<?> computation, Scope scope) {
			this.computation = (Fiber<Object>) computation;
			this.scope = scope;
			if (this.scope != null) {
				this.scope.started();
			}
		}

		/** Step this frame once as {@code entry}, reporting events to {@code effects}. */
		public <E> boolean step(E entry, Effects<E> effects, StepListener listener) {
			return FiberStep.step(this, entry, effects, listener);
		}

		/** The workforce this frame is billed to — children inherit it at a fork. */
		public Scope scope() {
			return scope;
		}

		/** The computation as of now — the drivers' snapshot read. */
		public Fiber<?> computation() {
			return computation;
		}

		@Override
		public String toString() {
			return "frame#" + Integer.toHexString(System.identityHashCode(this))
					+ "[" + computation.getClass().getSimpleName() + "]";
		}
	}

	/**
	 * Scheduling policy hooks. {@code completed} and {@code forked} mean the
	 * frame yielded control and must leave the run queue; {@code detached}
	 * reports an independent child while the frame itself keeps running —
	 * the child's frame is born with the given scope (null = unowned).
	 */
	public interface Effects<E> {
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
		 * The resume handle for the current frame, bound to its queue entry:
		 * it hands the frame its result and re-queues it through the
		 * scheduler's injections. Billing is the handle's own business — its
		 * two entry points are the two park kinds (see {@link ResumeHandle}).
		 */
		default ResumeHandle resumeHandle(E entry, Scope owner) {
			throw new UnsupportedOperationException(
					"Fiber.await is not supported by this scheduler yet");
		}

		/**
		 * The frame is parking at {@code at} — a {@link Source} for a value
		 * wait, a {@link Scope} for a seal wait: register it as held BEFORE
		 * the offer, so no completion can outrun the registration.
		 */
		default void suspended(E entry, Object at) {
			throw new UnsupportedOperationException(
					"Fiber.await is not supported by this scheduler yet");
		}
	}


	/**
	 * The dispatch: three families. PLUMBING (deferred, flatMap, done)
	 * mutates the frame and allocates nothing. MOVERS (detached, emit,
	 * forked) perform their effect and continue in the same step — none can
	 * block, none has a completion anyone may wait on. PARKERS (awaiting,
	 * sealed) follow the one always-park protocol and yield.
	 *
	 * @return true when the frame is still runnable; false when it yielded
	 * 		control through {@link Effects#completed} or a park
	 */
	@SuppressWarnings({"unchecked"})
	private static <E> boolean step(Frame frame, E entry, Effects<E> effects, StepListener listener) {
		Fiber<?> computation = frame.computation;
		listener.onStep(computation);

		if (computation instanceof Fiber.Deferred) {
			return stepDeferred(frame, (Fiber.Deferred<Object>) computation);
		}
		if (computation instanceof Fiber.FlatMap) {
			return stepFlatMap(frame, (Fiber.FlatMap<Object, Object>) computation);
		}
		if (computation instanceof Fiber.Done) {
			return stepDone(frame, entry, effects, listener, ((Fiber.Done<Object>) computation).getValue());
		}
		if (computation instanceof Fiber.Detached) {
			return stepDetached(frame, entry, effects, listener, (Fiber.Detached<?>) computation);
		}
		if (computation instanceof Fiber.Awaiting) {
			listener.onAwaiting((Fiber.Awaiting<?>) computation);
			return stepAwaiting(frame, entry, effects, (Fiber.Awaiting<?>) computation);
		}
		if (computation instanceof Fiber.Emit) {
			listener.onEmit((Fiber.Emit<?>) computation);
			return stepEmit(frame, (Fiber.Emit<?>) computation);
		}
		if (computation instanceof Fiber.Sealed) {
			listener.onSealed((Fiber.Sealed) computation);
			return stepSealed(frame, entry, effects, (Fiber.Sealed) computation);
		}
		if (computation instanceof Fiber.Forked) {
			return stepForked(frame, entry, effects, listener, (Fiber.Forked<Object>) computation);
		}
		throw new IllegalStateException("Unknown Fiber subclass: " + computation.getClass());
	}

	private static boolean stepDeferred(Frame frame, Fiber.Deferred<Object> deferred) {
		frame.computation = deferred.getRec().get();
		return true;
	}

	private static boolean stepFlatMap(Frame frame, Fiber.FlatMap<Object, Object> flat) {
		frame.ks.addLast(flat.getF());
		frame.computation = flat.getArg();
		return true;
	}

	private static <E> boolean stepDone(Frame frame, E entry, Effects<E> effects, StepListener listener,
			Object value) {
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

	private static <E> boolean stepDetached(Frame frame, E entry, Effects<E> effects, StepListener listener,
			Fiber.Detached<?> detached) {
		frame.computation = Fiber.done(Nothing.nothing());
		listener.onDetached(detached.getFiber());
		effects.detached(entry, detached.getFiber(), detached.getInto());
		return true;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static <E> boolean stepAwaiting(Frame frame, E entry, Effects<E> effects,
			Fiber.Awaiting<?> awaiting) {
		Source source = awaiting.getSource();
		Scope owner = frame.scope;
		// AN AWAIT ALWAYS YIELDS. Every record is placed BEFORE the offer,
		// so no completion can outrun the bookkeeping; nothing here may
		// touch the frame after the offer
		frame.scope = null;
		ResumeHandle handle = effects.resumeHandle(entry, owner);
		if (owner != null) {
			// the blocked record shields the owner's counters until the
			// resume is billed
			owner.blocked(frame, source.scope());
		}
		effects.suspended(entry, source);
		source.suspend(awaiting.getReady(), handle);
		if (owner != null) {
			// the pair closes AFTER the offer: an inline completion's
			// resumed() lands inside this frame's still-open pair, so the
			// counters are never transiently drained
			effects.detached(entry, owner.finished(), null);
		}
		return false;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static boolean stepEmit(Frame frame, Fiber.Emit<?> emit) {
		MonotoneCell cell = emit.getCell();
		// production is lawful only from the closing workforce: billing
		// and production are the same statement (emit.md). The one
		// identity check at the only place production executes.
		if (frame.scope != cell.scope()) {
			throw new IllegalStateException(
					"emit into a channel closed by a foreign workforce: " + cell);
		}
		cell.grow(emit.getDelta());
		frame.computation = Fiber.done(Nothing.nothing());
		return true;
	}

	private static <E> boolean stepSealed(Frame frame, E entry, Effects<E> effects, Fiber.Sealed sealedOn) {
		Scope target = sealedOn.getScope();
		if (frame.scope == target) {
			throw new IllegalStateException(
					"awaits the seal of its own workforce - a wait for yourself: " + target);
		}
		// THE PAIR STAYS OPEN: the ledger is the work, and a member that
		// will wake with a green light is still its home's work for the
		// whole wait - the home cannot drain past it, so no seal
		// (singleton or group) can pass it by. No blocked entry, no
		// re-billing at resume; the wait is visible only as an unfinished
		// unit and in the scheduler's held registry.
		ResumeHandle handle = effects.resumeHandle(entry, frame.scope);
		effects.suspended(entry, target);
		target.awaitSeal(handle);
		return false;
	}

	private static <E> boolean stepForked(Frame frame, E entry, Effects<E> effects, StepListener listener,
			Fiber.Forked<Object> fork) {
		if (fork.getOptions() != null && !fork.getOptions().isEmpty()) {
			listener.onForked(fork);
			effects.forked(entry, fork);
		}
		// fork completes immediately: the children are injected into the
		// ambient scope and the frame continues - a fork is a CONTROL
		// scatter, its completion carries nothing
		frame.computation = Fiber.done(Nothing.nothing());
		return true;
	}
}
