package org.clauseway.functional.tuples;

// ABOUTME: The tuple companion: generic construction from a members array —
// ABOUTME: typed classes through arity 8, the flat TupleN above.

public final class Tuples {

	private Tuples() {
	}

	/**
	 * A tuple of the members' arity; refuses arity 0. Dispatch is
	 * deterministic — 1-8 always the typed classes, wider always
	 * {@link TupleN} — so equal arities never meet as different shapes.
	 */
	public static Tuple of(Object... members) {
		switch (members.length) {
			case 1:
				return Tuple.of(members[0]);
			case 2:
				return Tuple.of(members[0], members[1]);
			case 3:
				return Tuple.of(members[0], members[1], members[2]);
			case 4:
				return Tuple.of(members[0], members[1], members[2], members[3]);
			case 5:
				return Tuple.of(members[0], members[1], members[2], members[3], members[4]);
			case 6:
				return Tuple.of(members[0], members[1], members[2], members[3], members[4],
						members[5]);
			case 7:
				return Tuple.of(members[0], members[1], members[2], members[3], members[4],
						members[5], members[6]);
			case 8:
				return Tuple.of(members[0], members[1], members[2], members[3], members[4],
						members[5], members[6], members[7]);
			case 0:
				throw new IllegalArgumentException("no tuple of arity 0");
			default:
				return new TupleN(members);
		}
	}
}
