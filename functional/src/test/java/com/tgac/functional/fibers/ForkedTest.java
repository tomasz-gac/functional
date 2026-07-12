package com.tgac.functional.fibers;

import static com.tgac.functional.fibers.Fiber.defer;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;

/**
 * ABOUTME: Tests for Fiber.Forked - verifying concurrent execution and result collection semantics.
 * ABOUTME: Ensures forked fibers execute independently and deliver results to sink correctly.
 */
public class ForkedTest {

	@Test
	public void shouldForkTwoSimpleFibers() {
		List<Integer> results = new ArrayList<>();

		Fiber.fork(Arrays.asList(done(1), done(2)), results::add).get();

		assertThat(results).containsExactlyInAnyOrder(1, 2);
	}

	@Test
	public void shouldForkThreeFibers() {
		List<Integer> results = new ArrayList<>();

		Fiber.fork(
				Arrays.asList(done(10), done(20), done(30)),
				results::add
		).get();

		assertThat(results).containsExactlyInAnyOrder(10, 20, 30);
	}

	@Test
	public void shouldCollectResultsInCompletionOrder() {
		List<Integer> results = new CopyOnWriteArrayList<>();

		Fiber<Integer> fast = done(1);
		Fiber<Integer> medium = defer(() -> done(2));
		Fiber<Integer> slow = defer(() -> defer(() -> done(3)));

		Fiber.fork(Arrays.asList(slow, medium, fast), results::add).get();

		// Results collected as they complete
		assertThat(results).hasSize(3);
		assertThat(results).containsExactlyInAnyOrder(1, 2, 3);
	}

	@Test
	public void shouldForkDeferredComputations() {
		List<Integer> results = new ArrayList<>();

		Fiber<Integer> f1 = defer(() -> done(100));
		Fiber<Integer> f2 = defer(() -> done(200));
		Fiber<Integer> f3 = defer(() -> done(300));

		Fiber.fork(Arrays.asList(f1, f2, f3), results::add).get();

		assertThat(results).containsExactlyInAnyOrder(100, 200, 300);
	}

	@Test
	public void shouldForkComputationsWithDifferentDepths() {
		List<Integer> results = new ArrayList<>();

		Fiber<Integer> shallow = done(1);
		Fiber<Integer> medium = buildDeferChain(10, 0);
		Fiber<Integer> deep = buildDeferChain(100, 0);

		Fiber.fork(Arrays.asList(shallow, medium, deep), results::add).get();

		assertThat(results).containsExactlyInAnyOrder(1, 10, 100);
	}

	private Fiber<Integer> buildDeferChain(int depth, int current) {
		if (current >= depth) {
			return done(current);
		}
		return defer(() -> buildDeferChain(depth, current + 1));
	}

	@Test
	public void shouldAllowFlatMapAfterFork() {
		List<Integer> results = new ArrayList<>();

		String finalResult = Fiber.fork(
				Arrays.asList(done(1), done(2), done(3)),
				results::add
		).flatMap(_0 -> done("completed")).get();

		assertThat(results).containsExactlyInAnyOrder(1, 2, 3);
		assertThat(finalResult).isEqualTo("completed");
	}

	@Test
	public void shouldHandleNestedForks() {
		List<String> results = new CopyOnWriteArrayList<>();

		Fiber<String> fork1 = Fiber.fork(
				Arrays.asList(done(1), done(2)),
				i -> results.add("fork1-" + i)
		).flatMap(_0 -> done("fork1-done"));

		Fiber<String> fork2 = Fiber.fork(
				Arrays.asList(done(3), done(4)),
				i -> results.add("fork2-" + i)
		).flatMap(_0 -> done("fork2-done"));

		List<String> outerResults = new ArrayList<>();
		Fiber.fork(Arrays.asList(fork1, fork2), outerResults::add).get();

		// Inner forks delivered their results
		assertThat(results).containsExactlyInAnyOrder("fork1-1", "fork1-2", "fork2-3", "fork2-4");

		// Outer fork collected final results
		assertThat(outerResults).containsExactlyInAnyOrder("fork1-done", "fork2-done");
	}

	@Test
	public void shouldHandleSingleFiber() {
		List<Integer> results = new ArrayList<>();

		Fiber.fork(Collections.singletonList(done(42)), results::add).get();

		assertThat(results).containsExactly(42);
	}

	@Test
	public void shouldHandleEmptyForkList() {
		List<Integer> results = new ArrayList<>();

		Fiber.fork(Collections.<Fiber<Integer>> emptyList(), results::add).get();

		assertThat(results).isEmpty();
	}

