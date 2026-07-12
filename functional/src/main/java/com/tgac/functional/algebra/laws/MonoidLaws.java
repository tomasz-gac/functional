package com.tgac.functional.algebra.laws;

// ABOUTME: Identity and associativity over caller-supplied samples — the
// ABOUTME: honesty gate every declared Monoid instance must pass.

import com.tgac.functional.algebra.Monoid;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonoidLaws {
	public static <M> void check(Monoid<M> m, List<M> xs) {
		for (M a : xs) {
			Laws.require(m.combine(m.empty(), a).equals(a), "left identity", a);
			Laws.require(m.combine(a, m.empty()).equals(a), "right identity", a);
			for (M b : xs) {
				for (M c : xs) {
					Laws.require(m.combine(m.combine(a, b), c).equals(m.combine(a, m.combine(b, c))),
							"associativity", a, b, c);
				}
			}
		}
	}
}
