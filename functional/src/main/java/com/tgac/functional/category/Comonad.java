package com.tgac.functional.category;

import java.util.function.Function;

public interface Comonad<C extends Comonad<C, ?>, A> extends Functor<Comonad<C, ?>, A> {
	A extract();

	<B> Comonad<C, B> extend(Function<? super Comonad<C, A>, B> f);

	default <B> Comonad<C, B> map(Function<? super A, B> f) {
		return extend(t -> f.apply(extract()));
	}
}
