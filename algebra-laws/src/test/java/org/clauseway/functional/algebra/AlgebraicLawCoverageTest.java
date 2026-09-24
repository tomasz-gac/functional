package org.clauseway.functional.algebra;

// ABOUTME: The gate for the algebra witnesses: scans their code source (reactor
// ABOUTME: dir or jar) and demands @LawsFor claims with after-hooks.

import org.clauseway.functional.laws.LawChecker;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.clauseway.functional.algebra.CheckedBy;

public class AlgebraicLawCoverageTest {

	@Test
	public void everyAlgebraicInstanceIsClaimedByALawsForTest() throws IOException {
		LawChecker.of(CheckedBy.class).verify(
				LawChecker.codeSource(Semirings.class),
				Paths.get("target", "test-classes"),
				AfterAll.class);
	}
}
