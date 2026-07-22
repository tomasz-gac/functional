package com.tgac.functional.algebra.laws;

// ABOUTME: Reflexivity, transitivity and antisymmetry-mod-equals for partial
// ABOUTME: orders; incomparable pairs are legal and should appear in samples.

import com.tgac.functional.algebra.PartialOrder;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PartialOrderLaws {

	public static <L extends PartialOrder<L>> void check(List<L> xs) {
		for (L a : xs) {
			Laws.require(a.leq(a), "leq reflexivity", a);
			for (L b : xs) {
				if (a.leq(b) && b.leq(a)) {
					Laws.require(a.equals(b), "leq antisymmetry (mod equals)", a, b);
				}
				for (L c : xs) {
					if (a.leq(b) && b.leq(c)) {
						Laws.require(a.leq(c), "leq transitivity", a, b, c);
					}
				}
			}
		}
		LawRegistry.recordSamples("partial-order", xs);
	}
}
