package com.tgac.functional.fibers;

// ABOUTME: The value-extraction contract: getDone is the loud Done-asserted
// ABOUTME: extractor; anything non-Done grounds on a deliberately built scheduler.

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.fibers.schedulers.BreadthFirstScheduler;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

public class DoneExtractionTest {

	@Test
	public void getDoneIsTheLoudExtractor() {
		assertThat(Fiber.done(5L).getDone("test")).isEqualTo(5L);
		assertThatThrownBy(() -> Fiber.defer(() -> Fiber.done(1L)).getDone("test site"))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("test site");
	}

	@Test
	public void getDoneInsideARunningEngineIsFine() {
		// Done extraction is a field read — safe anywhere
		Fiber<Long> fine = Fiber.defer(() -> Fiber.done(Fiber.done(1L).getDone("inline") + 1));
		AtomicLong out = new AtomicLong();
		new BreadthFirstScheduler<>(fine).run(out::set);
		assertThat(out.get()).isEqualTo(2L);
	}

	@Test
	public void groundingIsADeliberatelyBuiltScheduler() {
		assertThat(new BreadthFirstScheduler<>(Fiber.defer(() -> Fiber.done(7L))).get())
				.isEqualTo(7L);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void nestedGroundRefusesLoudly() {
		// ground-in-ground stacks engines on the JVM stack — the recursion
		// hazard; one ground per thread at a time
		Fiber<Integer> inner = Fiber.defer(() -> Fiber.done(1));
		Fiber<Integer> outer = Fiber.defer(() -> Fiber.done(inner.ground() + 1));
		assertThatThrownBy(outer::ground)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("nested ground");
	}

	@Test
	@SuppressWarnings("deprecation")
	public void sequentialGroundsAreFine() {
		assertThat(Fiber.defer(() -> Fiber.done(1)).ground()).isEqualTo(1);
		assertThat(Fiber.defer(() -> Fiber.done(2)).ground()).isEqualTo(2);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void groundInsideARunningEngineStaysSanctioned() {
		// the contract bars ground-in-ground, not ground-in-engine — the
		// sanctioned door stays open one level deep
		AtomicLong out = new AtomicLong();
		Fiber<Long> f = Fiber.defer(() ->
				Fiber.done(Fiber.defer(() -> Fiber.done(41L)).ground() + 1));
		new BreadthFirstScheduler<>(f).run(out::set);
		assertThat(out.get()).isEqualTo(42L);
	}
}
