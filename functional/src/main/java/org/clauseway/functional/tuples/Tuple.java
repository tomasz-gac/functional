package org.clauseway.functional.tuples;

// ABOUTME: Structural tuples, arities 1-8: vavr-shaped construction and accessors
// ABOUTME: plus the structural contract consumers decompose and rebuild through.

/**
 * The tuple family's shared face. Construction goes through {@link #of};
 * the per-arity classes carry vavr-shaped accessors ({@code _1()} and the
 * {@code _1} field) and {@code apply} destructuring. The structural
 * contract — {@link #arity()}, {@link #get(int)}, {@link #withMembers} —
 * is what generic consumers decompose and rebuild through: one code path
 * for every arity, no per-class tables, and a new arity cannot be
 * half-supported because the contract lives on the type itself.
 */
public interface Tuple {

	int arity();

	/** The i-th member, 1-indexed like the accessors; refuses out of range. */
	Object get(int i);

	/**
	 * Same arity, new cargo — the rebuild half of the structural contract;
	 * refuses a members array of any other length. Rebuild is inherently
	 * untyped (the cargo is the caller's claim), so the result is spoken
	 * through this interface.
	 */
	Tuple withMembers(Object[] members);

	static <T1> Tuple1<T1> of(T1 _1) {
		return new Tuple1<>(_1);
	}

	static <T1, T2> Tuple2<T1, T2> of(T1 _1, T2 _2) {
		return new Tuple2<>(_1, _2);
	}

	static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 _1, T2 _2, T3 _3) {
		return new Tuple3<>(_1, _2, _3);
	}

	static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(T1 _1, T2 _2, T3 _3, T4 _4) {
		return new Tuple4<>(_1, _2, _3, _4);
	}

	static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5) {
		return new Tuple5<>(_1, _2, _3, _4, _5);
	}

	static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> of(
			T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6) {
		return new Tuple6<>(_1, _2, _3, _4, _5, _6);
	}

	static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> of(
			T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7) {
		return new Tuple7<>(_1, _2, _3, _4, _5, _6, _7);
	}

	static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> of(
			T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7, T8 _8) {
		return new Tuple8<>(_1, _2, _3, _4, _5, _6, _7, _8);
	}

	/**
	 * The untyped door: a tuple of the members' arity, refusing arity 0.
	 * Dispatch is deterministic — 1-8 always the typed classes, wider
	 * always {@link TupleN} — so equal arities never meet as different
	 * shapes. The typed overloads win for 1-8 literal arguments; this one
	 * serves arrays and arities beyond 8.
	 */
	static Tuple of(Object... members) {
		switch (members.length) {
			case 1:
				return of(members[0]);
			case 2:
				return of(members[0], members[1]);
			case 3:
				return of(members[0], members[1], members[2]);
			case 4:
				return of(members[0], members[1], members[2], members[3]);
			case 5:
				return of(members[0], members[1], members[2], members[3], members[4]);
			case 6:
				return of(members[0], members[1], members[2], members[3], members[4],
						members[5]);
			case 7:
				return of(members[0], members[1], members[2], members[3], members[4],
						members[5], members[6]);
			case 8:
				return of(members[0], members[1], members[2], members[3], members[4],
						members[5], members[6], members[7]);
			case 0:
				throw new IllegalArgumentException("no tuple of arity 0");
			default:
				return new TupleN(members);
		}
	}
}
