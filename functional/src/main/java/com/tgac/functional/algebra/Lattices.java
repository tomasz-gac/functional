package com.tgac.functional.algebra;

// ABOUTME: Two-structure fixtures: Mask, the exact finite-subset case, and Range,
// ABOUTME: the inflationary-hull exemplar - meet inherited, join exposed as a projection.

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Lattices {

	/**
	 * Finite subsets of a 64-element universe: meet = ∩, join = ∪, leq = ⊆ —
	 * an EXACT lattice (absorption holds). This is the shape of the
	 * optimizer's adornment lattice.
	 */
	@Value(staticConstructor = "of")
	public static class Mask implements Semilattice<Mask>, PartialOrder<Mask>, Absorbing {
		long bits;

		@Override
		public Mask combine(Mask other) {
			return meet(other);
		}

		@Override
		public boolean leq(Mask other) {
			return meet(other).equals(this);
		}

		public Mask meet(Mask other) {
			return of(bits & other.bits);
		}

		public Mask join(Mask other) {
			return of(bits | other.bits);
		}

		@Override
		public boolean isAbsorbing() {
			return bits == 0L;
		}
	}

	/**
	 * Integer ranges: meet = intersection (exact), join = HULL (inflationary —
	 * an upper bound of the true join; absorption fails on disjoint ranges,
	 * which is exactly what LatticeLaws.checkInflationary permits).
	 */
	@Value
	public static class Range implements Semilattice<Range>, PartialOrder<Range>, Absorbing {
		int lo;
		int hi;

		@Override
		public Range combine(Range other) {
			return meet(other);
		}

		@Override
		public boolean leq(Range other) {
			return meet(other).equals(this);
		}

		public static Range of(int lo, int hi) {
			return lo > hi ? new Range(1, 0) : new Range(lo, hi);
		}

		public Range meet(Range other) {
			return of(Math.max(lo, other.lo), Math.min(hi, other.hi));
		}

		public Range join(Range other) {
			if (isAbsorbing()) {
				return other;
			}
			if (other.isAbsorbing()) {
				return this;
			}
			return of(Math.min(lo, other.lo), Math.max(hi, other.hi));
		}

		@Override
		public boolean isAbsorbing() {
			return lo > hi;
		}
	}
}
