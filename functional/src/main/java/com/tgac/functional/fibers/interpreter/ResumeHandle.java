package com.tgac.functional.fibers.interpreter;

// ABOUTME: The runtime's Await.Waiter: hands the frame its result, restores its
// ABOUTME: scope, and re-queues it - billing the resume only for value waiters.

import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;

/**
 * The resume handle bound to one suspended frame. Its two entry points are
 * its two callers, and the billing difference between them is the two parks'
 * whole difference (emit.md):
 * <ul>
 * <li>{@link #complete} — the cells' door, for VALUE waiters
 * ({@link Fiber.Awaiting}): the park closed the frame's started/finished
 * pair, so the resume re-bills it ({@link Scope#resumed},
 * started-before-unblocked);</li>
 * <li>{@link #resume} — the scope's door, for SEAL waiters
 * ({@link Fiber.Sealed}): the pair stayed open through the wait (the ledger
 * is the work), so the resume must not bill again.</li>
 * </ul>
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResumeHandle implements Await.Waiter<Object> {

	FiberStep.Frame frame;
	Scope owner;
	Runnable requeue;

	@Override
	public void complete(Await.Result<Object> result) {
		if (owner != null) {
			owner.resumed(frame);
		}
		deliver(result);
	}

	/** The seal's completion of a billed-through waiter — no re-billing. */
	void resume(Object value) {
		deliver(value);
	}

	private void deliver(Object value) {
		frame.scope = owner;
		frame.computation = Fiber.done(value);
		requeue.run();
	}
}
