package com.tgac.functional.fibers.interpreter;

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
public final class AwaitBoundary<E> {

	private final ConcurrentLinkedQueue<E> injections = new ConcurrentLinkedQueue<>();
	private final Map<E, Object> outstanding = Collections.synchronizedMap(new LinkedHashMap<E, Object>());

	/**
	 * The resume handle for {@code entry}: records the resume, hands the
	 * frame its result, and injects the entry.
	 */
	public ResumeHandle resumeHandle(E entry, FiberStep.Frame frame, Scope owner, boolean billedThrough) {
		return new ResumeHandle(frame, owner, () -> {
			outstanding.remove(entry);
			injections.add(entry);
		}, billedThrough);
	}

	/** The entry is about to be offered to {@code at}. */
	public void held(E entry, Object at) {
		outstanding.put(entry, at);
	}

	/** Move every injected entry back into the scheduler's run queue. */
	public void drainInto(Consumer<E> requeue) {
		E entry;
		while ((entry = injections.poll()) != null) {
			requeue.accept(entry);
		}
	}

	/** No injection is pending — safe to call the run queue empty. */
	public boolean quiet() {
		return injections.isEmpty();
	}

	/**
	 * The exhaustion check (docs/design/completion.md §6): a scheduler out of
	 * work may hold no live blocked frame — every source's seal completes its
	 * waiters, so a stranded one names a scope that never received work.
	 */
	public void refuseStranded() {
		if (outstanding.isEmpty()) {
			return;
		}
		throw new IllegalStateException("scheduler exhausted with " + outstanding.size()
				+ " frame(s) blocked at unsealed sources: " + outstanding.values());
	}
}
