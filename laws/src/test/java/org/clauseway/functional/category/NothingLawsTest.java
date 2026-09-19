package org.clauseway.functional.category;

// ABOUTME: Semilattice laws for Nothing — the one-point lattice, where every
// ABOUTME: law holds by uniqueness of the element.

import static org.clauseway.functional.category.Nothing.nothing;

import org.clauseway.functional.algebra.laws.LawCoverage;
import org.clauseway.functional.algebra.laws.LawsFor;
import org.clauseway.functional.algebra.laws.SemilatticeLaws;
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
