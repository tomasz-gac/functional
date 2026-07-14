package com.tgac.functional.algebra.laws;

// ABOUTME: The dedup license: a ⊕ a = a — the quotient where the structure
// ABOUTME: algebra becomes a state algebra.

import com.tgac.functional.algebra.IdempotentSemiring;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdempotentSemiringLaws {
	public static <S> void check(IdempotentSemiring<S> r, List<S> xs) {
		check(r, xs, Object::equals);
	}

	/** @param eq the quotient the laws are claimed up to */
	public static <S> void check(IdempotentSemiring<S> r, List<S> xs, BiPredicate<S, S> eq) {
		for (S a : xs) {
			Laws.require(eq.test(r.plus(a, a), a), "\u2295-idempotence a \u2295 a = a", a);
		}
		LawRegistry.record("idempotent-plus", r.getClass());
	}
}
