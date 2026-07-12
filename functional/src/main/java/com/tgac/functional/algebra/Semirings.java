package com.tgac.functional.algebra;

// ABOUTME: The standard plugs: counting, boolean, tropical (min,+) and the
// ABOUTME: saturating price arithmetic — each a lawful Semiring witness.

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Semirings {

	/** How many ways: (+, ×) over Long. Not idempotent — cyclic programs diverge. */
	public static final Semiring<Long> COUNTING = new Semiring<Long>() {
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
	};

	/** Exists at all: (∨, ∧). Idempotent, closed (a* = true). */
	public static final Semiring<Boolean> BOOLEAN = new Semiring<Boolean>() {
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
		public boolean isIdempotentPlus() {
			return true;
		}

		@Override
		public boolean isClosed() {
			return true;
		}

		@Override
		public Boolean star(Boolean a) {
			return Boolean.TRUE;
		}

		@Override
		public boolean isSuperior() {
			return true;
		}
	};

	/**
	 * Cheapest: (min, +) over nonnegative costs; 0 is +∞ ("cheapest of no
	 * routes"), 1 is cost 0. Idempotent, superior (Dijkstra-legal), closed for
	 * nonnegatives (a* = 0-cost).
	 */
	public static final Semiring<Long> MIN_PLUS = new Semiring<Long>() {
		@Override
		public Long zero() {
			return Long.MAX_VALUE;
		}

		@Override
		public Long one() {
			return 0L;
		}

		@Override
		public Long plus(Long a, Long b) {
			return Math.min(a, b);
		}

		@Override
		public Long times(Long a, Long b) {
			return a == Long.MAX_VALUE || b == Long.MAX_VALUE ? Long.MAX_VALUE : a + b;
		}

		@Override
		public boolean isIdempotentPlus() {
			return true;
		}

		@Override
		public boolean isClosed() {
			return true;
		}

		@Override
		public Long star(Long a) {
			return 0L;
		}

		@Override
		public boolean isSuperior() {
			return true;
		}
	};

	/**
	 * The optimizer's price arithmetic as the semiring it always was:
	 * counting over ℕ∪{∞} with saturation at Long.MAX_VALUE, 0·∞ = 0.
	 */
	public static final Semiring<Long> SATURATING = new Semiring<Long>() {
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
			if (a == Long.MAX_VALUE || b == Long.MAX_VALUE) {
				return Long.MAX_VALUE;
			}
			long s = a + b;
			return s < 0 ? Long.MAX_VALUE : s;
		}

		@Override
		public Long times(Long a, Long b) {
			if (a == 0 || b == 0) {
				return 0L;
			}
			if (a == Long.MAX_VALUE || b == Long.MAX_VALUE) {
				return Long.MAX_VALUE;
			}
			return a > Long.MAX_VALUE / b ? Long.MAX_VALUE : a * b;
		}
	};
}
