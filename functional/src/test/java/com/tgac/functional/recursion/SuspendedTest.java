package com.tgac.functional.recursion;

import static com.tgac.functional.recursion.Recur.done;
import static com.tgac.functional.recursion.Recur.recur;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;

/**
 * ABOUTME: Tests for Recur.Suspended functionality - verifying suspension and resumption semantics.
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
		Recur<Nothing> producer = recur(() -> {
			futures.get(0).complete(1);
			return recur(() -> {
				futures.get(1).complete(2);
				return recur(() -> {
					futures.get(2).complete(3);
					return done(Nothing.nothing());
				});
			});
		});

		// Consumer: suspends waiting for each value
		Recur<Nothing> consumer = Recur.suspend(futures.get(0), val1 -> {
			consumed.add(val1);
			return Recur.suspend(futures.get(1), val2 -> {
				consumed.add(val2);
				return Recur.suspend(futures.get(2), val3 -> {
					consumed.add(val3);
					return done(Nothing.nothing());
				});
			});
		});

		// Run both concurrently
		Recur<Nothing> program = Recur.forEach(Arrays.asList(producer, consumer), r -> {});

		// Execute with SimpleEngine
		try (Engine<Nothing> engine = SimpleEngine.of(program)) {
			engine.get();
		}

		// Verify consumer received all values
		assertThat(consumed).containsExactly(1, 2, 3);
	}

	@Test
	public void testSuspendedWithPreCompletedFuture() throws Exception {
		// Test that Suspended works when future is already completed
		CompletableFuture<String> future = CompletableFuture.completedFuture("hello");
		List<String> results = new CopyOnWriteArrayList<>();

		Recur<Nothing> program = Recur.suspend(future, value -> {
			results.add(value);
			return done(Nothing.nothing());
		});

		try (Engine<Nothing> engine = SimpleEngine.of(program)) {
			engine.get();
		}

		assertThat(results).containsExactly("hello");
	}

	@Test
	public void testMultipleSuspendedWaiters() throws Exception {
		// Test multiple suspended computations waiting on same future
		CompletableFuture<Integer> sharedFuture = new CompletableFuture<>();
		List<Integer> results = new CopyOnWriteArrayList<>();

		Recur<Nothing> waiter1 = Recur.suspend(sharedFuture, val -> {
			results.add(val * 2);
			return done(Nothing.nothing());
		});

		Recur<Nothing> waiter2 = Recur.suspend(sharedFuture, val -> {
			results.add(val * 3);
			return done(Nothing.nothing());
		});

		Recur<Nothing> completer = recur(() -> {
			sharedFuture.complete(10);
			return done(Nothing.nothing());
		});

		Recur<Nothing> program = Recur.forEach(Arrays.asList(waiter1, waiter2, completer), r -> {});

		try (Engine<Nothing> engine = SimpleEngine.of(program)) {
			engine.get();
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
		Recur<Nothing> consumer = awaitable1.await(val1 -> {
			results.add(val1);
			return awaitable2.await(val2 -> {
				results.add(val2);
				return done(Nothing.nothing());
			});
		});

		Recur<Nothing> producer = recur(() -> {
			futures.get(0).complete("first");
			return recur(() -> {
				futures.get(1).complete("second");
				return done(Nothing.nothing());
			});
		});

		Recur<Nothing> program = Recur.forEach(Arrays.asList(producer, consumer), r -> {});

		try (Engine<Nothing> engine = SimpleEngine.of(program)) {
			engine.get();
		}

		assertThat(results).containsExactly("first", "second");
	}
}
