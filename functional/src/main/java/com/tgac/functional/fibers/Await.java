package com.tgac.functional.fibers;

// ABOUTME: The await completion vocabulary: Result carries the value with its
// ABOUTME: finality, Waiter is the drive-owned resume handle a Source completes.

import lombok.Value;

/**
 * The two completions of a {@link Fiber#await}: {@code more(value)} — the
 * source grew past the waiter — and {@code sealed(value)} — the source's
 * account sealed, provably no further growth, and {@code value} is FINAL (a
 * waiter never re-reads the source after a sealed completion).
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

	/**
	 * The resume handle bound to one suspended frame. Completing it re-bills
	 * the frame's account, hands the frame its {@link Result} and re-queues it
	 * — billed-before-unblocked is internal, no caller can misorder it.
	 * Exactly once per held waiter: a leak strands the frame (loud at the
	 * drive's endgame), a double completion is unsound.
	 */
	public interface Waiter<V> {
		void complete(Result<V> result);
	}
}
