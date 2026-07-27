package com.tgac.functional.algebra.laws;

// ABOUTME: The absorber absorbs combine and is terminal in the accumulation order —
// ABOUTME: the laws that make isAbsorbing load-bearing rather than decorative.

import com.tgac.functional.algebra.Absorbing;
import com.tgac.functional.algebra.Semilattice;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AbsorbingLaws {
	public static <L extends Semilattice<L> & Absorbing> void check(List<L> xs) {
		boolean sawAbsorber = false;
		for (L a : xs) {
			if (a.isAbsorbing()) {
				sawAbsorber = true;
				for (L b : xs) {
					Laws.require(((Absorbing) a.combine(b)).isAbsorbing(), "absorber absorbs combine", a, b);
					Laws.require(b.absorbedBy(a), "absorber is terminal in accumulation", a, b);
				}
			}
		}
		Laws.require(sawAbsorber, "samples must include an absorbing element");
		LawRegistry.recordSamples("absorbing", xs);
	}
}
