package com.tgac.functional.fibers.schedulers;

// ABOUTME: The smallest interesting test semilattice: ints under max —
// ABOUTME: combine is max, absorption is ≤.

import com.tgac.functional.algebra.Semilattice;

final class MaxInt implements Semilattice<MaxInt> {

	final int value;

	private MaxInt(int value) {
		this.value = value;
	}

	static MaxInt of(int value) {
		return new MaxInt(value);
	}

	@Override
	public MaxInt combine(MaxInt other) {
		return value >= other.value ? this : other;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof MaxInt && value == ((MaxInt) o).value;
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public String toString() {
		return "max(" + value + ")";
	}
}
