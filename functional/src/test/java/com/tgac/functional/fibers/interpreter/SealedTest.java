package com.tgac.functional.fibers.interpreter;

// ABOUTME: The control await: drained(scope) completes with Nothing when the
// ABOUTME: workforce seals; claim is once-only; readers keep the sealed arm.

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

		Fiber<Nothing> program = Fiber.claim(sub, Fiber.fork(Arrays.asList(
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
		// sealed honestly: an empty claimed workforce finishes at once
		Fiber.claim(sub, done(nothing())).get();

		assertThat(Fiber.sealed(sub).get()).isEqualTo(nothing());
	}

	@Test
	public void aWorkforceIsClaimedAtMostOnce() {
		Scope sub = Scope.scope();
		List<String> ran = new ArrayList<>();

		// the claim CAS runs at the step: the first spawn wins, a racing or
		// re-stepped claim no-ops
		Fiber.claim(sub, Fiber.defer(() -> {
			ran.add("first");
			return done(nothing());
		})).get();
		Fiber.claim(sub, Fiber.defer(() -> {
			ran.add("second");
			return done(nothing());
		})).get();

		assertThat(ran).containsExactly("first");
	}

	@Test
	public void aSealedScopeWaitNeedsNoParkingSupport() {
		Scope sub = Scope.scope();
		// sealed honestly: an empty claimed workforce finishes at once
		Fiber.claim(sub, done(nothing())).get();

		// the seal is irrevocable - the green light is already on, so the
		// wait must complete inline on a driver whose park doors throw
		Object[] result = new Object[1];
		Frame.Effects<Object> effects = new Frame.Effects<Object>() {
			@Override
			public void completed(Object entry, Object value) {
				result[0] = value;
			}

			@Override
			public void forked(Object entry, List<Frame> children) {
				throw new AssertionError("no forks in this drive");
			}

			@Override
			public void detached(Object entry, Frame child) {
				throw new AssertionError("no detaches in this drive");
			}
		};
		Frame frame = new Frame(Fiber.sealed(sub));
		while (frame.step(new Object(), effects, StepListener.NO_OP)) {
		}

		assertThat(result[0]).isEqualTo(nothing());
	}

	@Test
	public void sealedOnAnUnclaimedScopeRefusesLoudly() {
		// nothing will ever run an unclaimed workforce, so its seal can never
		// fire: park the mistake loudly at the park, not at drive end
		Scope sub = Scope.scope();

		assertThatThrownBy(() -> Fiber.sealed(sub).get())
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("unclaimed");
	}

	@Test
	public void aRefusalNamesTheNamedScope() {
		Scope sub = Scope.scope("head-probe");

		assertThatThrownBy(() -> Fiber.sealed(sub).get())
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("head-probe");
	}

	@Test
	public void aRingOfSealWaitsIsARefusedDeadlockNotAFixpoint() {
		Scope a = Scope.scope();
		Scope b = Scope.scope();

		// each workforce's only member awaits the OTHER's seal; both waits
		// are billed through, so neither home can ever drain - unsatisfiable
		// by the counters, never falsely sealed by the walk (undrained
		// members abort it), and named loudly when the drive runs dry
		Fiber<Nothing> program = Fiber.claim(a, Fiber.sealed(b))
				.flatMap(__ -> Fiber.claim(b, Fiber.sealed(a)));

		// which refusal fires depends on whether a waiter reaches its park
		// before the other claim lands (unclaimed check) or after (strand
		// refusal at drive end) - EITHER is correct, silence is not
		assertThatThrownBy(program::get)
				.isInstanceOf(IllegalStateException.class);
		assertThat(a.isSealed()).isFalse();
		assertThat(b.isSealed()).isFalse();
	}

	@Test
	public void aLosingClaimantRunsItsAlternative() {
		Scope sub = Scope.scope();
		List<String> ran = new ArrayList<>();

		// the plain claim's loser no-ops silently; the OrElse loser runs its
		// alternative inline, in its own frame
		Fiber.claim(sub, Fiber.defer(() -> {
					ran.add("winner");
					return done(nothing());
				}))
				.flatMap(__ -> Fiber.claimOrElse(sub, Fiber.defer(() -> {
					ran.add("second winner");
					return done(nothing());
				}), Fiber.defer(() -> {
					ran.add("loser");
					return done(nothing());
				})))
				.get();

		assertThat(ran).containsExactly("winner", "loser");
	}

	@Test
	public void aForkInsideTheClaimedTreeGrowsTheWorkforceFromWithin() {
		Scope sub = Scope.scope();
		List<Integer> seen = new ArrayList<>();

		// the claimed tree forks mid-flight: membership grows from within,
		// and the seal waits for the late children too
		Fiber<Nothing> program = Fiber.claim(sub, Fiber.defer(() ->
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
	public void aNestedSealedInsideAClaimedTreeResolvesBottomUp() {
		Scope outer = Scope.scope();
		List<String> order = new ArrayList<>();

		// the claimed tree itself claims a sub-workforce and drains it -
		// the outer seal must wait for the inner chain to resolve
		Fiber<Nothing> innerWork = Fiber.defer(() -> {
			order.add("inner-work");
			return done(nothing());
		});
		Fiber<Nothing> tree = Fiber.defer(() -> {
			Scope inner = Scope.scope();
			return Fiber.claim(inner, innerWork)
					.flatMap(__ -> Fiber.sealed(inner))
					.flatMap(__ -> {
						order.add("inner-drained");
						return done(nothing());
					});
		});

		Fiber.claim(outer, tree)
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

		// one forked child claims and drains a sub-workforce; the outer
		// drain must wait for the whole chain, not seal past the parked child
		Fiber<Nothing> tree = Fiber.fork(Arrays.asList(
				Fiber.defer(() -> {
					order.add("sibling");
					return done(nothing());
				}),
				Fiber.defer(() -> {
					Scope inner = Scope.scope();
					return Fiber.claim(inner, Fiber.defer(() -> {
								order.add("inner-work");
								return done(nothing());
							}))
							.flatMap(__ -> Fiber.sealed(inner))
							.flatMap(__ -> {
								order.add("inner-drained");
								return done(nothing());
							});
				})));

		Fiber.claim(outer, tree)
				.flatMap(__ -> Fiber.sealed(outer))
				.flatMap(__ -> {
					order.add("outer-drained");
					return done(nothing());
				})
				.get();

		assertThat(order).containsExactly("sibling", "inner-work", "inner-drained", "outer-drained");
	}
}
