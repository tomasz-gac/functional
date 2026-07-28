package com.tgac.functional.fibers.schedulers;

// ABOUTME: The runtime's Await.Waiter: complete() records the frame running in its
// ABOUTME: owner's ledger, hands the frame its result, and re-queues it.

import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;

/**
 * The resume handle bound to one suspended frame. {@link #complete} records
 * the frame running — {@code owner.started()} then
 * {@code owner.unblocked(frame)} — hands the frame its result, and re-queues
 * it through the scheduler-supplied {@code requeue}. Exactly-once holds
 * structurally: the cell removes a held waiter under its monitor before
 * completing it, so no handle is ever completed twice.
 */
final class ResumeHandle implements Await.Waiter<Object> {

	private final FiberStep.Frame frame;
	private final Scope owner;
	private final Runnable requeue;

	ResumeHandle(FiberStep.Frame frame, Scope owner, Runnable requeue) {
		this.frame = frame;
		this.owner = owner;
		this.requeue = requeue;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void complete(Await.Result<Object> result) {
		// started-before-unblocked: a racing seal never reads quiescence in
		// the gap between the record leaving and the counter rising
		if (owner != null) {
			owner.started();
			owner.unblocked(frame);
		}
		frame.scope = owner;
		frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(result);
		requeue.run();
	}
}
