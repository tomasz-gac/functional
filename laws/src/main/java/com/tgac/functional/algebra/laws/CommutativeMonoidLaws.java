package com.tgac.functional.algebra.laws;

// ABOUTME: MonoidLaws plus commutativity — the license for parallel and
// ABOUTME: out-of-order folds.

import com.tgac.functional.algebra.Monoid;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommutativeMonoidLaws {
	public static <M> void check(Monoid<M> m, List<M> xs) {
		check(m, xs, Object::equals);
	}

	/** @param eq the quotient the laws are claimed up to */
	public static <M> void check(Monoid<M> m, List<M> xs, BiPredicate<M, M> eq) {
		MonoidLaws.check(m, xs, eq);
		for (M a : xs) {
			for (M b : xs) {
				Laws.require(eq.test(m.combine(a, b), m.combine(b, a)), "commutativity", a, b);
			}
		}
		LawRegistry.record("commutative", m.getClass());
	}
}
