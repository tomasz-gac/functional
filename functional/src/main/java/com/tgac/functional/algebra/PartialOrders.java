package com.tgac.functional.algebra;

// ABOUTME: Canonical partial-order witness: subset inclusion over a bitmask,
// ABOUTME: implementing leq DIRECTLY — no meet — the shape the interface permits.

import lombok.Value;

/**
 * The witness for {@link PartialOrder} implemented WITHOUT any lattice
 * operation: subset inclusion over a small universe. Incomparable pairs are
 * the common case ({@code {1}} vs {@code {2}}), which is exactly what a
 * total-order interface could not express.
 */
public final class PartialOrders {

	private PartialOrders() {
	}

	@Value(staticConstructor = "of")
	public static class Subset implements PartialOrder<Subset> {
		long bits;

		@Override
		public boolean leq(Subset other) {
			return (bits | other.bits) == other.bits;
		}
	}
}
