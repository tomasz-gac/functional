package org.clauseway.functional.category;

import org.clauseway.functional.monad.Either;
import org.clauseway.functional.monad.Future;
import org.clauseway.functional.monad.Option;
import org.clauseway.functional.monad.Stream;
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
