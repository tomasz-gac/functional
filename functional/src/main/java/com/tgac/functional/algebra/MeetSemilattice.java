package com.tgac.functional.algebra;

// ABOUTME: Knowledge values with a meet — the DIRECTION WITNESS for descending
// ABOUTME: structures: leq derives from the meet, smaller = knows more.

/**
 * F-bounded self-type: knowledge values we own implement this directly.
 *
 * <p>LAWS (checked by {@code SemilatticeLaws.checkMeet}):
 * <pre>
 * idempotence:   a ∧ a = a
 * commutativity: a ∧ b = b ∧ a
 * associativity: (a ∧ b) ∧ c = a ∧ (b ∧ c)
 * lower bound:   (a ∧ b) ⊑ a  and  (a ∧ b) ⊑ b
 * </pre>
 * What they buy: idempotence makes RE-POSTING knowledge free (duplicate
 * constraint posts are no-ops); commutativity + associativity make posting
 * ORDER irrelevant (agenda freedom, and chaotic iteration reaching one
 * fixpoint); and ENTAILMENT IS FREE FROM THE MEET —
 * {@code a ⊑ b ⟺ a ∧ b = a} — so any store with intersection and equality
 * can compare knowledge (subsumption keys, cache fallback) with no new
 * operation.
 *
 * <p>The name carries the DIRECTION — descending knowledge — because every
 * staleness and cache-fallback theorem is one-directional: stale bounds stay
 * sound where data shrinks and lie where it grows. A growing structure wants
 * {@link JoinSemilattice}, never this with a flipped convention.
 */
@CheckedBy({"meet", "lattice", "lattice-inflationary"})
public interface MeetSemilattice<L extends MeetSemilattice<L>> extends PartialOrder<L> {
	L meet(L other);

	@Override
	default boolean leq(L other) {
		return meet(other).equals(this);
	}
}
