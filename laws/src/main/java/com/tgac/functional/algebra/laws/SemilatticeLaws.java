package com.tgac.functional.algebra.laws;

// ABOUTME: The one semilattice kit — combine's algebra and the accumulation order —
// ABOUTME: plus coherence checks tying a type's explicit leq to that order.

import com.tgac.functional.algebra.PartialOrder;
import com.tgac.functional.algebra.Semilattice;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * There is ONE semilattice algebra; "meet" and "join" are a domain's names
 * for which way its explicit {@code leq} reads the accumulation order.
 * {@link #check} certifies the algebra; the coherence kits certify the
 * co-declaration: {@link #checkLeqAgreesWithAccumulation} for join-flavored
 * types (leq IS the accumulation order), {@link #checkLeqReversesAccumulation}
 * for meet-flavored ones (accumulating knowledge descends the extension).
 * Either coherence plus the algebra implies the order laws (reflexivity,
 * antisymmetry mod equals, transitivity), so both record partial-order
 * coverage.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SemilatticeLaws {

	public static <L extends Semilattice<L>> void check(List<L> xs) {
		// absorbedBy is derived through equals, so its laws are only claimable
		// at the equals quotient — quotient types get idempotence-under-eq only
		for (L a : xs) {
			Laws.require(a.absorbedBy(a), "absorbedBy reflexivity", a);
		}
		check(xs, Object::equals);
	}

	/** @param eq the quotient the laws are claimed up to (solved form for substitutions) */
	public static <L extends Semilattice<L>> void check(List<L> xs, BiPredicate<L, L> eq) {
		for (L a : xs) {
			Laws.require(eq.test(a.combine(a), a), "combine idempotence", a);
			for (L b : xs) {
				L ab = a.combine(b);
				Laws.require(eq.test(ab, b.combine(a)), "combine commutativity", a, b);
				Laws.require(eq.test(a.combine(ab), ab) && eq.test(b.combine(ab), ab),
						"combine result absorbs both arguments", a, b);
				for (L c : xs) {
					Laws.require(eq.test(a.combine(b).combine(c), a.combine(b.combine(c))),
							"combine associativity", a, b, c);
				}
			}
		}
		LawRegistry.recordSamples("semilattice", xs);
	}

	/** Join-flavored co-declaration: {@code leq} IS the accumulation order. */
	public static <L extends Semilattice<L> & PartialOrder<L>> void checkLeqAgreesWithAccumulation(List<L> xs) {
		check(xs);
		for (L a : xs) {
			for (L b : xs) {
				Laws.require(a.leq(b) == a.absorbedBy(b), "leq agrees with accumulation", a, b);
			}
		}
		LawRegistry.recordSamples("order-ascending", xs);
		LawRegistry.recordSamples("partial-order", xs);
	}

	/** Meet-flavored co-declaration: accumulating knowledge DESCENDS the extension. */
	public static <L extends Semilattice<L> & PartialOrder<L>> void checkLeqReversesAccumulation(List<L> xs) {
		check(xs);
		for (L a : xs) {
			for (L b : xs) {
				Laws.require(a.leq(b) == b.absorbedBy(a), "leq reverses accumulation", a, b);
			}
		}
		LawRegistry.recordSamples("order-descending", xs);
		LawRegistry.recordSamples("partial-order", xs);
	}
}
