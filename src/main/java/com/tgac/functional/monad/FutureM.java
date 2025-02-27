package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import com.tgac.functional.transformer.FutureMT;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FutureM<A> implements Monad<FutureM<?>, A>, Supplier<CompletableFuture<A>> {
	FutureMT<IdentityM<?>, A> value;

	public static <A> FutureM<A> of(CompletableFuture<A> f) {
		return new FutureM<>(FutureMT.of(IdentityM.of(f)));
	}

	public static <A> FutureM<A> just(A value) {
		return FutureM.of(CompletableFuture.completedFuture(value));
	}

	@Override
	public <B> FutureM<B> flatMap(Function<? super A, ? extends Monad<FutureM<?>, B>> f) {
		return new FutureM<>(value
				.flatMap(a -> ((FutureM<B>) f.apply(a)).value));
	}

	@Override
	public <B> FutureM<B> pure(B value) {
		return FutureM.of(CompletableFuture.completedFuture(value));
	}

	@Override
	public CompletableFuture<A> get() {
		return ((IdentityM<CompletableFuture<A>>) value.getValue()).getValue();
	}
}
