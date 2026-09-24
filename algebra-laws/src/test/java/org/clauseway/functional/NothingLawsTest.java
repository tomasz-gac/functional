package org.clauseway.functional;

// ABOUTME: Semilattice laws for Nothing — the one-point lattice, where every
// ABOUTME: law holds by uniqueness of the element.

import static org.clauseway.functional.Nothing.nothing;

import org.clauseway.functional.laws.LawChecker;
import org.clauseway.functional.laws.LawsFor;
import org.clauseway.functional.algebra.laws.SemilatticeLaws;
import java.util.Collections;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.clauseway.functional.algebra.CheckedBy;

@LawsFor(Nothing.class)
public class NothingLawsTest {

	@AfterAll
	public static void lawClaimsExercised() {
		LawChecker.of(CheckedBy.class).verifyClaimsExercised(NothingLawsTest.class);
	}

	@Test
	public void nothingFormsTheOnePointSemilattice() {
		SemilatticeLaws.check(Collections.singletonList(nothing()));
	}
}
