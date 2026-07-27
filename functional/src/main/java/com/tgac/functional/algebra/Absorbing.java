package com.tgac.functional.algebra;

// ABOUTME: The absorbing element as a value — the accumulation-terminal point past
// ABOUTME: which combine cannot move; layered separately because some semilattices lack one.

/**
 * LAWS (checked by {@code AbsorbingLaws}):
 * <pre>
 * absorbing: z · a  is absorbing
 * terminal:  a ⊑ z  in the accumulation order
 * </pre>
 * The absorbing element (semigroup's zero) is always the TOP of the
 * accumulation order — the point past which {@link Semilattice#combine}
 * cannot move. What the laws buy: reaching it is VALUE-LEVEL convergence,
 * so accumulators can short-circuit on it without the step function's
 * cooperation ({@code MonotoneDrain}'s early exit), and — because no
 * pending work can move the value past it — even a distributed fixpoint
 * may seal early at the absorber. What the absorber MEANS is the domain's
 * reading: knowledge-side it is ⊥, contradiction as a value — the first
 * wipeout kills a branch and stays killed through any further combines
 * (safe to race); answer-side it is saturation — the first witness decides
 * (the any/exists commit).
 *
 * <p>Kept separate from {@link Semilattice} because significant instances
 * lack an absorber (substitutions: unification failure is CPS absence, not
 * an element) — the hierarchy must not force one on them.
 */
@CheckedBy({"absorbing"})
public interface Absorbing {
	boolean isAbsorbing();
}
