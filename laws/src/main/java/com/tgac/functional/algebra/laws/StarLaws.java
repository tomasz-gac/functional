package com.tgac.functional.algebra.laws;

// ABOUTME: The closed-semiring unfolding law: a* = 1 ⊕ a⊗a* — cycles solved
// ABOUTME: analytically instead of iterated.

import com.tgac.functional.algebra.ClosedSemiring;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StarLaws {
	public static <S> void check(ClosedSemiring<S> r, List<S> xs) {
		check(r, xs, Object::equals);
	}

	/** @param eq the quotient the law is claimed up to (e.g. language equivalence) */
	public static <S> void check(ClosedSemiring<S> r, List<S> xs, BiPredicate<S, S> eq) {
		for (S a : xs) {
			Laws.require(eq.test(r.star(a), r.plus(r.one(), r.times(a, r.star(a)))),
					"star unfolding a* = 1 ⊕ a⊗a*", a);
		}
		LawRegistry.record("star", r.getClass());
	}
}
