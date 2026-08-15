package com.tgac.functional.fibers.interpreter;

// ABOUTME: Per-thread budget bounding JVM-stack nesting of eager Done.flatMap
// ABOUTME: applies — below it the continuation runs on the caller's stack.

/**
 * The eager-application budget: bounds JVM-STACK nesting of
 * {@code Done.flatMap} applies. The counter is BALANCED (push before the
 * apply, pop in a finally), so at any instant it equals the number of
 * eager applies open on this thread's stack — across engine nesting
 * too, which is exactly what bounds overflow. No reset anywhere: at a
 * normal scheduler step entry it is already zero by balance, and
 * inside a deliberately constructed nested engine the outer applies
 * are real stack that must keep counting.
 */
public final class EagerBudget {

	private static final ThreadLocal<int[]> EAGER = ThreadLocal.withInitial(() -> new int[1]);

	private EagerBudget() {
	}

	public static boolean left(int budget) {
		return EAGER.get()[0] < budget;
	}

	public static void push() {
		EAGER.get()[0]++;
	}

	public static void pop() {
		EAGER.get()[0]--;
	}
}
