package com.tgac.functional.category;

// ABOUTME: Semilattice laws for Nothing — the one-point lattice, where every
// ABOUTME: law holds by uniqueness of the element.

import static com.tgac.functional.category.Nothing.nothing;

import com.tgac.functional.algebra.laws.LawCoverage;
import com.tgac.functional.algebra.laws.LawsFor;
import com.tgac.functional.algebra.laws.SemilatticeLaws;
import java.util.Collections;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

@LawsFor(Nothing.class)
public class NothingLawsTest {

	@AfterAll
	public static void lawClaimsExercised() {
		LawCoverage.verifyClaimsExercised(NothingLawsTest.class);
	}

	@Test
	public void nothingFormsTheOnePointSemilattice() {
		SemilatticeLaws.check(Collections.singletonList(nothing()));
	}
}
