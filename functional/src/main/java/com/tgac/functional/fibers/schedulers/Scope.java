package com.tgac.functional.fibers.schedulers;

// ABOUTME: A sealable scope of work: the ledger, the seal, and the group walk —
// ABOUTME: termination detection for the workforce producing one MonotoneCell.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Source;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * The workforce half of a {@link MonotoneCell}: a {@link WorkLedger}
 * (running frames counted started/finished; blocked frames recorded with the
 * scope they wait at) and a SEAL — the upward-closed, CAS'd-once declaration
 * that the cell's work is finished. Racy seal reads are sound: a stale false
 * only defers.
 *
 * <p>THE SEAL RULE: counters drained and every blocked record HOME (waking
 * needs growth here, growth needs running work here — just ruled out). A
 * record at a FOREIGN scope defers: if that scope is sealed the record is a
 * resume in flight (the cell completes its waiters at the seal, and a
 * suspend on a sealed cell answers immediately, so sealed-place records are
 * always transient); if unsealed, the blocked frame may yet be woken. The
 * cross-scope case that never resolves by itself — a ring of drained scopes
 * mutually blocked — is {@link #groupSeal}'s: the singleton rule applied to
 * a virtual merge (docs/design/group-seal.md).
 *
 * <p>Sealing completes the cell's held waiters
 * ({@link #completeWaitersOnSeal}). Backwards propagation is frame-driven:
 * each resumed waiter's own {@code finished()} retries its scope's seal — no
 * cascade queue.
 */
public final class Scope {

	private final WorkLedger<Object, Wait> ledger = new WorkLedger<>();

	/** One blocked record: where the member waits, and how it can be woken. */
	static final class Wait {
		final Scope at;          // null: a foreign source, no workforce
		final boolean sealOnly;  // a drain-wait - woken by the seal alone

		Wait(Scope at, boolean sealOnly) {
			this.at = at;
			this.sealOnly = sealOnly;
		}
	}
	private final AtomicBoolean sealed = new AtomicBoolean(false);
	private final AtomicBoolean planted = new AtomicBoolean(false);
	/**
	 * The seal actions, one list, two registration sites: each closed cell
	 * registers its EOF translation at construction; each drained() waiter
	 * registers its completion at park. Guarded by this.
	 */
	private final List<Runnable> onSeal = new ArrayList<>();
	/** The degenerate channel drained() parks at. */
	private final Drain drain = new Drain();

	Scope() {
	}

	/** Mint a workforce. */
	public static Scope scope() {
		return new Scope();
	}

	/** The plant-once CAS: a workforce is planted exactly once (emit.md). */
	public void claimPlant() {
		if (!planted.compareAndSet(false, true)) {
			throw new IllegalStateException("workforce already planted: " + this);
		}
	}

	/** Register a seal action — run at once when the seal has already landed. */
	void onSeal(Runnable complete) {
		synchronized (this) {
			if (!isSealed()) {
				onSeal.add(complete);
				return;
			}
		}
		complete.run();
	}

	/** The read face for {@link Fiber#drained}: completes only at the seal. */
	public Source<Nothing> drainSource() {
		return drain;
	}

	/**
	 * The DEGENERATE CHANNEL — the scope as a source over the one-point
	 * lattice. A value that cannot ascend satisfies no readiness predicate,
	 * so the only completion this channel can ever deliver is EOF:
	 * sealed(nothing()), the seal spoken in value vocabulary with the only
	 * value there is. This is aggregation over the trivial lattice reaching
	 * the substrate (emit.md §5); drained() is an await on it.
	 */
	private final class Drain implements Source<Nothing> {
		@Override
		public void suspend(Predicate<Nothing> ready, Await.Waiter<Nothing> waiter) {
			synchronized (Scope.this) {
				if (!isSealed()) {
					onSeal.add(() -> waiter.complete(Await.Result.sealed(nothing())));
					return;
				}
			}
			waiter.complete(Await.Result.sealed(nothing()));
		}

		@Override
		public Scope scope() {
			return Scope.this;
		}

		@Override
		public boolean sealOnly() {
			return true;
		}

		@Override
		public String toString() {
			return "drain of " + Scope.this;
		}
	}

	// ---- the ledger writes ----

	public void started() {
		ledger.started();
	}

	public Fiber<Nothing> finished() {
		ledger.finished();
		return Fiber.defer(this::sealCascade);
	}

	public void blocked(Object sleeper, Scope at, boolean sealOnly) {
		ledger.blocked(sleeper, new Wait(at, sealOnly));
	}

	/**
	 * The frame resumed: one transition, started-before-unblocked inside it —
	 * a racing seal never reads quiescence between the record leaving and
	 * the counter rising.
	 */
	public void resumed(Object waiter) {
		ledger.started();
		ledger.unblocked(waiter);
	}

	// ---- the seal ----

	public boolean isSealed() {
		return sealed.get();
	}

	/** Manual seal — external certificates. Completes no waiter. */
	public void seal() {
		sealed.set(true);
	}

	/**
	 * Seal this scope if quiescent. Waiter completions inject their frames,
	 * and each resumed frame's own {@code finished()} retries its scope — the
	 * return value exists only to satisfy {@link #finished}'s shape.
	 */
	public Fiber<Nothing> sealCascade() {
		if (!sealIfQuiescent() && !isSealed() && ledger.drained()) {
			// the singleton rule refused on a drained scope: the obstruction
			// is a record at a foreign scope — try the ring
			groupSeal(this);
		}
		return done(nothing());
	}

	private boolean sealIfQuiescent() {
		if (!ledger.quiescent(w -> w.at == this)) {
			return false;
		}
		if (!sealed.compareAndSet(false, true)) {
			return false;
		}
		completeOnSeal();
		return true;
	}

	/**
	 * The seal speaks to each audience on its own channel: drain-waiters get
	 * Nothing (the control event), each closed cell translates the seal into
	 * sealed(value) for its value-waiters (EOF on the data channel).
	 */
	private void completeOnSeal() {
		List<Runnable> actions;
		synchronized (this) {
			actions = new ArrayList<>(onSeal);
			onSeal.clear();
		}
		for (Runnable action : actions) {
			action.run();
		}
	}

	/**
	 * THE GROUP SEAL — the singleton rule applied to a VIRTUAL MERGE
	 * (docs/design/group-seal.md): ledger = sum of the members', blocked
	 * records = union, HOME = membership. The walk closes {@code start} under
	 * blocked places; a member with running work aborts (its own finished
	 * retries), a sealed place aborts (a resume in flight — retried by the
	 * resumed frame's finished), a null place defers forever (a wait with no
	 * workforce). The two-phase read over the MONOTONE started counters
	 * reconstructs atomicity without nested monitors: two equal reads bracket
	 * an interval with no started() in it. Racing group seals arbitrate per
	 * member by the flag CAS; every member is MARKED before any member's
	 * waiters are completed, so the first resumed frame reads the whole ring
	 * as sealed.
	 */
	private static void groupSeal(Scope start) {
		LinkedHashMap<Scope, Long> members = new LinkedHashMap<>();
		ArrayDeque<Scope> frontier = new ArrayDeque<>();
		frontier.add(start);
		while (!frontier.isEmpty()) {
			Scope scope = frontier.poll();
			if (members.containsKey(scope) || scope.isSealed()) {
				continue;
			}
			WorkLedger.Snapshot<Wait> snapshot = scope.ledger.drainedSnapshot();
			if (snapshot == null) {
				return;
			}
			members.put(scope, snapshot.started);
			for (Wait w : snapshot.blockedAt) {
				if (w.at == scope) {
					continue;
				}
				if (w.sealOnly) {
					// A DRAIN-EDGE IS NOT A RING EDGE: its waiter is woken by
					// the target's seal itself, so sealing the group would wake
					// a member with pending work on a sealed scope. The target
					// seals by its own cascade (or its own cell-wait ring) and
					// the waiter's home seals later, at true quiescence.
					return;
				}
				if (w.at == null || w.at.isSealed()) {
					// no workforce, or a resume in flight — defer
					return;
				}
				frontier.add(w.at);
			}
		}
		for (Map.Entry<Scope, Long> m : members.entrySet()) {
			if (m.getKey().ledger.startedCount() != m.getValue()) {
				return;
			}
		}
		// mark EVERY member before completing any waiter: at each resume the
		// whole group must already read as sealed (SEALED ⟹ SOLVABLE)
		ArrayDeque<Scope> won = new ArrayDeque<>();
		for (Scope member : members.keySet()) {
			if (member.sealed.compareAndSet(false, true)) {
				won.add(member);
			}
		}
		for (Scope member : won) {
			member.completeOnSeal();
		}
	}
}
