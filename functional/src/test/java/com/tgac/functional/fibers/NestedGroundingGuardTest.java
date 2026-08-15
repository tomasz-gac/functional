package com.tgac.functional.fibers;

// ABOUTME: Fiber.get on a non-Done fiber inside a running engine is nested
// ABOUTME: grounding — it must throw loudly, never silently spin an inner engine.

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.fibers.schedulers.BreadthFirstScheduler;
import org.junit.jupiter.api.Test;

public class NestedGroundingGuardTest {

	@Test
	public void getOnNonDoneInsideARunningEngineThrows() {
		// the offender grounds a non-Done fiber from within the engine's
		// own drive — the silent-corruption class the guard exists for
		Fiber<Long> offender = Fiber.defer(() -> {
			Fiber.defer(() -> Fiber.done(1L)).get();
			return Fiber.done(2L);
		});

		assertThatThrownBy(() -> new BreadthFirstScheduler<>(offender).run(v -> {
		})).isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("nested");
	}

	@Test
	public void getOnDoneInsideARunningEngineIsFine() {
		// Done.get is a field read — safe anywhere, guard must not fire
		Fiber<Long> fine = Fiber.defer(() -> Fiber.done(Fiber.done(1L).get() + 1));
		java.util.concurrent.atomic.AtomicLong out = new java.util.concurrent.atomic.AtomicLong();
		new BreadthFirstScheduler<>(fine).run(out::set);
		assertThat(out.get()).isEqualTo(2L);
	}

	@Test
	public void getDoneIsTheLoudExtractor() {
		assertThat(Fiber.done(5L).getDone("test")).isEqualTo(5L);
		assertThatThrownBy(() -> Fiber.defer(() -> Fiber.done(1L)).getDone("test site"))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("test site");
	}

	@Test
	public void topLevelGetStillGrounds() {
		// outside any engine, get() is the legitimate entry — unchanged
		assertThat(Fiber.defer(() -> Fiber.done(7L)).get()).isEqualTo(7L);
	}
}
