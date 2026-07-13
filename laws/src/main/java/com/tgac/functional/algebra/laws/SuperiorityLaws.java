package com.tgac.functional.algebra.laws;

// ABOUTME: Sobrinho's condition — the interlock for best-first agendas, the
// ABOUTME: rare tuning knob that corrupts answers when the law fails.

import com.tgac.functional.algebra.SuperiorSemiring;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SuperiorityLaws {
	/** Requires a selective ⊕ (picks one argument); order: a ≤ b iff a ⊕ b = a. */
	public static <S> void check(SuperiorSemiring<S> r, List<S> xs) {
		for (S a : xs) {
			for (S b : xs) {
				S p = r.plus(a, b);
				Laws.require(p.equals(a) || p.equals(b), "selective ⊕", a, b);
				Laws.require(r.plus(a, r.times(a, b)).equals(a), "superiority a ≤ a⊗b", a, b);
			}
		}
		LawRegistry.record("superiority", r.getClass());
	}
}
