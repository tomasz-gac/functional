package com.tgac.functional.fibers.schedulers;

// ABOUTME: The runtime's Await.Waiter: complete() records the frame resumed in its
// ABOUTME: owner's ledger, hands the frame its result, and re-queues it.

import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;

/**
 * The resume handle bound to one suspended frame. {@link #complete} records
 * the resume — {@link Scope#resumed}, started-before-unblocked — hands the
 * frame its result, and re-queues it through the scheduler-supplied
 * {@code requeue}.
 *
 * <p>No referee lives here, because no race exists: every record is placed
 * BEFORE the source is offered this handle, and a completion can only
 * happen after the offer. The completing frame is itself billed while it
 * completes (a producer's growth runs inside the producer's own open
 * started/finished pair; an inline completion runs inside the suspending
 * frame's), so no quiescence check can pass mid-completion. Exactly-once
 * holds structurally: the source removes a held waiter under its monitor
 * before completing it.
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
		if (owner != null) {
			owner.resumed(frame);
		}
		frame.scope = owner;
		frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(result);
		requeue.run();
	}
}
