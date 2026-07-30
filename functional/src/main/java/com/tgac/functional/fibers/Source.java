package com.tgac.functional.fibers;

// ABOUTME: A monotone value fibers can await: suspend takes the waiter and completes
// ABOUTME: it exactly once - immediately when ready or sealed, at growth, or at seal.

import com.tgac.functional.algebra.Semilattice;
import com.tgac.functional.fibers.interpreter.Scope;
import java.util.function.Predicate;

/**
 * A monotone value a fiber can {@link Fiber#await}: the value only grows, so
 * a waiter's readiness predicate must be upward-closed — once true it stays
 * true, and a completion carrying a fresher value than the growth that
 * triggered it only reveals more.
 *
 * <p>THE SOURCE IS THE TOKEN: a source is a monotone value plus the
 * workforce producing it. {@link Fiber#detachTo} records work as producing
 * into a source; a suspended frame is held by the source it waits at; the
 * negative completion — {@code sealed}, provably no further growth — is the
 * quiescence of the source's own workforce. A foreign implementation of
 * this interface has no workforce the runtime can count, so it never seals:
 * waits on it need their own completion regime (timeouts), and work
 * detached into it runs unowned. One source per workforce — a production
 * publishing two values models them as one source of a product value.
 *
 * <p>A source belongs to ONE scheduler. Waiters from foreign schedulers
 * would get correct wakes (an {@link Await.Waiter} resumes through the
 * scheduler that created it), but stranded-waiter detection assumes every
 * possible completer runs in the same scheduler — a foreign scheduler
 * still growing this source reads as a false strand and is refused
 * loudly. Cross-scheduler sharing is unsupported, and fails loud, never
 * silent.
 */
public interface Source<V extends Semilattice<V>> {

	/**
	 * Take the waiter and complete it EXACTLY ONCE: immediately — possibly
	 * synchronously, before this call returns — when {@code ready} holds of
	 * the current value or the source is sealed; otherwise at the first
	 * growth satisfying the predicate, or at the seal, with the final value.
	 * An await always yields; there is no immediate-answer path. The
	 * decision must be atomic with growth and seal, and this call must not
	 * block.
	 */
	void suspend(Predicate<V> ready, Await.Waiter<V> waiter);

	/**
	 * The workforce that closes this source — the producer set whose
	 * quiescence seals it. Null for a FOREIGN source: no workforce the
	 * runtime can count, hence never sealed from within (fiber-external.md).
	 */
	default Scope scope() {
		return null;
	}
}
