package com.tgac.functional.fibers;

// ABOUTME: What the interpreter records frames' work in: one started/finished pair
// ABOUTME: per frame, finished returning the seal-attempt fiber to run after it.

import com.tgac.functional.category.Nothing;

/**
 * The face of a scope the fiber interpreter sees. Every frame constructed
 * with a scope calls {@link #started} at construction and, on completion,
 * runs the fiber {@link #finished} returns as its own continuation — the
 * seal attempt, and any work the seal spawns, stepped by the same
 * scheduler. The interpreter owns the pairing, so exactly-once holds by
 * construction; consumer code never calls these methods.
 */
public interface WorkScope {

	void started();

	/** Record the matching finish and return the seal-attempt work to run next. */
	Fiber<Nothing> finished();

	/**
	 * Record that a piece of this scope's work is blocked, wakeable only by
	 * {@code at}. The interpreter writes the record BEFORE the
	 * started/finished pair closes, so a racing seal never sees drained
	 * counters with no blocked record.
	 */
	default void blocked(Object waiter, WorkScope at) {
	}

	/** The blocked piece is no longer an obstruction — resumed or proven dead. */
	default void unblocked(Object waiter) {
	}

	/**
	 * Whether this scope's work is provably finished — an upward-closed read
	 * (a stale false only defers a seal, never unsounds one).
	 */
	default boolean isSealed() {
		return false;
	}
}
