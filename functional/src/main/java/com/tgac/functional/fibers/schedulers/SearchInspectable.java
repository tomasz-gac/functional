package com.tgac.functional.fibers.schedulers;

// ABOUTME: Capability of schedulers that hold a walkable run queue: snapshot the live search.
// ABOUTME: Not on Scheduler — a parallel scheduler has no central queue to inspect.

/**
 * A scheduler whose live search can be photographed. The sequential
 * schedulers keep their pending frames in an inspectable queue and implement
 * this; a parallel scheduler spreads work across pool-worker deques and does
 * not. Test capability with {@code instanceof SearchInspectable}.
 */
public interface SearchInspectable {

	/** A snapshot of the frames currently alive. */
	SearchSnapshot snapshot();
}