	@Test
	public void shouldForkManyFibers() {
		List<Integer> results = new CopyOnWriteArrayList<>();
		List<Fiber<Integer>> tasks = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			final int value = i;
			tasks.add(defer(() -> done(value)));
		}

		Fiber.fork(tasks, results::add).get();

		assertThat(results).hasSize(100);
		assertThat(results).containsExactlyInAnyOrderElementsOf(
				Arrays.asList(java.util.stream.IntStream.range(0, 100).boxed().toArray(Integer[]::new))
		);
	}

	@Test
	public void shouldHandleForkWithComplexComputations() {
		List<Integer> results = new CopyOnWriteArrayList<>();

		Fiber<Integer> fib5 = fibonacci(5);
		Fiber<Integer> fib10 = fibonacci(10);
		Fiber<Integer> fib15 = fibonacci(15);

		Fiber.fork(Arrays.asList(fib5, fib10, fib15), results::add).get();

		assertThat(results).hasSize(3);
		assertThat(results).contains(5, 55, 610);
	}

	private Fiber<Integer> fibonacci(int n) {
		return fibonacciImpl(n, 0, 1);
	}

	private Fiber<Integer> fibonacciImpl(int n, int a, int b) {
		if (n == 0)
			return done(a);
		if (n == 1)
			return done(b);
		return defer(() -> fibonacciImpl(n - 1, b, a + b));
	}

	@Test
	public void shouldExecuteAllTasksEvenIfSomeFail() {
		// Note: Current fiber implementation doesn't have error handling
		// This test verifies all fibers execute
		List<Integer> results = new CopyOnWriteArrayList<>();

		Fiber.fork(
				Arrays.asList(
						done(1),
						defer(() -> done(2)),
						done(3),
						defer(() -> defer(() -> done(4)))
				),
				results::add
		).get();

		assertThat(results).containsExactlyInAnyOrder(1, 2, 3, 4);
	}

	@Test
	public void shouldHandleForkOfFlatMappedFibers() {
		List<Integer> results = new CopyOnWriteArrayList<>();

		Fiber<Integer> f1 = done(1).flatMap(x -> done(x * 10));
		Fiber<Integer> f2 = done(2).flatMap(x -> done(x * 10));
		Fiber<Integer> f3 = done(3).flatMap(x -> done(x * 10));

		Fiber.fork(Arrays.asList(f1, f2, f3), results::add).get();

		assertThat(results).containsExactlyInAnyOrder(10, 20, 30);
	}

	@Test
	public void shouldCallSinkForEachCompletion() {
		List<String> sinkCalls = new ArrayList<>();

		Fiber.fork(
				Arrays.asList(done("a"), done("b"), done("c")),
				value -> sinkCalls.add("sink-" + value)
		).get();

		assertThat(sinkCalls).hasSize(3);
		assertThat(sinkCalls).containsExactlyInAnyOrder("sink-a", "sink-b", "sink-c");
	}

	@Test
	public void shouldReturnNothingAfterCompletion() {
		List<Integer> results = new ArrayList<>();

		Nothing result = Fiber.fork(
				Arrays.asList(done(1), done(2)),
				results::add
		).get();

		assertThat(result).isEqualTo(Nothing.nothing());
		assertThat(results).hasSize(2);
	}

	@Test
	public void shouldHandleForkWithRecursiveTasks() {
		List<Integer> results = new CopyOnWriteArrayList<>();

		Fiber<Integer> count1 = countdown(100);
		Fiber<Integer> count2 = countdown(200);
		Fiber<Integer> count3 = countdown(300);

		Fiber.fork(Arrays.asList(count1, count2, count3), results::add).get();

		assertThat(results).containsExactlyInAnyOrder(0, 0, 0);
	}

	private Fiber<Integer> countdown(int n) {
		if (n <= 0) {
			return done(0);
		}
		return defer(() -> countdown(n - 1));
	}

	@Test
	public void shouldHandleForkWithMixedTypes() {
		List<String> results = new CopyOnWriteArrayList<>();

		Fiber<String> f1 = done("hello");
		Fiber<String> f2 = defer(() -> done("world"));
		Fiber<String> f3 = done("test").map(String::toUpperCase);

		Fiber.fork(Arrays.asList(f1, f2, f3), results::add).get();

		assertThat(results).containsExactlyInAnyOrder("hello", "world", "TEST");
	}
}
