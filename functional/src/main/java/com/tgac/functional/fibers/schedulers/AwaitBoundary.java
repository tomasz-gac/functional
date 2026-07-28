package com.tgac.functional.fibers.schedulers;

// ABOUTME: The await boundary shared by the queue drivers: held entries, the
// ABOUTME: injection queue resumed waiters re-enter through, the exhaustion refusal.

import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Source;
import com.tgac.functional.fibers.WorkScope;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * One scheduler's await state: which queue entries are held by a {@link Source},
 * and the injection queue their resumes re-enter through — drained at the top
 * of every step, so an injected frame competes fairly like any other. Handles
 * are thread-safe: a source may complete them from any thread.
 */
final class AwaitBoundary<E> {

	private final ConcurrentLinkedQueue<E> injections = new ConcurrentLinkedQueue<>();
	private final Map<E, Source<?>> outstanding = Collections.synchronizedMap(new LinkedHashMap<E, Source<?>>());

	/**
	 * The resume handle for {@code entry}: calls owner.started() BEFORE
	 * owner.unblocked(frame) — a racing seal never reads quiescence in the
	 * gap — then hands the frame its result and injects the entry.
	 */
	@SuppressWarnings("unchecked")
	Await.Waiter<Object> resumeHandle(E entry, FiberStep.Frame frame, WorkScope owner) {
		return result -> {
			if (owner != null) {
				owner.started();
				owner.unblocked(frame);
			}
			frame.scope = owner;
			frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(result);
			outstanding.remove(entry);
			injections.add(entry);
		};
	}

	/** The entry is about to be offered to {@code at}. */
	void held(E entry, Source<?> at) {
		outstanding.put(entry, at);
	}

	/** The suspend was answered immediately — the entry was never held. */
	void cancelled(E entry) {
		outstanding.remove(entry);
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
