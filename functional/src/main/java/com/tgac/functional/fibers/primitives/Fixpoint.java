package com.tgac.functional.fibers.primitives;

// ABOUTME: A distributed fixpoint over parked continuations: producers grow a
// ABOUTME: semilattice value, growth FEEDS the value to subscribers, quiescence seals.

import com.tgac.functional.algebra.Semilattice;
import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The fixpoint, with its mechanism in its shape: subscribers are PARKED
 * CONTINUATIONS (each {@code S} carries a resume point and a cursor into the
 * value), and the injected {@link #feed} is how growth pushes the GROWN VALUE
 * into them — a subscriber never reaches back to poll: the value arrives
 * pushed by growth, or handed over when a park refuses. The lifecycle is
 * four verbs:
 *
 * <pre>
 * master(work)                 a producer, detached into this fixpoint's scope
 * grow(delta)                  join the value; every subscriber the growth
 *                              drains is FED the grown value - billed-before-
 *                              awoken - as a detached fiber riding the tail
 * park(s, caughtUp)            a subscriber out of value parks, its owner's
 *                              ledger kept honest; right(sealAttempt) when
 *                              parked, left(freshValue) when the value moved -
 *                              keep reading what you were handed
 * onSealed / seal              convergence: the workforce is provably
 *                              exhausted, no subscriber can ever be fed again
 * </pre>
 *
 * THE FIXPOINT CONDITION is quiescence of the workforce, not re-application
 * of a function - the producing "function" is running work, not reified
 * re-runnable data, so it can only be proven exhausted (the scope's ledger
 * is that proof), never re-applied. Direction is not this class's business:
 * every semilattice ascends its own accumulation order.
 */
public final class Fixpoint<V extends Semilattice<V>, S> {

	private final MonotoneCell<V, S> cell;
	private final Scope<S> scope;
	private final Function<S, Scope<S>> ownerOf;
	private final BiFunction<S, V, Fiber<Nothing>> feed;

	/**
	 * @param ownerOf which fixpoint's scope a subscriber works FOR (null =
	 * 		unowned top-level work, unbilled, gating nothing)
	 * @param feed how a drained subscriber consumes the grown value - the
	 * 		continuation push, owned by the domain
	 */
	public Fixpoint(V initial,
			Function<S, Fixpoint<?, S>> ownerOf,
			BiFunction<S, V, Fiber<Nothing>> feed) {
		this.cell = new MonotoneCell<>(initial);
		this.ownerOf = s -> {
			Fixpoint<?, S> owner = ownerOf.apply(s);
			return owner == null ? null : owner.scope;
		};
		this.scope = new Scope<>(this.ownerOf);
		this.scope.drainOnSeal(cell::drainParked);
		this.feed = feed;
	}

	// ---- the value ----

	public V read() {
		return cell.read();
	}

	/**
	 * Join {@code delta} into the value. An absorbed delta (no new
	 * knowledge) is inert; strict growth drains every parked subscriber and
	 * FEEDS each one the grown value - billed to its owner before its
	 * sleeping record is removed, so a racing seal never reads quiescence in
	 * the gap - as detached fibers riding the returned tail.
	 */
	public Fiber<Nothing> grow(V delta) {
		Option<List<S>> drained = cell.grow(delta);
		if (drained.isEmpty()) {
			return Fiber.done(Nothing.nothing());
		}
		// a racing later grow may make this snapshot even fresher - sound:
		// subscribers read by cursor, so a newer value only feeds them more
		V grown = cell.read();
		Fiber<Nothing> tail = Fiber.done(Nothing.nothing());
		for (S subscriber : drained.get()) {
			Scope<S> owner = ownerOf.apply(subscriber);
			Fiber<Nothing> fed = owner == null
					? Fiber.detach(feed.apply(subscriber, grown))
					: owner.respawn(subscriber, feed.apply(subscriber, grown));
			Fiber<Nothing> prev = tail;
			tail = prev.flatMap(__ -> fed);
		}
		return tail;
	}

	// ---- the workforce ----

	/** A producer: {@code work} runs detached, billed to this fixpoint. */
	public Fiber<Nothing> master(Fiber<Nothing> work) {
		return Fiber.detachTo(scope, work);
	}

	/**
	 * Park a subscriber that ran out of value, its OWNER's ledger kept
	 * honest: the sleeping record lands BEFORE the park (a feed can only
	 * drain a parked subscriber, so the record is always there to remove),
	 * and a refused park - the value moved past the subscriber - removes it
	 * again and hands the FRESH VALUE back: keep reading, never poll. The
	 * owner is {@link #ownerOf}'s answer - the same authority {@link #grow}
	 * bills respawns to, so sleep and respawn can never split ledgers.
	 *
	 * @return right(the owner's seal attempt) when parked - parking may have
	 * 		completed the owner's region, the emit rides the tail; left(the
	 * 		fresh value) when the value moved past the subscriber
	 */
	public Either<V, Fiber<Nothing>> park(S subscriber, Predicate<V> caughtUp) {
		Scope<S> ownerScope = ownerOf.apply(subscriber);
		if (ownerScope != null) {
			ownerScope.blocked(subscriber, scope);
		}
		if (cell.park(subscriber, caughtUp)) {
			return Either.right(ownerScope == null
					? Fiber.done(Nothing.nothing())
					: Fiber.defer(ownerScope::sealCascade));
		}
		if (ownerScope != null) {
			ownerScope.unblocked(subscriber);
		}
		return Either.left(cell.read());
	}

	public int parkedCount() {
		return cell.parkedCount();
	}

	// ---- the seal ----

	/** Register the fiber to spawn when this fixpoint seals, given the drained subscribers. */
	public void onSealed(Function<List<S>, Fiber<Nothing>> work) {
		scope.onSealed(work);
	}

	public boolean isSealed() {
		return scope.isSealed();
	}

	/** Manual seal - tests and external certificates. */
	public void seal() {
		scope.seal();
	}
}
