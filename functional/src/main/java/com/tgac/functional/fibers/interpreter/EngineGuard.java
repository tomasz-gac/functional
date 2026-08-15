package com.tgac.functional.fibers.interpreter;

// ABOUTME: Thread-local marker for "an engine is driving on this thread",
// ABOUTME: plus the eager-application budget for Done.flatMap.

/**
 * A reentrancy depth per thread, entered by every scheduler drive entry
 * (including parallel workers) — the marker for "an engine is driving on
 * this thread". Deliberate nesting has one sanctioned shape: the
 * deprecated-as-marker {@code Fiber.ground}, a FRESH scheduler run
 * explicitly over a pure fiber.
 */
public final class EngineGuard {

	private static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[1]);

	private EngineGuard() {
	}

	public static void enter() {
		DEPTH.get()[0]++;
	}

	public static void exit() {
		DEPTH.get()[0]--;
	}

	public static boolean driving() {
		return DEPTH.get()[0] > 0;
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

	public static boolean eagerBudgetLeft(int budget) {
		return EAGER.get()[0] < budget;
	}

	public static void eagerPush() {
		EAGER.get()[0]++;
	}

	public static void eagerPop() {
		EAGER.get()[0]--;
	}
}
