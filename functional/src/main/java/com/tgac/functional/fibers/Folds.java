package com.tgac.functional.fibers;

// ABOUTME: The fold shapes over fibers: chained (dependent, failure-aware),
// ABOUTME: sequential collection, and forkAll — the real parallel join via Forked.

import com.tgac.functional.algebra.Monoid;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BiFunction;

/**
 * Three shapes, honestly labeled:
 * <ul>
 * <li>{@link #foldChained} — DEPENDENT: each step sees its predecessors'
 * state; {@code none} annihilates (0 ⊗ x = 0, structurally).</li>
 * <li>{@link #collectAll}/{@link #foldZip} — SEQUENTIAL: {@code Fiber.zip}
 * is {@code flatMap} (left completes before right starts); use these when
 * order is wanted and concurrency is not.</li>
 * <li>{@link #forkAll} — PARALLEL JOIN: children are handed to the scheduler
 * as independent frames ({@code Fiber.fork}/{@code Forked}); a work-stealing
 * scheduler runs them concurrently, a countdown resumes the parent. Merging
 * forked results through a monoid is lawful in any grouping (associativity);
 * completion ORDER is scheduler-dependent, so order-sensitive merges must
 * use the index-preserved list this returns, and commutative merges may
 * ignore it — certified by the algebra law kits.</li>
 * </ul>
 */
public final class Folds {

	private Folds() {
	}

	/** TRUE parallel join: fork all fibers to the scheduler, resume with results in producer order. */
	public static <A> Fiber<List<A>> forkAll(List<Fiber<A>> fibers) {
		if (fibers.isEmpty()) {
			return Fiber.done(new ArrayList<>());
		}
		AtomicReferenceArray<A> slots = new AtomicReferenceArray<>(fibers.size());
		List<Fiber<Tuple2<Integer, A>>> tagged = new ArrayList<>(fibers.size());
		for (int i = 0; i < fibers.size(); i++) {
			int slot = i;
			tagged.add(fibers.get(i).map(a -> Tuple.of(slot, a)));
		}
		return Fiber.fork(tagged, t -> slots.set(t._1, t._2))
				.map(done -> {
					List<A> out = new ArrayList<>(fibers.size());
					for (int i = 0; i < fibers.size(); i++) {
						out.add(slots.get(i));
					}
					return out;
				});
	}

	/** Sequential collection in producer order — zip is flatMap; no concurrency. */
	public static <A> Fiber<List<A>> collectAll(Iterable<Fiber<A>> fibers) {
		Fiber<List<A>> acc = Fiber.done(new ArrayList<>());
		for (Fiber<A> fiber : fibers) {
			Fiber<List<A>> soFar = acc;
			acc = Fiber.zip(soFar, fiber).map(t -> {
				t._1.add(t._2);
				return t._1;
			});
		}
		return acc;
	}

	/** Sequential merge through the monoid — for the parallel version, forkAll then fold. */
	public static <M> Fiber<M> foldZip(Monoid<M> m, Iterable<Fiber<M>> fibers) {
		Fiber<M> acc = Fiber.done(m.empty());
		for (Fiber<M> fiber : fibers) {
			Fiber<M> soFar = acc;
			acc = Fiber.zip(soFar, fiber).map(t -> m.combine(t._1, t._2));
		}
		return acc;
	}

	/**
	 * The dependent fold: each step sees the state its predecessors produced;
	 * a {@code none} kills the chain.
	 */
	public static <S, A> MFiber<S> foldChained(
			S initial,
			Iterable<A> items,
			BiFunction<S, A, MFiber<S>> step) {
		MFiber<S> acc = MFiber.mdone(initial);
		for (A item : items) {
			MFiber<S> soFar = acc;
			acc = soFar.flatMap(state -> step.apply(state, item));
		}
		return acc;
	}
}
