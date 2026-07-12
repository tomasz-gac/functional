package com.tgac.functional.algebra;

// ABOUTME: Drains a worklist synchronously over a bounded meet-semilattice state,
// ABOUTME: enforcing per-step contraction — finite descent is the termination proof.

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The worklist algorithm as a lattice theorem: if every step contracts the
 * state ({@code outcome ⊑ state}) and only discovers work under STRICT descent,
 * then on a finite-height lattice the drain terminates — no fuel, no external
 * fairness. Both laws are enforced per step; a violating step is a bug in the
 * step function, not a search failure, so it throws. ⊥ short-circuits: a dead
 * state processes no further work.
 *
 * <p>This is the scheduling-free core; {@code fibers.Worklist.drainMonotone} is
 * the same contract drained one fiber step per item, and delegates its checks
 * here.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonotoneDrain {

	/** A step's outcome: continue with more work, or stop draining early. */
	public static final class Step<W, S> {
		final S state;
		final Iterable<W> more;
		final boolean stopped;

		private Step(S state, Iterable<W> more, boolean stopped) {
			this.state = state;
			this.more = more;
			this.stopped = stopped;
		}

		public static <W, S> Step<W, S> proceed(S state) {
			return new Step<>(state, Collections.emptyList(), false);
		}

		public static <W, S> Step<W, S> proceed(S state, Iterable<W> more) {
			return new Step<>(state, more, false);
		}

		public static <W, S> Step<W, S> stop(S state) {
			return new Step<>(state, Collections.emptyList(), true);
		}
	}

	public static <W, S extends MeetSemilattice<S> & Bottomed> S drain(
			S initial,
			Iterable<W> work,
			BiFunction<S, W, Step<W, S>> step) {
		return go(initial, work, step, true);
	}

	/** The unchecked twin: same contract by convention, ⊥ short-circuit kept, no law checks. */
	public static <W, S extends MeetSemilattice<S> & Bottomed> S drainUnsafe(
			S initial,
			Iterable<W> work,
			BiFunction<S, W, Step<W, S>> step) {
		return go(initial, work, step, false);
	}

	private static <W, S extends MeetSemilattice<S> & Bottomed> S go(
			S initial,
			Iterable<W> work,
			BiFunction<S, W, Step<W, S>> step,
			boolean checked) {
		ArrayDeque<W> queue = new ArrayDeque<>();
		work.forEach(queue::add);
		S state = initial;
		while (!state.isBottom() && !queue.isEmpty()) {
			Step<W, S> outcome = step.apply(state, queue.poll());
			if (checked) {
				check(state, outcome.state, outcome.more.iterator().hasNext());
			}
			if (outcome.stopped) {
				return outcome.state;
			}
			outcome.more.forEach(queue::add);
			state = outcome.state;
		}
		return state;
	}

	/** The two contraction laws, shared with the fibered drain. */
	public static <S extends MeetSemilattice<S> & Bottomed> void check(
			S before, S after, boolean emitsWork) {
		if (!after.leq(before)) {
			throw new IllegalStateException(
					"monotone worklist step must contract: " + after + " ⋢ " + before);
		}
		if (emitsWork && after.equals(before)) {
			throw new IllegalStateException(
					"monotone worklist step emitted work without strict descent at " + before);
		}
	}
}
