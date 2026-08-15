package com.tgac.functional.fibers.interpreter;

// ABOUTME: Pins the channel's contract: strict growth swaps and wakes satisfied
// ABOUTME: waiters exactly once, an absorbed delta changes nothing, seal refuses growth.

import com.tgac.functional.fibers.schedulers.BreadthFirstScheduler;
import static com.tgac.functional.category.Nothing.nothing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.fibers.AwaitResult;
import com.tgac.functional.fibers.Fiber;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ChannelTest {

	/**
	 * A parked waiter, observed through the real delivery path: the handle
	 * writes the Result into the frame, and the requeue hook records it.
	 */
	private static final class Probe {
		final List<AwaitResult<?>> completions;
		final ResumeHandle handle;

		Probe(List<AwaitResult<?>> completions) {
			this.completions = completions;
			Frame frame = new Frame(Fiber.done(nothing()));
			this.handle = new ResumeHandle(frame, null, () ->
					completions.add((AwaitResult<?>) ((Fiber.Done<?>) frame.computation).getValue()));
		}
	}

	private static ResumeHandle recording(List<AwaitResult<?>> completions) {
		return new Probe(completions).handle;
	}

	@Test
	public void growSwapsTheValueAndWakesSatisfiedWaiters() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<AwaitResult<?>> completions = new ArrayList<>();
		cell.suspend(v -> v.value > 0, recording(completions));
		cell.suspend(v -> v.value > 0, recording(completions));
		assertThat(completions).isEmpty();

		cell.grow(MaxInt.of(1));

		assertThat(completions).hasSize(2);
		assertThat(completions.get(0).getValue()).isEqualTo(MaxInt.of(1));
		assertThat(completions.get(0).isSealed()).isFalse();
		assertThat(cell.read()).isEqualTo(MaxInt.of(1));
	}

	@Test
	public void anAbsorbedDeltaLeavesTheValueUnchanged() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(7));

		// 3 ⊑ 7 and 7 ⊑ 7 — both deltas are absorbed by the join, and grow
		// returns before the waiter scan on an absorbed delta, so no
		// suspension is ever woken by a value that did not move. Held
		// waiters cannot witness that skip (their predicates are
		// upward-closed), so the value is the only honest observable
		cell.grow(MaxInt.of(3));
		cell.grow(MaxInt.of(7));
		assertThat(cell.read()).isEqualTo(MaxInt.of(7));
	}

	@Test
	public void suspendAnswersImmediatelyWhenNoLongerCaughtUp() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		cell.grow(MaxInt.of(1));

		// the waiter believes the value is still 0 — the completion arrives
		// at once, possibly synchronously: an await always yields
		List<AwaitResult<?>> completions = new ArrayList<>();
		cell.suspend(v -> v.value > 0, recording(completions));
		assertThat(completions).hasSize(1);
		assertThat(completions.get(0).getValue()).isEqualTo(MaxInt.of(1));
	}

	@Test
	public void batchedGrowthsWakeAHeldWaiterExactlyOnce() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<AwaitResult<?>> completions = new ArrayList<>();
		cell.suspend(v -> v.value > 0, recording(completions));

		// the first satisfying growth completes and REMOVES the waiter; the
		// second lands in the value only — nothing dropped, nothing doubled
		cell.grow(MaxInt.of(1));
		cell.grow(MaxInt.of(2));

		assertThat(completions).hasSize(1);
		assertThat(completions.get(0).getValue()).isEqualTo(MaxInt.of(1));
		assertThat(cell.read()).isEqualTo(MaxInt.of(2));
	}

	@Test
	public void growOnASealedCellRefuses() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		// sealed honestly: the claimed workforce finishes without emitting
		new BreadthFirstScheduler<>(Fiber.produce(cell, emit -> Fiber.done(nothing()))).get();
		assertThatThrownBy(() -> cell.grow(MaxInt.of(1)))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("sealed");
	}
}
