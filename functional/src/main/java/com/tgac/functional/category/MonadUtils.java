package com.tgac.functional.category;

import com.tgac.functional.monad.Either;
import com.tgac.functional.monad.Future;
import com.tgac.functional.monad.Option;
import com.tgac.functional.monad.Stream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MonadUtils {

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static <A> Option<A> asMonad(Optional<A> o) {
		return Option.of(o);
	}

	public static <A> Future<A> asMonad(CompletableFuture<A> v) {
		return Future.of(v);
	}

	public static <A> Stream<A> asMonad(java.util.stream.Stream<A> s) {
		return Stream.of(s);
	}

	public static <L, R> Either<L, R> asLeft(L value) {
		return Either.left(value);
	}

	public static <L, R> Either<L, R> asRight(R value) {
		return Either.right(value);
	}
}
