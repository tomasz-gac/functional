package org.clauseway.functional.tuples;

// ABOUTME: A 6-ary function: the apply seat of Tuple6, mirroring vavr's
// ABOUTME: arity family over plain Java.

@FunctionalInterface
public interface Function6<T1, T2, T3, T4, T5, T6, R> {

	R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);
}
