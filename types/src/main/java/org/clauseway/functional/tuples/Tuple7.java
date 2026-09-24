package org.clauseway.functional.tuples;

// ABOUTME: A 7-tuple: vavr-shaped accessors and apply over the structural
// ABOUTME: contract shared by all arities.

import java.util.function.Function;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Tuple7<T1, T2, T3, T4, T5, T6, T7> implements Tuple {

	public final T1 _1;
	public final T2 _2;
	public final T3 _3;
	public final T4 _4;
	public final T5 _5;
	public final T6 _6;
	public final T7 _7;

	Tuple7(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7) {
		this._1 = _1;
		this._2 = _2;
		this._3 = _3;
		this._4 = _4;
		this._5 = _5;
		this._6 = _6;
		this._7 = _7;
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

	public <U> U apply(Function7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends U> f) {
		return f.apply(_1, _2, _3, _4, _5, _6, _7);
	}

	public <U1, U2, U3, U4, U5, U6, U7> Tuple7<U1, U2, U3, U4, U5, U6, U7> map(
			Function<? super T1, ? extends U1> f1,
			Function<? super T2, ? extends U2> f2,
			Function<? super T3, ? extends U3> f3,
			Function<? super T4, ? extends U4> f4,
			Function<? super T5, ? extends U5> f5,
			Function<? super T6, ? extends U6> f6,
			Function<? super T7, ? extends U7> f7) {
		return new Tuple7<>(f1.apply(_1), f2.apply(_2), f3.apply(_3), f4.apply(_4), f5.apply(_5), f6.apply(_6), f7.apply(_7));
	}

	public <U> Tuple7<U, T2, T3, T4, T5, T6, T7> map1(Function<? super T1, ? extends U> f) {
		return new Tuple7<>(f.apply(_1), _2, _3, _4, _5, _6, _7);
	}

	public <U> Tuple7<T1, U, T3, T4, T5, T6, T7> map2(Function<? super T2, ? extends U> f) {
		return new Tuple7<>(_1, f.apply(_2), _3, _4, _5, _6, _7);
	}

	public <U> Tuple7<T1, T2, U, T4, T5, T6, T7> map3(Function<? super T3, ? extends U> f) {
		return new Tuple7<>(_1, _2, f.apply(_3), _4, _5, _6, _7);
	}

	public <U> Tuple7<T1, T2, T3, U, T5, T6, T7> map4(Function<? super T4, ? extends U> f) {
		return new Tuple7<>(_1, _2, _3, f.apply(_4), _5, _6, _7);
	}

	public <U> Tuple7<T1, T2, T3, T4, U, T6, T7> map5(Function<? super T5, ? extends U> f) {
		return new Tuple7<>(_1, _2, _3, _4, f.apply(_5), _6, _7);
	}

	public <U> Tuple7<T1, T2, T3, T4, T5, U, T7> map6(Function<? super T6, ? extends U> f) {
		return new Tuple7<>(_1, _2, _3, _4, _5, f.apply(_6), _7);
	}

	public <U> Tuple7<T1, T2, T3, T4, T5, T6, U> map7(Function<? super T7, ? extends U> f) {
		return new Tuple7<>(_1, _2, _3, _4, _5, _6, f.apply(_7));
	}

	@Override
	public int arity() {
		return 7;
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
			default:
				throw new IllegalArgumentException("no member " + i + " in a Tuple7");
		}
	}

	@Override
	public Tuple withMembers(Object[] members) {
		if (members.length != 7) {
			throw new IllegalArgumentException(
					"a Tuple7 rebuild needs 7 members, got " + members.length);
		}
		return new Tuple7<>(members[0], members[1], members[2], members[3], members[4], members[5], members[6]);
	}

	@Override
	public String toString() {
		return "(" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ", " + _5 + ", " + _6 + ", " + _7 + ")";
	}
}
