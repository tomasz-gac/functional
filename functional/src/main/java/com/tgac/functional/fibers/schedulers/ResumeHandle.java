package com.tgac.functional.fibers.schedulers;

// ABOUTME: The runtime's Await.Waiter and the handoff's referee: heldAt places the
// ABOUTME: records iff no completion won; complete resumes the frame and re-queues.

import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import java.util.function.Consumer;

/**
 * The resume handle bound to one suspended frame — the one object both the
 * suspending thread and the completing thread hold, so its monitor referees
 * the handoff. {@link #heldAt} runs on the suspending thread after the
 * source confirmed the hold: unless a completion already won, it places the
 * blocked record and the scheduler's registration in one guarded step.
 * {@link #complete} records the resume — {@code owner.resumed(frame)} when
 * the record was placed, a bare {@code owner.started()} when the completion
 * outran {@code heldAt} (the new quantum is real either way; the original
 * pair is closed by the suspending thread's own {@code finished()}) — hands
 * the frame its result, and re-queues it. Exactly-once holds structurally:
 * the source removes a held waiter under its monitor before completing it.
 */
final class ResumeHandle implements Await.Waiter<Object> {

	private final FiberStep.Frame frame;
	private final Scope owner;
	private final Consumer<Boolean> requeue;
	private boolean completed;
	private boolean recorded;

	ResumeHandle(FiberStep.Frame frame, Scope owner, Consumer<Boolean> requeue) {
		this.frame = frame;
		this.owner = owner;
		this.requeue = requeue;
	}

	/**
	 * The suspension stuck: place the blocked record and run the scheduler's
	 * registration — unless a completion already won, in which case nothing
	 * is placed and there is nothing to undo.
	 */
	/**
	 * @return whether the records were placed - false means a completion
	 * 		outran the hold: the frame's original quantum CONTINUES into the
	 * 		resumed run, so the caller must not close its started/finished
	 * 		pair (closing it would open a window in which the frame is
	 * 		recorded nowhere and a racing seal reads quiescence)
	 */
	synchronized boolean heldAt(Scope place, Runnable register) {
		if (completed) {
			return false;
		}
		recorded = true;
		if (owner != null) {
			owner.blocked(frame, place);
		}
		register.run();
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void complete(Await.Result<Object> result) {
		boolean wasRecorded;
		synchronized (this) {
			completed = true;
			wasRecorded = recorded;
		}
		if (owner != null && wasRecorded) {
			// the frame resumed from a placed record; when the completion
			// outran heldAt there is nothing to bill - the original quantum
			// continues into this run and its final Done closes the pair
			owner.resumed(frame);
		}
		frame.scope = owner;
		frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(result);
		// the requeue learns whether heldAt placed the registration - its
		// releases must mirror exactly what was placed, nothing more
		requeue.accept(wasRecorded);
	}
}
