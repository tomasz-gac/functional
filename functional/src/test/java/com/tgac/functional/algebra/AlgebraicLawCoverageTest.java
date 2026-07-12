package com.tgac.functional.algebra;

// ABOUTME: The thin coverage gate: implementors of any @CheckedBy algebra must
// ABOUTME: be claimed by a @LawsFor test; algebras derive from annotations.

import com.tgac.functional.algebra.laws.LawCoverage;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class AlgebraicLawCoverageTest {

	@Test
	public void everyAlgebraicInstanceIsClaimedByALawsForTest() throws IOException {
		LawCoverage.verify(Paths.get("target", "classes"), Paths.get("target", "test-classes"));
	}
}
