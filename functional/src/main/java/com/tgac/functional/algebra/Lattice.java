package com.tgac.functional.algebra;

// ABOUTME: Meet and join on one value type; join may be INFLATIONARY (an upper
// ABOUTME: bound of the true join — the interval-hull relaxation), see LatticeLaws.

/**
 * LAWS: both semilattices, plus — for EXACT lattices
 * ({@code LatticeLaws.check}) — absorption:
 * <pre>
 * a ∧ (a ∨ b) = a     a ∨ (a ∧ b) = a
 * </pre>
 * which makes the two derived orders agree (this interface resolves the
 * diamond to the MEET reading). INFLATIONARY lattices
 * ({@code LatticeLaws.checkInflationary}) relax the join to any upper bound
 * of the true join — interval hulls, convex approximations. What the
 * relaxation buys: joins that only ever GENERALIZE stay sound for
 * harvesting common knowledge from alternatives (extra generosity weakens
 * conclusions, never corrupts them) while remaining representable in
 * domains that cannot express exact unions.
 */
public interface Lattice<L extends Lattice<L>> extends MeetSemilattice<L>, JoinSemilattice<L> {
	@Override
	default boolean leq(L other) {
		return MeetSemilattice.super.leq(other);
	}
}
