package com.tgac.functional.algebra.laws;

// ABOUTME: Identity and associativity over caller-supplied samples — the
// ABOUTME: honesty gate every declared Monoid instance must pass.

import com.tgac.functional.algebra.Monoid;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonoidLaws {
	public static <M> void check(Monoid<M> m, List<M> xs) {
		check(m, xs, Object::equals);
	}

	/** @param eq the quotient the laws are claimed up to */
	public static <M> void check(Monoid<M> m, List<M> xs, BiPredicate<M, M> eq) {
		for (M a : xs) {
			Laws.require(eq.test(m.combine(m.empty(), a), a), "left identity", a);
			Laws.require(eq.test(m.combine(a, m.empty()), a), "right identity", a);
			for (M b : xs) {
				for (M c : xs) {
					Laws.require(eq.test(m.combine(m.combine(a, b), c), m.combine(a, m.combine(b, c))),
							"associativity", a, b, c);
				}
			}
		}
		LawRegistry.record("monoid", m.getClass());
	}
}
