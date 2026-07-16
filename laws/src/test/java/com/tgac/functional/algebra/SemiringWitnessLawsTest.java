package com.tgac.functional.algebra;

// ABOUTME: Laws for every semiring witness (and the derived monoid views the
// ABOUTME: Semiring interface declares) — claimed via @LawsFor for the gate.

import com.tgac.functional.algebra.laws.BoundedSemiringLaws;
import com.tgac.functional.algebra.laws.CommutativeMonoidLaws;
import com.tgac.functional.algebra.laws.IdempotentSemiringLaws;
import com.tgac.functional.algebra.laws.LawsFor;
import com.tgac.functional.algebra.laws.MonoidLaws;
import com.tgac.functional.algebra.laws.SemiringLaws;
import com.tgac.functional.algebra.laws.StarLaws;
import com.tgac.functional.algebra.laws.SuperiorityLaws;
import java.util.Arrays;
import java.util.List;
import com.tgac.functional.algebra.laws.LawCoverage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

@LawsFor({Semirings.class, Semiring.class})
public class SemiringWitnessLawsTest {

	@AfterAll
	static void lawClaimsExercised() {
		LawCoverage.verifyClaimsExercised(SemiringWitnessLawsTest.class);
	}

	private static final List<Long> SMALL = Arrays.asList(0L, 1L, 2L, 3L, 7L);
	private static final List<Long> BOUNDARY = Arrays.asList(
			0L, 1L, 2L, Long.MAX_VALUE / 2, Long.MAX_VALUE - 1, Long.MAX_VALUE);
	private static final List<Boolean> BOOLS = Arrays.asList(true, false);
	private static final List<Long> COSTS = Arrays.asList(0L, 1L, 5L, 12L, Long.MAX_VALUE);

	@Test
	public void counting() {
		SemiringLaws.check(Semirings.COUNTING, SMALL);
	}

	@Test
	public void booleanBoundedSuperior() {
		SemiringLaws.check(Semirings.BOOLEAN, BOOLS);
		IdempotentSemiringLaws.check(Semirings.BOOLEAN, BOOLS);
		StarLaws.check(Semirings.BOOLEAN, BOOLS);
		BoundedSemiringLaws.check(Semirings.BOOLEAN, BOOLS);
		SuperiorityLaws.check(Semirings.BOOLEAN, BOOLS);
	}

	@Test
	public void minPlusBoundedSuperior() {
		SemiringLaws.check(Semirings.MIN_PLUS, COSTS);
		IdempotentSemiringLaws.check(Semirings.MIN_PLUS, COSTS);
		StarLaws.check(Semirings.MIN_PLUS, COSTS);
		BoundedSemiringLaws.check(Semirings.MIN_PLUS, COSTS);
		SuperiorityLaws.check(Semirings.MIN_PLUS, COSTS);
	}

	@Test
	public void saturatingEvenAtTheBoundary() {
		SemiringLaws.check(Semirings.SATURATING, BOUNDARY);
	}

	@Test
	public void provenanceClosedIdempotentByLanguage() {
		// a free (regex) semiring: laws hold up to LANGUAGE equivalence, not the
		// structural tree equals — the quotient the eq-parameterized kits take.
		List<Provenance> xs = Arrays.asList(
				Provenance.empty(), Provenance.epsilon(),
				Provenance.sym("a"), Provenance.sym("b"),
				Provenance.alt(Provenance.sym("a"), Provenance.sym("b")),
				Provenance.cat(Provenance.sym("a"), Provenance.sym("b")),
				Provenance.star(Provenance.sym("a")));
		SemiringLaws.check(Semirings.PROVENANCE, xs, (p, q) -> p.sameLanguage(q, 5));
		IdempotentSemiringLaws.check(Semirings.PROVENANCE, xs, (p, q) -> p.sameLanguage(q, 5));
		StarLaws.check(Semirings.PROVENANCE, xs, (p, q) -> p.sameLanguage(q, 5));
	}

	@Test
	public void derivedMonoidViews() {
		CommutativeMonoidLaws.check(Semirings.COUNTING.additive(), SMALL);
		MonoidLaws.check(Semirings.COUNTING.multiplicative(), SMALL);
	}
}
