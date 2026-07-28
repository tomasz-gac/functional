package com.tgac.functional.fibers.schedulers;

// ABOUTME: A sealable scope of work: the ledger, the seal, and the cascade —
// ABOUTME: termination detection without a published value; Fixpoint adds the cell.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import io.vavr.collection.List;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The termination-detection half of a {@link Fixpoint}: a {@link WorkLedger}
 * (everything working for the scope — running fibers and blocked
 * subscribers, each recorded with the scope it waits at) and a SEAL — the
 * upward-closed, CAS'd-once declaration that the scope's work is finished.
 * Racy seal reads are sound: a stale false only defers.
 *
 * <p>A scope needs no published value to be sealable: subscribers that wait
 * only for the seal {@link #awaitSeal} here and are drained when it fires. A
 * {@link Fixpoint} composes a scope with a {@link MonotoneCell} and redirects
 * the drain to the cell's parked subscribers.
 *
 * <p>The one domain-specific input is {@code ownerOf}: given a subscriber,
 * which scope's work is it (null = unowned top-level work, recorded
 * nowhere, gating nothing). Everything else is the theorem:
 *
 * <p>The SEAL RULE (internal): counters drained and every sleeper parked
 * HOME (waking needs new growth here, which needs running work here — just
 * ruled out) or at an already-sealed scope (never grows again). Then flag
 * CAS, then the parked subscribers are provably dead.
 *
 * <p>{@link #sealCascade} propagates seals backwards along sleeper edges:
 * sealing kills the sleepers parked here; each dead sleeper's owner loses
 * an obstruction and is rechecked. Monitors never nest — each scope's
 * rule runs under its own locks, the walk happens outside them.
 */
final class Scope<S> {

	private final WorkLedger<Object, Scope<?>> ledger = new WorkLedger<>();
	private final AtomicBoolean sealed = new AtomicBoolean(false);
	private final Function<S, Scope<S>> ownerOf;
	private final ArrayList<S> awaitingSeal = new ArrayList<>();

	/**
	 * Work to spawn the moment this scope seals, given the drained
	 * subscribers — dead branches for plain tabling, EMIT targets for closed
	 * tabling, the fold for aggregation. Inert by default.
	 */
	private Function<List<S>, Fiber<Nothing>> onSealed = drained -> done(nothing());

	/**
	 * Where dead subscribers are harvested at seal time: this scope's own
	 * {@link #awaitSeal}ed list by default; a {@link Fixpoint} redirects to its
	 * cell's parked subscribers.
	 */
	private Supplier<List<S>> drainOnSeal = this::drainParked;

	/** The cell's two-phase completion of held frames, run once the flag is set. */
	private Runnable completeWaitersOnSeal = () -> {
	};

	public Scope(Function<S, Scope<S>> ownerOf) {
		this.ownerOf = ownerOf;
	}

	/** Register the fiber to spawn when this scope seals. */
	public void onSealed(Function<List<S>, Fiber<Nothing>> work) {
		this.onSealed = work;
	}

	// ---- the WorkScope methods ----

	public void started() {
		ledger.started();
	}

	public Fiber<Nothing> finished() {
		ledger.finished();
		return Fiber.defer(this::sealCascade);
	}

	void drainOnSeal(Supplier<List<S>> drain) {
		// composed, not replaced: the cell's parked subscribers AND this
		// scope's own awaitSeal list are both dead at the seal
		this.drainOnSeal = () -> drain.get().appendAll(drainParked());
	}

	void completeWaitersOnSeal(Runnable complete) {
		this.completeWaitersOnSeal = complete;
	}

	// ---- seal-only subscribers ----

	/** Park a subscriber that waits only for the seal — drained when it fires. */
	public synchronized void awaitSeal(S subscriber) {
		awaitingSeal.add(subscriber);
	}

	private synchronized List<S> drainParked() {
		List<S> dead = List.ofAll(awaitingSeal);
		awaitingSeal.clear();
		return dead;
	}

	// ---- the work half ----

	public void blocked(Object sleeper, Scope<?> at) {
		ledger.blocked(sleeper, at);
	}

	public void unblocked(Object sleeper) {
		ledger.unblocked(sleeper);
	}

	/**
	 * Respawn a woken sleeper's continuation: record it as running
	 * (counted) before removing the blocked record — the transition must
	 * never leave a window where the record is gone but the running count
	 * has not risen,
	 * or a racing seal (parallel schedulers) reads this scope as quiescent
	 * and seals it out from under the consumer. The eager started() is why
	 * this lives here and not on the ambient path, whose started() lands at
	 * frame construction — after the unblock. Over-counting for the instant between
	 * only delays a seal, which is always sound.
	 */
	public Fiber<Nothing> respawn(S sleeper, Fiber<Nothing> work) {
		Fiber<Nothing> tracked = ledger.counted(work, this::sealCascade);
		unblocked(sleeper);
		return Fiber.detach(tracked);
	}

	// ---- the seal ----

	public boolean isSealed() {
		return sealed.get();
	}

	/** Manual seal — tests and external certificates. */
	public void seal() {
		sealed.set(true);
	}

	/**
	 * Seal this scope if quiescent and propagate along sleeper edges.
	 *
	 * @return the fiber composing every newly sealed scope's {@link #onSealed}
	 * 		work (the star emit) — inert for plain tabling
	 */
	public Fiber<Nothing> sealCascade() {
		ArrayList<Fiber<Nothing>> emits = new ArrayList<>();
		ArrayDeque<Scope<S>> queue = new ArrayDeque<>();
		queue.add(this);
		while (!queue.isEmpty()) {
			Scope<S> scope = queue.poll();
			List<S> dead = scope.sealIfQuiescent(emits);
			if (dead == null) {
				// the singleton rule refused; if the scope is drained and
				// unsealed, the obstruction is a foreign-unsealed sleeper —
				// try sealing its sleeper-closure as a group
				if (!scope.isSealed() && scope.ledger.drained()) {
					dead = groupSeal(scope, emits);
				}
				if (dead == null) {
					continue;
				}
			}
			for (S sleeper : dead) {
				Scope<S> owner = ownerOf.apply(sleeper);
				if (owner != null) {
					owner.unblocked(sleeper);
					queue.add(owner);
				}
			}
		}
		Fiber<Nothing> result = done(nothing());
		for (Fiber<Nothing> emit : emits) {
			Fiber<Nothing> tail = emit;
			result = result.flatMap(__ -> tail);
		}
		return result;
	}

	private List<S> sealIfQuiescent(ArrayList<Fiber<Nothing>> emits) {
		if (!ledger.quiescent(at -> at == this || (at != null && at.isSealed()))) {
			return null;
		}
		if (!sealed.compareAndSet(false, true)) {
			return null;
		}
		completeWaitersOnSeal.run();
		List<S> drained = drainOnSeal.get();
		emits.add(onSealed.apply(drained));
		return drained;
	}

	/**
	 * THE GROUP SEAL (Tier 2) — the singleton rule applied to a VIRTUAL
	 * MERGE (docs/design/group-seal.md). Define the merge of a set S of
	 * scopes: ledger = sum of the members', sleepers = union, HOME =
	 * membership in S. The group condition is then the ordinary seal rule
	 * on merge(S), verbatim — merged ledger drained, every merged sleeper
	 * home or at a sealed scope — and its soundness argument transfers
	 * with it: growth inside S needs running S-work (none), and nothing
	 * outside injects, because growth is recorded in the grower's own scope.
	 *
	 * <p>WHICH merge: the smallest one that makes all sleepers home — the
	 * walk below is a fixpoint ascent in the finite join-semilattice of
	 * scope sets, closing {start} under sleeper-targets (a closure
	 * operator; running members abort the ascent, and their own finish
	 * events retry it).
	 *
	 * <p>The two-phase read is the price of evaluating the merged rule
	 * WITHOUT materializing a merged ledger: constituents keep their own
	 * monitors, and atomicity across them is reconstructed from the
	 * MONOTONE started counters — two equal reads bracket a spawn-free
	 * interval, a consistent snapshot with no nested monitors. Racing
	 * group seals are arbitrated per member by the flag CAS — a lost CAS
	 * just skips that member's drain. The merge exists for the duration of
	 * one rule-evaluation and is then discarded; eager permanent merging
	 * (SLG's ASCC) is the same algorithm with a different merge lifetime.
	 *
	 * @return the dead sleepers drained from every sealed member, or null
	 * 		when the group cannot seal yet
	 */
	@SuppressWarnings("unchecked")
	private List<S> groupSeal(Scope<S> start, ArrayList<Fiber<Nothing>> emits) {
		LinkedHashMap<Scope<S>, Long> members = new LinkedHashMap<>();
		ArrayDeque<Scope<S>> frontier = new ArrayDeque<>();
		frontier.add(start);
		while (!frontier.isEmpty()) {
			Scope<S> scope = frontier.poll();
			if (members.containsKey(scope) || scope.isSealed()) {
				continue;
			}
			// ONE atomic read per member: drained + counter + sleepers together —
			// a racing respawn otherwise slips between the reads (see
			// WorkLedger.drainedSnapshot) and the re-verify below cannot see it
			WorkLedger.Snapshot<Scope<?>> snapshot = scope.ledger.drainedSnapshot();
			if (snapshot == null) {
				return null;
			}
			members.put(scope, snapshot.started);
			for (Scope<?> at : snapshot.blockedAt) {
				if (at == scope) {
					continue;
				}
				if (at == null) {
					// a place with no workforce can never seal - defer forever
					return null;
				}
				if (!at.isSealed()) {
					frontier.add((Scope<S>) at);
				}
			}
		}
		for (Map.Entry<Scope<S>, Long> m : members.entrySet()) {
			if (m.getKey().ledger.startedCount() != m.getValue()) {
				return null;
			}
		}
		// mark EVERY member before completing or announcing any: at each hook
		// the whole group must already read as sealed (SEALED ⟹ SOLVABLE),
		// so the first-announced hook can act on the full closure
		ArrayList<Scope<S>> won = new ArrayList<>();
		for (Scope<S> member : members.keySet()) {
			if (member.sealed.compareAndSet(false, true)) {
				won.add(member);
			}
		}
		// all marked: bill-and-complete each member's held frames, drain the
		// dead S-subscribers, then announce
		LinkedHashMap<Scope<S>, List<S>> drains = new LinkedHashMap<>();
		for (Scope<S> member : won) {
			member.completeWaitersOnSeal.run();
			drains.put(member, member.drainOnSeal.get());
		}
		List<S> dead = List.empty();
		for (Map.Entry<Scope<S>, List<S>> m : drains.entrySet()) {
			emits.add(m.getKey().onSealed.apply(m.getValue()));
			dead = dead.appendAll(m.getValue());
		}
		return dead;
	}
}
