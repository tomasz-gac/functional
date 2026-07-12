package com.tgac.functional.algebra;

// ABOUTME: Pins the synchronous checked monotone drain: FIFO order, ⊥ short-circuit,
// ABOUTME: early stop, and the two contraction laws enforced per step.

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class MonotoneDrainTest {

	@Test
	public void drainsToTheMeetOfAllWork() {
		Lattices.Mask result = MonotoneDrain.drain(Lattices.Mask.of(0b1111L),
				Arrays.asList(Lattices.Mask.of(0b0111L), Lattices.Mask.of(0b0011L)),
				(state, w) -> MonotoneDrain.Step.proceed(state.meet(w)));
		assertThat(result).isEqualTo(Lattices.Mask.of(0b0011L));
	}

	@Test
	public void discoveredWorkIsAppended() {
		Lattices.Mask result = MonotoneDrain.drain(Lattices.Mask.of(0b1111L),
				Collections.singletonList(Lattices.Mask.of(0b0111L)),
				(state, w) -> {
					Lattices.Mask narrowed = state.meet(w);
					if (w.equals(Lattices.Mask.of(0b0111L))) {
						return MonotoneDrain.Step.proceed(narrowed,
								Collections.singletonList(Lattices.Mask.of(0b0011L)));
					}
					return MonotoneDrain.Step.proceed(narrowed);
				});
		assertThat(result).isEqualTo(Lattices.Mask.of(0b0011L));
	}

	@Test
	public void bottomShortCircuitsTheRemainingWork() {
		AtomicInteger processed = new AtomicInteger();
		Lattices.Mask result = MonotoneDrain.drain(Lattices.Mask.of(0b0011L),
				Arrays.asList(Lattices.Mask.of(0b0100L), Lattices.Mask.of(0b0010L)),
				(state, w) -> {
					processed.incrementAndGet();
					return MonotoneDrain.Step.proceed(state.meet(w));
				});
		assertThat(result.isBottom()).isTrue();
		assertThat(processed.get()).isEqualTo(1);
	}

	@Test
	public void stopEndsTheDrainEarly() {
		AtomicInteger processed = new AtomicInteger();
		Lattices.Mask result = MonotoneDrain.drain(Lattices.Mask.of(0b1111L),
				Arrays.asList(Lattices.Mask.of(0b0111L), Lattices.Mask.of(0b0011L)),
				(state, w) -> {
					processed.incrementAndGet();
					return MonotoneDrain.Step.stop(state.meet(w));
				});
		assertThat(result).isEqualTo(Lattices.Mask.of(0b0111L));
		assertThat(processed.get()).isEqualTo(1);
	}

	@Test
	public void rejectsExpansion() {
		assertThatThrownBy(() -> MonotoneDrain.drain(Lattices.Mask.of(0b0001L),
				Collections.singletonList(Lattices.Mask.of(0b0110L)),
				(state, w) -> MonotoneDrain.Step.proceed(state.join(w))))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("contract");
	}

	@Test
	public void unsafeDrainSkipsTheChecksButKeepsTheBottomShortCircuit() {
		// the same expanding step the checked drain rejects: tolerated here
		Lattices.Mask expanded = MonotoneDrain.drainUnsafe(Lattices.Mask.of(0b0001L),
				Collections.singletonList(Lattices.Mask.of(0b0110L)),
				(state, w) -> MonotoneDrain.Step.proceed(state.join(w)));
		assertThat(expanded).isEqualTo(Lattices.Mask.of(0b0111L));

		AtomicInteger processed = new AtomicInteger();
		Lattices.Mask dead = MonotoneDrain.drainUnsafe(Lattices.Mask.of(0b0011L),
				Arrays.asList(Lattices.Mask.of(0b0100L), Lattices.Mask.of(0b0010L)),
				(state, w) -> {
					processed.incrementAndGet();
					return MonotoneDrain.Step.proceed(state.meet(w));
				});
		assertThat(dead.isBottom()).isTrue();
		assertThat(processed.get()).isEqualTo(1);
	}

	@Test
	public void rejectsWorkEmittedWithoutStrictDescent() {
		assertThatThrownBy(() -> MonotoneDrain.drain(Lattices.Mask.of(0b0011L),
				Collections.singletonList(Lattices.Mask.of(0b1111L)),
				(state, w) -> MonotoneDrain.Step.proceed(state.meet(w),
						Collections.singletonList(Lattices.Mask.of(0b0001L)))))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("strict descent");
	}
}
