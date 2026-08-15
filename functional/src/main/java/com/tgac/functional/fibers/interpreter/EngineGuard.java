package com.tgac.functional.fibers.interpreter;

// ABOUTME: The ground-nesting guard and the eager-application budget bounding
// ABOUTME: JVM-stack nesting of Done.flatMap applies.

/**
 * The eager-application budget for {@code Done.flatMap}, and the nesting
 * guard for {@code Fiber.ground} — the deprecated-as-marker door, a FRESH
 * scheduler run explicitly over a pure fiber, sanctioned ONE level deep.
 */
public final class EngineGuard {

	private EngineGuard() {
	}

	/**
	 * The ground-nesting flag: {@code Fiber.ground} drives a fresh engine on
	 * the caller's stack, so a fiber being grounded that grounds again
	 * STACKS engines — recursion through ground is stack recursion, and it
	 * compounds. One ground per thread at a time; the second refuses loudly.
	 */
	private static final ThreadLocal<boolean[]> GROUNDING = ThreadLocal.withInitial(() -> new boolean[1]);

	public static void enterGround() {
		boolean[] grounding = GROUNDING.get();
		if (grounding[0]) {
			throw new IllegalStateException(
					"nested ground(): a fiber being grounded is grounding another — "
							+ "engines would stack on the JVM stack and recursion through "
							+ "ground compounds; compose the inner fiber instead");
		}
		grounding[0] = true;
	}

	public static void exitGround() {
		GROUNDING.get()[0] = false;
	}

	/**
	 * The eager-application budget: bounds JVM-STACK nesting of
	 * Done.flatMap applies. The counter is BALANCED (push before the
	 * apply, pop in a finally), so at any instant it equals the number of
	 * eager applies open on this thread's stack — across engine nesting
	 * too, which is exactly what bounds overflow. No reset anywhere: at a
	 * normal scheduler step entry it is already zero by balance, and
	 * inside a deliberately nested engine (ground()) the outer applies
	 * are real stack that must keep counting.
	 */
	private static final ThreadLocal<int[]> EAGER = ThreadLocal.withInitial(() -> new int[1]);

	/**
	 * The budget's value: {@code fiber.eagerBudget} property when set,
	 * otherwise 16 — the depth where the eager win SATURATES on the logic
	 * benchmark's vision lanes (the sweep: 16 already buys the full ~20%
	 * tabling gain of any deeper budget). Kept small so the worst-case
	 * stack exposure — budget × the heaviest continuation frame — stays
	 * comfortably inside any default thread stack.
	 */
	private static volatile int BUDGET = Integer.getInteger("fiber.eagerBudget", 16);

	public static boolean eagerBudgetLeft() {
		return EAGER.get()[0] < BUDGET;
	}

	public static int eagerBudget() {
		return BUDGET;
	}

	/** Fixes the budget — the test seam and the deployment override. */
	public static void setEagerBudget(int budget) {
		if (budget < 0) {
			throw new IllegalArgumentException("negative eager budget: " + budget);
		}
		BUDGET = budget;
	}

	public static void eagerPush() {
		EAGER.get()[0]++;
	}

	public static void eagerPop() {
		EAGER.get()[0]--;
	}
}
