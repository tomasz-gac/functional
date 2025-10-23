package com.tgac.functional.transformer;

import com.tgac.functional.category.Monad;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class FutureT<M extends Monad<M, ?>, A> implements Monad<FutureT<M, ?>, A> {
	Monad<M, CompletableFuture<A>> value;

	public static <M extends Monad<M, ?>, A> FutureT<M, A> just(Monad<M, A> value) {
		return new FutureT<>(value.map(CompletableFuture::completedFuture));
	}

	@Override
	public <B> FutureT<M, B> flatMap(Function<? super A, ? extends Monad<FutureT<M, ?>, B>> f) {
		return new FutureT<>(value
				.flatMap(futureA ->
						f.apply(futureA.join())
								.<FutureT<M, B>> cast()
								.value));
	}

	@Override
	public <B> FutureT<M, B> pure(B value) {
		return just(this.value.pure(value));
	}

}
