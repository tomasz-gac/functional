package com.tgac.functional.fibers.schedulers;

// ABOUTME: Fiber.await lifecycle tests: immediate results, growth wakes with the
// ABOUTME: grown value, sealed carries the final value, census and endgame honesty.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Scheduler;
import com.tgac.functional.fibers.Source;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

public class AwaitTest {

	/**
	 * A monotone integer source: the value only rises, growth completes the
	 * held waiters it satisfies, seal completes the rest with the final value.
	 */
	private static final class IntSource implements Source<Integer> {
		private int value = 0;
		private boolean sealed = false;
		private final List<Object[]> held = new ArrayList<>();

		@Override
		@SuppressWarnings("unchecked")
		public void suspend(Predicate<Integer> ready, Await.Waiter<Integer> waiter) {
			Await.Result<Integer> immediate;
			synchronized (this) {
				if (sealed) {
					immediate = Await.Result.sealed(value);
				} else if (ready.test(value)) {
					immediate = Await.Result.more(value);
				} else {
					held.add(new Object[] { ready, waiter });
					return;
				}
			}
			waiter.complete(immediate);
		}

		@SuppressWarnings("unchecked")
		synchronized void grow(int v) {
			value = Math.max(value, v);
			List<Object[]> woken = new ArrayList<>();
			for (Object[] h : held) {
				if (((Predicate<Integer>) h[0]).test(value)) {
					woken.add(h);
				}
			}
			held.removeAll(woken);
			for (Object[] h : woken) {
				((Await.Waiter<Integer>) h[1]).complete(Await.Result.more(value));
			}
		}

		@SuppressWarnings("unchecked")
		synchronized void seal() {
			sealed = true;
			List<Object[]> rest = new ArrayList<>(held);
			held.clear();
			for (Object[] h : rest) {
				((Await.Waiter<Integer>) h[1]).complete(Await.Result.sealed(value));
			}
		}
	}

	@Test
	public void aReadyValueCompletesTheAwaitWithoutSuspending() {
		IntSource source = new IntSource();
		source.grow(3);

		Await.Result<Integer> r = Fiber.await(source, v -> v >= 1).get();

		assertThat(r.getValue()).isEqualTo(3);
		assertThat(r.isSealed()).isFalse();
		assertThat(source.held).isEmpty();
	}

	@Test
	public void growthWakesABlockedAwaiterWithTheGrownValue() {
		IntSource source = new IntSource();
		List<Integer> seen = new ArrayList<>();

		Fiber<Nothing> consumer = Fiber.await(source, v -> v >= 1)
				.flatMap(r -> {
					seen.add(r.getValue());
					return done(nothing());
				});
		Fiber.detach(consumer)
				.flatMap(__ -> Fiber.defer(() -> {
					source.grow(7);
					return done(nothing());
				})).get();

		assertThat(seen).containsExactly(7);
	}

	@Test
	public void theReArmLoopCollectsEveryGrowthThenEndsAtSeal() {
		IntSource source = new IntSource();
		List<String> log = new ArrayList<>();

		Fiber.detach(collectAbove(source, 0, log))
				.flatMap(__ -> Fiber.defer(() -> {
					source.grow(1);
					return done(nothing());
				}))
				.flatMap(__ -> Fiber.defer(() -> {
					source.grow(2);
					return done(nothing());
				}))
				.flatMap(__ -> Fiber.defer(() -> {
					source.seal();
					return done(nothing());
				})).get();

		// rotation order between producer steps and the re-arm is scheduler
		// policy; the invariants are not: nothing dropped, nothing doubled,
		// the loop ends at the sealed completion carrying the final value
		assertThat(log.get(0)).isEqualTo("more@1");
		assertThat(log.get(log.size() - 1)).isEqualTo("sealed@2");
		assertThat(log).doesNotHaveDuplicates();
	}

