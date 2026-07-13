package com.tgac.functional.algebra.laws;

// ABOUTME: The dedup license: a ⊕ a = a — the quotient where the structure
// ABOUTME: algebra becomes a state algebra.

import com.tgac.functional.algebra.IdempotentSemiring;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdempotentSemiringLaws {
	public static <S> void check(IdempotentSemiring<S> r, List<S> xs) {
		LawRegistry.record("idempotent-plus", r.getClass());
		for (S a : xs) {
			Laws.require(r.plus(a, a).equals(a), "\u2295-idempotence a \u2295 a = a", a);
		}
	}
}
