package com.tgac.functional.fibers.schedulers;

// ABOUTME: The await boundary shared by the queue drivers: held entries, the
// ABOUTME: injection queue resumed waiters re-enter through, the exhaustion refusal.

import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Source;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * One scheduler's await state: which queue entries are held by a {@link Source},
 * and the injection queue their resumes re-enter through — drained at the top
 * of every step, so an injected frame competes fairly like any other.
 *
 * <p>The queue exists for the WRONG-TIME case, not a wrong-thread one:
 * completions fire mid-step, from inside grow and the seal cascade, and
 * "a completion never touches a run structure" is one rule instead of a
 * re-entrancy proof per scheduler. Today every completion arrives on the
 * scheduler's own thread — growth and seals run in fiber steps, and
 * cross-scheduler sharing is refused. The concurrent types are deliberate
 * forward capacity: when externally-completed sources land, this queue is
 * the designated publication point — the enqueue is the happens-before
 * edge that publishes the completed frame's plain fields to the drive
 * thread.
 */
final class AwaitBoundary<E> {

	private final ConcurrentLinkedQueue<E> injections = new ConcurrentLinkedQueue<>();
	private final Map<E, Source<?>> outstanding = Collections.synchronizedMap(new LinkedHashMap<E, Source<?>>());

	/**
	 * The resume handle for {@code entry}: calls owner.started() BEFORE
	 * owner.unblocked(frame) — a racing seal never reads quiescence in the
	 * gap — then hands the frame its result and injects the entry.
	 */
	ResumeHandle resumeHandle(E entry, FiberStep.Frame frame, Scope owner) {
		return new ResumeHandle(frame, owner, recorded -> {
			outstanding.remove(entry);
			injections.add(entry);
		});
	}

	/** The entry is about to be offered to {@code at}. */
	void held(E entry, Source<?> at) {
		outstanding.put(entry, at);
	}

	/** Move every injected entry back into the scheduler's run queue. */
	void drainInto(Consumer<E> requeue) {
		E entry;
		while ((entry = injections.poll()) != null) {
			requeue.accept(entry);
		}
	}

	/** No injection is pending — safe to call the run queue empty. */
	boolean quiet() {
		return injections.isEmpty();
	}

	/**
	 * The exhaustion check (docs/design/completion.md §6): a scheduler out of
	 * work may hold no live blocked frame — every source's seal completes its
	 * waiters, so a stranded one names a scope that never received work.
	 */
	void refuseStranded() {
		if (outstanding.isEmpty()) {
			return;
		}
		throw new IllegalStateException("scheduler exhausted with " + outstanding.size()
				+ " frame(s) blocked at unsealed sources: " + outstanding.values());
	}
}