	/** await → consume → re-arm: the run-once loop, sequenced by flatMap. */
	private static Fiber<Nothing> collectAbove(IntSource source, int cursor, List<String> log) {
		return Fiber.await(source, v -> v > cursor)
				.flatMap(r -> {
					if (r.isSealed() && r.getValue() <= cursor) {
						log.add("sealed@" + r.getValue());
						return done(nothing());
					}
					log.add((r.isSealed() ? "sealed@" : "more@") + r.getValue());
					return r.isSealed()
							? done(nothing())
							: Fiber.defer(() -> collectAbove(source, r.getValue(), log));
				});
	}

	@Test
	public void aSealCompletesABlockedAwaiterWithTheFinalValue() {
		IntSource source = new IntSource();
		List<String> log = new ArrayList<>();

		Fiber.detach(Fiber.await(source, v -> v >= 5).flatMap(r -> {
					log.add(r.isSealed() + "@" + r.getValue());
					return done(nothing());
				}))
				.flatMap(__ -> Fiber.defer(() -> {
					source.grow(2); // not enough to satisfy the waiter
					source.seal();
					return done(nothing());
				})).get();

		assertThat(log).containsExactly("true@2");
	}

	@Test
	public void growthsLandingBeforeTheConsumerRestepsAreEachDeliveredExactlyOnce() {
		IntSource source = new IntSource();
		List<String> log = new ArrayList<>();

		// both growths and the seal land in one producer step, before the woken
		// consumer is re-driven: the wake carries 1, and the re-arm meets the
		// seal, whose result carries the FINAL value - the slipped-in 2 arrives
		// inside the sealed completion, never dropped, never doubled
		Fiber.detach(collectAbove(source, 0, log))
				.flatMap(__ -> Fiber.defer(() -> {
					source.grow(1);
					source.grow(2);
					source.seal();
					return done(nothing());
				})).get();

		assertThat(log).containsExactly("more@1", "sealed@2");
	}

	@Test
	public void aBlockedFrameDoesNotHoldItsCellOpenAndBothCellsSeal() {
		MonotoneCell<MaxInt> consumers = new MonotoneCell<>(MaxInt.of(0));
		MonotoneCell<MaxInt> producers = new MonotoneCell<>(MaxInt.of(0));
		List<Integer> seen = new ArrayList<>();

		Fiber<Nothing> consumer = Fiber.await(producers, v -> v.value >= 1)
				.flatMap(r -> {
					seen.add(r.getValue().value);
					return done(nothing());
				});
		Fiber<Nothing> producer = Fiber.defer(() -> {
			producers.grow(MaxInt.of(4));
			return done(nothing());
		});

		Fiber.detachTo(consumers, consumer)
				.flatMap(__ -> Fiber.detachTo(producers, producer)).get();

		assertThat(seen).containsExactly(4);
		assertThat(consumers.isSealed()).isTrue();
		assertThat(producers.isSealed()).isTrue();
	}

	@Test
	public void theSealCompletesAHeldFrameWithTheFinalValue() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
		List<String> log = new ArrayList<>();

		// the consumer wants more than the master ever produces: only the
		// SEAL - the workforce quiescing - can complete it, with the final value
		Fiber<Nothing> consumer = Fiber.await(cell, v -> v.value >= 10)
				.flatMap(r -> {
					log.add(r.isSealed() + "@" + r.getValue().value);
					return done(nothing());
				});
		Fiber<Nothing> master = Fiber.defer(() -> {
			cell.grow(MaxInt.of(4));
			return done(nothing());
		});

		Fiber.detach(consumer)
				.flatMap(__ -> Fiber.detachTo(cell, master)).get();

