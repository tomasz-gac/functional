package com.tgac.functional.fibers.interpreter;

// ABOUTME: Thread-local marker for "an engine is driving on this thread" —
// ABOUTME: lets Fiber.get refuse nested grounding loudly instead of silently.

/**
 * A reentrancy depth per thread, entered by every scheduler drive entry
 * (including parallel workers). {@code Fiber.get} consults it: grounding a
 * non-Done fiber while an engine is driving would silently spin an inner
 * engine sharing the outer run's state — the guard turns that into an
 * exception at the offending call site. Deliberate nesting has one
 * sanctioned shape: running a FRESH scheduler explicitly (the workforce
 * protocol), which never routes through {@code Fiber.get}.
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
}
