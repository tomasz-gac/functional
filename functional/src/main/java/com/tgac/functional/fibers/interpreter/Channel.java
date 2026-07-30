package com.tgac.functional.fibers.interpreter;

// ABOUTME: The value channel: a monotone value plus the workforce producing it.
// ABOUTME: Growth wakes held waiters; the workforce's quiescence seals and finalizes.

import com.tgac.functional.algebra.Semilattice;
import com.tgac.functional.fibers.AwaitResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A channel: a monotone value PLUS the workforce producing it.
 * The VALUE is a persistent {@link Semilattice} element that only grows —
 * growth is {@link Semilattice#combine} with a delta, an absorbed delta
 * refuses (strict ascent is a law of the algebra, not caller discipline);
 * the WORKFORCE is the private {@link Scope} that work detached into this
 * cell is recorded in, whose quiescence SEALS the value.
 *
 * <p>Waiters are frames, via {@link com.tgac.functional.fibers.Fiber#await}:
 * held by {@link #suspend}, completed with {@code more(value)} by the first
 * satisfying growth, or with {@code sealed(value)} — the FINAL value — at
 * the seal.
 *
 * <p>{@link #grow} on a sealed cell THROWS: growing past a delivered
 * sealed result would falsify it.
 */
public class Channel<V extends Semilattice<V>> {

	private static final class Held<V> {
		final Predicate<V> ready;
		final ResumeHandle waiter;

		Held(Predicate<V> ready, ResumeHandle waiter) {
			this.ready = ready;
			this.waiter = waiter;
		}
	}

	private V value;
	private final ArrayList<Held<V>> held = new ArrayList<>();
	private final Scope scope;

	/** A channel closed by its own private workforce. */
	public Channel(V initial) {
		this(initial, Scope.scope());
	}

	/**
	 * A channel CLOSED BY the given workforce (emit.md): several cells may
	 * share one scope, sealing together at its quiescence. The cell registers
	 * its EOF translation — the seal completes value-waiters with
	 * sealed(value).
	 */
	public Channel(V initial, Scope closedBy) {
		this.value = initial;
		this.scope = closedBy;
		this.scope.onSeal(this::completeAllSealed);
	}

	/** The workforce — for the interpreter's token resolution. */
	public Scope scope() {
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

	/**
	 * Take the waiter and complete it EXACTLY ONCE: immediately — possibly
	 * synchronously, before this call returns — when {@code ready} holds of
	 * the current value or the channel is sealed; otherwise at the first
	 * growth satisfying the predicate, or at the seal, with the final value.
	 * An await always yields; there is no immediate-answer path. The
	 * decision is atomic with growth and seal under this monitor.
	 */
	void suspend(Predicate<V> ready, ResumeHandle waiter) {
		AwaitResult<V> immediate;
		synchronized (this) {
			// the seal read is atomic and upward-closed; a stale false parks a
			// waiter the seal's drain then completes — never a lost waiter
			if (scope.isSealed()) {
				immediate = AwaitResult.sealed(value);
			} else if (ready.test(value)) {
				immediate = AwaitResult.more(value);
			} else {
				held.add(new Held<>(ready, waiter));
				return;
			}
		}
		// completed outside the monitor - the cell is a leaf; synchronous is
		// fine: the suspending frame's own open pair shields the counters
		waiter.complete(immediate);
	}

	/**
	 * Join {@code delta} into the value. An absorbed delta is inert; strict
	 * growth completes every held frame whose predicate the grown value
	 * satisfies (outside the cell monitor — the cell is a leaf). Production
	 * runs through {@link com.tgac.functional.fibers.Fiber#produce} and the
	 * emit step — only the interpreter grows a cell.
	 *
	 * @throws IllegalStateException on a sealed cell — no new value is
	 * 		derivable at a seal, and growing past a delivered sealed result
	 * 		would falsify it
	 */
	void grow(V delta) {
		ArrayList<Held<V>> woken = new ArrayList<>();
		V grown;
		synchronized (this) {
			if (scope.isSealed()) {
				throw new IllegalStateException("grow on a sealed channel: " + delta);
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
			h.waiter.complete(AwaitResult.more(grown));
		}
	}

	/**
	 * The seal's completion of held frames, run by {@link Scope} once the
	 * flag is set: every held frame receives the final value.
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
			h.waiter.complete(AwaitResult.sealed(finalValue));
		}
	}
}
