package com.tgac.functional.fibers.schedulers;

// ABOUTME: The chaos driver's pins: every seed yields the same solution set as
// ABOUTME: the fair drivers, and one seed replays its exact order every time.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.interpreter.Channel;
import com.tgac.functional.fibers.interpreter.MaxInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RandomizedSchedulerTest {

	/** A fork of forks, each leaf recording its value - order is the chaos. */
	private static Fiber<Nothing> leaves(List<Integer> sink) {
		return Fiber.fork(Arrays.asList(
				Fiber.fork(Arrays.asList(
						Fiber.defer(() -> done(sink.add(1))),
						Fiber.defer(() -> done(sink.add(2))))),
				Fiber.fork(Arrays.asList(
						Fiber.defer(() -> done(sink.add(3))),
						Fiber.defer(() -> done(sink.add(4)))))));
	}

	@Test
	public void everySeedDeliversTheSameSolutionSet() {
		for (long seed = 0; seed < 32; seed++) {
			List<Integer> seen = new ArrayList<>();
			RandomizedScheduler.of(leaves(seen), seed).get();
			assertThat(seen).containsExactlyInAnyOrder(1, 2, 3, 4);
		}
	}

	@Test
	public void aSeedReplaysItsExactOrder() {
		List<Integer> first = new ArrayList<>();
		List<Integer> second = new ArrayList<>();
		RandomizedScheduler.of(leaves(first), 42L).get();
		RandomizedScheduler.of(leaves(second), 42L).get();

		assertThat(second).containsExactlyElementsOf(first);
	}

	@Test
	public void chaosHonorsTheAwaitMachinery() {
		for (long seed = 0; seed < 16; seed++) {
			Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
			List<Integer> seen = new ArrayList<>();

			Fiber<Nothing> consumer = Fiber.await(cell, v -> v.value >= 1)
					.flatMap(r -> {
						seen.add(r.getValue().value);
						return done(nothing());
					});
			Fiber<Nothing> program = Fiber.detach(consumer)
					.flatMap(__ -> Fiber.produce(cell, emit -> emit.emit(MaxInt.of(7))));

			RandomizedScheduler.of(program, seed).get();

			assertThat(seen).containsExactly(7);
			assertThat(cell.isSealed()).isTrue();
		}
	}
}
