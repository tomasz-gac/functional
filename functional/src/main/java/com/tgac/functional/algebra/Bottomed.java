package com.tgac.functional.algebra;

// ABOUTME: Failure as a value — layered separately because important lattices
// ABOUTME: (substitutions) have no bottom: their failure lives outside, as absence.

/**
 * LAWS (checked by {@code BottomedLaws}, for meet-semilattices):
 * <pre>
 * absorbing: ⊥ ∧ a = ⊥
 * minimal:   ⊥ ⊑ a
 * </pre>
 * What they buy: ⊥ is contradiction as a VALUE — the first wipeout kills a
 * branch and stays killed through any further meets (safe to race), and
 * drains can short-circuit on it without the step function's cooperation.
 * Kept separate from {@link MeetSemilattice} because significant lattices
 * lack a bottom value (substitutions: unification failure is CPS absence,
 * not an element) — the hierarchy must not force one on them.
 */
public interface Bottomed {
	boolean isBottom();
}
