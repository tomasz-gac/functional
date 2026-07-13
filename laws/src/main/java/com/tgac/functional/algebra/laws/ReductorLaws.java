package com.tgac.functional.algebra.laws;

// ABOUTME: The step contract of chaotic iteration: an endofunction that only
// ABOUTME: shrinks (f(x) ⊑ x) and preserves the order — a propagator body's laws.

import com.tgac.functional.algebra.MeetSemilattice;
import java.util.List;
import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The endo special case of {@link MonotoneLaws} plus the deflationary axiom:
 * {@code f(x) ⊑ x} and {@code x ⊑ y ⇒ f(x) ⊑ f(y)} — together, the
 * step contract of chaotic iteration: a family of such steps drained in any
 * fair order reaches the same greatest fixpoint. Law-test a step at the desk
 * before wiring it into its loop — a constraint propagator's body (as in
 * the logic engine's FD store) is the canonical example.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReductorLaws {

	public static <L extends MeetSemilattice<L>> void check(UnaryOperator<L> f, List<L> samples) {
		for (L x : samples) {
			Laws.require(f.apply(x).leq(x), "deflationary: f(x) ⊑ x", x, f.apply(x));
		}
		for (L x : samples) {
			for (L y : samples) {
				if (x.leq(y)) {
					Laws.require(f.apply(x).leq(f.apply(y)),
							"monotone: x ⊑ y ⇒ f(x) ⊑ f(y)", x, y);
				}
			}
		}
	}
}
