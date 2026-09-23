package org.clauseway.functional.tuples;

// ABOUTME: A 4-tuple: vavr-shaped accessors and apply over the structural
// ABOUTME: contract shared by all arities.

import java.util.function.Function;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Tuple4<T1, T2, T3, T4> implements Tuple {

	public final T1 _1;
	public final T2 _2;
	public final T3 _3;
	public final T4 _4;

	Tuple4(T1 _1, T2 _2, T3 _3, T4 _4) {
		this._1 = _1;
		this._2 = _2;
		this._3 = _3;
		this._4 = _4;
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

	public <U> U apply(Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends U> f) {
		return f.apply(_1, _2, _3, _4);
	}

	public <U1, U2, U3, U4> Tuple4<U1, U2, U3, U4> map(
			Function<? super T1, ? extends U1> f1,
			Function<? super T2, ? extends U2> f2,
			Function<? super T3, ? extends U3> f3,
			Function<? super T4, ? extends U4> f4) {
		return new Tuple4<>(f1.apply(_1), f2.apply(_2), f3.apply(_3), f4.apply(_4));
	}

	public <U> Tuple4<U, T2, T3, T4> map1(Function<? super T1, ? extends U> f) {
		return new Tuple4<>(f.apply(_1), _2, _3, _4);
	}

	public <U> Tuple4<T1, U, T3, T4> map2(Function<? super T2, ? extends U> f) {
		return new Tuple4<>(_1, f.apply(_2), _3, _4);
	}

	public <U> Tuple4<T1, T2, U, T4> map3(Function<? super T3, ? extends U> f) {
		return new Tuple4<>(_1, _2, f.apply(_3), _4);
	}

	public <U> Tuple4<T1, T2, T3, U> map4(Function<? super T4, ? extends U> f) {
		return new Tuple4<>(_1, _2, _3, f.apply(_4));
	}

	@Override
	public int arity() {
		return 4;
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
			default:
				throw new IllegalArgumentException("no member " + i + " in a Tuple4");
		}
	}

	@Override
	public Tuple withMembers(Object[] members) {
		if (members.length != 4) {
			throw new IllegalArgumentException(
					"a Tuple4 rebuild needs 4 members, got " + members.length);
		}
		return new Tuple4<>(members[0], members[1], members[2], members[3]);
	}

	@Override
	public String toString() {
		return "(" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ")";
	}
}
