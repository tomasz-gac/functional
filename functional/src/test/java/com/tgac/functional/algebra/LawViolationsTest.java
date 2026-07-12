package com.tgac.functional.algebra;

// ABOUTME: Proves every law kit can FAIL: deliberately unlawful instances must
// ABOUTME: throw, with the violated law named — the kits' own honesty gate.

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.algebra.laws.BottomedLaws;
import com.tgac.functional.algebra.laws.CommutativeMonoidLaws;
import com.tgac.functional.algebra.laws.LatticeLaws;
import com.tgac.functional.algebra.laws.Lattices;
import com.tgac.functional.algebra.laws.MonoidLaws;
import com.tgac.functional.algebra.laws.SemiringLaws;
import com.tgac.functional.algebra.laws.Semirings;
import com.tgac.functional.algebra.laws.StarLaws;
import com.tgac.functional.algebra.laws.SuperiorityLaws;
import java.util.Arrays;
import java.util.List;
import lombok.Value;
import org.junit.jupiter.api.Test;

public class LawViolationsTest {

	private static final List<Long> SAMPLES = Arrays.asList(0L, 1L, 2L, 3L);

	@Test
	public void monoidLawsRejectSubtraction() {
		Monoid<Long> subtraction = new Monoid<Long>() {
			@Override
			public Long empty() {
				return 0L;
			}

			@Override
			public Long combine(Long a, Long b) {
				return a - b;
			}
		};
		assertThatThrownBy(() -> MonoidLaws.check(subtraction, SAMPLES))
				.isInstanceOf(AssertionError.class)
				.hasMessageContaining("associativity");
	}

	@Test
	public void commutativeLawsRejectStringConcat() {
		Monoid<String> concat = new Monoid<String>() {
			@Override
			public String empty() {
				return "";
			}

			@Override
			public String combine(String a, String b) {
				return a + b;
			}
		};
		assertThatThrownBy(() -> CommutativeMonoidLaws.check(concat, Arrays.asList("a", "b")))
				.isInstanceOf(AssertionError.class)
				.hasMessageContaining("commutativity");
	}

	@Test
	public void semiringLawsRejectBrokenAnnihilation() {
		Semiring<Long> broken = new Semiring<Long>() {
			@Override
			public Long zero() {
				return 0L;
			}

			@Override
			public Long one() {
				return 0L;
			}

			@Override
			public Long plus(Long a, Long b) {
				return a + b;
			}

			@Override
			public Long times(Long a, Long b) {
				return a + b;
			}
		};
		assertThatThrownBy(() -> SemiringLaws.check(broken, SAMPLES))
				.isInstanceOf(AssertionError.class);
	}

	@Test
	public void semiringLawsRejectAClaimedIdempotenceLie() {
		Semiring<Long> liar = new Semiring<Long>() {
			@Override
			public Long zero() {
				return Semirings.COUNTING.zero();
			}

			@Override
			public Long one() {
				return Semirings.COUNTING.one();
			}

			@Override
			public Long plus(Long a, Long b) {
				return Semirings.COUNTING.plus(a, b);
			}

			@Override
			public Long times(Long a, Long b) {
				return Semirings.COUNTING.times(a, b);
			}

			@Override
			public boolean isIdempotentPlus() {
				return true;
			}
		};
		assertThatThrownBy(() -> SemiringLaws.check(liar, SAMPLES))
				.isInstanceOf(AssertionError.class)
				.hasMessageContaining("idempotence");
	}

	@Test
	public void superiorityLawsRejectNonSelectivePlus() {
		Semiring<Long> counting = new Semiring<Long>() {
			@Override
			public Long zero() {
				return 0L;
			}

			@Override
			public Long one() {
				return 1L;
			}

			@Override
			public Long plus(Long a, Long b) {
				return a + b;
			}

			@Override
			public Long times(Long a, Long b) {
				return a * b;
			}

			@Override
			public boolean isSuperior() {
				return true;
			}
		};
		assertThatThrownBy(() -> SuperiorityLaws.check(counting, Arrays.asList(1L, 2L)))
				.isInstanceOf(AssertionError.class)
				.hasMessageContaining("selective");
	}

	@Test
	public void starLawsRejectABrokenStar() {
		Semiring<Boolean> brokenStar = new Semiring<Boolean>() {
			@Override
			public Boolean zero() {
				return Boolean.FALSE;
			}

			@Override
			public Boolean one() {
				return Boolean.TRUE;
			}

			@Override
			public Boolean plus(Boolean a, Boolean b) {
				return a || b;
			}

			@Override
			public Boolean times(Boolean a, Boolean b) {
				return a && b;
			}

			@Override
			public boolean isClosed() {
				return true;
			}

			@Override
			public Boolean star(Boolean a) {
				return Boolean.FALSE;
			}
		};
		assertThatThrownBy(() -> StarLaws.check(brokenStar, Arrays.asList(true, false)))
				.isInstanceOf(AssertionError.class)
				.hasMessageContaining("star");
	}

	/** join returns the left argument — not an upper bound. */
	@Value
	private static class LeftBiased implements Lattice<LeftBiased>, Bottomed {
		int lo;
		int hi;

		@Override
		public LeftBiased meet(LeftBiased other) {
			int l = Math.max(lo, other.lo), h = Math.min(hi, other.hi);
			return new LeftBiased(Math.min(l, h + 1), h);
		}

		@Override
		public LeftBiased join(LeftBiased other) {
			return this;
		}

		@Override
		public boolean isBottom() {
			return lo > hi;
		}
	}

	@Test
	public void inflationaryLawsRejectAJoinThatIsNotAnUpperBound() {
		assertThatThrownBy(() -> LatticeLaws.checkInflationary(
				Arrays.asList(new LeftBiased(0, 3), new LeftBiased(5, 9))))
				.isInstanceOf(AssertionError.class)
				.hasMessageContaining("upper bound");
	}

	@Test
	public void bottomedLawsDemandABottomSample() {
		assertThatThrownBy(() -> BottomedLaws.check(
				Arrays.asList(Lattices.Mask.of(0b1L), Lattices.Mask.of(0b11L))))
				.isInstanceOf(AssertionError.class)
				.hasMessageContaining("must include a bottom");
	}
}
