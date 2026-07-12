package com.tgac.functional.algebra;

// ABOUTME: Laws and fold certificates for the aggregate monoid witnesses.

import com.tgac.functional.algebra.laws.CommutativeMonoidLaws;
import com.tgac.functional.algebra.laws.FoldLaws;
import com.tgac.functional.algebra.laws.LawsFor;
import com.tgac.functional.algebra.laws.MonoidLaws;
import io.vavr.collection.List;
import java.util.Arrays;
import com.tgac.functional.algebra.laws.LawCoverage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

@LawsFor(Monoids.class)
public class MonoidWitnessLawsTest {

	@AfterAll
	static void lawClaimsExercised() {
		LawCoverage.verifyClaimsExercised(MonoidWitnessLawsTest.class);
	}

	private static final java.util.List<Long> SMALL = Arrays.asList(0L, 1L, 2L, 3L, 7L);
	private static final java.util.List<Integer> SMALL_INTS =
			Arrays.asList(Integer.MIN_VALUE, -1, 0, 1, 7, Integer.MAX_VALUE);

	@Test
	public void commutativeWitnesses() {
		CommutativeMonoidLaws.check(Monoids.LONG_SUM, SMALL);
		CommutativeMonoidLaws.check(Monoids.LONG_MIN, SMALL);
		CommutativeMonoidLaws.check(Monoids.LONG_MAX, SMALL);
	}

	@Test
	public void intWitnesses() {
		CommutativeMonoidLaws.check(Monoids.INT_SUM, Arrays.asList(-1, 0, 1, 2, 7));
		CommutativeMonoidLaws.check(Monoids.INT_MIN, SMALL_INTS);
		CommutativeMonoidLaws.check(Monoids.INT_MAX, SMALL_INTS);
	}

	@Test
	public void listConcatIsAMonoidOnly() {
		MonoidLaws.check(Monoids.<Long> list(),
				Arrays.asList(List.of(1L), List.of(2L, 3L), List.empty()));
	}

	@Test
	public void foldEarlyEqualsFoldLate() {
		FoldLaws.check(Monoids.LONG_SUM, Arrays.asList(1L, 2L), Arrays.asList(3L, 4L));
		FoldLaws.check(Monoids.LONG_MIN, Arrays.asList(5L, 2L), Arrays.asList(9L));
		FoldLaws.check(Monoids.LONG_MAX, Arrays.asList(5L, 2L), Arrays.asList(9L));
		FoldLaws.check(Monoids.<Long> list(),
				Arrays.asList(List.of(1L), List.of(2L)), Arrays.asList(List.of(3L)));
	}
}
