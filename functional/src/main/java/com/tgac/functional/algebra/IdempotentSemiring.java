package com.tgac.functional.algebra;

// ABOUTME: A semiring whose ⊕ is idempotent — the dedup license as a type:
// ABOUTME: re-deriving a known answer merges to nothing.

/**
 * LAW (checked by {@code IdempotentSemiringLaws}): {@code a ⊕ a = a}.
 *
 * <p>What it buys: the natural order {@code x ⊑ y ⟺ x⊕y = y} makes ⊕ a
 * JOIN — an idempotent semiring is a join-semilattice with a compatible ⊗,
 * the point where the two algebras meet. Operationally: at-least-once delivery
 * is safe (duplicated work merges harmlessly) and answer dedup is lawful.
 * What idempotence does NOT buy is cyclic-fixpoint termination — that is
 * {@link BoundedSemiring boundedness} ({@code a* = 1}): idempotence absorbs an
 * IDENTICAL re-derivation ({@code x ⊕ x = x}), but a loop yields the DIFFERENT
 * value {@code ℓ⊗x}, which only {@code 1 ⊕ ℓ = 1} absorbs — so an
 * idempotent-but-unbounded semiring (provenance) still diverges on a cycle.
 * Counting and probability cannot implement even this dedup — duplicates
 * double-count — so the impossibility is a type error at the call sites that
 * demand it, not a runtime surprise.
 */
@CheckedBy({"idempotent-plus"})
public interface IdempotentSemiring<S> extends Semiring<S> {
}
