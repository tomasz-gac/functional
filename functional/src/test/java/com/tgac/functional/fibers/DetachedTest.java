package com.tgac.functional.fibers;

import static com.tgac.functional.fibers.Fiber.defer;
import static com.tgac.functional.fibers.Fiber.detach;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * ABOUTME: Tests for Fiber.Detached - verifying independent background execution semantics.
 * ABOUTME: Ensures detached fibers run independently while parent continues immediately.
 */
public class DetachedTest {

	@Test
	public void shouldDetachSimpleFiber() {
		AtomicInteger counter = new AtomicInteger(0);

		Nothing result = detach(defer(() -> {
			counter.incrementAndGet();
			return done(42);
		})).get();

		assertThat(result).isEqualTo(Nothing.nothing());
		// Detached fiber should have executed
		assertThat(counter.get()).isEqualTo(1);
	}

	@Test
	public void shouldReturnImmediately() {
		AtomicInteger executionFlag = new AtomicInteger(0);

		Fiber<Nothing> parent = detach(defer(() -> {
			executionFlag.set(1);
			return done("background work");
		}));

		// Parent fiber should complete, returning Nothing
		Nothing result = parent.get();
		assertThat(result).isEqualTo(Nothing.nothing());

		// Background work should have completed
		assertThat(executionFlag.get()).isEqualTo(1);
	}

	@Test
	public void shouldAllowParentToContinueAfterDetach() {
		List<String> events = new CopyOnWriteArrayList<>();

		Fiber<String> program = detach(defer(() -> {
			events.add("detached-work");
			return done("detached-result");
		})).flatMap(_0 -> {
			events.add("parent-continues");
			return done("parent-result");
		});

		String result = program.get();

		assertThat(result).isEqualTo("parent-result");
		assertThat(events).containsExactlyInAnyOrder("detached-work", "parent-continues");
	}

	@Test
	public void shouldDetachLongRunningComputation() {
		AtomicInteger result = new AtomicInteger(0);

		Nothing nothing = detach(
				buildDeferChain(10_000, 0).map(x -> {
					result.set(x);
					return x;
				})
		).get();

		assertThat(nothing).isEqualTo(Nothing.nothing());
		assertThat(result.get()).isEqualTo(10_000);
	}

	private Fiber<Integer> buildDeferChain(int depth, int current) {
		if (current >= depth) {
			return done(current);
		}
		return defer(() -> buildDeferChain(depth, current + 1));
	}

	@Test
	public void shouldDetachMultipleFibers() {
		List<Integer> results = new CopyOnWriteArrayList<>();

		Fiber<Nothing> program = detach(done(1).map(results::add))
				.flatMap(_0 -> detach(done(2).map(results::add)))
				.flatMap(_0 -> detach(done(3).map(results::add)));

		program.get();

		assertThat(results).containsExactlyInAnyOrder(1, 2, 3);
	}

	@Test
	public void shouldDetachWithinFork() {
		List<String> results = new CopyOnWriteArrayList<>();

		Fiber<Nothing> task1 = detach(done("task1").map(results::add));
		Fiber<Nothing> task2 = detach(done("task2").map(results::add));
		Fiber<Nothing> task3 = detach(done("task3").map(results::add));

		Fiber.fork(
				java.util.Arrays.asList(task1, task2, task3),
				_0 -> results.add("fork-done")
		).get();

		assertThat(results).contains("task1", "task2", "task3");
		assertThat(results.stream().filter(s -> s.equals("fork-done")).count()).isEqualTo(3);
	}

	@Test
	public void shouldDetachRecursiveComputation() {
		AtomicInteger finalValue = new AtomicInteger(0);

		Nothing result = detach(
				factorial(10).map(x -> {
					finalValue.set(x);
					return x;
				})
		).get();

		assertThat(result).isEqualTo(Nothing.nothing());
		assertThat(finalValue.get()).isEqualTo(3628800); // 10!
	}

