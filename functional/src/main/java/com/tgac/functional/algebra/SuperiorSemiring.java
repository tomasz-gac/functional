package com.tgac.functional.algebra;

// ABOUTME: Sobrinho's condition as a type — extension never improves, so
// ABOUTME: best-first commitment (Dijkstra) is lawful. Extends is a theorem.

/**
 * LAWS (checked by {@code SuperiorityLaws}): ⊕ is SELECTIVE
 * ({@code a ⊕ b ∈ {a, b}}) and SUPERIOR ({@code a ⊕ (a ⊗ b) = a} —
 * extending a derivation never improves it).
 *
 * <p>{@code extends IdempotentSemiring} is a theorem, not a convenience:
 * set {@code b = 1} in superiority and {@code a ⊕ a = a} falls out.
 *
 * <p>What it buys: best-first agendas may COMMIT — Dijkstra's pop, greedy
 * search, committed choice over weighted goals. The rare capability that
 * CORRUPTS ANSWERS when the law fails (one negative edge and label-setting
 * silently lies), hence the interlock: call sites that commit demand this
 * type, and the gate demands the law exercise from every implementor.
 */
@CheckedBy({"superiority"})
public interface SuperiorSemiring<S> extends IdempotentSemiring<S> {
}
