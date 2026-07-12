package com.tgac.functional.algebra;

// ABOUTME: The aggregate folds as monoid witnesses: sum, min, max (commutative)
// ABOUTME: and list concatenation (associative only — answer order matters).

import io.vavr.collection.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Monoids {

	public static final CommutativeMonoid<Long> LONG_SUM = new CommutativeMonoid<Long>() {
		@Override
		public Long empty() {
			return 0L;
		}

		@Override
		public Long combine(Long a, Long b) {
			return a + b;
		}
	};

	public static final CommutativeMonoid<Long> LONG_MIN = new CommutativeMonoid<Long>() {
		@Override
		public Long empty() {
			return Long.MAX_VALUE;
		}

		@Override
		public Long combine(Long a, Long b) {
			return Math.min(a, b);
		}
	};

	public static final CommutativeMonoid<Long> LONG_MAX = new CommutativeMonoid<Long>() {
		@Override
		public Long empty() {
			return Long.MIN_VALUE;
		}

		@Override
		public Long combine(Long a, Long b) {
			return Math.max(a, b);
		}
	};

	public static <T> Monoid<List<T>> list() {
		return new Monoid<List<T>>() {
			@Override
			public List<T> empty() {
				return List.empty();
			}

			@Override
			public List<T> combine(List<T> a, List<T> b) {
				return a.appendAll(b);
			}
		};
	}
}
