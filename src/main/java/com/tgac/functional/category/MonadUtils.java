package com.tgac.functional.category;

import com.tgac.functional.monad.EitherM;
import com.tgac.functional.monad.FutureM;
import com.tgac.functional.monad.OptionM;
import com.tgac.functional.monad.StreamM;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class MonadUtils {

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static <A> OptionM<A> asMonad(Optional<A> o) {
		return OptionM.of(o);
	}

	public static <A> FutureM<A> asMonad(CompletableFuture<A> v) {
		return FutureM.of(v);
	}

	public static <A> StreamM<A> asMonad(Stream<A> s) {
		return StreamM.of(s);
	}

	public static <L, R> EitherM<L, R> asLeft(L value) {
		return EitherM.left(value);
	}

	public static <L, R> EitherM<L, R> asRight(R value) {
		return EitherM.right(value);
	}
}
