package com.tgac.functional.algebra;

// ABOUTME: Witness-style monoid: computation carriers are often foreign types
// ABOUTME: (Long, collections), so the algebra is an object, not a self-type.

/**
 * LAWS (checked by {@code MonoidLaws}):
 * <pre>
 * identity:      combine(empty, a) = a = combine(a, empty)
 * associativity: combine(combine(a, b), c) = combine(a, combine(b, c))
 * </pre>
 * What they buy: identity is the seed of every fold (an empty search
 * aggregates to {@code empty()} instead of a special case); associativity is
 * the license to REGROUP — folding in batches, chunks or trees gives the same
 * answer, which is the foldEarly == foldLate certificate's foundation
 * ({@code FoldLaws}).
 */
@CheckedBy({"monoid", "commutative"})
public interface Monoid<M> {
	M empty();

	M combine(M a, M b);
}
