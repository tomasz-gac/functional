package com.tgac.functional.algebra;

// ABOUTME: A semiring whose 1 is the top (a ⊕ 1 = 1) — so its Kleene star is
// ABOUTME: degenerate (a* = 1) and cyclic streaming terminates.

/**
 * LAWS (checked by {@code BoundedSemiringLaws}): {@code a ⊕ 1 = 1} — 1 is the
 * TOP of the ⊕-order ({@code a ⊑ 1} for every a) — from which {@code a* = 1}
 * follows, so {@code star} is fixed to {@link #one()} here.
 *
 * <p>{@code extends IdempotentSemiring, ClosedSemiring} is a theorem, not a
 * convenience: boundedness makes ⊕ a bounded join-semilattice (idempotent) and
 * supplies the degenerate star (closed).
 *
 * <p>What it buys: STREAMING fixpoints TERMINATE. {@code a* = 1} is exactly
 * "looping past the base adds nothing", so a monotone fixpoint over a cyclic
 * program is stationary — the termination guarantee tabling's streaming path
 * rests on. Nonnegative min-plus (1 = 0 is the top), Viterbi, boolean are all
 * bounded; PROVENANCE (regular languages) is idempotent but NOT bounded
 * ({@code a* ≠ 1}, cyclic streaming diverges), which is why the streaming path
 * demands THIS type, not merely {@link IdempotentSemiring idempotence}.
 */
@CheckedBy({"bounded"})
public interface BoundedSemiring<S> extends IdempotentSemiring<S>, ClosedSemiring<S> {

	@Override
	default S star(S a) {
		return one();
	}
}
