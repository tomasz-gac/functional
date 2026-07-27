package com.tgac.functional.algebra.laws;

// ABOUTME: Two-structure laws: absorption between a meet witness and a join
// ABOUTME: projection, and the INFLATIONARY variant (interval hulls) without it.

import com.tgac.functional.algebra.PartialOrder;
import com.tgac.functional.algebra.Semilattice;
import java.util.List;
import java.util.function.BinaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A lattice is NOT a semilattice — it is one value type carrying two
 * semilattice structures, so the second structure arrives here as a
 * PROJECTION ({@code join}, a plain binary op), never as a second
 * inherited face. The value type implements a meet-flavored {@link Semilattice}
 * (its canonical order); these kits check the projection's own
 * semilattice laws and the absorption that interlocks the two.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LatticeLaws {

	/** Exact two-structure values: both semilattice kits plus absorption. */
	public static <L extends Semilattice<L> & PartialOrder<L>> void check(List<L> xs, BinaryOperator<L> join) {
		SemilatticeLaws.checkLeqReversesAccumulation(xs);
		checkJoinProjection(xs, join);
		for (L a : xs) {
			for (L b : xs) {
				Laws.require(a.combine(join.apply(a, b)).equals(a), "absorption meet-join", a, b);
				Laws.require(join.apply(a, a.combine(b)).equals(a), "absorption join-meet", a, b);
			}
		}
		LawRegistry.recordSamples("lattice", xs);
	}

	/** Approximate joins: sound to generalize over, not exact — skip absorption. */
	public static <L extends Semilattice<L> & PartialOrder<L>> void checkInflationary(List<L> xs, BinaryOperator<L> join) {
		SemilatticeLaws.checkLeqReversesAccumulation(xs);
		for (L a : xs) {
			for (L b : xs) {
				L ab = join.apply(a, b);
				Laws.require(a.leq(ab) && b.leq(ab), "inflationary join is an upper bound", a, b);
			}
		}
		LawRegistry.recordSamples("lattice-inflationary", xs);
		LawRegistry.recordSamples("join-inflationary", xs);
	}

	/** The projection's own semilattice algebra: idempotent, commutative, associative. */
	private static <L> void checkJoinProjection(List<L> xs, BinaryOperator<L> join) {
		for (L a : xs) {
			Laws.require(join.apply(a, a).equals(a), "join idempotence", a);
			for (L b : xs) {
				L ab = join.apply(a, b);
				Laws.require(ab.equals(join.apply(b, a)), "join commutativity", a, b);
				for (L c : xs) {
					Laws.require(join.apply(ab, c).equals(join.apply(a, join.apply(b, c))),
							"join associativity", a, b, c);
				}
			}
		}
		LawRegistry.recordSamples("join", xs);
	}
}
