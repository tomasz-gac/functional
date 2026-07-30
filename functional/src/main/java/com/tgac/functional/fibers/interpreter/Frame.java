package com.tgac.functional.fibers.interpreter;

// ABOUTME: A fiber under evaluation — computation, continuation stack, ambient scope —
// ABOUTME: owning the single-step interpreter every scheduler drives through step().

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Source;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

/**
 * A fiber under evaluation: the current computation, the stack of pending
 * continuations, and the AMBIENT SCOPE the frame's work is recorded in
 * (null = unowned). {@link #step} is the single-step interpreter every
 * scheduler drives through.
 *
 * <p>The Scope calls are the interpreter's, not the schedulers': a frame
 * constructed with a scope calls started() at construction (no gap for a
 * racing seal); on completion finished() runs and the seal attempt it
 * returns runs as the frame's own continuation — same scheduler, same
 * fairness. {@link Fiber.Detached} carries the one legal escape from
 * inheritance: an explicit re-parenting scope, or null for unowned.
 * Exactly-once holds by construction — consumer code never calls the
 * Scope methods.
 *
 * <p>The rare events — a frame completing, forking, or detaching a child —
 * are reported through {@link Effects}; the common path (unwrap a deferred,
 * descend into a flatMap, apply a continuation) mutates the frame and
 * allocates nothing.
 */
public final class Frame {

	Fiber<?> computation;
	Scope scope;
	final Deque<Function<Object, Fiber<Object>>> ks = new ArrayDeque<>();

	public Frame(Fiber<?> computation) {
		this(computation, null);
	}

	/** Scoped frames are MINTED BY THE INTERPRETER (billing at birth). */
	Frame(Fiber<?> computation, Scope scope) {
		this.computation = computation;
		this.scope = scope;
		if (this.scope != null) {
			this.scope.started();
		}
	}

	/** The workforce this frame is billed to — children inherit it at a fork. */
	public Scope scope() {
		return scope;
	}

