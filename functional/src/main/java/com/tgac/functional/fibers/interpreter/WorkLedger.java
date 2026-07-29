package com.tgac.functional.fibers.interpreter;

// ABOUTME: A production's work ledger: the running half as two monotone counters,
// ABOUTME: the blocked half as who-blocks-where — quiescence is both halves empty.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Termination detection for one scope of work whose pieces are either
 * RUNNING (live fibers — counted by the Dijkstra–Scholten pair
 * {@code started}/{@code finished}, both monotone), BLOCKED (waiting at
 * some place {@code P}, wakeable), or dead (silence). {@link #quiescent}
 * is both halves reading empty: all fibers ended and every blocked piece
 * waits where the caller's predicate says it can never wake.
 *
 * <p>{@link #counted} is the ONE pairing discipline: started() runs
 * synchronously at wrap time (no gap for a racing quiescence check),
 * finished() when the work's fiber ends, followed by the caller's hook. A
 * leaked pair never completes (sound, useless); a doubled one completes
 * early (unsound) — every unit of work must pass through here exactly once.
 *
 * <p>Counters and blocked records guarded by this monitor; the quiescence
 * predicate may read foreign state lock-free when that state is
 * upward-closed (a stale false only defers).
 */
final class WorkLedger<S, P> {


	private long started;
	private long finished;

	/** Blocked pieces of this scope, mapped to the place each waits at. */
	private final Map<S, P> blocked = new HashMap<>();
	/** Who holds each open started/finished pair — diagnostic for refusals. */
	private final Map<Object, Integer> open = new HashMap<>();


	public synchronized void started() {
		started++;
	}

	public synchronized void started(Object holder) {
		started++;
		open.merge(holder, 1, Integer::sum);
	}

	public synchronized void finished() {
		finished++;
	}

	public synchronized void finished(Object holder) {
		finished++;
		open.merge(holder, -1, Integer::sum);
		if (Integer.valueOf(0).equals(open.get(holder))) {
			open.remove(holder);
		}
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

	/** The places this scope's blocked pieces wait at — a snapshot. */
	public synchronized List<P> blockedAt() {
		return new ArrayList<>(blocked.values());
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
	/** Diagnostic state for refusal messages — one monitor hold. */
	public synchronized String describe() {
		return "started=" + started + " finished=" + finished + " blockedAt=" + blocked.values()
				+ " open=" + open;
	}

	public synchronized Snapshot<P> drainedSnapshot() {
		if (started == 0 || finished != started) {
			return null;
		}
		return new Snapshot<>(started, new ArrayList<>(blocked.values()));
	}

	/** One member's atomically-read admission state for the group walk. */
	public static final class Snapshot<P> {
		public final long started;
		public final List<P> blockedAt;

		Snapshot(long started, List<P> blockedAt) {
			this.started = started;
			this.blockedAt = blockedAt;
		}
	}

	public synchronized boolean quiescent(Predicate<P> cannotWake) {
		if (started == 0 || finished != started) {
			return false;
		}
		for (P at : blocked.values()) {
			if (!cannotWake.test(at)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Count {@code work} as one unit of this ledger's running work. When the
	 * work's fiber ends, {@code onFinished} runs (the seal attempt) and the fiber
	 * it returns becomes this fiber's tail — so any work a seal spawns (the star
	 * emit) is stepped by the same scheduler.
	 */
	public Fiber<Nothing> counted(Fiber<Nothing> work, Supplier<Fiber<Nothing>> onFinished) {
		started();
		return work.flatMap(__ -> {
			finished();
			return onFinished.get();
		});
	}
}
