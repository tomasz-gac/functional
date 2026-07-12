package com.tgac.functional.algebra;

// ABOUTME: A monoid whose combine commutes — parallel and out-of-order folds
// ABOUTME: are lawful exactly here.

/**
 * LAW (checked by {@code CommutativeMonoidLaws}, on top of the monoid laws):
 * <pre>
 * commutativity: combine(a, b) = combine(b, a)
 * </pre>
 * What it buys: the license to REORDER — contributions may arrive in any
 * order, so parallel reduction, work-stealing folds, and out-of-order
 * delivery (a fiber finishing early, a message arriving late) cannot change
 * the result. Aggregation over a fair or parallel search REQUIRES this;
 * plain {@link Monoid} (list concatenation) does not have it, which is why
 * answer ORDER is scheduler-dependent while answer COUNTS are not.
 */
@CheckedBy({"commutative"})
public interface CommutativeMonoid<M> extends Monoid<M> {
}
