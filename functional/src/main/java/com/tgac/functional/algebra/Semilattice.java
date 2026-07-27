package com.tgac.functional.algebra;

// ABOUTME: The direction-neutral semilattice: one idempotent commutative associative
// ABOUTME: op and the ACCUMULATION ORDER it induces — what a Fixpoint requires of its value.

/**
 * The one semilattice algebra, direction unnamed: {@link #combine} is
 * idempotent, commutative and associative, and induces the ACCUMULATION
 * ORDER {@code x ⊑ y ⟺ combine(x, y) = y} — the direction accumulation
 * moves, under which every semilattice ascends by definition. "Meet" and
 * "join" are the DOMAIN's names for which way it reads this order:
 * {@link JoinSemilattice} extends this with {@code combine = join} (its
 * {@code leq} coincides with the accumulation order);
 * {@link MeetSemilattice} with {@code combine = meet} (its accumulation
 * order is its knowledge order reversed). A LATTICE is deliberately NOT a
 * semilattice: one value type carrying two semilattice structures has no
 * answer to "which one am I?", so a two-structure type exposes its
 * structures as PROJECTIONS (witness operations, the law kits' style),
 * never by inheriting both faces.
 *
 * <p>What the laws buy an accumulator: idempotence makes duplicate delivery
 * free, commutativity + associativity make arrival order irrelevant, and
 * {@link #absorbedBy} — growth refusal — is decidable from the op and
 * equality alone.
 */
public interface Semilattice<L extends Semilattice<L>> {

	L combine(L other);

	/** The accumulation order: this contributes nothing {@code other} lacks. */
	default boolean absorbedBy(L other) {
		return combine(other).equals(other);
	}
}
