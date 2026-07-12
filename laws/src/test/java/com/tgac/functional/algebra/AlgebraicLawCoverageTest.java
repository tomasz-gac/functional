package com.tgac.functional.algebra;

// ABOUTME: The gate for the algebra witnesses: scans their code source (reactor
// ABOUTME: dir or jar) and demands @LawsFor claims with after-hooks.

import com.tgac.functional.algebra.laws.LawCoverage;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

public class AlgebraicLawCoverageTest {

	@Test
	public void everyAlgebraicInstanceIsClaimedByALawsForTest() throws IOException {
		LawCoverage.verify(
				LawCoverage.codeSource(Semirings.class),
				Paths.get("target", "test-classes"),
				AfterAll.class);
	}
}
