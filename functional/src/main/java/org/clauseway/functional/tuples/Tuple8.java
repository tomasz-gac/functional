package org.clauseway.functional.tuples;

// ABOUTME: A 8-tuple: vavr-shaped accessors and apply over the structural
// ABOUTME: contract shared by all arities.

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> implements Tuple {

	public final T1 _1;
	public final T2 _2;
	public final T3 _3;
	public final T4 _4;
	public final T5 _5;
	public final T6 _6;
	public final T7 _7;
	public final T8 _8;

	Tuple8(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7, T8 _8) {
		this._1 = _1;
		this._2 = _2;
		this._3 = _3;
		this._4 = _4;
		this._5 = _5;
		this._6 = _6;
		this._7 = _7;
		this._8 = _8;
	}

	public T1 _1() {
		return _1;
	}

	public T2 _2() {
		return _2;
	}

	public T3 _3() {
		return _3;
	}

	public T4 _4() {
		return _4;
	}

	public T5 _5() {
		return _5;
	}

	public T6 _6() {
		return _6;
	}

	public T7 _7() {
		return _7;
	}

	public T8 _8() {
		return _8;
	}

	public <U> U apply(Function8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends U> f) {
		return f.apply(_1, _2, _3, _4, _5, _6, _7, _8);
	}

	@Override
	public int arity() {
		return 8;
	}

	@Override
	public Object get(int i) {
		switch (i) {
			case 1:
				return _1;
			case 2:
				return _2;
			case 3:
				return _3;
			case 4:
				return _4;
			case 5:
				return _5;
			case 6:
				return _6;
			case 7:
				return _7;
			case 8:
				return _8;
			default:
				throw new IllegalArgumentException("no member " + i + " in a Tuple8");
		}
	}

	@Override
	public Tuple withMembers(Object[] members) {
		if (members.length != 8) {
			throw new IllegalArgumentException(
					"a Tuple8 rebuild needs 8 members, got " + members.length);
		}
		return new Tuple8<>(members[0], members[1], members[2], members[3], members[4], members[5], members[6], members[7]);
	}

	@Override
	public String toString() {
		return "(" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ", " + _5 + ", " + _6 + ", " + _7 + ", " + _8 + ")";
	}
}
