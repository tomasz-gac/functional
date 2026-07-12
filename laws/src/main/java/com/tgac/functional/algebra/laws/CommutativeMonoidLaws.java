package com.tgac.functional.algebra.laws;

// ABOUTME: MonoidLaws plus commutativity — the license for parallel and
// ABOUTME: out-of-order folds.

import com.tgac.functional.algebra.Monoid;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommutativeMonoidLaws {
	public static <M> void check(Monoid<M> m, List<M> xs) {
		LawRegistry.record("commutative", m.getClass());
		MonoidLaws.check(m, xs);
		for (M a : xs) {
			for (M b : xs) {
				Laws.require(m.combine(a, b).equals(m.combine(b, a)), "commutativity", a, b);
			}
		}
	}
}
