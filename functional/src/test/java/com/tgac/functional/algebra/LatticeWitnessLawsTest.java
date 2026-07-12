package com.tgac.functional.algebra;

// ABOUTME: Laws for the lattice witnesses: Mask exact both directions,
// ABOUTME: Range inflationary, both bottomed.

import com.tgac.functional.algebra.laws.BottomedLaws;
import com.tgac.functional.algebra.laws.LatticeLaws;
import com.tgac.functional.algebra.laws.Lattices;
import com.tgac.functional.algebra.laws.LawsFor;
import com.tgac.functional.algebra.laws.SemilatticeLaws;
import java.util.Arrays;
import java.util.List;
import com.tgac.functional.algebra.laws.LawCoverage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

@LawsFor(Lattices.class)
public class LatticeWitnessLawsTest {

	@AfterAll
	static void lawClaimsExercised() {
		LawCoverage.verifyClaimsExercised(LatticeWitnessLawsTest.class);
	}

	@Test
	public void maskIsAnExactLatticeBothDirections() {
		List<Lattices.Mask> xs = Arrays.asList(
				Lattices.Mask.of(0b0000L), Lattices.Mask.of(0b1010L),
				Lattices.Mask.of(0b0110L), Lattices.Mask.of(0b1111L));
		LatticeLaws.check(xs);
		SemilatticeLaws.checkMeet(xs);
		SemilatticeLaws.checkJoin(xs);
		BottomedLaws.check(xs);
	}

	@Test
	public void rangesAreInflationary() {
		List<Lattices.Range> xs = Arrays.asList(
				Lattices.Range.of(0, 10), Lattices.Range.of(3, 5),
				Lattices.Range.of(8, 12), Lattices.Range.of(1, 0));
		LatticeLaws.checkInflationary(xs);
		BottomedLaws.check(xs);
	}
}
