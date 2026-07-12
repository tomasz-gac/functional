package com.tgac.functional.algebra;

// ABOUTME: The thin coverage gate: every algebraic implementor on the main
// ABOUTME: classpath must be claimed by a @LawsFor-annotated test class.

import com.tgac.functional.algebra.laws.LawCoverage;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class AlgebraicLawCoverageTest {

	@Test
	public void everyAlgebraicInstanceIsClaimedByALawsForTest() throws IOException {
		LawCoverage.verify(
				Paths.get("target", "classes"),
				Paths.get("target", "test-classes"),
				MeetSemilattice.class, JoinSemilattice.class, Lattice.class,
				Monoid.class, CommutativeMonoid.class, Semiring.class, Bottomed.class);
	}
}