	/** The computation as of now — the drivers' snapshot read. */
	public Fiber<?> computation() {
		return computation;
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
		 * Queue the fork's children, already MINTED AND BILLED by the
		 * interpreter (they inherit the forking frame's ambient scope, and
		 * their birth happened under the forking frame's still-open pair —
		 * the membership shield is the interpreter's fact, not driver
		 * etiquette). Clause order preserved; always at least one child.
		 * The forking frame continues in the same step, so the children
		 * must be queued before it runs again.
		 */
		void forked(E entry, List<Frame> children);

		/**
		 * Queue one independent child, minted and billed by the interpreter
		 * (its scope was carried by the Detached node; null = unowned).
		 */
		void detached(E entry, Frame child);

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
	 * Step this frame once as {@code entry}, reporting events to
	 * {@code effects}. The dispatch: three families. PLUMBING (deferred, flatMap, done)
	 * mutates the frame and allocates nothing. MOVERS (detached, emit,
	 * forked) perform their effect and continue in the same step — none can
	 * block, none has a completion anyone may wait on. PARKERS (awaiting,
	 * sealed) follow the one always-park protocol and yield.
	 *
	 * @return true when the frame is still runnable; false when it yielded
	 * 		control through {@link Effects#completed} or a park
	 */
	@SuppressWarnings({"unchecked"})
	public <E> boolean step(E entry, Effects<E> effects, StepListener listener) {
		Fiber<?> computation = this.computation;
		listener.onStep(computation);

		if (computation instanceof Fiber.Deferred) {
			return stepDeferred((Fiber.Deferred<Object>) computation);
		}
		if (computation instanceof Fiber.FlatMap) {
			return stepFlatMap((Fiber.FlatMap<Object, Object>) computation);
		}
		if (computation instanceof Fiber.Done) {
			return stepDone(entry, effects, listener, ((Fiber.Done<Object>) computation).getValue());
		}
		if (computation instanceof Fiber.Detached) {
			return stepDetached(entry, effects, listener, (Fiber.Detached<?>) computation);
		}
		if (computation instanceof Fiber.Awaiting) {
			listener.onAwaiting((Fiber.Awaiting<?>) computation);
			return stepAwaiting(entry, effects, (Fiber.Awaiting<?>) computation);
		}
		if (computation instanceof Fiber.Emit) {
			listener.onEmit((Fiber.Emit<?>) computation);
			return stepEmit((Fiber.Emit<?>) computation);
		}
		if (computation instanceof Fiber.Sealed) {
			listener.onSealed((Fiber.Sealed) computation);
			return stepSealed(entry, effects, (Fiber.Sealed) computation);
		}
		if (computation instanceof Fiber.Forked) {
			return stepForked(entry, effects, listener, (Fiber.Forked<Object>) computation);
		}
		throw new IllegalStateException("Unknown Fiber subclass: " + computation.getClass());
	}

	private boolean stepDeferred(Fiber.Deferred<Object> deferred) {
		computation = deferred.getRec().get();
		return true;
	}

	private boolean stepFlatMap(Fiber.FlatMap<Object, Object> flat) {
		ks.addLast(flat.getF());
		computation = flat.getArg();
		return true;
	}

	private <E> boolean stepDone(E entry, Effects<E> effects, StepListener listener,
			Object value) {
		if (ks.isEmpty()) {
			if (scope != null) {
				// finished() and the seal attempt run as this frame's
				// continuation — the same scheduler steps the cascade and
				// whatever it emits
				Scope owner = scope;
				scope = null;
				computation = owner.finished().map(__ -> value);
				return true;
			}
			listener.onCompleted(value);
			effects.completed(entry, value);
			return false;
		}
		Function<Object, Fiber<Object>> k = ks.pollLast();
		computation = k.apply(value);
		return true;
	}

	private <E> boolean stepDetached(E entry, Effects<E> effects, StepListener listener,
			Fiber.Detached<?> detached) {
		computation = Fiber.done(Nothing.nothing());
		listener.onDetached(detached.getFiber());
		effects.detached(entry, new Frame(detached.getFiber(), detached.getInto()));
		return true;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private <E> boolean stepAwaiting(E entry, Effects<E> effects,
			Fiber.Awaiting<?> awaiting) {
		Source source = awaiting.getSource();
		Scope owner = scope;
		// AN AWAIT ALWAYS YIELDS. Every record is placed BEFORE the offer,
		// so no completion can outrun the bookkeeping; nothing here may
		// touch the frame after the offer
		scope = null;
		ResumeHandle handle = effects.resumeHandle(entry, owner);
		if (owner != null) {
			// the blocked record shields the owner's counters until the
			// resume is billed
			owner.blocked(this, source.scope());
		}
		effects.suspended(entry, source);
		source.suspend(awaiting.getReady(), handle);
		if (owner != null) {
			// the pair closes AFTER the offer: an inline completion's
			// resumed() lands inside this frame's still-open pair, so the
			// counters are never transiently drained
			effects.detached(entry, new Frame(owner.finished()));
		}
		return false;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private boolean stepEmit(Fiber.Emit<?> emit) {
		MonotoneCell cell = emit.getCell();
		// production is lawful only from the closing workforce: billing
		// and production are the same statement (emit.md). The one
		// identity check at the only place production executes.
		if (scope != cell.scope()) {
			throw new IllegalStateException(
					"emit into a channel closed by a foreign workforce: " + cell);
		}
		cell.grow(emit.getDelta());
		computation = Fiber.done(Nothing.nothing());
		return true;
	}

	private <E> boolean stepSealed(E entry, Effects<E> effects, Fiber.Sealed sealedOn) {
		Scope target = sealedOn.getScope();
		if (scope == target) {
			throw new IllegalStateException(
					"awaits the seal of its own workforce - a wait for yourself: " + target);
		}
		// THE PAIR STAYS OPEN: the ledger is the work, and a member that
		// will wake with a green light is still its home's work for the
		// whole wait - the home cannot drain past it, so no seal
		// (singleton or group) can pass it by. No blocked entry, no
		// re-billing at resume; the wait is visible only as an unfinished
		// unit and in the scheduler's held registry.
		ResumeHandle handle = effects.resumeHandle(entry, scope);
		effects.suspended(entry, target);
		target.awaitSeal(handle);
		return false;
	}

	private <E> boolean stepForked(E entry, Effects<E> effects, StepListener listener,
			Fiber.Forked<Object> fork) {
		if (fork.getOptions() != null && !fork.getOptions().isEmpty()) {
			listener.onForked(fork);
			// minted BEFORE the parent continues: each child's birth bills
			// under this frame's still-open pair - membership from within
			List<Frame> children = new ArrayList<>(fork.getOptions().size());
			for (Fiber<Object> option : fork.getOptions()) {
				children.add(new Frame(option, scope));
			}
			effects.forked(entry, children);
		}
		// fork completes immediately: the children are injected into the
		// ambient scope and the frame continues - a fork is a CONTROL
		// scatter, its completion carries nothing
		computation = Fiber.done(Nothing.nothing());
		return true;
	}

	@Override
	public String toString() {
		return "frame#" + Integer.toHexString(System.identityHashCode(this))
				+ "[" + computation.getClass().getSimpleName() + "]";
	}
}
