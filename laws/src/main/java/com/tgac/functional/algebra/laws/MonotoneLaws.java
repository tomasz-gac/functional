package com.tgac.functional.algebra.laws;

// ABOUTME: Monotone maps between two orders: as the input ascends its declared
// ABOUTME: chain, the output never moves against the supplied target order.

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The law of maps between DIFFERENT orders — {@code a ⊑ b ⇒ f(a) ⊑ f(b)} —
 * checked along a caller-declared ascending chain, so the source side needs
 * no interface (a Package chain works without Package being a lattice). The
 * target order is supplied: an optimizer pricer is monotone into counts
 * ordered DOWNWARD (more knowledge in, fewer-or-equal answers out — pass
 * {@code (x, y) -> x >= y}), and its free theorem is the one the optimizer
 * stands on: stale prices stay sound as knowledge grows.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonotoneLaws {

	/**
	 * @param ascending samples DECLARED ascending in the source order,
	 * 		weakest first
	 * @param leq the target order's ≤
	 */
	public static <A, B> void check(List<A> ascending, Function<A, B> f, BiPredicate<B, B> leq) {
		for (int i = 0; i < ascending.size(); i++) {
			for (int j = i + 1; j < ascending.size(); j++) {
				B lo = f.apply(ascending.get(i));
				B hi = f.apply(ascending.get(j));
				Laws.require(leq.test(lo, hi),
						"monotone: a ⊑ b ⇒ f(a) ⊑ f(b)", i, j, lo, hi);
			}
		}
	}
}
