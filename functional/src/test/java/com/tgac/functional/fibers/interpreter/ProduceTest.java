package com.tgac.functional.fibers.interpreter;

// ABOUTME: The produce/emit contract: emits fold and wake consumers, abort is
// ABOUTME: silence, claims race deterministically, foreign emits refuse loudly.

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

public class ProduceTest {

	@Test
	public void emitsFoldIntoTheCellAndTheSealDeliversTheFold() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<Integer> seen = new ArrayList<>();

		Fiber.produce(cell, emit -> emit.emit(MaxInt.of(3)).flatMap(__ -> emit.emit(MaxInt.of(7))))
				.flatMap(__ -> Fiber.sealed(cell.scope()))
				.flatMap(__ -> {
					seen.add(cell.read().value);
					return done(nothing());
				})
				.ground();

		assertThat(seen).containsExactly(7);
	}

	@Test
	public void anEmitWakesAParkedConsumerMidSearch() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<Integer> log = new ArrayList<>();

		Fiber<Nothing> consumer = Fiber.await(cell, v -> v.value >= 3)
				.flatMap(r -> {
					log.add(r.getValue().value);
					return done(nothing());
				});
		Fiber.fork(Arrays.asList(
						consumer,
						Fiber.produce(cell, emit -> emit.emit(MaxInt.of(3)))))
				.flatMap(__ -> Fiber.sealed(cell.scope()))
				.ground();

		assertThat(log).containsExactly(3);
	}

	@Test
	public void abortIsSilence() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));

		Fiber.produce(cell, emit -> done(nothing()))
				.flatMap(__ -> Fiber.sealed(cell.scope()))
				.ground();

		assertThat(cell.read()).isEqualTo(MaxInt.of(0));
		assertThat(cell.isSealed()).isTrue();
	}

	@Test
	public void racingClaimantsResolveAtTheStepAndLosersNoOp() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<String> ran = new ArrayList<>();

		// constructing a produce claims nothing - the CAS runs at the step, so
		// racing callers are welcome and only the first spawn builds a body
		Fiber<Nothing> first = Fiber.produce(cell, emit -> {
			ran.add("first");
			return done(nothing());
		});
		Fiber<Nothing> second = Fiber.produce(cell, emit -> {
			ran.add("second");
			return done(nothing());
		});
		first.ground();
		second.ground();
		// a RE-STEPPED produce fiber is a loser too: one claim, one spawn
		first.ground();

		assertThat(ran).containsExactly("first");
	}

	@Test
	public void aLosingProducerRunsItsAlternative() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<String> ran = new ArrayList<>();

		Fiber.produce(cell, emit -> {
					ran.add("master");
					return done(nothing());
				})
				.flatMap(__ -> Fiber.produceOrElse(cell, emit -> {
					ran.add("second master");
					return done(nothing());
				}, Fiber.defer(() -> {
					ran.add("reader");
					return done(nothing());
				})))
				.ground();

		assertThat(ran).containsExactly("master", "reader");
	}

	@Test
	public void aLeakedEmitterRefusesInAForeignWorkforce() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		AtomicReference<Emitter<MaxInt>> leaked = new AtomicReference<>();

		Fiber<Nothing> program = Fiber.produce(cell, emit -> {
					leaked.set(emit);
					return done(nothing());
				})
				// the leaked emitter runs OUTSIDE cell's workforce - the
				// membership check at the emit step refuses
				.flatMap(__ -> leaked.get().emit(MaxInt.of(9)));

		assertThatThrownBy(program::ground)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("foreign workforce");
	}
}
