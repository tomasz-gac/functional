package com.tgac.functional.algebra;

// ABOUTME: The dual direction witness: growing structures with a join,
// ABOUTME: leq derives as a ∨ b = b.

/**
 * LAWS (checked by {@code SemilatticeLaws.checkJoin}): idempotence,
 * commutativity, associativity, and {@code a ⊑ (a ∨ b)} (upper bound).
 *
 * <p>What they buy, on the GROWING side: merging accumulated knowledge
 * (answer sets, gossip between workers) commutes and absorbs duplicates —
 * at-least-once delivery and out-of-order arrival are safe, which is the
 * CRDT convergence argument. Upper-bound-ness is what makes "what all
 * alternatives agree on" (a join over hypothetical outcomes) sound
 * knowledge.
 */
public interface JoinSemilattice<L extends JoinSemilattice<L>> {
	L join(L other);

	default boolean leq(L other) {
		return join(other).equals(other);
	}
}
