package com.tgac.functional.algebra.laws;

// ABOUTME: Both unit monoids, distributivity (condition 2 of DP — the forgetting
// ABOUTME: license), annihilation, and the claimed-capability checks.

import com.tgac.functional.algebra.Semiring;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SemiringLaws {
	public static <S> void check(Semiring<S> r, List<S> xs) {
		check(r, xs, Object::equals);
	}

	/** @param eq the quotient the laws are claimed up to */
	public static <S> void check(Semiring<S> r, List<S> xs, BiPredicate<S, S> eq) {
		CommutativeMonoidLaws.check(r.additive(), xs, eq);
		MonoidLaws.check(r.multiplicative(), xs, eq);
		for (S a : xs) {
			Laws.require(eq.test(r.times(r.zero(), a), r.zero()), "left annihilation", a);
			Laws.require(eq.test(r.times(a, r.zero()), r.zero()), "right annihilation", a);
			for (S b : xs) {
				for (S c : xs) {
					Laws.require(eq.test(r.times(a, r.plus(b, c)), r.plus(r.times(a, b), r.times(a, c))),
							"left distributivity", a, b, c);
					Laws.require(eq.test(r.times(r.plus(a, b), c), r.plus(r.times(a, c), r.times(b, c))),
							"right distributivity", a, b, c);
				}
			}
		}
		LawRegistry.record("semiring", r.getClass());
	}
}