	private Fiber<Integer> factorial(int n) {
		if (n <= 1) {
			return done(1);
		}
		return defer(() -> factorial(n - 1))
				.flatMap(rest -> done(n * rest));
	}

	@Test
	public void shouldDetachFiberThatUsesMap() {
		AtomicInteger result = new AtomicInteger(0);

		detach(
				done(10)
						.map(x -> x * 2)
						.map(x -> x + 5)
						.map(x -> {
							result.set(x);
							return x;
						})
		).get();

		assertThat(result.get()).isEqualTo(25);
	}

	@Test
	public void shouldDetachFiberThatUsesFlatMap() {
		AtomicInteger result = new AtomicInteger(0);

		detach(
				done(5)
						.flatMap(x -> done(x * 2))
						.flatMap(x -> done(x + 10))
						.map(x -> {
							result.set(x);
							return x;
						})
		).get();

		assertThat(result.get()).isEqualTo(20);
	}

	@Test
	public void shouldHandleNestedDetach() {
		List<String> events = new CopyOnWriteArrayList<>();

		detach(
				detach(done("inner").map(events::add))
						.flatMap(_0 -> done("outer").map(events::add))
		).get();

		assertThat(events).containsExactlyInAnyOrder("inner", "outer");
	}

	@Test
	public void shouldAllowChainingAfterDetach() {
		List<String> events = new CopyOnWriteArrayList<>();

		String result = detach(done("detached").map(events::add))
				.flatMap(_0 -> done("after-detach").map(events::add))
				.flatMap(_0 -> done("final"))
				.get();

		assertThat(result).isEqualTo("final");
		assertThat(events).containsExactlyInAnyOrder("detached", "after-detach");
	}

	@Test
	public void shouldDetachComplexFiberGraph() {
		AtomicInteger sum = new AtomicInteger(0);

		Nothing result = detach(
				done(1)
						.flatMap(a -> done(2)
								.flatMap(b -> done(3)
										.map(c -> {
											sum.set(a + b + c);
											return c;
										})))
		).get();

		assertThat(result).isEqualTo(Nothing.nothing());
		assertThat(sum.get()).isEqualTo(6);
	}

	@Test
	public void shouldHandleDetachOfDeferredComputation() {
		List<Integer> results = new CopyOnWriteArrayList<>();

		detach(
				defer(() -> defer(() -> defer(() -> done(42))))
						.map(results::add)
		).get();

		assertThat(results).containsExactly(42);
	}

	@Test
	public void shouldDetachWithSideEffects() {
		List<String> log = new CopyOnWriteArrayList<>();

		detach(
				done(1).map(x -> {
					log.add("step1");
					return x + 1;
				}).flatMap(x -> done(x * 2).map(y -> {
					log.add("step2");
					return y;
				}))
		).get();

		assertThat(log).containsExactly("step1", "step2");
	}

	@Test
	public void shouldExecuteDetachedFiberCompletely() {
		AtomicInteger counter = new AtomicInteger(0);

		detach(
				countdown(1000)
						.map(x -> counter.incrementAndGet())
		).get();

		assertThat(counter.get()).isEqualTo(1);
	}

	private Fiber<Integer> countdown(int n) {
		if (n <= 0) {
			return done(0);
		}
		return defer(() -> countdown(n - 1));
	}

	@Test
	public void shouldHandleMultipleDetachedFibersWithSharedState() {
		AtomicInteger sharedCounter = new AtomicInteger(0);

		Fiber<Nothing> program = detach(done(1).map(x -> sharedCounter.addAndGet(x)))
				.flatMap(_0 -> detach(done(2).map(x -> sharedCounter.addAndGet(x))))
				.flatMap(_0 -> detach(done(3).map(x -> sharedCounter.addAndGet(x))));

		program.get();

		assertThat(sharedCounter.get()).isEqualTo(6);
	}
}
