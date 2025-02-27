package com.tgac.functional.transformer;

import com.tgac.functional.category.Monad;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class FutureMT<M extends Monad<M, ?>, A> implements Monad<FutureMT<M, ?>, A> {
	Monad<M, CompletableFuture<A>> value;

	public static <M extends Monad<M, ?>, A> FutureMT<M, A> just(Monad<M, A> value) {
		return new FutureMT<>(value.map(CompletableFuture::completedFuture));
	}

	@Override
	public <B> FutureMT<M, B> flatMap(Function<? super A, ? extends Monad<FutureMT<M, ?>, B>> f) {
		return new FutureMT<>(value
				.flatMap(futureA ->
						f.apply(futureA.join())
								.<FutureMT<M, B>> cast()
								.value));
	}

	@Override
	public <B> FutureMT<M, B> pure(B value) {
		return just(this.value.pure(value));
	}

}
