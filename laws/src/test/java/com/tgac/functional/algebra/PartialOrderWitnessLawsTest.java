package com.tgac.functional.algebra;

// ABOUTME: Laws for the direct-leq partial-order witness: subset inclusion,
// ABOUTME: sampled with incomparable pairs.

import com.tgac.functional.algebra.laws.LawCoverage;
import com.tgac.functional.algebra.laws.LawsFor;
import com.tgac.functional.algebra.laws.PartialOrderLaws;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

@LawsFor(PartialOrders.class)
public class PartialOrderWitnessLawsTest {

	@AfterAll
	static void lawClaimsExercised() {
		LawCoverage.verifyClaimsExercised(PartialOrderWitnessLawsTest.class);
	}

	@Test
	public void subsetInclusionIsAPartialOrder() {
		// {}, {1}, {2}, {1,2} — {1} and {2} are incomparable, the case a
		// total order could not represent
		List<PartialOrders.Subset> xs = Arrays.asList(
				PartialOrders.Subset.of(0b00L), PartialOrders.Subset.of(0b01L),
				PartialOrders.Subset.of(0b10L), PartialOrders.Subset.of(0b11L));
		PartialOrderLaws.check(xs);
	}
}
