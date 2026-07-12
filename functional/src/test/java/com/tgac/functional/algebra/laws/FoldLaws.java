package com.tgac.functional.algebra.laws;

// ABOUTME: foldEarly == foldLate: folding commutes with grouping — the
// ABOUTME: executable certificate that folding inside cells is lawful.

import com.tgac.functional.algebra.Monoid;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FoldLaws {
	public static <M> void check(Monoid<M> m, List<M> xs, List<M> ys) {
		M late = fold(m, concat(xs, ys));
		M early = m.combine(fold(m, xs), fold(m, ys));
		Laws.require(late.equals(early), "foldEarly == foldLate", xs, ys);
	}

	private static <M> M fold(Monoid<M> m, List<M> xs) {
		M acc = m.empty();
		for (M x : xs) {
			acc = m.combine(acc, x);
		}
		return acc;
	}

	private static <M> List<M> concat(List<M> xs, List<M> ys) {
		List<M> out = new java.util.ArrayList<>(xs);
		out.addAll(ys);
		return out;
	}
}
