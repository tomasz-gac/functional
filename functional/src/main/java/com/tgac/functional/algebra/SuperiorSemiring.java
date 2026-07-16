package com.tgac.functional.algebra;

// ABOUTME: Sobrinho's condition as a type — extension never improves, so
// ABOUTME: best-first commitment (Dijkstra) is lawful. Extends is a theorem.

/**
 * LAWS (checked by {@code SuperiorityLaws}): ⊕ is SELECTIVE
 * ({@code a ⊕ b ∈ {a, b}}) and SUPERIOR ({@code a ⊕ (a ⊗ b) = a} —
 * extending a derivation never improves it).
 *
 * <p>{@code extends BoundedSemiring} is a theorem, not a convenience: superiority
 * at {@code b = 1} gives {@code a ⊕ a = a} (idempotence), and at {@code a = 1}
 * gives {@code 1 ⊕ b = 1} (boundedness) — so a superior semiring is bounded and
 * inherits its degenerate {@code a* = 1}. Superiority adds a TOTAL order
 * (selectivity) on top: the strongest rung of the capability ladder.
 *
 * <p>What it buys: best-first agendas may COMMIT — Dijkstra's pop, greedy
 * search, committed choice over weighted goals. The rare capability that
 * CORRUPTS ANSWERS when the law fails (one negative edge and label-setting
 * silently lies), hence the interlock: call sites that commit demand this
 * type, and the gate demands the law exercise from every implementor.
 */
@CheckedBy({"superiority"})
public interface SuperiorSemiring<S> extends BoundedSemiring<S> {
}
