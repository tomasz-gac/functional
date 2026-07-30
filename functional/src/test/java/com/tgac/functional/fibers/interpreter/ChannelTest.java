package com.tgac.functional.fibers.interpreter;

// ABOUTME: Pins the channel's contract: strict growth swaps and wakes satisfied
// ABOUTME: waiters exactly once, an absorbed delta changes nothing, seal refuses growth.

import static com.tgac.functional.category.Nothing.nothing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.fibers.Await;
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
		final List<Await.Result<?>> completions;
		final ResumeHandle handle;

		Probe(List<Await.Result<?>> completions) {
			this.completions = completions;
			Frame frame = new Frame(Fiber.done(nothing()));
			this.handle = new ResumeHandle(frame, null, () ->
					completions.add((Await.Result<?>) ((Fiber.Done<?>) frame.computation).getValue()));
		}
	}

	private static ResumeHandle recording(List<Await.Result<?>> completions) {
		return new Probe(completions).handle;
	}

	@Test
	public void growSwapsTheValueAndWakesSatisfiedWaiters() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<Await.Result<?>> completions = new ArrayList<>();
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
	public void anAbsorbedDeltaChangesNothingAndWakesNobody() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(7));
		List<Await.Result<?>> completions = new ArrayList<>();
		cell.suspend(v -> v.value > 7, recording(completions));

		// 3 ⊑ 7 — the delta contributes nothing, growth refuses
		cell.grow(MaxInt.of(3));
		cell.grow(MaxInt.of(7));
		assertThat(cell.read()).isEqualTo(MaxInt.of(7));
		assertThat(completions).isEmpty();
	}

	@Test
	public void suspendAnswersImmediatelyWhenNoLongerCaughtUp() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		cell.grow(MaxInt.of(1));

		// the waiter believes the value is still 0 — the completion arrives
		// at once, possibly synchronously: an await always yields
		List<Await.Result<?>> completions = new ArrayList<>();
		cell.suspend(v -> v.value > 0, recording(completions));
		assertThat(completions).hasSize(1);
		assertThat(completions.get(0).getValue()).isEqualTo(MaxInt.of(1));
	}

	@Test
	public void batchedGrowthsWakeAHeldWaiterExactlyOnce() {
		Channel<MaxInt> cell = new Channel<>(MaxInt.of(0));
		List<Await.Result<?>> completions = new ArrayList<>();
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
		cell.seal();
		assertThatThrownBy(() -> cell.grow(MaxInt.of(1)))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("sealed");
	}
}
