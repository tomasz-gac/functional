package org.clauseway.functional.tuples;

// ABOUTME: A 2-tuple: vavr-shaped accessors and apply over the structural
// ABOUTME: contract shared by all arities.

import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Tuple2<T1, T2> implements Tuple {

	public final T1 _1;
	public final T2 _2;

	Tuple2(T1 _1, T2 _2) {
		this._1 = _1;
		this._2 = _2;
	}

	public T1 _1() {
		return _1;
	}

	public T2 _2() {
		return _2;
	}

	public <U> U apply(BiFunction<? super T1, ? super T2, ? extends U> f) {
		return f.apply(_1, _2);
	}

	public <U> Tuple2<U, T2> map1(Function<? super T1, ? extends U> f) {
		return new Tuple2<>(f.apply(_1), _2);
	}

	public <U> Tuple2<T1, U> map2(Function<? super T2, ? extends U> f) {
		return new Tuple2<>(_1, f.apply(_2));
	}

	public <U1, U2> Tuple2<U1, U2> map(BiFunction<? super T1, ? super T2, Tuple2<U1, U2>> f) {
		return f.apply(_1, _2);
	}

	public <U1, U2> Tuple2<U1, U2> map(
			Function<? super T1, ? extends U1> f1,
			Function<? super T2, ? extends U2> f2) {
		return new Tuple2<>(f1.apply(_1), f2.apply(_2));
	}

	@Override
	public int arity() {
		return 2;
	}

	@Override
	public Object get(int i) {
		switch (i) {
			case 1:
				return _1;
			case 2:
				return _2;
			default:
				throw new IllegalArgumentException("no member " + i + " in a Tuple2");
		}
	}

	@Override
	public Tuple withMembers(Object[] members) {
		if (members.length != 2) {
			throw new IllegalArgumentException(
					"a Tuple2 rebuild needs 2 members, got " + members.length);
		}
		return new Tuple2<>(members[0], members[1]);
	}

	@Override
	public String toString() {
		return "(" + _1 + ", " + _2 + ")";
	}
}
