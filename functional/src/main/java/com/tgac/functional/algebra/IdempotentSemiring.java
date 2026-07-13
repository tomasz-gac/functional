package com.tgac.functional.algebra;

// ABOUTME: A semiring whose ⊕ is idempotent — the dedup license as a type:
// ABOUTME: re-deriving a known answer merges to nothing.

/**
 * LAW (checked by {@code IdempotentSemiringLaws}): {@code a ⊕ a = a}.
 *
 * <p>What it buys: the natural order {@code x ⊑ y ⟺ x⊕y = y} makes ⊕ a
 * JOIN — an idempotent semiring is a join-semilattice with a compatible ⊗,
 * the point where the two algebras meet. Operationally: fixpoints over CYCLIC
 * programs terminate (tabling's termination argument), at-least-once
 * delivery is safe (duplicated work merges harmlessly), and answer dedup
 * is lawful. Counting and probability cannot implement this — cycles
 * diverge and duplicates double-count — and that impossibility is now a
 * type error at the call sites that demand it, not a runtime surprise.
 */
@CheckedBy({"idempotent-plus"})
public interface IdempotentSemiring<S> extends Semiring<S> {
}
