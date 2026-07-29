// ABOUTME: The drivers: queue disciplines over the fiber interpreter - breadth-first,
// ABOUTME: depth-first, round-robin, unfair breadth-first and fork-join.

/**
 * Drivers over {@link com.tgac.functional.fibers.interpreter.FiberStep}: each owns
 * a queue and a granularity policy, nothing else. They differ only in which frame
 * they step next — fairness is the whole difference.
 */
package com.tgac.functional.fibers.schedulers;
