package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import com.tgac.functional.transformer.FutureT;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Future<A> implements Monad<Future<?>, A>, Supplier<CompletableFuture<A>> {
	FutureT<Identity<?>, A> value;

	public static <A> Future<A> of(CompletableFuture<A> f) {
		return new Future<>(FutureT.of(Identity.of(f)));
	}

	public static <A> Future<A> just(A value) {
		return Future.of(CompletableFuture.completedFuture(value));
	}

	@Override
	public <B> Future<B> flatMap(Function<? super A, ? extends Monad<Future<?>, B>> f) {
		return new Future<>(value
				.flatMap(a -> ((Future<B>) f.apply(a)).value));
	}

	@Override
	public <B> Future<B> pure(B value) {
		return Future.of(CompletableFuture.completedFuture(value));
	}

	@Override
	public CompletableFuture<A> get() {
		return ((Identity<CompletableFuture<A>>) value.getValue()).getValue();
	}
}
