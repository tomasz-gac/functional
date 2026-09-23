package org.clauseway.functional.tuples;

// ABOUTME: A 1-tuple: vavr-shaped accessor and apply over the structural
// ABOUTME: contract shared by all arities.

import java.util.function.Function;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Tuple1<T1> implements Tuple {

	public final T1 _1;

	Tuple1(T1 _1) {
		this._1 = _1;
	}

	public T1 _1() {
		return _1;
	}

	public <U> U apply(Function<? super T1, ? extends U> f) {
		return f.apply(_1);
	}

	public <U> Tuple1<U> map(Function<? super T1, ? extends U> f) {
		return new Tuple1<>(f.apply(_1));
	}

	@Override
	public int arity() {
		return 1;
	}

	@Override
	public Object get(int i) {
		if (i == 1) {
			return _1;
		}
		throw new IllegalArgumentException("no member " + i + " in a Tuple1");
	}

	@Override
	public Tuple withMembers(Object[] members) {
		if (members.length != 1) {
			throw new IllegalArgumentException(
					"a Tuple1 rebuild needs 1 member, got " + members.length);
		}
		return new Tuple1<>(members[0]);
	}

	@Override
	public String toString() {
		return "(" + _1 + ")";
	}
}
