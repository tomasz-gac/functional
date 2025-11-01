package com.tgac.functional.fibers;

import static com.tgac.functional.fibers.Fiber.done;
import static com.tgac.functional.fibers.Fiber.defer;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.schedulers.BredthFirstScheduler;
import com.tgac.functional.fibers.schedulers.ExecutorServiceScheduler;
import com.tgac.functional.fibers.schedulers.RoundRobin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;

/**
 * ABOUTME: Tests for Fiber.Suspended functionality - verifying suspension and resumption semantics.
 * ABOUTME: Covers producer/consumer patterns and CompletableFuture integration with the fiber execution model.
 */
public class SuspendedTest {

	@Test
	public void testSuspendedProducerConsumer() throws Exception {
		// Simple producer/consumer test using Suspended
		List<Integer> consumed = new CopyOnWriteArrayList<>();
		List<CompletableFuture<Integer>> futures = new ArrayList<>();

		// Create 3 futures for producer to complete
		for (int i = 0; i < 3; i++) {
			futures.add(new CompletableFuture<>());
		}

		// Producer: completes futures with values 1, 2, 3
		Fiber<Nothing> producer = defer(() -> {
			futures.get(0).complete(1);
			return defer(() -> {
				futures.get(1).complete(2);
				return defer(() -> {
					futures.get(2).complete(3);
					return done(Nothing.nothing());
				});
			});
		});

		// Consumer: suspends waiting for each value
		Fiber<Nothing> consumer = Fiber.suspend(futures.get(0), val1 -> {
			consumed.add(val1);
			return Fiber.suspend(futures.get(1), val2 -> {
				consumed.add(val2);
				return Fiber.suspend(futures.get(2), val3 -> {
					consumed.add(val3);
					return done(Nothing.nothing());
				});
			});
		});

		// Run both concurrently
		Fiber<Nothing> program = Fiber.forEach(Arrays.asList(producer, consumer), r -> {});

		// Execute with SimpleEngine
		try (Scheduler<Nothing> scheduler = RoundRobin.of(program)) {
			scheduler.get();
		}

		// Verify consumer received all values
		assertThat(consumed).containsExactly(1, 2, 3);
	}

	@Test
	public void testSuspendedWithPreCompletedFuture() throws Exception {
		// Test that Suspended works when future is already completed
		CompletableFuture<String> future = CompletableFuture.completedFuture("hello");
		List<String> results = new CopyOnWriteArrayList<>();

		Fiber<Nothing> program = Fiber.suspend(future, value -> {
			results.add(value);
			return done(Nothing.nothing());
		});

		try (Scheduler<Nothing> scheduler = RoundRobin.of(program)) {
			scheduler.get();
		}

		assertThat(results).containsExactly("hello");
	}

	@Test
	public void testMultipleSuspendedWaiters() throws Exception {
		// Test multiple suspended computations waiting on same future
		CompletableFuture<Integer> sharedFuture = new CompletableFuture<>();
		List<Integer> results = new CopyOnWriteArrayList<>();

		Fiber<Nothing> waiter1 = Fiber.suspend(sharedFuture, val -> {
			results.add(val * 2);
			return done(Nothing.nothing());
		});

		Fiber<Nothing> waiter2 = Fiber.suspend(sharedFuture, val -> {
			results.add(val * 3);
			return done(Nothing.nothing());
		});

		Fiber<Nothing> completer = defer(() -> {
			sharedFuture.complete(10);
			return done(Nothing.nothing());
		});

		Fiber<Nothing> program = Fiber.forEach(Arrays.asList(waiter1, waiter2, completer), r -> {});

		try (Scheduler<Nothing> scheduler = RoundRobin.of(program)) {
			scheduler.get();
		}

		assertThat(results).containsExactlyInAnyOrder(20, 30);
	}

	@Test
	public void testAwaitableInterface() throws Exception {
		// Test Awaitable interface provides clean API
		List<String> results = new CopyOnWriteArrayList<>();
		List<CompletableFuture<String>> futures = new ArrayList<>();

		// Create an Awaitable that manages its own future
		Awaitable<String> awaitable1 = () -> {
			CompletableFuture<String> future = new CompletableFuture<>();
			futures.add(future);
			return future;
		};

		Awaitable<String> awaitable2 = () -> {
			CompletableFuture<String> future = new CompletableFuture<>();
			futures.add(future);
			return future;
		};

		// Use clean await() API instead of manual suspend()
		Fiber<Nothing> consumer = awaitable1.await(val1 -> {
			results.add(val1);
			return awaitable2.await(val2 -> {
				results.add(val2);
				return done(Nothing.nothing());
			});
		});

		Fiber<Nothing> producer = defer(() -> {
			futures.get(0).complete("first");
			return defer(() -> {
				futures.get(1).complete("second");
				return done(Nothing.nothing());
			});
		});

		Fiber<Nothing> program = Fiber.forEach(Arrays.asList(producer, consumer), r -> {});

		try (Scheduler<Nothing> scheduler = RoundRobin.of(program)) {
			scheduler.get();
		}

		assertThat(results).containsExactly("first", "second");
	}

	@Test
	public void testSuspendedWithBFSEngine() throws Exception {
		// Test Suspended with BFSEngine
		List<Integer> consumed = new CopyOnWriteArrayList<>();
		List<CompletableFuture<Integer>> futures = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			futures.add(new CompletableFuture<>());
		}

		Fiber<Nothing> producer = defer(() -> {
			futures.get(0).complete(1);
			return defer(() -> {
				futures.get(1).complete(2);
				return defer(() -> {
					futures.get(2).complete(3);
					return done(Nothing.nothing());
				});
			});
		});

		Fiber<Nothing> consumer = Fiber.suspend(futures.get(0), val1 -> {
			consumed.add(val1);
			return Fiber.suspend(futures.get(1), val2 -> {
				consumed.add(val2);
				return Fiber.suspend(futures.get(2), val3 -> {
					consumed.add(val3);
					return done(Nothing.nothing());
				});
			});
		});

		Fiber<Nothing> program = Fiber.forEach(Arrays.asList(producer, consumer), r -> {});

		try (Scheduler<Nothing> scheduler = new BredthFirstScheduler<>(program)) {
			scheduler.get();
		}

		assertThat(consumed).containsExactly(1, 2, 3);
	}

	@Test
	public void testSuspendedWithExecutorServiceEngine() throws Exception {
		// Test Suspended with ExecutorServiceEngine
		List<Integer> consumed = new CopyOnWriteArrayList<>();
		List<CompletableFuture<Integer>> futures = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			futures.add(new CompletableFuture<>());
		}

		Fiber<Nothing> producer = defer(() -> {
			futures.get(0).complete(1);
			return defer(() -> {
				futures.get(1).complete(2);
				return defer(() -> {
					futures.get(2).complete(3);
					return done(Nothing.nothing());
				});
			});
		});

		Fiber<Nothing> consumer = Fiber.suspend(futures.get(0), val1 -> {
			consumed.add(val1);
			return Fiber.suspend(futures.get(1), val2 -> {
				consumed.add(val2);
				return Fiber.suspend(futures.get(2), val3 -> {
					consumed.add(val3);
					return done(Nothing.nothing());
				});
			});
		});

		Fiber<Nothing> program = Fiber.forEach(Arrays.asList(producer, consumer), r -> {});

		try (Scheduler<Nothing> scheduler = new ExecutorServiceScheduler<>(program, new java.util.concurrent.ForkJoinPool())) {
			scheduler.get();
		}

		assertThat(consumed).containsExactly(1, 2, 3);
	}
}
