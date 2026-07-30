package com.tgac.functional.fibers.schedulers;

// ABOUTME: Fiber.await lifecycle tests: growth wakes with the grown value, sealed
// ABOUTME: carries the final value, the re-arm loop, census and endgame honesty.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.AwaitResult;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Scheduler;
import com.tgac.functional.fibers.interpreter.Channel;
import com.tgac.functional.fibers.interpreter.MaxInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class AwaitTest {

	@Test
	public void awaitOnASealedChannelCompletesImmediatelyWithTheFinalValue() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));

		AwaitResult<MaxInt> r = Fiber.produce(cell, emit -> emit.emit(MaxInt.of(3)))
				.flatMap(__ -> Fiber.sealed(cell.scope()))
				.flatMap(__ -> Fiber.await(cell, v -> v.value >= 1))
				.get();

		assertThat(r.getValue()).isEqualTo(MaxInt.of(3));
		assertThat(r.isSealed()).isTrue();
	}

	@Test
	public void growthWakesABlockedAwaiterWithTheGrownValue() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<Integer> seen = new ArrayList<>();

		Fiber<Nothing> consumer = Fiber.await(cell, v -> v.value >= 1)
				.flatMap(r -> {
					seen.add(r.getValue().value);
					return done(nothing());
				});
		Fiber.detach(consumer)
				.flatMap(__ -> Fiber.produce(cell, emit -> emit.emit(MaxInt.of(7))))
				.get();

		assertThat(seen).containsExactly(7);
	}

	@Test
	public void theReArmLoopCollectsEveryGrowthThenEndsAtSeal() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<String> log = new ArrayList<>();

		Fiber.detach(collectAbove(cell, 0, log))
				.flatMap(__ -> Fiber.produce(cell, emit ->
						emit.emit(MaxInt.of(1)).flatMap(___ -> emit.emit(MaxInt.of(2)))))
				.get();

		// rotation order between producer steps and the re-arm is scheduler
		// policy; the invariants are not: nothing dropped, nothing doubled,
		// the loop ends at the sealed completion carrying the final value
		assertThat(log.get(0)).isEqualTo("more@1");
		assertThat(log.get(log.size() - 1)).isEqualTo("sealed@2");
		assertThat(log).doesNotHaveDuplicates();
	}

	/** await → consume → re-arm: the run-once loop, sequenced by flatMap. */
	private static Fiber<Nothing> collectAbove(Channel<MaxInt> cell, int cursor, List<String> log) {
		return Fiber.await(cell, v -> v.value > cursor)
				.flatMap(r -> {
					if (r.isSealed() && r.getValue().value <= cursor) {
						log.add("sealed@" + r.getValue().value);
						return done(nothing());
					}
					log.add((r.isSealed() ? "sealed@" : "more@") + r.getValue().value);
					return r.isSealed()
							? done(nothing())
							: Fiber.defer(() -> collectAbove(cell, r.getValue().value, log));
				});
	}

	@Test
	public void aSealCompletesABlockedAwaiterWithTheFinalValue() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<String> log = new ArrayList<>();

		Fiber.detach(Fiber.await(cell, v -> v.value >= 5).flatMap(r -> {
					log.add(r.isSealed() + "@" + r.getValue().value);
					return done(nothing());
				}))
				// 2 never satisfies the waiter - only the seal completes it
				.flatMap(__ -> Fiber.produce(cell, emit -> emit.emit(MaxInt.of(2))))
				.get();

		assertThat(log).containsExactly("true@2");
	}

	@Test
	public void aBlockedFrameDoesNotHoldItsCellOpenAndBothCellsSeal() {
		Channel<MaxInt> consumers = new Channel<>(MaxInt.of(0));
		Channel<MaxInt> producers = new Channel<>(MaxInt.of(0));
		List<Integer> seen = new ArrayList<>();

		Fiber<Nothing> consumer = Fiber.await(producers, v -> v.value >= 1)
				.flatMap(r -> {
					seen.add(r.getValue().value);
					return done(nothing());
				});
		Fiber<Nothing> producer = Fiber.produce(producers, emit -> emit.emit(MaxInt.of(4)));

		Fiber.claim(consumers.scope(), consumer)
				.flatMap(__ -> producer).get();

		assertThat(seen).containsExactly(4);
		assertThat(consumers.isSealed()).isTrue();
		assertThat(producers.isSealed()).isTrue();
	}

	@Test
	public void theSealCompletesAHeldFrameWithTheFinalValue() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<String> log = new ArrayList<>();

		// the consumer wants more than the master ever produces: only the
		// SEAL - the workforce quiescing - can complete it, with the final value
		Fiber<Nothing> consumer = Fiber.await(cell, v -> v.value >= 10)
				.flatMap(r -> {
					log.add(r.isSealed() + "@" + r.getValue().value);
					return done(nothing());
				});
		Fiber<Nothing> master = Fiber.produce(cell, emit -> emit.emit(MaxInt.of(4)));

		Fiber.detach(consumer)
				.flatMap(__ -> master).get();

		assertThat(log).containsExactly("true@4");
		assertThat(cell.isSealed()).isTrue();
	}

	@Test
	public void anEmitOnASealedCellRefusesLoudly() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));

		// a manual seal lands while the producer still runs: the late emit
		// must refuse - growing past a delivered sealed result would falsify it
		Fiber<Nothing> program = Fiber.produce(cell, emit -> Fiber.defer(() -> {
			cell.seal();
			return emit.emit(MaxInt.of(2));
		}));

		assertThatThrownBy(program::get)
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
			Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
			List<String> log = Collections.synchronizedList(new ArrayList<String>());
			Fiber<Nothing> program = Fiber.detach(collectAbove(cell, 0, log))
					.flatMap(__ -> Fiber.produce(cell, emit ->
							emit.emit(MaxInt.of(1)).flatMap(___ -> emit.emit(MaxInt.of(2)))));

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
			Channel<MaxInt> consumers = new Channel<>(MaxInt.of(0));
			Channel<MaxInt> producers = new Channel<>(MaxInt.of(0));
			List<Integer> seen = Collections.synchronizedList(new ArrayList<Integer>());

			Fiber<Nothing> program = Fiber.claim(consumers.scope(), Fiber.await(producers, v -> v.value >= 1)
							.flatMap(r -> {
								seen.add(r.getValue().value);
								return done(nothing());
							}))
					.flatMap(__ -> Fiber.produce(producers, emit -> emit.emit(MaxInt.of(4))));

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
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<String> order = new ArrayList<>();

		Fiber<Nothing> child = Fiber.await(cell, v -> v.value >= 1)
				.flatMap(r -> {
					order.add("child");
					return done(nothing());
				});
		Fiber<Nothing> program = Fiber.produce(cell, emit -> emit.emit(MaxInt.of(5)))
				.flatMap(__ -> Fiber.fork(Collections.singletonList(child)))
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
	public void aDriveWhoseRootCompletesWithAParkedFrameRefusesLoudly() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<Integer> seen = new ArrayList<>();

		// the root finishes while a detached consumer is still parked at a
		// channel nobody will ever grow: both drive endings - queue ran dry,
		// root completed - must consult the held registry; silence here would
		// abandon a deadlocked frame without a word
		Fiber<Nothing> program = Fiber.detach(Fiber.await(cell, v -> v.value >= 1)
						.flatMap(r -> {
							seen.add(r.getValue().value);
							return done(nothing());
						}))
				.flatMap(__ -> done(nothing()));

		assertThatThrownBy(program::get)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("blocked");
		assertThat(seen).isEmpty();
	}

	@Test
	public void aStrandNamesTheNamedChannel() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0), "answers");

		assertThatThrownBy(() -> Fiber.await(cell, v -> v.value >= 1).get())
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("answers");
	}

	@Test
	public void aDriveExhaustedWithALiveBlockedFrameRefusesLoudly() {
		// nobody ever claims this channel's workforce - it can never seal
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));

		Fiber<AwaitResult<MaxInt>> stranded = Fiber.await(cell, v -> v.value >= 1);

		assertThatThrownBy(stranded::get)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("blocked");
	}
}
