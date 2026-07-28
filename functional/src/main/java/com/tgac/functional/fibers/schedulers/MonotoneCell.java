package com.tgac.functional.fibers.schedulers;

// ABOUTME: The runtime's Source: a monotone value plus the workforce producing it.
// ABOUTME: Growth wakes held waiters; the workforce's quiescence seals and finalizes.

import com.tgac.functional.algebra.Semilattice;
import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Source;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A source as defined: a monotone value PLUS the workforce producing it.
 * The VALUE is a persistent {@link Semilattice} element that only grows —
 * growth is {@link Semilattice#combine} with a delta, an absorbed delta
 * refuses (strict ascent is a law of the algebra, not caller discipline);
 * the WORKFORCE is the private {@link Scope} that work detached into this
 * cell is recorded in, whose quiescence SEALS the value.
 *
 * <p>Two kinds of waiter, one completion discipline:
 * <ul>
 * <li>frames, via {@link com.tgac.functional.fibers.Fiber#await}: held by
 * {@link #suspend}, completed with {@code more(value)} by the first
 * satisfying growth, or with {@code sealed(value)} — the FINAL value — at
 * the seal. The seal path calls {@link ResumeHandle#markRunning} on EVERY
 * resumed frame before delivering any result, so no blocked record can be
 * read as dead while its sealed-arm work is pending;</li>
 * <li>data subscribers S (the interim {@link Fixpoint} path): parked by
 * {@link #park}, drained wholesale by the next growth or by the seal.</li>
 * </ul>
 *
 * <p>{@link #grow} on a sealed cell THROWS: growing past a delivered
 * sealed result would falsify it.
 */
public class MonotoneCell<V extends Semilattice<V>> implements Source<V> {

	private static final class Held<V> {
		final Predicate<V> ready;
		final Await.Waiter<V> waiter;

		Held(Predicate<V> ready, Await.Waiter<V> waiter) {
			this.ready = ready;
			this.waiter = waiter;
		}
	}

	private V value;
	private final ArrayList<Held<V>> held = new ArrayList<>();
	private final Scope scope = new Scope();

	public MonotoneCell(V initial) {
		this.value = initial;
		this.scope.completeWaitersOnSeal(this::completeAllSealed);
	}

	/** The workforce — for the interpreter's token resolution. */
	Scope scope() {
		return scope;
	}

	/** A consistent snapshot; the value is persistent, so read it lock-free after. */
	public synchronized V read() {
		return value;
	}

	public boolean isSealed() {
		return scope.isSealed();
	}

	/** Manual seal — external certificates. Completes no waiter; see {@link Scope#seal}. */
	public void seal() {
		scope.seal();
	}

	@Override
	public synchronized Await.Result<V> suspend(Predicate<V> ready, Await.Waiter<V> waiter) {
		// the seal read is atomic and upward-closed; a stale false parks a
		// waiter the seal's drain then completes — never a lost waiter
		if (scope.isSealed()) {
			return Await.Result.sealed(value);
		}
		if (ready.test(value)) {
			return Await.Result.more(value);
		}
		held.add(new Held<>(ready, waiter));
		return null;
	}

	/**
	 * Join {@code delta} into the value. An absorbed delta is inert; strict
	 * growth completes every held frame whose predicate the grown value
	 * satisfies (outside the cell monitor — the cell is a leaf).
	 *
	 * @throws IllegalStateException on a sealed cell — no new value is
	 * 		derivable at a seal, and growing past a delivered sealed result
	 * 		would falsify it
	 */
	public void grow(V delta) {
		ArrayList<Held<V>> woken = new ArrayList<>();
		V grown;
		synchronized (this) {
			if (scope.isSealed()) {
				throw new IllegalStateException("grow on a sealed source: " + delta);
			}
			V combined = value.combine(delta);
			if (combined.equals(value)) {
				return;
			}
			value = combined;
			grown = combined;
			for (Iterator<Held<V>> it = held.iterator(); it.hasNext(); ) {
				Held<V> h = it.next();
				if (h.ready.test(grown)) {
					woken.add(h);
					it.remove();
				}
			}
		}
		for (Held<V> h : woken) {
			h.waiter.complete(Await.Result.more(grown));
		}
	}

	/**
	 * The seal's completion of held frames, run by {@link Scope} once the
	 * flag is set: FIRST mark every runtime waiter running
	 * ({@link ResumeHandle#markRunning} — its owner's ledger reads it as
	 * running before any blocked record can satisfy a quiescence predicate),
	 * THEN deliver the final value.
	 */
	private void completeAllSealed() {
		ArrayList<Held<V>> rest;
		V finalValue;
		synchronized (this) {
			rest = new ArrayList<>(held);
			held.clear();
			finalValue = value;
		}
		for (Held<V> h : rest) {
			h.waiter.complete(Await.Result.sealed(finalValue));
		}
	}

}
