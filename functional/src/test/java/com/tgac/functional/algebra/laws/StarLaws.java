package com.tgac.functional.algebra.laws;

// ABOUTME: The closed-semiring unfolding law: a* = 1 ⊕ a⊗a* — cycles solved
// ABOUTME: analytically instead of iterated.

import com.tgac.functional.algebra.Semiring;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StarLaws {
	public static <S> void check(Semiring<S> r, List<S> xs) {
		Laws.require(r.isClosed(), "star law checked on a semiring not claiming closure");
		for (S a : xs) {
			Laws.require(r.star(a).equals(r.plus(r.one(), r.times(a, r.star(a)))),
					"star unfolding a* = 1 ⊕ a⊗a*", a);
		}
	}
}
