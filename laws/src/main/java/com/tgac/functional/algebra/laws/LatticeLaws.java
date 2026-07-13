package com.tgac.functional.algebra.laws;

// ABOUTME: Full-lattice absorption, and the INFLATIONARY variant for
// ABOUTME: approximate joins (interval hulls): upper-bound-ness without absorption.

import com.tgac.functional.algebra.Lattice;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LatticeLaws {
	/** Exact lattices: both semilattice kits plus absorption. */
	public static <L extends Lattice<L>> void check(List<L> xs) {
		SemilatticeLaws.checkMeet(xs);
		SemilatticeLaws.checkJoin(xs);
		for (L a : xs) {
			for (L b : xs) {
				Laws.require(a.meet(a.join(b)).equals(a), "absorption meet-join", a, b);
				Laws.require(a.join(a.meet(b)).equals(a), "absorption join-meet", a, b);
			}
		}
		LawRegistry.recordSamples("lattice", xs);
	}

	/** Approximate joins: sound to generalize over, not exact — skip absorption. */
	public static <L extends Lattice<L>> void checkInflationary(List<L> xs) {
		SemilatticeLaws.checkMeet(xs);
		for (L a : xs) {
			for (L b : xs) {
				Laws.require(a.leq(a.join(b)) && b.leq(a.join(b)), "inflationary join is an upper bound", a, b);
			}
		}
		LawRegistry.recordSamples("lattice-inflationary", xs);
		LawRegistry.recordSamples("join-inflationary", xs);
	}
}
