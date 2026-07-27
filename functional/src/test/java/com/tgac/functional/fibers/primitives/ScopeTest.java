package com.tgac.functional.fibers.primitives;

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
		Scope<String> scope = new Scope<>(s -> null);
		List<String> drainedAtHook = new ArrayList<>();
		scope.onSealed(drained -> {
			drained.forEach(drainedAtHook::add);
			return done(nothing());
		});
		scope.park("the-fold");

		Scope.track(scope, Fiber.defer(() -> done(nothing()))).get();

		assertThat(scope.isSealed()).isTrue();
		assertThat(drainedAtHook).containsExactly("the-fold");
	}

	@Test
	public void aSleeperAtAForeignUnsealedScopeDefersTheSeal() {
		Map<String, Scope<String>> owners = new HashMap<>();
		Function<String, Scope<String>> ownerOf = owners::get;
		Scope<String> outer = new Scope<>(ownerOf);
		Scope<String> dependency = new Scope<>(ownerOf);

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
		outer.sleeping("waiter", dependency);
		dependency.park("waiter");

		Scope.track(outer, Fiber.defer(() -> done(nothing()))).get();
		assertThat(outer.isSealed()).isFalse();

		// the dependency finishing seals it, kills the waiter, and cascades to outer
		Scope.track(dependency, Fiber.defer(() -> done(nothing()))).get();
		assertThat(dependency.isSealed()).isTrue();
		assertThat(outer.isSealed()).isTrue();
		assertThat(sealedOrder).containsExactly("dependency", "outer");
	}

	@Test
	public void aSleeperRingGroupSealsMarkingEveryMemberBeforeAnyHook() {
		Map<String, Scope<String>> owners = new HashMap<>();
		Function<String, Scope<String>> ownerOf = owners::get;
		Scope<String> a = new Scope<>(ownerOf);
		Scope<String> b = new Scope<>(ownerOf);

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
		a.sleeping("a-reader", b);
		b.park("a-reader");
		owners.put("b-reader", b);
		b.sleeping("b-reader", a);
		a.park("b-reader");

		Scope.track(a, Fiber.defer(() -> done(nothing()))).get();
		Scope.track(b, Fiber.defer(() -> done(nothing()))).get();

		assertThat(a.isSealed()).isTrue();
		assertThat(b.isSealed()).isTrue();
		assertThat(groupSealedAtHook).containsExactly(true, true);
	}
}
