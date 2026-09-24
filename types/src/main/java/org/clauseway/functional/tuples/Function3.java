package org.clauseway.functional.tuples;

// ABOUTME: A 3-ary function: the apply seat of Tuple3, mirroring vavr's
// ABOUTME: arity family over plain Java.

@FunctionalInterface
public interface Function3<T1, T2, T3, R> {

	R apply(T1 t1, T2 t2, T3 t3);
}
