package com.tgac.functional.fibers.interpreter;

// ABOUTME: An immutable photograph of a scheduler's live search — the frames currently alive.
// ABOUTME: The static counterpart to StepListener's per-step film: breadth, depth, node shape.

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import lombok.Value;

/**
 * A snapshot of the frames alive in a scheduler at one instant: how many
 * branches, at what depths, and what fiber node each is reduced to. Because
 * frames reference immutable packages, taking one is a shallow read.
 *
 * Meant to be read between steps on the same thread (drive the scheduler with
 * {@code run(iterations, sink)} and snapshot in between), not concurrently
 * with a running {@code run}.
 */
@Value
public class SearchSnapshot {

	/** Total live branches. */
	int frameCount;
	/** Live frame count per search depth (all at depth 0 for depth-agnostic schedulers). */
	Map<Integer, Integer> framesByDepth;
	/** Live frame count per current fiber node kind (Deferred, FlatMap, Done, Forked, Detached). */
	Map<String, Integer> nodeTypes;

	SearchSnapshot(int frameCount, Map<Integer, Integer> framesByDepth, Map<String, Integer> nodeTypes) {
		this.frameCount = frameCount;
		this.framesByDepth = Collections.unmodifiableMap(framesByDepth);
		this.nodeTypes = Collections.unmodifiableMap(nodeTypes);
	}

	@Override
	public String toString() {
		return "live frames: " + frameCount
				+ "\n  by depth: " + framesByDepth
				+ "\n  by type:  " + nodeTypes;
	}

	/** Accumulates frames as a scheduler walks its live queue. */
	public static final class Builder {
		private int count = 0;
		private final Map<Integer, Integer> byDepth = new TreeMap<>();
		private final Map<String, Integer> byType = new TreeMap<>();

		public void add(int depth, Frame frame) {
			count++;
			byDepth.merge(depth, 1, Integer::sum);
			byType.merge(frame.computation.getClass().getSimpleName(), 1, Integer::sum);
		}

		public SearchSnapshot build() {
			return new SearchSnapshot(count, byDepth, byType);
		}
	}
}
