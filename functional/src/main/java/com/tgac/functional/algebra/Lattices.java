package com.tgac.functional.algebra;

// ABOUTME: Lattice witnesses: Mask, the exact finite-subset lattice (the adornment
// ABOUTME: prototype), and Range, the inflationary-join exemplar (interval hulls).

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
	public static class Mask implements Lattice<Mask>, Bottomed {
		long bits;

		@Override
		public Mask meet(Mask other) {
			return of(bits & other.bits);
		}

		@Override
		public Mask join(Mask other) {
			return of(bits | other.bits);
		}

		@Override
		public boolean isBottom() {
			return bits == 0L;
		}
	}

	/**
	 * Integer ranges: meet = intersection (exact), join = HULL (inflationary —
	 * an upper bound of the true join; absorption fails on disjoint ranges,
	 * which is exactly what LatticeLaws.checkInflationary permits).
	 */
	@Value
	public static class Range implements Lattice<Range>, Bottomed {
		int lo;
		int hi;

		public static Range of(int lo, int hi) {
			return lo > hi ? new Range(1, 0) : new Range(lo, hi);
		}

		@Override
		public Range meet(Range other) {
			return of(Math.max(lo, other.lo), Math.min(hi, other.hi));
		}

		@Override
		public Range join(Range other) {
			if (isBottom()) {
				return other;
			}
			if (other.isBottom()) {
				return this;
			}
			return of(Math.min(lo, other.lo), Math.max(hi, other.hi));
		}

		@Override
		public boolean isBottom() {
			return lo > hi;
		}
	}
}
