package com.tgac.functional.fibers;

// ABOUTME: A worklist drained as a fiber: one item per deferred step, each step may
// ABOUTME: add work and advance state, a stop short-circuits. Fairness via the scheduler.

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.tgac.functional.algebra.Bottomed;
import com.tgac.functional.algebra.MeetSemilattice;
import com.tgac.functional.algebra.MonotoneDrain;
import io.vavr.Tuple2;
import io.vavr.collection.Queue;
import java.util.Collections;
import java.util.function.BiFunction;

/**
 * The worklist-to-quiescence loop as a schedulable value: pop one item, apply the
 * step, append whatever work it discovered, defer, repeat until the queue runs dry
 * or a step stops early. Each item is one {@link Fiber} step, so whatever scheduler
 * drives the enclosing computation interleaves fairly between items.
 *
 * <p>For plain {@link #drain}, termination is the CALLER'S obligation: a step that
 * keeps discovering genuinely new work loops forever (fairly). The standard
 * argument is monotonicity — steps that only ever shrink a finite state can only
 * add finitely much work. The MONOTONE variants make that argument part of the
 * API: the state is a {@link MeetSemilattice} with a {@link Bottomed} failure
 * value, the drain short-circuits at ⊥, and the SAFE variant enforces the two
 * contraction laws per step — the new state must entail the old
 * ({@code new ⊑ old}), and emitting new work requires STRICT descent (the
 * equal-domain termination guard, generalized: wake-on-narrowing must descend
 * strictly or stop). The UNSAFE variant keeps the ⊥ short-circuit and skips the
 * law checks — the hot-path twin, same contract by convention.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Worklist {


	/** A step's outcome: continue with more work, or stop draining early. */
	public static final class Step<W, S> {
		private final S state;
		private final Iterable<W> more;
		private final boolean stopped;

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

	public static <W, S> Fiber<S> drain(
			S initial,
			Iterable<W> work,
			BiFunction<S, W, Step<W, S>> step) {
		return go(initial, Queue.ofAll(work), step);
	}

	/** The checked monotone drain: contraction laws enforced per step, ⊥ short-circuits. */
	public static <W, S extends MeetSemilattice<S> & Bottomed> Fiber<S> drainMonotone(
			S initial,
			Iterable<W> work,
			BiFunction<S, W, Step<W, S>> step) {
		return goMonotone(initial, Queue.ofAll(work), step, true);
	}

	/** The unchecked twin: same contract by convention, ⊥ short-circuit kept, no law checks. */
	public static <W, S extends MeetSemilattice<S> & Bottomed> Fiber<S> drainMonotoneUnsafe(
			S initial,
			Iterable<W> work,
			BiFunction<S, W, Step<W, S>> step) {
		return goMonotone(initial, Queue.ofAll(work), step, false);
	}

	private static <W, S extends MeetSemilattice<S> & Bottomed> Fiber<S> goMonotone(
			S state,
			Queue<W> queue,
			BiFunction<S, W, Step<W, S>> step,
			boolean checked) {
		if (state.isBottom() || queue.isEmpty()) {
			return Fiber.done(state);
		}
		Tuple2<W, Queue<W>> popped = queue.dequeue();
		Step<W, S> outcome = step.apply(state, popped._1);
		if (checked) {
			MonotoneDrain.check(state, outcome.state, outcome.more.iterator().hasNext());
		}
		if (outcome.stopped) {
			return Fiber.done(outcome.state);
		}
		Queue<W> remaining = popped._2.appendAll(outcome.more);
		return Fiber.defer(() -> goMonotone(outcome.state, remaining, step, checked));
	}

	private static <W, S> Fiber<S> go(S state, Queue<W> queue, BiFunction<S, W, Step<W, S>> step) {
		if (queue.isEmpty()) {
			return Fiber.done(state);
		}
		Tuple2<W, Queue<W>> popped = queue.dequeue();
		Step<W, S> outcome = step.apply(state, popped._1);
		if (outcome.stopped) {
			return Fiber.done(outcome.state);
		}
		Queue<W> remaining = popped._2.appendAll(outcome.more);
		return Fiber.defer(() -> go(outcome.state, remaining, step));
	}
}
