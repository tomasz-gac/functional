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
}
