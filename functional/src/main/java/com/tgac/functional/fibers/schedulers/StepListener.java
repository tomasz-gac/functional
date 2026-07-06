package com.tgac.functional.fibers.schedulers;

// ABOUTME: Observes fiber reductions at the one place every scheduler shares — FiberStep.
// ABOUTME: Off by default (NO_OP); install with a scheduler's withListener for a uniform trace.

import com.tgac.functional.fibers.Fiber;

/**
 * A hook into the step interpreter. Because every scheduler drives fibers
 * through {@link FiberStep}, a listener installed on any of them sees the
 * same events in the same shape. All methods default to no-ops, so a
 * listener implements only what it cares about and the default
 * {@link #NO_OP} costs nothing.
 *
 * On a parallel scheduler the callbacks fire from multiple pool threads and
 * their events interleave; a listener that must order them across threads is
 * responsible for its own synchronization.
 */
public interface StepListener {

	StepListener NO_OP = new StepListener() {};

	/** Fired for every reduction, before the node is dispatched. */
	default void onStep(Fiber<?> node) {
	}

	/** A frame reduced to its final value. */
	default void onCompleted(Object value) {
	}

	/** A frame forked into alternatives (always at least one). */
	default void onForked(Fiber.Forked<?> fork) {
	}

	/** A frame spawned an independent child. */
	default void onDetached(Fiber<?> child) {
	}
}
