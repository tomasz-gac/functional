package com.tgac.functional.fibers;

// ABOUTME: What the interpreter bills frames to: one started/finished pair per unit
// ABOUTME: of work, finished returning the seal-attempt fiber to run as the tail.

import com.tgac.functional.category.Nothing;

/**
 * The billing face of a scope, as the fiber interpreter sees it. Every frame
 * born with a scope ticks {@link #started} at birth and, on completion, runs
 * the fiber {@link #finished} returns as its own tail — the seal attempt, and
 * any work the seal spawns, stepped by the same driver. The interpreter owns
 * the pairing, so exactly-once billing holds by construction; consumer code
 * never touches these doors.
 */
public interface WorkScope {

	void started();

	/** Tick the matching finish and return the seal-attempt work to run as the tail. */
	Fiber<Nothing> finished();

	/**
	 * Record that a piece of this scope's work is blocked, wakeable only by
	 * {@code at}. The interpreter places the record BEFORE the running pair
	 * closes, so a racing seal never sees drained counters with no sleeper.
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
