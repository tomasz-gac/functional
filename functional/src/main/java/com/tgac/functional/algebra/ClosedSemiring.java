package com.tgac.functional.algebra;

// ABOUTME: A semiring with Kleene closure — a cycle's contribution computed
// ABOUTME: analytically instead of iterated; star lives HERE, not on Semiring.

/**
 * LAW (checked by {@code StarLaws}): {@code a* = 1 ⊕ a ⊗ a*}.
 *
 * <p>What it buys: cycles solved as equations (geometric series,
 * Floyd–Warshall) — the escape hatch for non-idempotent plugs on cyclic
 * queries, where iteration diverges. Declaring {@code star} here instead
 * of as a throwing default on {@link Semiring} makes "no closure"
 * unrepresentable rather than an {@code UnsupportedOperationException}.
 */
@CheckedBy({"star"})
public interface ClosedSemiring<S> extends Semiring<S> {

	S star(S a);
}
