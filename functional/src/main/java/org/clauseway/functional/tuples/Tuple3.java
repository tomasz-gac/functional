package org.clauseway.functional.tuples;

// ABOUTME: A 3-tuple: vavr-shaped accessors and apply over the structural
// ABOUTME: contract shared by all arities.

import java.util.function.Function;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Tuple3<T1, T2, T3> implements Tuple {

	public final T1 _1;
	public final T2 _2;
	public final T3 _3;

	Tuple3(T1 _1, T2 _2, T3 _3) {
		this._1 = _1;
		this._2 = _2;
		this._3 = _3;
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

	public <U> U apply(Function3<? super T1, ? super T2, ? super T3, ? extends U> f) {
		return f.apply(_1, _2, _3);
	}

	public <U1, U2, U3> Tuple3<U1, U2, U3> map(
			Function<? super T1, ? extends U1> f1,
			Function<? super T2, ? extends U2> f2,
			Function<? super T3, ? extends U3> f3) {
		return new Tuple3<>(f1.apply(_1), f2.apply(_2), f3.apply(_3));
	}

	public <U> Tuple3<U, T2, T3> map1(Function<? super T1, ? extends U> f) {
		return new Tuple3<>(f.apply(_1), _2, _3);
	}

	public <U> Tuple3<T1, U, T3> map2(Function<? super T2, ? extends U> f) {
		return new Tuple3<>(_1, f.apply(_2), _3);
	}

	public <U> Tuple3<T1, T2, U> map3(Function<? super T3, ? extends U> f) {
		return new Tuple3<>(_1, _2, f.apply(_3));
	}

	@Override
	public int arity() {
		return 3;
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
			default:
				throw new IllegalArgumentException("no member " + i + " in a Tuple3");
		}
	}

	@Override
	public Tuple withMembers(Object[] members) {
		if (members.length != 3) {
			throw new IllegalArgumentException(
					"a Tuple3 rebuild needs 3 members, got " + members.length);
		}
		return new Tuple3<>(members[0], members[1], members[2]);
	}

	@Override
	public String toString() {
		return "(" + _1 + ", " + _2 + ", " + _3 + ")";
	}
}
