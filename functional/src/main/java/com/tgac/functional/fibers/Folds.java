package com.tgac.functional.fibers;

// ABOUTME: The two generic fold shapes over fibers: chained (dependent,
// ABOUTME: failure-aware) and forked (independent, collected, then merged).

import com.tgac.functional.algebra.Monoid;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * The chained/forked distinction is a CONTRACT, not a convenience: chain when
 * each step must see its predecessors' state; fork when the producers are
 * independent (built against one snapshot) — a work-stealing scheduler then
 * parallelizes across them and inside them. Merging forked results through a
 * {@link Monoid} is lawful in any GROUPING (associativity); reordering the
 * merge additionally requires commutativity — which is exactly what the
 * algebra package's law kits certify.
 */
public final class Folds {

	private Folds() {
	}

	/** Run independent fibers, collect results in producer order. */
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

	/** The lifted monoid's fold: independent fibers merged through m. */
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
	 * a {@code none} kills the chain (0 ⊗ x = 0, structurally).
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
