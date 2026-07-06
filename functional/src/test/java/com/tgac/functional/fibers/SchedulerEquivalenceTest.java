package com.tgac.functional.fibers;

import static com.tgac.functional.fibers.Fiber.defer;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.schedulers.BreadthFirstScheduler;
import com.tgac.functional.fibers.schedulers.RoundRobin;
import com.tgac.functional.fibers.schedulers.UnfairBreadthFirstScheduler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/**
 * Every scheduler must produce the same results for the same fiber tree —
 * they differ in scheduling policy, never in step semantics.
 */
public class SchedulerEquivalenceTest {

	private final List<Function<Fiber<Nothing>, Scheduler<Nothing>>> schedulers = Arrays.asList(
			BreadthFirstScheduler::new,
			RoundRobin::of,
			UnfairBreadthFirstScheduler::of);

	private void onEachScheduler(Function<Function<Fiber<Nothing>, Scheduler<Nothing>>, Runnable> scenario) {
		for (Function<Fiber<Nothing>, Scheduler<Nothing>> factory : schedulers) {
			scenario.apply(factory).run();
		}
	}

	@Test
	public void shouldRunDeepDeferChains() {
		onEachScheduler(factory -> () -> {
			AtomicInteger result = new AtomicInteger();
			Fiber<Nothing> program = countdown(10_000)
					.flatMap(v -> {
						result.set(v);
						return done(Nothing.nothing());
					});

			factory.apply(program).get();

			assertThat(result.get()).isZero();
		});
	}

	private Fiber<Integer> countdown(int n) {
		return n == 0 ? done(0) : defer(() -> countdown(n - 1));
	}

	@Test
	public void shouldCollectAllForkedResults() {
		onEachScheduler(factory -> () -> {
			List<Integer> results = new CopyOnWriteArrayList<>();
			Fiber<Nothing> program = Fiber.fork(
					Arrays.asList(done(1), defer(() -> done(2)), countdown(100).map(v -> v + 3)),
					results::add);

			factory.apply(program).get();

			assertThat(results).containsExactlyInAnyOrder(1, 2, 3);
		});
	}

	@Test
	public void shouldCollectNestedForks() {
		onEachScheduler(factory -> () -> {
			List<Integer> inner = new CopyOnWriteArrayList<>();
			List<Integer> outer = new CopyOnWriteArrayList<>();

			Fiber<Nothing> program = Fiber.fork(Arrays.asList(
							Fiber.<Integer> fork(Arrays.asList(done(1), done(2)), inner::add)
									.map(n -> 10),
							done(20)),
					outer::add);

			factory.apply(program).get();

			assertThat(inner).containsExactlyInAnyOrder(1, 2);
			assertThat(outer).containsExactlyInAnyOrder(10, 20);
		});
	}

	@Test
	public void shouldRunDetachedFibersToCompletionAndDiscardTheirResults() {
		onEachScheduler(factory -> () -> {
			List<Integer> sideEffects = new CopyOnWriteArrayList<>();
			List<Integer> results = new CopyOnWriteArrayList<>();

			Fiber<Nothing> program = Fiber.fork(Arrays.asList(
							Fiber.detach(countdown(50).map(v -> {
								sideEffects.add(v + 100);
								return v;
							})).map(n -> 1),
							done(2)),
					results::add);

			factory.apply(program).get();

			assertThat(sideEffects).containsExactly(100);
			assertThat(results).containsExactlyInAnyOrder(1, 2);
		});
	}

	@Test
	public void shouldHandleEmptyForks() {
		onEachScheduler(factory -> () -> {
			List<Object> results = new ArrayList<>();
			Fiber<Nothing> program = Fiber.fork(Arrays.asList(), results::add);

			factory.apply(program).get();

			assertThat(results).isEmpty();
		});
	}

	@Test
	public void shouldInterleaveWithoutStarvation() {
		// a deep branch must not prevent a shallow sibling from producing
		onEachScheduler(factory -> () -> {
			List<Integer> results = new CopyOnWriteArrayList<>();
			Fiber<Nothing> program = Fiber.fork(
					Arrays.asList(countdown(5_000).map(v -> 1), done(2)),
					results::add);

			factory.apply(program).get();

			assertThat(results).containsExactlyInAnyOrder(1, 2);
		});
	}
}
