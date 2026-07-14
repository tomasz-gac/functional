package com.tgac.functional.algebra;

// ABOUTME: Pins the Eq seam: law kits check equalities through a supplied
// ABOUTME: quotient, so witnesses whose elements lack canonical equals qualify.

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.algebra.laws.IdempotentSemiringLaws;
import com.tgac.functional.algebra.laws.SemilatticeLaws;
import com.tgac.functional.algebra.laws.SemiringLaws;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import org.junit.jupiter.api.Test;

public class EqParameterizedLawsTest {

	// a join-semilattice (join = max) with NO equals override, so its laws
	// hold only up to the value quotient — the substitution-lattice case
	static final class Boxed implements JoinSemilattice<Boxed> {
		final long v;

		Boxed(long v) {
			this.v = v;
		}

		@Override
		public Boxed join(Boxed other) {
			return new Boxed(Math.max(v, other.v));
		}
	}

	private static final BiPredicate<Boxed, Boxed> BY_VALUE = (a, b) -> a.v == b.v;

	private static final List<Boxed> BOXED_JOINS = Arrays.asList(
			new Boxed(0), new Boxed(1), new Boxed(2), new Boxed(3));

	@Test
	public void joinLawsHoldThroughTheSuppliedQuotient() {
		SemilatticeLaws.checkJoin(BOXED_JOINS, BY_VALUE);
	}

	@Test
	public void joinEqualsEntryPointCannotExpressTheQuotient() {
		assertThatThrownBy(() -> SemilatticeLaws.checkJoin(BOXED_JOINS))
				.isInstanceOf(AssertionError.class);
	}

	// counting over length-1 arrays: equals is reference identity, so the
	// laws hold only up to the contents quotient
	private static final Semiring<long[]> BOXED_COUNTING = new Semiring<long[]>() {
		@Override
		public long[] zero() {
			return new long[] {0};
		}

		@Override
		public long[] one() {
			return new long[] {1};
		}

		@Override
		public long[] plus(long[] a, long[] b) {
			return new long[] {a[0] + b[0]};
		}

		@Override
		public long[] times(long[] a, long[] b) {
			return new long[] {a[0] * b[0]};
		}
	};

	private static final IdempotentSemiring<long[]> BOXED_MAX_TIMES = new IdempotentSemiring<long[]>() {
		@Override
		public long[] zero() {
			return new long[] {0};
		}

		@Override
		public long[] one() {
			return new long[] {1};
		}

		@Override
		public long[] plus(long[] a, long[] b) {
			return new long[] {Math.max(a[0], b[0])};
		}

		@Override
		public long[] times(long[] a, long[] b) {
			return new long[] {a[0] * b[0]};
		}
	};

	private static final BiPredicate<long[], long[]> CONTENTS = (a, b) -> a[0] == b[0];

	private static final List<long[]> SAMPLES = Arrays.asList(
			new long[] {0}, new long[] {1}, new long[] {2}, new long[] {3});

	@Test
	public void lawsHoldThroughTheSuppliedQuotient() {
		SemiringLaws.check(BOXED_COUNTING, SAMPLES, CONTENTS);
		IdempotentSemiringLaws.check(BOXED_MAX_TIMES, SAMPLES, CONTENTS);
	}

	@Test
	public void equalsBasedEntryPointCannotExpressTheQuotient() {
		assertThatThrownBy(() -> SemiringLaws.check(BOXED_COUNTING, SAMPLES))
				.isInstanceOf(AssertionError.class);
	}
}
