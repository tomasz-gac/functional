package com.tgac.functional.fibers.primitives;

// ABOUTME: A distributed fixpoint: a semilattice value grown by billed concurrent
// ABOUTME: work, sealed at quiescence — the sealed value is the fixpoint.

import com.tgac.functional.algebra.Semilattice;
import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import io.vavr.collection.List;
import io.vavr.control.Option;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A fixpoint computed by a DISTRIBUTED workforce: a {@link Scope} (billing
 * ledger, seal, cascade) composed with a {@link MonotoneCell} (the published
 * {@link Semilattice} value, grown by combine, waking parked subscribers).
 * {@link #read} is the current iterate, {@link #grow} one accumulation step
 * — and direction is not this class's business: every semilattice ascends
 * its own accumulation order, whatever the domain calls the op.
 *
 * <p>THE FIXPOINT CONDITION is quiescence of the workforce, not
 * re-application of a function — because the producing "function" here is
 * the running work itself, not reified re-runnable data. {@code
 * MonotoneDrain} can test {@code F(x) = x} directly (its propagators are
 * enumerable objects it can apply once more); a distributed workforce
 * cannot be re-applied, only proven exhausted. The ledger's counting is
 * that proof: no producer remains, no waiter can wake — under monotone
 * growth this implies no delta is pending, which IS {@code F(x) = x},
 * reached from the only side reachable. Same theorem, operational face.
 *
 * <p>{@code MonotoneDrain} needs none of this ceremony because one loop
 * owns all its work — termination is its own queue running empty, and
 * nobody watches the value mid-flight. Here the work is many independent
 * fibers that park across unit boundaries and may form cycles, so
 * completion is a global property of a dynamic graph: counted work, sleeper
 * edges, the seal CAS, the group seal for rings. The ceremony prices
 * DISTRIBUTION, not direction.
 */
public final class Fixpoint<V extends Semilattice<V>, S> {

	private final MonotoneCell<V, S> cell;
	private final Scope<S> scope;

	public Fixpoint(V initial, Function<S, Fixpoint<V, S>> ownerOf) {
		this.cell = new MonotoneCell<>(initial);
		this.scope = new Scope<>(s -> {
			Fixpoint<V, S> owner = ownerOf.apply(s);
			return owner == null ? null : owner.scope;
		});
		this.scope.drainOnSeal(cell::drainParked);
	}

	/** The termination-detection half — the graph face scope-only consumers share. */
	public Scope<S> scope() {
		return scope;
	}

	/** Register the fiber to spawn when this fixpoint seals, given the drained subscribers. */
	public void onSealed(Function<List<S>, Fiber<Nothing>> work) {
		scope.onSealed(work);
	}

	/**
	 * Enclose a workforce: bill {@code seed} to this fixpoint, and when the
	 * seal fires — all transitively billed work exhausted — run
	 * {@code atSeal} with the sealed value. The one-call form of the
	 * track/onSealed pairing for consumers that only want the fixpoint.
	 */
	public Fiber<Nothing> enclose(Fiber<Nothing> seed, Function<V, Fiber<Nothing>> atSeal) {
		return scope.enclose(seed, () -> atSeal.apply(read()));
	}

	// ---- the value half ----

	public V read() {
		return cell.read();
	}

	/** @return the drained subscribers to wake, or none when the delta was absorbed */
	public Option<List<S>> grow(V delta) {
		return cell.grow(delta);
	}

	/** @return false if the value moved past the subscriber — keep reading */
	public boolean park(S subscriber, Predicate<V> caughtUp) {
		return cell.park(subscriber, caughtUp);
	}

	public int parkedCount() {
		return cell.parkedCount();
	}

	// ---- the work half, delegated to the scope ----

	/**
	 * Bill {@code work} as one unit of this fixpoint's running work, with a
	 * cascade attempt on finish. Null-tolerant statically: unowned work
	 * runs unbilled.
	 */
	public static <V extends Semilattice<V>, S> Fiber<Nothing> track(Fixpoint<V, S> fixpoint, Fiber<Nothing> work) {
		return Scope.track(fixpoint == null ? null : fixpoint.scope, work);
	}

	public void sleeping(S sleeper, Fixpoint<V, S> at) {
		scope.sleeping(sleeper, at.scope);
	}

	public void awake(S sleeper) {
		scope.awake(sleeper);
	}

	// ---- the seal ----

	public boolean isSealed() {
		return scope.isSealed();
	}

	/** Manual seal — tests and external certificates. */
	public void seal() {
		scope.seal();
	}

	/**
	 * Seal this fixpoint if quiescent and propagate along sleeper edges.
	 *
	 * @return the fiber composing every newly sealed unit's {@link #onSealed}
	 * 		work (the star emit) — inert for plain tabling
	 */
	public Fiber<Nothing> sealCascade() {
		return scope.sealCascade();
	}
}
