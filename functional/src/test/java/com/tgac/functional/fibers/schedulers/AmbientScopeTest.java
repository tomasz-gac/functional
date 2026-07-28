package com.tgac.functional.fibers.schedulers;

// ABOUTME: Ambient billing: detached work seals with no manual billing — forks inherit,
// ABOUTME: detachTo re-parents, and the parallel scheduler bills race-free.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.schedulers.ForkJoinScheduler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class AmbientScopeTest {

	@Test
	public void detachedWorkSealsWithNoManualBilling() {
		Scope<String> scope = new Scope<>(s -> null);
		List<String> events = new ArrayList<>();

		Fiber<Nothing> work = Fiber.defer(() -> done(nothing()))
				.flatMap(__ -> {
					events.add("worked");
					return done(nothing());
				});

		scope.onSealed(drained -> {
			events.add("sealed");
			return done(nothing());
		});
		Fiber.detachTo(scope, work).get();

		assertThat(scope.isSealed()).isTrue();
		assertThat(events).containsExactly("worked", "sealed");
	}

	@Test
	public void forkedChildrenInheritTheAmbientScopeAndGateTheSeal() {
		Scope<String> scope = new Scope<>(s -> null);
		AtomicInteger childrenRun = new AtomicInteger();
		List<Integer> childrenAtSeal = new ArrayList<>();

		Fiber<Nothing> work = Fiber.fork(Arrays.asList(
						Fiber.defer(() -> done(childrenRun.incrementAndGet())),
						Fiber.defer(() -> done(childrenRun.incrementAndGet())),
						Fiber.defer(() -> done(childrenRun.incrementAndGet()))),
				v -> {
				});

		scope.onSealed(drained -> {
			childrenAtSeal.add(childrenRun.get());
			return done(nothing());
		});
		Fiber.detachTo(scope, work).get();

		assertThat(scope.isSealed()).isTrue();
		// the seal fired only after every forked child completed
		assertThat(childrenAtSeal).containsExactly(3);
	}

	@Test
	public void detachToReParentsAcrossScopes() {
		Scope<String> caller = new Scope<>(s -> null);
		Scope<String> entry = new Scope<>(s -> null);
		List<String> sealOrder = new ArrayList<>();
		entry.onSealed(drained -> {
			sealOrder.add("entry");
			return done(nothing());
		});

		// the caller detaches a "master" to the entry's scope and finishes at
		// once; the entry seals only when the master's work completes
		AtomicInteger masterSteps = new AtomicInteger();
		Fiber<Nothing> master = Fiber.defer(() -> done(masterSteps.incrementAndGet()))
				.flatMap(__ -> Fiber.defer(() -> done(nothing())));

		caller.onSealed(drained -> {
			sealOrder.add("caller");
			return done(nothing());
		});
		Fiber.detachTo(caller, Fiber.detachTo(entry, master)).get();

		assertThat(caller.isSealed()).isTrue();
		assertThat(entry.isSealed()).isTrue();
		assertThat(masterSteps.get()).isEqualTo(1);
		assertThat(sealOrder).contains("entry", "caller");
	}

	@Test
	public void parallelSchedulerBillsAmbientlyRaceFree() throws Exception {
		for (int round = 0; round < 20; round++) {
			Scope<String> scope = new Scope<>(s -> null);
			AtomicInteger sum = new AtomicInteger();
			List<Fiber<Integer>> tasks = new ArrayList<>();
			for (int i = 0; i < 32; i++) {
				tasks.add(Fiber.defer(() -> done(sum.incrementAndGet())));
			}
			AtomicInteger atSeal = new AtomicInteger(-1);
			scope.onSealed(drained -> {
				atSeal.set(sum.get());
				return done(nothing());
			});
			Fiber<Nothing> work = Fiber.detachTo(scope,
					Fiber.fork(tasks, v -> {
					}));

			try (ForkJoinScheduler<Nothing> engine = new ForkJoinScheduler<>(work)) {
				engine.get();
			}
			assertThat(scope.isSealed()).isTrue();
			assertThat(atSeal.get()).isEqualTo(32);
		}
	}
}
