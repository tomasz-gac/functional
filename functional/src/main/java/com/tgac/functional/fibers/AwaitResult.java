package com.tgac.functional.fibers;

// ABOUTME: The await completion: the value with its finality - more(value) on
// ABOUTME: growth, sealed(value) as the final EOF.

import lombok.Value;

/**
 * The two completions of a {@link Fiber#await}: {@code more(value)} — the
 * channel grew past the waiter — and {@code sealed(value)} — the channel's
 * scope sealed, provably no further growth, and {@code value} is FINAL (a
 * waiter never re-reads the channel after a sealed completion).
 */
@Value
public class AwaitResult<V> {
	V value;
	boolean sealed;

	public static <V> AwaitResult<V> more(V value) {
		return new AwaitResult<>(value, false);
	}

	public static <V> AwaitResult<V> sealed(V value) {
		return new AwaitResult<>(value, true);
	}
}
