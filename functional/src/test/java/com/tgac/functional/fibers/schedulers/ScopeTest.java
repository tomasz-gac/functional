package com.tgac.functional.fibers.schedulers;

// ABOUTME: Bare-scope seal tests: a cell-less scope bills work, parks seal-only
// ABOUTME: subscribers, defers on foreign sleepers, and group-seals rings.

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

public class ScopeTest {

	@Test
	public void trackedWorkSealsAndTheHookReceivesSealOnlySubscribers() {
		MonotoneCell<MaxInt, String> cell = new MonotoneCell<>(MaxInt.of(0));
		Scope<String> scope = cell.scope();
		List<String> drainedAtHook = new ArrayList<>();
		scope.onSealed(drained -> {
			drained.forEach(drainedAtHook::add);
			return done(nothing());
		});
		scope.awaitSeal("the-fold");

		Fiber.detachTo(cell, Fiber.defer(() -> done(nothing()))).get();

		assertThat(scope.isSealed()).isTrue();
		assertThat(drainedAtHook).containsExactly("the-fold");
	}

	@Test
	public void aSleeperAtAForeignUnsealedScopeDefersTheSeal() {
		Map<String, Scope<String>> owners = new HashMap<>();
		Function<String, Scope<String>> ownerOf = owners::get;
		MonotoneCell<MaxInt, String> outerCell = new MonotoneCell<>(MaxInt.of(0), ownerOf);
		MonotoneCell<MaxInt, String> dependencyCell = new MonotoneCell<>(MaxInt.of(0), ownerOf);
		Scope<String> outer = outerCell.scope();
		Scope<String> dependency = dependencyCell.scope();

		List<String> sealedOrder = new ArrayList<>();
		outer.onSealed(drained -> {
			sealedOrder.add("outer");
			return done(nothing());
		});
		dependency.onSealed(drained -> {
			sealedOrder.add("dependency");
			return done(nothing());
		});

		// outer's subscriber waits on the dependency's seal
		owners.put("waiter", outer);
		outer.blocked("waiter", dependency);
		dependency.awaitSeal("waiter");

		Fiber.detachTo(outerCell, Fiber.defer(() -> done(nothing()))).get();
		assertThat(outer.isSealed()).isFalse();

		// the dependency finishing seals it, kills the waiter, and cascades to outer
		Fiber.detachTo(dependencyCell, Fiber.defer(() -> done(nothing()))).get();
		assertThat(dependency.isSealed()).isTrue();
		assertThat(outer.isSealed()).isTrue();
		assertThat(sealedOrder).containsExactly("dependency", "outer");
	}

	@Test
	public void aSleeperRingGroupSealsMarkingEveryMemberBeforeAnyHook() {
		Map<String, Scope<String>> owners = new HashMap<>();
		Function<String, Scope<String>> ownerOf = owners::get;
		MonotoneCell<MaxInt, String> aCell = new MonotoneCell<>(MaxInt.of(0), ownerOf);
		MonotoneCell<MaxInt, String> bCell = new MonotoneCell<>(MaxInt.of(0), ownerOf);
		Scope<String> a = aCell.scope();
		Scope<String> b = bCell.scope();

		List<Boolean> groupSealedAtHook = new ArrayList<>();
		a.onSealed(drained -> {
			groupSealedAtHook.add(a.isSealed() && b.isSealed());
			return done(nothing());
		});
		b.onSealed(drained -> {
			groupSealedAtHook.add(a.isSealed() && b.isSealed());
			return done(nothing());
		});

		owners.put("a-reader", a);
		a.blocked("a-reader", b);
		b.awaitSeal("a-reader");
		owners.put("b-reader", b);
		b.blocked("b-reader", a);
		a.awaitSeal("b-reader");

		Fiber.detachTo(aCell, Fiber.defer(() -> done(nothing()))).get();
		Fiber.detachTo(bCell, Fiber.defer(() -> done(nothing()))).get();

		assertThat(a.isSealed()).isTrue();
		assertThat(b.isSealed()).isTrue();
		assertThat(groupSealedAtHook).containsExactly(true, true);
	}
}
