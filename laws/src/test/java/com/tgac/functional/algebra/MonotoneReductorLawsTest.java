package com.tgac.functional.algebra;

// ABOUTME: Pins the operator kits: monotone maps along a declared chain and
// ABOUTME: reductors (deflationary + monotone) on a lattice, with violation pins.

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.algebra.laws.MonotoneLaws;
import com.tgac.functional.algebra.laws.ReductorLaws;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;

public class MonotoneReductorLawsTest {

	// masks ascending by subset
	private static final List<Lattices.Mask> CHAIN = Arrays.asList(
			Lattices.Mask.of(0b0001L),
			Lattices.Mask.of(0b0011L),
			Lattices.Mask.of(0b0111L));

	@Test
	public void pricerShapedMapIsMonotoneIntoDescendingCounts() {
		// more bits known -> fewer-or-equal "answers"
		MonotoneLaws.check(CHAIN,
				m -> 8 - Long.bitCount(m.getBits()),
				(lo, hi) -> lo >= hi);
	}

	@Test
	public void risingPriceIsRejected() {
		assertThatThrownBy(() -> MonotoneLaws.check(CHAIN,
				m -> Long.bitCount(m.getBits()),
				(lo, hi) -> (int) lo >= (int) hi))
				.isInstanceOf(AssertionError.class)
				.hasMessageContaining("monotone");
	}

	@Test
	public void intersectionWithAConstantIsAReductor() {
		UnaryOperator<Lattices.Mask> f = m -> m.meet(Lattices.Mask.of(0b0101L));
		ReductorLaws.check(f, Arrays.asList(
				Lattices.Mask.of(0b0001L),
				Lattices.Mask.of(0b0111L),
				Lattices.Mask.of(0b1111L)));
	}

	@Test
	public void anExpandingStepIsRejected() {
		UnaryOperator<Lattices.Mask> expand = m -> m.join(Lattices.Mask.of(0b1000L));
		assertThatThrownBy(() -> ReductorLaws.check(expand,
				Arrays.asList(Lattices.Mask.of(0b0001L))))
				.isInstanceOf(AssertionError.class)
				.hasMessageContaining("deflationary");
	}
}
