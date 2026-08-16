package com.tgac.functional.fibers.interpreter;

// ABOUTME: The debug switch for origin capture: scopes and Named nodes record
// ABOUTME: their construction sites only when enabled — per thread, per block.

/**
 * Origin capture records a construction-site stack trace on the objects the
 * profiler labels — scopes and Named nodes. Capture costs a Throwable
 * fill-in per construction, so it is off by default; the
 * {@code fiber.captureOrigins} property seeds every thread's default, and
 * {@link #within} turns it on for a block on the calling thread — only
 * objects constructed there and then carry origins.
 */
public final class OriginCapture {

	private static final boolean DEFAULT = Boolean.getBoolean("fiber.captureOrigins");

	private static final ThreadLocal<boolean[]> ENABLED =
			ThreadLocal.withInitial(() -> new boolean[] {DEFAULT});

	private OriginCapture() {
	}

	public static boolean enabled() {
		return ENABLED.get()[0];
	}

	/** Runs the block with capture on, restoring the thread's previous state. */
	public static void within(Runnable block) {
		boolean[] state = ENABLED.get();
		boolean previous = state[0];
		state[0] = true;
		try {
			block.run();
		} finally {
			state[0] = previous;
		}
	}
}