		assertThat(log).containsExactly("true@4");
		assertThat(cell.isSealed()).isTrue();
	}

	@Test
	public void growOnASealedCellRefusesLoudly() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
		Fiber.detachTo(cell, Fiber.defer(() -> {
			cell.grow(MaxInt.of(1));
			return done(nothing());
		})).get();

		assertThat(cell.isSealed()).isTrue();
		assertThatThrownBy(() -> cell.grow(MaxInt.of(2)))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("sealed");
	}

	@Test
	public void everySchedulerDrivesTheReArmLoopToTheSealedEnd() {
		List<Function<Fiber<Nothing>, Scheduler<Nothing>>> drivers = Arrays.asList(
				BreadthFirstScheduler::new,
				DepthFirstScheduler::of,
				RoundRobin::of,
				UnfairBreadthFirstScheduler::of,
				ForkJoinScheduler::new);
		for (Function<Fiber<Nothing>, Scheduler<Nothing>> driver : drivers) {
			IntSource source = new IntSource();
			List<String> log = Collections.synchronizedList(new ArrayList<String>());
			Fiber<Nothing> program = Fiber.detach(collectAbove(source, 0, log))
					.flatMap(__ -> Fiber.defer(() -> {
						source.grow(1);
						return done(nothing());
					}))
					.flatMap(__ -> Fiber.defer(() -> {
						source.grow(2);
						return done(nothing());
					}))
					.flatMap(__ -> Fiber.defer(() -> {
						source.seal();
						return done(nothing());
					}));

			driver.apply(program).get();

			// scheduling order varies by driver; the invariants do not:
			// values are never duplicated, and the loop always ends at the
			// sealed completion carrying the final value
			assertThat(log).isNotEmpty();
			assertThat(log).doesNotHaveDuplicates();
			assertThat(log.get(log.size() - 1)).isEqualTo("sealed@2");
		}
	}

	@Test
	public void forkJoinBillsAwaitsRaceFree() {
		for (int round = 0; round < 20; round++) {
			MonotoneCell<MaxInt> consumers = new MonotoneCell<>(MaxInt.of(0));
			MonotoneCell<MaxInt> producers = new MonotoneCell<>(MaxInt.of(0));
			List<Integer> seen = Collections.synchronizedList(new ArrayList<Integer>());

			Fiber<Nothing> program = Fiber.detachTo(consumers, Fiber.await(producers, v -> v.value >= 1)
							.flatMap(r -> {
								seen.add(r.getValue().value);
								return done(nothing());
							}))
					.flatMap(__ -> Fiber.detachTo(producers, Fiber.defer(() -> {
						producers.grow(MaxInt.of(4));
						return done(nothing());
					})));

			try (ForkJoinScheduler<Nothing> engine = new ForkJoinScheduler<>(program)) {
				engine.get();
			}
			assertThat(seen).containsExactly(4);
			assertThat(consumers.isSealed()).isTrue();
			assertThat(producers.isSealed()).isTrue();
		}
	}

	@Test
	public void aForkContinuesWithoutWaitingForItsChildren() {
		IntSource source = new IntSource();
		source.grow(5); // the child's await answers immediately
		List<String> order = new ArrayList<>();

		Fiber<Nothing> child = Fiber.await(source, v -> v >= 1)
				.flatMap(r -> {
					order.add("child");
					return done(nothing());
				});
		Fiber<Nothing> program = Fiber.fork(
						Collections.singletonList(child))
				.flatMap(__ -> Fiber.defer(() -> {
					order.add("parent");
					return done(nothing());
				}));

		program.get();

		// fork is a control scatter: the parent continues at once, promising
		// nothing about the child - but the drive drains the child before
		// get() returns, so its effect is still here
		assertThat(order).containsExactly("parent", "child");
	}

	@Test
	public void aDriveExhaustedWithALiveBlockedFrameRefusesLoudly() {
		// a foreign Source has no workforce - it can never seal
		IntSource source = new IntSource();

		Fiber<Await.Result<Integer>> stranded = Fiber.await(source, v -> v >= 1);

		assertThatThrownBy(stranded::get)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("blocked");
	}
}
