package com.tgac.functional.fibers.primitives;

// ABOUTME: A sealable region of work: a growing value, the work producing it,
// ABOUTME: the seal, and the cascade — a Scope composed with a MonotoneCell.

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import io.vavr.collection.List;
import io.vavr.control.Option;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The termination-detection unit for work that PUBLISHES: a {@link Scope}
 * (billing ledger, seal, cascade) composed with a {@link MonotoneCell} (the
 * region's published value, growing monotonically, waking parked
 * subscribers). The seal rule and the cascade live wholly on the scope; the
 * cell's contribution is what the seal drains — subscribers parked on the
 * value are the dead-at-seal harvest, so {@code ownerOf} can awaken their
 * owners along the sleeper edges.
 */
public final class Region<V, S> {

	private final MonotoneCell<V, S> cell;
	private final Scope<S> scope;

	public Region(V initial, Function<S, Region<V, S>> ownerOf) {
		this.cell = new MonotoneCell<>(initial);
		this.scope = new Scope<>(s -> {
			Region<V, S> owner = ownerOf.apply(s);
			return owner == null ? null : owner.scope;
		});
		this.scope.drainOnSeal(cell::drainParked);
	}

	/** The termination-detection half — the graph face scope-only consumers share. */
	public Scope<S> scope() {
		return scope;
	}

	/** Register the fiber to spawn when this region seals, given the drained subscribers. */
	public void onSealed(Function<List<S>, Fiber<Nothing>> work) {
		scope.onSealed(work);
	}

	// ---- the value half ----

	public V read() {
		return cell.read();
	}

	/** @return the drained subscribers to wake, or none when the step refused */
	public Option<List<S>> grow(Function<V, Option<V>> step) {
		return cell.grow(step);
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
	 * Bill {@code work} as one unit of this region's running work, with a
	 * cascade attempt on finish. Null-tolerant statically: unowned work
	 * runs unbilled.
	 */
	public static <V, S> Fiber<Nothing> track(Region<V, S> region, Fiber<Nothing> work) {
		return Scope.track(region == null ? null : region.scope, work);
	}

	public void sleeping(S sleeper, Region<V, S> at) {
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
	 * Seal this region if quiescent and propagate along sleeper edges.
	 *
	 * @return the fiber composing every newly sealed region's {@link #onSealed}
	 * 		work (the star emit) — inert for plain tabling
	 */
	public Fiber<Nothing> sealCascade() {
		return scope.sealCascade();
	}
}
