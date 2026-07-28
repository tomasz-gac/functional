package com.tgac.functional.fibers.schedulers;

// ABOUTME: The runtime's Await.Waiter: markRunning() moves the frame to running in its
// ABOUTME: owner's ledger, complete() delivers the result and re-queues the frame.

import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;

/**
 * The resume handle bound to one suspended frame. {@link #markRunning} moves the
 * frame to running — {@code owner.started()} then
 * {@code owner.unblocked(frame)}, exactly once — and {@link #complete}
 * marks it running (if not already), hands the frame its result, and
 * re-queues it through the scheduler-supplied {@code requeue}. The
 * mark/deliver split exists for the SEAL path: {@link MonotoneCell} marks
 * every resumed frame running before delivering any result, so no blocked
 * record can satisfy a quiescence predicate while its sealed-arm work is
 * pending.
 */
final class ResumeHandle implements Await.Waiter<Object> {

	private final FiberStep.Frame frame;
	private final Scope<?> owner;
	private final Runnable requeue;
	private boolean running;

	ResumeHandle(FiberStep.Frame frame, Scope<?> owner, Runnable requeue) {
		this.frame = frame;
		this.owner = owner;
		this.requeue = requeue;
	}

	/** started-before-unblocked, exactly once — a racing seal never reads quiescence in the gap. */
	synchronized void markRunning() {
		if (running) {
			return;
		}
		running = true;
		if (owner != null) {
			owner.started();
			owner.unblocked(frame);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void complete(Await.Result<Object> result) {
		markRunning();
		frame.scope = owner;
		frame.computation = (Fiber<Object>) (Fiber<?>) Fiber.done(result);
		requeue.run();
	}
}
