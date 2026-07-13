package com.tgac.functional.algebra.laws;

// ABOUTME: The closed-semiring unfolding law: a* = 1 ⊕ a⊗a* — cycles solved
// ABOUTME: analytically instead of iterated.

import com.tgac.functional.algebra.ClosedSemiring;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StarLaws {
	public static <S> void check(ClosedSemiring<S> r, List<S> xs) {
		LawRegistry.record("star", r.getClass());
		for (S a : xs) {
			Laws.require(r.star(a).equals(r.plus(r.one(), r.times(a, r.star(a)))),
					"star unfolding a* = 1 ⊕ a⊗a*", a);
		}
	}
}
