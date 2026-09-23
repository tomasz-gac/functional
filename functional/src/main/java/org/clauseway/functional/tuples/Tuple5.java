package org.clauseway.functional.tuples;

// ABOUTME: A 5-tuple: vavr-shaped accessors and apply over the structural
// ABOUTME: contract shared by all arities.

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Tuple5<T1, T2, T3, T4, T5> implements Tuple {

	public final T1 _1;
	public final T2 _2;
	public final T3 _3;
	public final T4 _4;
	public final T5 _5;

	Tuple5(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5) {
		this._1 = _1;
		this._2 = _2;
		this._3 = _3;
		this._4 = _4;
		this._5 = _5;
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

	public <U> U apply(Function5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends U> f) {
		return f.apply(_1, _2, _3, _4, _5);
	}

	@Override
	public int arity() {
		return 5;
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
			default:
				throw new IllegalArgumentException("no member " + i + " in a Tuple5");
		}
	}

	@Override
	public Tuple withMembers(Object[] members) {
		if (members.length != 5) {
			throw new IllegalArgumentException(
					"a Tuple5 rebuild needs 5 members, got " + members.length);
		}
		return new Tuple5<>(members[0], members[1], members[2], members[3], members[4]);
	}

	@Override
	public String toString() {
		return "(" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ", " + _5 + ")";
	}
}
