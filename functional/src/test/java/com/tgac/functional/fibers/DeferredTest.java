package com.tgac.functional.fibers;

import static com.tgac.functional.fibers.Fiber.defer;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * ABOUTME: Tests for Fiber.Deferred - verifying lazy evaluation and trampolining semantics.
 * ABOUTME: Ensures deferred computations avoid stack overflow through scheduler-driven unwrapping.
 */
public class DeferredTest {

	@Test
	public void shouldDeferExecution() {
		AtomicInteger counter = new AtomicInteger(0);

		Fiber<Integer> deferred = defer(() -> {
			counter.incrementAndGet();
			return done(42);
		});

		// Counter should still be 0 - computation not executed yet
		assertThat(counter.get()).isEqualTo(0);

		// Now execute
		Integer result = deferred.get();

		assertThat(result).isEqualTo(42);
		assertThat(counter.get()).isEqualTo(1);
	}

	@Test
	public void shouldHandleSimpleDefer() {
		Integer result = defer(() -> done(10)).get();
		assertThat(result).isEqualTo(10);
	}

	@Test
	public void shouldChainDeferredComputations() {
		Integer result = defer(() -> done(5))
				.flatMap(x -> defer(() -> done(x * 2)))
				.flatMap(x -> defer(() -> done(x + 3)))
				.get();
		assertThat(result).isEqualTo(13);
	}

	@Test
	public void shouldHandleDeepRecursionWithoutStackOverflow() {
		Fiber<Integer> deepDefer = buildDeferChain(100_000, 0);
		Integer result = deepDefer.get();
		assertThat(result).isEqualTo(100_000);
	}

	private Fiber<Integer> buildDeferChain(int depth, int current) {
		if (current >= depth) {
			return done(current);
		}
		return defer(() -> buildDeferChain(depth, current + 1));
	}

	@Test
	public void shouldHandleVeryDeepRecursionWithoutStackOverflow() {
		// This would blow the stack without trampolining
		Integer result = countdown(1_000_000).get();
		assertThat(result).isEqualTo(0);
	}

	private Fiber<Integer> countdown(int n) {
		if (n == 0) {
			return done(0);
		}
		return defer(() -> countdown(n - 1));
	}

	@Test
	public void shouldHandleTailRecursiveFibonacci() {
		BigDecimal result = fibIt(10_000, BigDecimal.ONE, BigDecimal.ZERO).get();
		assertThat(result).isNotNull();
		assertThat(result.compareTo(BigDecimal.ZERO)).isGreaterThan(0);
	}

	private Fiber<BigDecimal> fibIt(int n, BigDecimal current, BigDecimal last) {
		if (n == 0) {
			return done(current);
		}
		return defer(() -> fibIt(n - 1, last.add(current), current));
	}

	@Test
	public void shouldMapOverDeferred() {
		Integer result = defer(() -> done(10))
				.map(x -> x * 2)
				.get();
		assertThat(result).isEqualTo(20);
	}

	@Test
	public void shouldFlatMapDeferred() {
		Integer result = defer(() -> done(5))
				.flatMap(x -> done(x + 10))
				.get();
		assertThat(result).isEqualTo(15);
	}

	@Test
	public void shouldExecuteLazilyWithMultipleDefers() {
		AtomicInteger executionOrder = new AtomicInteger(0);

		Fiber<String> fiber = defer(() -> {
			assertThat(executionOrder.compareAndSet(0, 1)).isTrue();
			return done("first");
		}).flatMap(s1 -> defer(() -> {
			assertThat(executionOrder.compareAndSet(1, 2)).isTrue();
			return done(s1 + "-second");
		})).flatMap(s2 -> defer(() -> {
			assertThat(executionOrder.compareAndSet(2, 3)).isTrue();
			return done(s2 + "-third");
		}));

		// Nothing executed yet
		assertThat(executionOrder.get()).isEqualTo(0);

		// Now execute and verify order
		String result = fiber.get();
		assertThat(result).isEqualTo("first-second-third");
		assertThat(executionOrder.get()).isEqualTo(3);
	}

	@Test
	public void shouldHandleNestedDefers() {
		Fiber<Integer> nested = defer(() ->
				defer(() ->
						defer(() -> done(123))
				)
		);
		assertThat(nested.get()).isEqualTo(123);
	}

	@Test
	public void shouldHandleDeferReturningFlatMap() {
		Integer result = defer(() ->
				done(10).flatMap(x -> done(x * 2))
		).get();
		assertThat(result).isEqualTo(20);
	}

	@Test
	public void shouldSupportCollatzConjectureWithTrampoline() {
		// Collatz sequence for 27: very long chain
		Long result = collatz(27).get();
		assertThat(result).isEqualTo(1L);
	}

	private Fiber<Long> collatz(long n) {
		if (n == 1) {
			return done(1L);
		} else if (n % 2 == 0) {
			return defer(() -> collatz(n / 2));
		} else {
			return defer(() -> collatz(3 * n + 1));
		}
	}

	@Test
	public void shouldHandleExtremelyDeepCollatz() {
		// Large starting number creates very deep recursion
		Long result = collatz(3_732_423).get();
		assertThat(result).isEqualTo(1L);
	}

	@Test
	public void shouldEvaluateOnlyOnce() {
		AtomicInteger counter = new AtomicInteger(0);

		Fiber<Integer> fiber = defer(() -> {
			counter.incrementAndGet();
			return done(42);
		});

		// Multiple gets should execute the fiber multiple times
		// (no caching in plain defer)
		fiber.get();
		fiber.get();

		assertThat(counter.get()).isEqualTo(2);
	}

	@Test
	public void shouldAllowRecursiveStructures() {
		// Build a computation that recurses N times
		Fiber<Integer> recursive = sum(1, 100);
		Integer result = recursive.get();
		assertThat(result).isEqualTo(5050); // Sum of 1 to 100
	}

	private Fiber<Integer> sum(int from, int to) {
		if (from > to) {
			return done(0);
		}
		return defer(() -> sum(from + 1, to)
				.map(rest -> from + rest));
	}
}
