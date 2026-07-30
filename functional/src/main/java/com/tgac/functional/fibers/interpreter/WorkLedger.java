package com.tgac.functional.fibers.interpreter;

// ABOUTME: A production's work ledger: the running half as two monotone counters,
// ABOUTME: the blocked half as who-blocks-where — quiescence is both halves empty.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Termination detection for one scope of work whose pieces are either
 * RUNNING (live fibers — counted by the Dijkstra–Scholten pair
 * {@code started}/{@code finished}, both monotone), BLOCKED (waiting at
 * some place {@code P}, wakeable), or dead (silence). The quiescence
 * judgment lives in the group walk ({@code Scope}): {@link #drainedSnapshot}
 * hands it one member's atomically-read state.
 *
 * <p>The pairing discipline: a frame's constructor bills started()
 * synchronously (no gap for a racing quiescence check), its finished() runs
 * as its own final continuation. A leaked pair never completes (sound,
 * useless); a doubled one completes early (unsound) — every unit of work
 * passes through exactly once.
 *
 * <p>Counters and blocked records guarded by this monitor.
 */
final class WorkLedger<S, P> {

	private long started;
	private long finished;

	/** Blocked pieces of this scope, mapped to the place each waits at. */
	private final Map<S, P> blocked = new HashMap<>();

	public synchronized void started() {
		started++;
	}

	public synchronized void finished() {
		finished++;
	}

	public synchronized void blocked(S sleeper, P at) {
		blocked.put(sleeper, at);
	}

	public synchronized void unblocked(S sleeper) {
		blocked.remove(sleeper);
	}

	/** Monotone — two equal reads bracket a spawn-free interval. */
	public synchronized long startedCount() {
		return started;
	}

	/** Counters drained: the scope has run and all its fibers ended. */
	public synchronized boolean drained() {
		return started > 0 && finished == started;
	}

	/**
	 * The group walk's admission read: drained-ness, the started counter, and the
	 * blocked places, in ONE monitor hold. Atomicity is load-bearing: read
	 * separately, a respawn can interleave — drained sees the old quiet state,
	 * the snapshot records the counter AFTER the spawn (so the two-phase
	 * re-verify compares the new value against itself and misses it), and the
	 * blocked read sees the post-unblock empty set, hiding the edge that would
	 * have aborted the walk. One hold makes the spawn land wholly before
	 * (not drained) or wholly after (counter mismatch at re-verify).
	 *
	 * @return the snapshot, or null when the counters are not drained
	 */
	public synchronized Snapshot<P> drainedSnapshot() {
		if (started == 0 || finished != started) {
			return null;
		}
		return new Snapshot<>(started, new ArrayList<>(blocked.values()));
	}

	/** One member's atomically-read admission state for the group walk. */
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
	public static final class Snapshot<P> {
		long started;
		List<P> blockedAt;
	}

}
