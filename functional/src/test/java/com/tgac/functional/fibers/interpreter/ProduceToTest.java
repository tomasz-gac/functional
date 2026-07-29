package com.tgac.functional.fibers.interpreter;

// ABOUTME: The produceTo/emit contract: emits fold and wake consumers, abort is
// ABOUTME: silence, plants race deterministically, foreign emits refuse loudly.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Emitter;
import com.tgac.functional.fibers.Fiber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

public class ProduceToTest {

	@Test
	public void emitsFoldIntoTheCellAndTheSealDeliversTheFold() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
		List<Integer> seen = new ArrayList<>();

		Fiber.produceTo(cell, emit -> emit.emit(MaxInt.of(3)).flatMap(__ -> emit.emit(MaxInt.of(7))))
				.flatMap(__ -> Fiber.sealed(cell.scope()))
				.flatMap(__ -> {
					seen.add(cell.read().value);
					return done(nothing());
				})
				.get();

		assertThat(seen).containsExactly(7);
	}

	@Test
	public void anEmitWakesAParkedConsumerMidSearch() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
		List<Integer> log = new ArrayList<>();

		Fiber<Nothing> consumer = Fiber.await(cell, v -> v.value >= 3)
				.flatMap(r -> {
					log.add(r.getValue().value);
					return done(nothing());
				});
		Fiber.fork(Arrays.asList(
						consumer,
						Fiber.produceTo(cell, emit -> emit.emit(MaxInt.of(3)))))
				.flatMap(__ -> Fiber.sealed(cell.scope()))
				.get();

		assertThat(log).containsExactly(3);
	}

	@Test
	public void abortIsSilence() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));

		Fiber.produceTo(cell, emit -> done(nothing()))
				.flatMap(__ -> Fiber.sealed(cell.scope()))
				.get();

		assertThat(cell.read()).isEqualTo(MaxInt.of(0));
		assertThat(cell.isSealed()).isTrue();
	}

	@Test
	public void aSecondPlantThrowsAndTheTryFormLosesQuietly() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
		Fiber.produceTo(cell, emit -> done(nothing()));

		assertThatThrownBy(() -> Fiber.produceTo(cell, emit -> done(nothing())))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("already planted");
		assertThat(Fiber.tryProduceTo(cell, emit -> done(nothing())).isEmpty()).isTrue();
	}

	@Test
	public void aLeakedEmitterRefusesInAForeignWorkforce() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
		AtomicReference<Emitter<MaxInt>> leaked = new AtomicReference<>();

		Fiber<Nothing> program = Fiber.produceTo(cell, emit -> {
					leaked.set(emit);
					return done(nothing());
				})
				// the leaked emitter runs OUTSIDE cell's workforce - the
				// membership check at the emit step refuses
				.flatMap(__ -> leaked.get().emit(MaxInt.of(9)));

		assertThatThrownBy(program::get)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("foreign workforce");
	}
}
