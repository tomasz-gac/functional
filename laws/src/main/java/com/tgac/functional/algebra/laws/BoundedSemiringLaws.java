package com.tgac.functional.algebra.laws;

// ABOUTME: The boundedness license: a ⊕ 1 = 1 (1 is the top) — from which the
// ABOUTME: degenerate star a* = 1 follows, the guarantee cyclic streaming halts.

import com.tgac.functional.algebra.BoundedSemiring;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BoundedSemiringLaws {
	public static <S> void check(BoundedSemiring<S> r, List<S> xs) {
		check(r, xs, Object::equals);
	}

	/** @param eq the quotient the laws are claimed up to */
	public static <S> void check(BoundedSemiring<S> r, List<S> xs, BiPredicate<S, S> eq) {
		for (S a : xs) {
			Laws.require(eq.test(r.plus(a, r.one()), r.one()), "boundedness a ⊕ 1 = 1", a);
			Laws.require(eq.test(r.star(a), r.one()), "degenerate star a* = 1", a);
		}
		LawRegistry.record("bounded", r.getClass());
	}
}
