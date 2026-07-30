package com.tgac.functional.category;

import com.tgac.functional.algebra.Semilattice;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** The one-point semilattice: a single element, so every law holds by uniqueness. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Nothing implements Semilattice<Nothing> {
	private static final Nothing INSTANCE = new Nothing();

	public static Nothing nothing() {
		return INSTANCE;
	}

	@Override
	public Nothing combine(Nothing other) {
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "Nothing";
	}
}
