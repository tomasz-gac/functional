package com.tgac.functional.fibers.primitives;

// ABOUTME: Pins the monotone cell's contract: strict growth swaps and drains all
// ABOUTME: parked, an absorbed delta changes nothing, park races grow toward reading.

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

public class MonotoneCellTest {

	@Test
	public void growSwapsTheValueAndDrainsAllParked() {
		MonotoneCell<MaxInt, String> cell = new MonotoneCell<>(MaxInt.of(0));
		assertThat(cell.park("a", v -> v.value == 0)).isTrue();
		assertThat(cell.park("b", v -> v.value == 0)).isTrue();

		Option<List<String>> drained = cell.grow(MaxInt.of(1));

		assertThat(drained.isDefined()).isTrue();
		assertThat(drained.get()).containsExactly("a", "b");
		assertThat(cell.read()).isEqualTo(MaxInt.of(1));
		assertThat(cell.parkedCount()).isEqualTo(0);
	}

	@Test
	public void anAbsorbedDeltaChangesNothingAndWakesNobody() {
		MonotoneCell<MaxInt, String> cell = new MonotoneCell<>(MaxInt.of(7));
		cell.park("sleeper", v -> true);

		// 3 ⊑ 7 — the delta contributes nothing, growth refuses
		assertThat(cell.grow(MaxInt.of(3)).isDefined()).isFalse();
		assertThat(cell.grow(MaxInt.of(7)).isDefined()).isFalse();
		assertThat(cell.read()).isEqualTo(MaxInt.of(7));
		assertThat(cell.parkedCount()).isEqualTo(1);
	}

	@Test
	public void parkRefusesWhenNoLongerCaughtUp() {
		MonotoneCell<MaxInt, String> cell = new MonotoneCell<>(MaxInt.of(0));
		cell.grow(MaxInt.of(1));

		// the subscriber believes the value is still 0 — it must keep reading
		assertThat(cell.park("stale", v -> v.value == 0)).isFalse();
		assertThat(cell.parkedCount()).isEqualTo(0);
	}

	@Test
	public void drainParkedHarvestsEveryone() {
		MonotoneCell<MaxInt, String> cell = new MonotoneCell<>(MaxInt.of(0));
		cell.park("a", v -> true);
		cell.park("b", v -> true);

		assertThat(cell.drainParked()).containsExactly("a", "b");
		assertThat(cell.parkedCount()).isEqualTo(0);
		assertThat(cell.drainParked()).isEmpty();
	}
}
