package com.tgac.functional.fibers.interpreter;

// ABOUTME: The debug switch for origin capture: scopes and Named nodes record
// ABOUTME: their construction sites only when enabled.

/**
 * Origin capture records a construction-site stack trace on the objects the
 * profiler labels — scopes and Named nodes. Capture costs a
 * Throwable fill-in per construction, so it is off by default and enabled
 * for profiling runs: the {@code fiber.captureOrigins} property at JVM
 * start for full coverage, or {@link #enable} at runtime for everything
 * constructed afterwards.
 */
public final class OriginCapture {

	private static volatile boolean ENABLED = Boolean.getBoolean("fiber.captureOrigins");

	private OriginCapture() {
	}

	public static boolean enabled() {
		return ENABLED;
	}

	public static void enable(boolean enabled) {
		ENABLED = enabled;
	}
}
