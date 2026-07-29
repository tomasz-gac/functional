package com.tgac.functional.fibers.interpreter;

// ABOUTME: The control await: drained(scope) completes with Nothing when the
// ABOUTME: workforce seals; plant is once-only; readers keep the sealed arm.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SealedTest {

	@Test
	public void sealedCompletesWhenTheWorkforceFinishes() {
		Scope sub = Scope.scope();
		List<String> order = new ArrayList<>();

		Fiber<Nothing> program = Fiber.plant(sub, Fiber.fork(Arrays.asList(
						Fiber.defer(() -> {
							order.add("child-1");
							return done(nothing());
						}),
						Fiber.defer(() -> {
							order.add("child-2");
							return done(nothing());
						}))))
				.flatMap(__ -> Fiber.sealed(sub))
				.flatMap(__ -> {
					order.add("exhausted");
					return done(nothing());
				});

		program.get();

		assertThat(order).containsExactly("child-1", "child-2", "exhausted");
	}

	@Test
	public void sealedOnASealedScopeCompletesImmediately() {
		Scope sub = Scope.scope();
		sub.seal();

		assertThat(Fiber.sealed(sub).get()).isEqualTo(nothing());
	}

	@Test
	public void aWorkforceIsPlantedExactlyOnce() {
		Scope sub = Scope.scope();
		Fiber.plant(sub, done(nothing()));

		assertThatThrownBy(() -> Fiber.plant(sub, done(nothing())))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("already planted");
	}

	@Test
	public void aForkInsideThePlantedTreeGrowsTheWorkforceFromWithin() {
		Scope sub = Scope.scope();
		List<Integer> seen = new ArrayList<>();

		// the planted tree forks mid-flight: membership grows from within,
		// and the seal waits for the late children too
		Fiber<Nothing> program = Fiber.plant(sub, Fiber.defer(() ->
						Fiber.fork(Arrays.asList(
								Fiber.defer(() -> {
									seen.add(1);
									return done(nothing());
								}),
								Fiber.defer(() -> Fiber.fork(Arrays.asList(Fiber.defer(() -> {
									seen.add(2);
									return done(nothing());
								}))))))))
				.flatMap(__ -> Fiber.sealed(sub));

		program.get();

		assertThat(seen).containsExactlyInAnyOrder(1, 2);
	}
	@Test
	public void aNestedSealedInsideAPlantedTreeResolvesBottomUp() {
		Scope outer = Scope.scope();
		List<String> order = new ArrayList<>();

		// the planted tree itself plants a sub-workforce and drains it -
		// the outer seal must wait for the inner chain to resolve
		Fiber<Nothing> innerWork = Fiber.defer(() -> {
			order.add("inner-work");
			return done(nothing());
		});
		Fiber<Nothing> tree = Fiber.defer(() -> {
			Scope inner = Scope.scope();
			return Fiber.plant(inner, innerWork)
					.flatMap(__ -> Fiber.sealed(inner))
					.flatMap(__ -> {
						order.add("inner-drained");
						return done(nothing());
					});
		});

		Fiber.plant(outer, tree)
				.flatMap(__ -> Fiber.sealed(outer))
				.flatMap(__ -> {
					order.add("outer-drained");
					return done(nothing());
				})
				.get();

		assertThat(order).containsExactly("inner-work", "inner-drained", "outer-drained");
	}

	@Test
	public void aForkedChildsNestedSealedWaitHoldsTheOuterSealOpen() {
		Scope outer = Scope.scope();
		List<String> order = new ArrayList<>();

		// one forked child plants and drains a sub-workforce; the outer
		// drain must wait for the whole chain, not seal past the parked child
		Fiber<Nothing> tree = Fiber.fork(Arrays.asList(
				Fiber.defer(() -> {
					order.add("sibling");
					return done(nothing());
				}),
				Fiber.defer(() -> {
					Scope inner = Scope.scope();
					return Fiber.plant(inner, Fiber.defer(() -> {
								order.add("inner-work");
								return done(nothing());
							}))
							.flatMap(__ -> Fiber.sealed(inner))
							.flatMap(__ -> {
								order.add("inner-drained");
								return done(nothing());
							});
				})));

		Fiber.plant(outer, tree)
				.flatMap(__ -> Fiber.sealed(outer))
				.flatMap(__ -> {
					order.add("outer-drained");
					return done(nothing());
				})
				.get();

		assertThat(order).containsExactly("sibling", "inner-work", "inner-drained", "outer-drained");
	}
}
