package com.tgac.functional.fibers.schedulers;

// ABOUTME: An immutable photograph of a scheduler's live search — the frames currently alive.
// ABOUTME: The static counterpart to StepListener's per-step film: breadth, depth, node shape.

import com.tgac.functional.fibers.Fiber;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * A snapshot of the frames alive in a scheduler at one instant: how many
 * branches, at what depths, and what fiber node each is reduced to. Because
 * frames reference immutable packages, taking one is a shallow read.
 *
 * Meant to be read between steps on the same thread (drive the scheduler with
 * {@code run(iterations, sink)} and snapshot in between), not concurrently
 * with a running {@code run}.
 */
public final class SearchSnapshot {

	private final int frameCount;
	private final Map<Integer, Integer> framesByDepth;
	private final Map<String, Integer> nodeTypes;

	SearchSnapshot(int frameCount, Map<Integer, Integer> framesByDepth, Map<String, Integer> nodeTypes) {
		this.frameCount = frameCount;
		this.framesByDepth = Collections.unmodifiableMap(framesByDepth);
		this.nodeTypes = Collections.unmodifiableMap(nodeTypes);
	}

	/** Total live branches. */
	public int getFrameCount() {
		return frameCount;
	}

	/** Live frame count per search depth (all at depth 0 for depth-agnostic schedulers). */
	public Map<Integer, Integer> getFramesByDepth() {
		return framesByDepth;
	}

	/** Live frame count per current fiber node kind (Deferred, FlatMap, Done, Forked, Detached). */
	public Map<String, Integer> getNodeTypes() {
		return nodeTypes;
	}

	@Override
	public String toString() {
		return "live frames: " + frameCount
				+ "\n  by depth: " + framesByDepth
				+ "\n  by type:  " + nodeTypes;
	}

	/** Accumulates frames as a scheduler walks its live queue. */
	static final class Builder {
		private int count = 0;
		private final Map<Integer, Integer> byDepth = new TreeMap<>();
		private final Map<String, Integer> byType = new TreeMap<>();

		void add(int depth, Fiber<?> node) {
			count++;
			byDepth.merge(depth, 1, Integer::sum);
			byType.merge(node.getClass().getSimpleName(), 1, Integer::sum);
		}

		SearchSnapshot build() {
			return new SearchSnapshot(count, byDepth, byType);
		}
	}
}
