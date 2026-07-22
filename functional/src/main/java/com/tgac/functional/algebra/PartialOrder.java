package com.tgac.functional.algebra;

// ABOUTME: The entailment order alone — leq without any lattice operation.
// ABOUTME: Incomparable pairs are legal; Comparable's total order cannot say that.

/**
 * A partial order over an F-bounded self-type: {@code leq} and nothing else.
 *
 * <p>LAWS (checked by {@code PartialOrderLaws.check}): reflexivity,
 * transitivity, antisymmetry mod {@code equals}. Incomparability —
 * {@code !a.leq(b) && !b.leq(a)} — is expected, which is why this is not
 * {@link Comparable}: a total order must rank every pair and would have to
 * lie about incomparable knowledge values.
 *
 * <p>This is the ENTAILMENT reading: {@code a.leq(b)} = "a entails b" =
 * "a knows at least as much as b" (smaller = knows more, the descending
 * convention). Consumers that only compare knowledge — subsumption keys,
 * cache-reuse checks, dedup — should demand THIS bound and no more; both
 * semilattices extend it, deriving {@code leq} from their operation, so any
 * lattice value already qualifies.
 */
@CheckedBy({"partial-order"})
public interface PartialOrder<L extends PartialOrder<L>> {
	boolean leq(L other);
}
