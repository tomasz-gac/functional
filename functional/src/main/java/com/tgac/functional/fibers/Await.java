package com.tgac.functional.fibers;

// ABOUTME: The await completion vocabulary: Result carries the value with its
// ABOUTME: finality - more(value) on growth, sealed(value) as the final EOF.

import lombok.Value;

/**
 * The two completions of a {@link Fiber#await}: {@code more(value)} — the
 * channel grew past the waiter — and {@code sealed(value)} — the channel's
 * scope sealed, provably no further growth, and {@code value} is FINAL (a
 * waiter never re-reads the channel after a sealed completion).
 */
public final class Await {

	private Await() {
	}

	@Value
	public static class Result<V> {
		V value;
		boolean sealed;

		public static <V> Result<V> more(V value) {
			return new Result<>(value, false);
		}

		public static <V> Result<V> sealed(V value) {
			return new Result<>(value, true);
		}
	}
}
