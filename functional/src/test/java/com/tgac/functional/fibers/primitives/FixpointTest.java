package com.tgac.functional.fibers.primitives;

// ABOUTME: Fixpoint lifecycle tests: growth feeds parked subscribers, parks refuse
// ABOUTME: past-moved values, and sleeper rings group-seal before any hook fires.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.fibers.Fiber;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class FixpointTest {

	@Test
	public void growthFeedsParkedSubscribersAbsorbedDeltasDoNot() {
		List<String> fed = new ArrayList<>();
		Fixpoint<MaxInt, String> f = new Fixpoint<>(MaxInt.of(0), s -> null,
				s -> {
					fed.add(s);
					return done(nothing());
				});

		assertThat(f.parkFrom(null, "sub", v -> v.value == 0).isDefined()).isTrue();
		f.grow(MaxInt.of(1)).get();
		assertThat(fed).containsExactly("sub");

		// absorbed delta: nobody is fed, nothing changes
		f.grow(MaxInt.of(1)).get();
		assertThat(fed).containsExactly("sub");
		assertThat(f.read()).isEqualTo(MaxInt.of(1));
	}

	@Test
	public void aParkRefusesWhenTheValueMovedPast() {
		Fixpoint<MaxInt, String> f = new Fixpoint<>(MaxInt.of(0), s -> null, s -> done(nothing()));
		f.grow(MaxInt.of(1)).get();

		// the subscriber believes the value is still 0 — keep reading instead
		assertThat(f.parkFrom(null, "stale", v -> v.value == 0).isDefined()).isFalse();
		assertThat(f.parkedCount()).isEqualTo(0);
	}

	/**
	 * A sleeper ring group-seals as one unit: at the moment any member's
	 * onSealed hook runs, EVERY member of the group must already read as
	 * sealed. This is what lets a mode's first-announced hook solve the whole
	 * closure instead of stashing work for the last announcement.
	 */
	@Test
	public void groupSealMarksEveryMemberBeforeAnyHookFires() {
		Map<String, Fixpoint<MaxInt, String>> owners = new HashMap<>();
		Function<String, Fixpoint<?, String>> ownerOf = owners::get;
		Fixpoint<MaxInt, String> a = new Fixpoint<>(MaxInt.of(0), ownerOf, s -> done(nothing()));
		Fixpoint<MaxInt, String> b = new Fixpoint<>(MaxInt.of(0), ownerOf, s -> done(nothing()));

		List<Boolean> groupSealedAtHook = new ArrayList<>();
		a.onSealed(drained -> {
			groupSealedAtHook.add(a.isSealed() && b.isSealed());
			return done(nothing());
		});
		b.onSealed(drained -> {
			groupSealedAtHook.add(a.isSealed() && b.isSealed());
			return done(nothing());
		});

		// the ring: a's subscriber waits at b, b's subscriber waits at a —
		// each park through the safe door, the owner's ledger kept honest
		owners.put("a-reader", a);
		assertThat(b.parkFrom(a, "a-reader", v -> true).isDefined()).isTrue();
		owners.put("b-reader", b);
		assertThat(a.parkFrom(b, "b-reader", v -> true).isDefined()).isTrue();

		// each fixpoint runs one master; the last finish's cascade attempt
		// finds the drained ring and group-seals it
		a.master(Fiber.defer(() -> done(nothing()))).get();
		b.master(Fiber.defer(() -> done(nothing()))).get();

		assertThat(a.isSealed()).isTrue();
		assertThat(b.isSealed()).isTrue();
		assertThat(groupSealedAtHook).containsExactly(true, true);
	}
}
