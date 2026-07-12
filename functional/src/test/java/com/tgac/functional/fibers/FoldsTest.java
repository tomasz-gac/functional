package com.tgac.functional.fibers;

// ABOUTME: Pins the two fold shapes: chained threading with failure
// ABOUTME: short-circuit, forked collection order, and the lifted monoid fold.

import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.algebra.laws.Monoids;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class FoldsTest {

	@Test
	public void collectAllPreservesProducerOrder() {
		assertThat(Folds.collectAll(Arrays.asList(
				Fiber.done("a"), Fiber.defer(() -> Fiber.done("b")), Fiber.done("c"))).get())
				.containsExactly("a", "b", "c");
	}

	@Test
	public void forkAllJoinsAllChildrenAndPreservesProducerOrder() {
		assertThat(Folds.forkAll(Arrays.asList(
				Fiber.defer(() -> Fiber.done("a")),
				Fiber.done("b"),
				Fiber.defer(() -> Fiber.defer(() -> Fiber.done("c"))))).get())
				.containsExactly("a", "b", "c");
	}

	@Test
	public void foldZipMergesThroughTheMonoid() {
		assertThat(Folds.foldZip(Monoids.LONG_SUM, Arrays.asList(
				Fiber.done(1L), Fiber.done(2L), Fiber.done(4L))).get())
				.isEqualTo(7L);
	}

	@Test
	public void chainedStepsSeeTheirPredecessorsAndNoneShortCircuits() {
		AtomicInteger stepsRun = new AtomicInteger();
		MFiber<Long> chained = Folds.foldChained(10L, Arrays.asList(1L, 100L, 2L),
				(state, item) -> {
					stepsRun.incrementAndGet();
					return item == 100L ? MFiber.none() : MFiber.mdone(state + item);
				});
		assertThat(chained.get().isDefined()).isFalse();
		// the third step never runs: none annihilates the chain
		assertThat(stepsRun.get()).isEqualTo(2);
	}
}
