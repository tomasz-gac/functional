package com.tgac.functional.fibers.interpreter;

// ABOUTME: The ground-nesting guard and the calibrated eager-application
// ABOUTME: budget bounding JVM-stack nesting of Done.flatMap applies.

import com.tgac.functional.fibers.Fiber;

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
	 * otherwise calibrated on first use by {@link #probeCapacity} — a fresh
	 * default-stack thread runs the REAL eager path to StackOverflowError,
	 * and the budget is the measured capacity over {@link #MARGIN}. Negative
	 * means not yet calibrated.
	 */
	private static volatile int BUDGET = Integer.getInteger("fiber.eagerBudget", -1);

	/**
	 * Measured capacity over margin = the budget: the slack covers
	 * continuations heavier than the probe's minimal one, chains that start
	 * mid-stack rather than at a thread's root, and headroom for the
	 * innermost continuation's own body. The heavy-chain receipt in the
	 * calibration test bounds the weight assumption empirically.
	 */
	private static final int MARGIN = 4;
	private static final int FLOOR = 64;

	public static boolean eagerBudgetLeft() {
		int open = EAGER.get()[0];
		if (open < 0) {
			// only the probe biases its counter negative: unlimited, and it
			// must not fall into calibration (it IS calibration)
			return true;
		}
		int budget = BUDGET;
		return open < (budget < 0 ? calibrated() : budget);
	}

	public static int eagerBudget() {
		int budget = BUDGET;
		return budget < 0 ? calibrated() : budget;
	}

	/** Fixes the budget — the test seam, and the end of calibration's say. */
	public static void setEagerBudget(int budget) {
		if (budget < 0) {
			throw new IllegalArgumentException("negative eager budget: " + budget);
		}
		BUDGET = budget;
	}

	private static synchronized int calibrated() {
		if (BUDGET < 0) {
			BUDGET = Math.max(FLOOR, probeCapacity() / MARGIN);
		}
		return BUDGET;
	}

	/**
	 * How many REAL eager applies a fresh default-stack thread survives:
	 * the probe self-recurses through the actual {@code Done.flatMap} eager
	 * path — its counter biased negative so the budget check passes — until
	 * StackOverflowError, which unwinds a stack of nothing but probe frames
	 * and ends the throwaway thread. Interpreted (pre-JIT) frames are
	 * fatter, so an early probe under-measures — the safe direction.
	 */
	static synchronized int probeCapacity() {
		int[] deepest = new int[1];
		Thread probe = new Thread(() -> {
			EAGER.get()[0] = Integer.MIN_VALUE;
			try {
				probeChain(0, deepest);
			} catch (Throwable expected) {
				// StackOverflowError: unwound, measured, done
			}
		}, "eager-budget-probe");
		try {
			probe.start();
			probe.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return deepest[0];
	}

	private static void probeChain(int level, int[] deepest) {
		Fiber.done(level).flatMap(v -> {
			deepest[0] = v;
			probeChain(v + 1, deepest);
			return Fiber.done(v);
		});
	}

	public static void eagerPush() {
		EAGER.get()[0]++;
	}

	public static void eagerPop() {
		EAGER.get()[0]--;
	}
}
