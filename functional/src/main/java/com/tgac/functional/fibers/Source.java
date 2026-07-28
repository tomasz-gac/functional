package com.tgac.functional.fibers;

// ABOUTME: A monotone value fibers can await: suspend answers immediately or holds
// ABOUTME: the waiter; growth and the scope's seal complete held waiters.

import java.util.function.Predicate;

/**
 * A monotone value a fiber can {@link Fiber#await}: the value only grows, so
 * a waiter's readiness predicate must be upward-closed — once true it stays
 * true, and a completion carrying a fresher value than the growth that
 * triggered it only reveals more.
 *
 * <p>The negative completion comes from the scope: when {@link #scope()}
 * seals — its ledger proves no growth can ever arrive — the source
 * completes every held waiter with a sealed {@link Await.Result} carrying
 * the final value.
 *
 * <p>A source belongs to ONE scheduler. Waiters from foreign schedulers
 * would get correct wakes (an {@link Await.Waiter} resumes through the
 * scheduler that created it), but stranded-waiter detection assumes every
 * possible completer runs in the same scheduler — a foreign scheduler
 * still growing this source reads as a false strand and is refused
 * loudly. Cross-scheduler sharing is unsupported, and fails loud, never
 * silent.
 */
public interface Source<V> {

	/**
	 * The scope whose seal is this source's negative completion — the place
	 * recorded ({@link WorkScope#blocked}) for every waiter held here. Null
	 * for sources with no seal (externally completed work; waits there need
	 * their own completion regime, e.g. timeouts).
	 */
	WorkScope scope();

	/**
	 * Attempt to suspend a waiter. Atomic with growth and seal: either the
	 * answer is already available — {@code ready} holds of the current value,
	 * or the source is sealed — and the immediate result is returned, or the
	 * waiter is HELD and null is returned. A held waiter is completed exactly
	 * once — at the first growth satisfying its predicate, or at seal — and
	 * never synchronously inside this call. Must not block.
	 */
	Await.Result<V> suspend(Predicate<V> ready, Await.Waiter<V> waiter);
}
