package com.tgac.functional.algebra;

// ABOUTME: Laws for every semiring witness (and the derived monoid views the
// ABOUTME: Semiring interface declares) — claimed via @LawsFor for the gate.

import com.tgac.functional.algebra.laws.CommutativeMonoidLaws;
import com.tgac.functional.algebra.laws.LawsFor;
import com.tgac.functional.algebra.laws.MonoidLaws;
import com.tgac.functional.algebra.laws.SemiringLaws;
import com.tgac.functional.algebra.laws.Semirings;
import com.tgac.functional.algebra.laws.StarLaws;
import com.tgac.functional.algebra.laws.SuperiorityLaws;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

@LawsFor({Semirings.class, Semiring.class})
public class SemiringWitnessLawsTest {

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
	public void booleanClosedIdempotentSuperior() {
		SemiringLaws.check(Semirings.BOOLEAN, BOOLS);
		StarLaws.check(Semirings.BOOLEAN, BOOLS);
		SuperiorityLaws.check(Semirings.BOOLEAN, BOOLS);
	}

	@Test
	public void minPlusClosedSuperior() {
		SemiringLaws.check(Semirings.MIN_PLUS, COSTS);
		StarLaws.check(Semirings.MIN_PLUS, COSTS);
		SuperiorityLaws.check(Semirings.MIN_PLUS, COSTS);
	}

	@Test
	public void saturatingEvenAtTheBoundary() {
		SemiringLaws.check(Semirings.SATURATING, BOUNDARY);
	}

	@Test
	public void derivedMonoidViews() {
		CommutativeMonoidLaws.check(Semirings.COUNTING.additive(), SMALL);
		MonoidLaws.check(Semirings.COUNTING.multiplicative(), SMALL);
	}
}
