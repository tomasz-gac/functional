package com.tgac.functional.algebra.laws;

// ABOUTME: Bottom is absorbing for meet and minimal in the order — the laws
// ABOUTME: that make isBottom load-bearing rather than decorative.

import com.tgac.functional.algebra.Bottomed;
import com.tgac.functional.algebra.MeetSemilattice;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BottomedLaws {
	public static <L extends MeetSemilattice<L> & Bottomed> void check(List<L> xs) {
		LawRegistry.recordSamples("bottomed", xs);
		boolean sawBottom = false;
		for (L a : xs) {
			if (a.isBottom()) {
				sawBottom = true;
				for (L b : xs) {
					Laws.require(a.meet(b).isBottom(), "bottom absorbs meet", a, b);
					Laws.require(a.leq(b), "bottom is minimal", a, b);
				}
			}
		}
		Laws.require(sawBottom, "samples must include a bottom element");
	}
}
