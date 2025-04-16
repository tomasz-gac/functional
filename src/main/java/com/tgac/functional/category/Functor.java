package com.tgac.functional.category;

import java.util.function.Function;

public interface Functor<F extends Functor<F, ?>, A> extends TypeConstructor<Functor<F, ?>, A> {
	default <B> Functor<F, B> map(Function<? super A, B> f) {
		throw new UnsupportedOperationException();
	}
}
