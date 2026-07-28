package com.tgac.functional.fibers.schedulers;

// ABOUTME: Pins the monotone cell's contract: strict growth swaps and wakes satisfied
// ABOUTME: waiters, an absorbed delta changes nothing, suspend races grow toward reading.

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.fibers.Await;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MonotoneCellTest {

	private static Await.Waiter<MaxInt> recording(List<Await.Result<MaxInt>> completions) {
		return completions::add;
	}

	@Test
	public void growSwapsTheValueAndWakesSatisfiedWaiters() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
		List<Await.Result<MaxInt>> completions = new ArrayList<>();
		assertThat(cell.suspend(v -> v.value > 0, recording(completions))).isNull();
		assertThat(cell.suspend(v -> v.value > 0, recording(completions))).isNull();

		cell.grow(MaxInt.of(1));

		assertThat(completions).hasSize(2);
		assertThat(completions.get(0).getValue()).isEqualTo(MaxInt.of(1));
		assertThat(completions.get(0).isSealed()).isFalse();
		assertThat(cell.read()).isEqualTo(MaxInt.of(1));
	}

	@Test
	public void anAbsorbedDeltaChangesNothingAndWakesNobody() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(7));
		List<Await.Result<MaxInt>> completions = new ArrayList<>();
		cell.suspend(v -> v.value > 7, recording(completions));

		// 3 ⊑ 7 — the delta contributes nothing, growth refuses
		cell.grow(MaxInt.of(3));
		cell.grow(MaxInt.of(7));
		assertThat(cell.read()).isEqualTo(MaxInt.of(7));
		assertThat(completions).isEmpty();
	}

	@Test
	public void suspendAnswersImmediatelyWhenNoLongerCaughtUp() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
		cell.grow(MaxInt.of(1));

		// the waiter believes the value is still 0 — it must keep reading
		Await.Result<MaxInt> immediate = cell.suspend(v -> v.value > 0, recording(new ArrayList<>()));
		assertThat(immediate).isNotNull();
		assertThat(immediate.getValue()).isEqualTo(MaxInt.of(1));
	}

	@Test
	public void growOnASealedCellRefuses() {
		MonotoneCell<MaxInt> cell = new MonotoneCell<>(MaxInt.of(0));
		cell.seal();
		assertThatThrownBy(() -> cell.grow(MaxInt.of(1)))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("sealed");
	}
}
