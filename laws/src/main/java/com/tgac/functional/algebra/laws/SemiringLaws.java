package com.tgac.functional.algebra.laws;

// ABOUTME: Both unit monoids, distributivity (condition 2 of DP — the forgetting
// ABOUTME: license), annihilation, and the claimed-capability checks.

import com.tgac.functional.algebra.Semiring;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SemiringLaws {
	public static <S> void check(Semiring<S> r, List<S> xs) {
		LawRegistry.record("semiring", r.getClass());
		CommutativeMonoidLaws.check(r.additive(), xs);
		MonoidLaws.check(r.multiplicative(), xs);
		for (S a : xs) {
			Laws.require(r.times(r.zero(), a).equals(r.zero()), "left annihilation", a);
			Laws.require(r.times(a, r.zero()).equals(r.zero()), "right annihilation", a);
			for (S b : xs) {
				for (S c : xs) {
					Laws.require(r.times(a, r.plus(b, c)).equals(r.plus(r.times(a, b), r.times(a, c))),
							"left distributivity", a, b, c);
					Laws.require(r.times(r.plus(a, b), c).equals(r.plus(r.times(a, c), r.times(b, c))),
							"right distributivity", a, b, c);
				}
			}
		}
	}
}
