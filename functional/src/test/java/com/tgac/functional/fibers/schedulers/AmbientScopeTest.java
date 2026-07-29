package com.tgac.functional.fibers.schedulers;

// ABOUTME: Ambient billing: detached work seals its cell with no manual recording —
// ABOUTME: forks inherit, detachTo re-parents, and the parallel scheduler is race-free.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class AmbientScopeTest {

	@Test
	public void detachedWorkSealsWithNoManualRecording() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
		List<String> events = new ArrayList<>();

		Fiber<Nothing> work = Fiber.defer(() -> done(nothing()))
				.flatMap(__ -> {
					events.add("worked");
					return done(nothing());
				});

		Fiber.detachTo(cell, work).get();

		assertThat(events).containsExactly("worked");
		assertThat(cell.isSealed()).isTrue();
	}

	@Test
	public void forkedChildrenInheritTheAmbientScopeAndGateTheSeal() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
		AtomicInteger childrenRun = new AtomicInteger();

		Fiber<Nothing> work = Fiber.fork(Arrays.asList(
						Fiber.defer(() -> done(childrenRun.incrementAndGet())),
						Fiber.defer(() -> done(childrenRun.incrementAndGet())),
						Fiber.defer(() -> done(childrenRun.incrementAndGet()))));

		Fiber.detachTo(cell, work).get();

		// the seal fired, and only after every forked child completed
		assertThat(cell.isSealed()).isTrue();
		assertThat(childrenRun.get()).isEqualTo(3);
	}

	@Test
	public void detachToReParentsAcrossCells() {
		MonotoneCell<MaxInt> caller = new MonotoneCell<>(MaxInt.of(0));
		MonotoneCell<MaxInt> entry = new MonotoneCell<>(MaxInt.of(0));

		// the caller detaches a "master" into the entry's cell and finishes at
		// once; the entry seals only when the master's work completes
		AtomicInteger masterSteps = new AtomicInteger();
		Fiber<Nothing> master = Fiber.defer(() -> done(masterSteps.incrementAndGet()))
				.flatMap(__ -> Fiber.defer(() -> done(nothing())));

		Fiber.detachTo(caller, Fiber.detachTo(entry, master)).get();

		assertThat(caller.isSealed()).isTrue();
		assertThat(entry.isSealed()).isTrue();
		assertThat(masterSteps.get()).isEqualTo(1);
	}

	@Test
	public void parallelSchedulerRecordsAmbientlyRaceFree() {
		for (int round = 0; round < 20; round++) {
			MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
			AtomicInteger sum = new AtomicInteger();
			List<Fiber<Integer>> tasks = new ArrayList<>();
			for (int i = 0; i < 32; i++) {
				tasks.add(Fiber.defer(() -> done(sum.incrementAndGet())));
			}
			Fiber<Nothing> work = Fiber.detachTo(cell,
					Fiber.fork(tasks));

			try (ForkJoinScheduler<Nothing> engine = new ForkJoinScheduler<>(work)) {
				engine.get();
			}
			assertThat(cell.isSealed()).isTrue();
			assertThat(sum.get()).isEqualTo(32);
		}
	}
}
