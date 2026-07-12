package com.tgac.functional.algebra;

// ABOUTME: The evaluator spec for and/or structure: ⊕ merges alternatives,
// ABOUTME: ⊗ chains steps; each question about all-the-ways is one of these.

/**
 * A compositional question about an and/or search — exists? how many?
 * cheapest? — must say what {@code or} and {@code and} do to its quantity;
 * that pair IS a semiring.
 *
 * <p>LAWS (checked by {@code SemiringLaws}):
 * <pre>
 * (S, ⊕, 0) commutative monoid   — alternatives merge in any order/grouping
 * (S, ⊗, 1) monoid               — steps chain associatively, 1 = fresh branch
 * distributivity: a ⊗ (b ⊕ c) = (a ⊗ b) ⊕ (a ⊗ c)   (and symmetrically)
 * annihilation:   0 ⊗ a = 0 = a ⊗ 0
 * </pre>
 * What they buy: DISTRIBUTIVITY is the big one — restructuring the search
 * tree (factoring, reordering, folding inside table cells before enumeration
 * finishes) cannot change the answer; it is dynamic programming's "optimal
 * substructure" as an equation, and the license every optimizer rewrite
 * runs on. ANNIHILATION is failure propagation: a derivation through a dead
 * subgoal is dead, so failure needs no bookkeeping — absence does the work.
 *
 * <p>OPTIONAL CAPABILITIES, each with its own kit when claimed:
 * <ul>
 * <li>{@link #isIdempotentPlus()} — {@code a ⊕ a = a}: re-deriving a known
 * answer adds nothing, so fixpoints over CYCLIC programs terminate (tabling's
 * termination argument) and at-least-once delivery is safe (duplicated work
 * merges harmlessly). Counting and probability lack it: cycles diverge and
 * duplicates double-count.</li>
 * <li>{@link #isClosed()} / {@link #star(Object)} — {@code a* = 1 ⊕ a⊗a*}:
 * a cycle's contribution computed analytically (geometric series,
 * Floyd–Warshall) instead of iterated; the escape hatch for non-idempotent
 * plugs on cyclic queries.</li>
 * <li>{@link #isSuperior()} — selective ⊕ with {@code a ⊕ (a ⊗ b) = a}:
 * extending a derivation never improves it, so BEST-FIRST agendas (Dijkstra)
 * are correct. The rare tuning knob that CORRUPTS ANSWERS when the law
 * fails, hence the interlock.</li>
 * </ul>
 */
public interface Semiring<S> {
	S zero();

	S one();

	S plus(S a, S b);

	S times(S a, S b);

	default boolean isIdempotentPlus() {
		return false;
	}

	default boolean isClosed() {
		return false;
	}

	default S star(S a) {
		throw new UnsupportedOperationException("not a closed semiring");
	}

	default boolean isSuperior() {
		return false;
	}

	default CommutativeMonoid<S> additive() {
		return new CommutativeMonoid<S>() {
			@Override
			public S empty() {
				return zero();
			}

			@Override
			public S combine(S a, S b) {
				return plus(a, b);
			}
		};
	}

	default Monoid<S> multiplicative() {
		return new Monoid<S>() {
			@Override
			public S empty() {
				return one();
			}

			@Override
			public S combine(S a, S b) {
				return times(a, b);
			}
		};
	}
}
