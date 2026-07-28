package com.tgac.functional.fibers.schedulers;

// ABOUTME: The runtime's Await.Waiter: bill() moves the frame to running in its
// ABOUTME: owner's ledger, complete() delivers the result and re-queues the frame.

import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;

/**
 * The resume handle bound to one suspended frame. {@link #bill} records the
 * frame as running again — {@code owner.started()} then
 * {@code owner.unblocked(frame)}, idempotent — and {@link #complete} bills
 * (if not already billed), hands the frame its result, and re-queues it
 * through the scheduler-supplied {@code requeue}. The bill/deliver split
 * exists for the SEAL path: {@link MonotoneCell} bills every resumed frame
 * before delivering any result, so no blocked record can satisfy a
 * quiescence predicate while its sealed-arm work is pending.
 */
final class ResumeHandle implements Await.Waiter<Object> {

	private final FiberStep.Frame frame;
	private final Scope<?> owner;
	private final Runnable requeue;
	private boolean billed;

	ResumeHandle(FiberStep.Frame frame, Scope<?> owner, Runnable requeue) {
		this.frame = frame;
		this.owner = owner;
		this.requeue = requeue;
	}

	/** started-before-unblocked, exactly once — a racing seal never reads quiescence in the gap. */
	synchronized void bill() {
		if (billed) {
			return;
		}
		billed = true;
		if (owner != null) {
			owner.started();
			owner.unblocked(frame);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void complete(Await.Result<Object> result) {
		bill();
		frame.scope = owner;
		frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(result);
		requeue.run();
	}
}
