package com.tgac.functional.fibers.schedulers;

// ABOUTME: The runtime's Await.Waiter and the handoff's referee: heldAt places the
// ABOUTME: records iff no completion won; complete resumes the frame and re-queues.

import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;

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
	private final Runnable requeue;
	private boolean completed;
	private boolean recorded;

	ResumeHandle(FiberStep.Frame frame, Scope owner, Runnable requeue) {
		this.frame = frame;
		this.owner = owner;
		this.requeue = requeue;
	}

	/**
	 * The suspension stuck: place the blocked record and run the scheduler's
	 * registration — unless a completion already won, in which case nothing
	 * is placed and there is nothing to undo.
	 */
	synchronized void heldAt(Scope place, Runnable register) {
		if (completed) {
			return;
		}
		recorded = true;
		if (owner != null) {
			owner.blocked(frame, place);
		}
		register.run();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void complete(Await.Result<Object> result) {
		boolean wasRecorded;
		synchronized (this) {
			completed = true;
			wasRecorded = recorded;
		}
		if (owner != null) {
			if (wasRecorded) {
				owner.resumed(frame);
			} else {
				owner.started();
			}
		}
		frame.scope = owner;
		frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(result);
		requeue.run();
	}
}
