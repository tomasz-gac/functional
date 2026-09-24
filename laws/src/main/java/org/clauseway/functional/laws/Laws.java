package org.clauseway.functional.laws;

// ABOUTME: The shared assertion helper for law kits: plain AssertionError,
// ABOUTME: usable from any test framework downstream.

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Laws {
	public static void require(boolean condition, String law, Object... witnesses) {
		if (!condition) {
			StringBuilder sb = new StringBuilder("law violated: ").append(law).append(" — witnesses:");
			for (Object w : witnesses) {
				sb.append(' ').append(w);
			}
			throw new AssertionError(sb.toString());
		}
	}
}
