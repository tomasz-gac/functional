package com.tgac.functional.category;

import java.util.function.Function;

public interface Monad<M extends Monad<M, ?>, A> extends Functor<M, A> {
	<B> Monad<M, B> flatMap(Function<? super A, ? extends Monad<M, B>> f);

	default <B> Monad<M, B> map(Function<? super A, B> f) {
		return flatMap(a -> pure(f.apply(a)));
	}

	default <R> R to(Function<? super Monad<M, A>, R> f) {
		return f.apply(this);
	}

	<B> Monad<M, B> pure(B value);
}
