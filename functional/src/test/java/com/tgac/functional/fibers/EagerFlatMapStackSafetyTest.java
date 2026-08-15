package com.tgac.functional.fibers;

// ABOUTME: RED until the depth-budgeted patch: eager flatMap on Done runs the
// ABOUTME: continuation on the caller's stack at construction — deep chains overflow.

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * The hazard receipts for Done.flatMap's eager application. The dangerous
 * shape is a continuation that CONSTRUCTS the next link inside its own
 * apply — recursion at build time, on the JVM stack, unbounded. The safe
 * shapes are kept as green controls so the future budget patch is scoped
 * to exactly the hazard: recursion-in-continuation overflows; iterative
 * accumulation and defer-broken recursion do not.
 */
public class EagerFlatMapStackSafetyTest {

	private static final long DEEP = 1_000_000;

	/** The hazard: each apply constructs the next flatMap — stack recursion. */
	private static Fiber<Long> countdown(long n) {
		if (n == 0) {
			return Fiber.done(0L);
		}
		return Fiber.done(n).flatMap(v -> countdown(v - 1));
	}

	/** The escape hatch users must currently remember: defer breaks eagerness. */
	private static Fiber<Long> countdownDeferred(long n) {
		if (n == 0) {
			return Fiber.done(0L);
		}
		return Fiber.done(n).flatMap(v -> Fiber.defer(() -> countdownDeferred(v - 1)));
	}

	@Test
	public void deepDoneRecursionMustNotOverflowAtConstruction() {
		// RED today: countdown(DEEP) overflows the stack BEFORE any scheduler
		// runs — the failure is at construction, not at execution
		assertThat(countdown(DEEP).ground()).isEqualTo(0L);
	}

	@Test
	public void constructionAloneMustNotOverflow() {
		// RED today: even just BUILDING the fiber (no get, no scheduler)
		// blows the stack — the eager applies run the whole recursion now
		Fiber<Long> built = countdown(DEEP);
		assertThat(built).isNotNull();
	}

	@Test
	public void deferBrokenRecursionSurvives() {
		// GREEN control: the current escape hatch — Deferred is not Done,
		// so the chain trampolines through the scheduler
		assertThat(countdownDeferred(DEEP).ground()).isEqualTo(0L);
	}

	@Test
	public void iterativeAccumulationSurvives() {
		// GREEN control: loop-shaped chaining applies one level per
		// iteration and returns — no stack growth; the patch must not
		// penalize this shape
		Fiber<Long> acc = Fiber.done(0L);
		for (long i = 0; i < DEEP; i++) {
			acc = acc.flatMap(v -> Fiber.done(v + 1));
		}
		assertThat(acc.ground()).isEqualTo(DEEP);
	}
}
