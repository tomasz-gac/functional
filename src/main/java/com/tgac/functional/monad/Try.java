package com.tgac.functional.monad;

import com.tgac.functional.Exceptions;
import com.tgac.functional.category.Monad;
import com.tgac.functional.transformer.EitherT;
import com.tgac.functional.transformer.TryT;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Try<A> implements Monad<Try<?>, A>, Supplier<A> {
	TryT<Identity<?>, A> value;

	public static <A> Try<A> of(Either<Throwable, A> value) {
		return new Try<>(TryT.of(value.fold(
				t -> EitherT.left(Identity.of(t)),
				a -> EitherT.right(Identity.of(a)))));
	}

	public static <A> Try<A> of(Callable<A> callable) {
		return new Try<>(TryT.<Identity<?>, A> of(
				() -> Identity.of(callable.call()),
				Identity::of));
	}

	public static <A> Try<A> just(A value) {
		return Try.of(Either.right(value));
	}

	public static <A> Try<A> fail(Throwable t) {
		return Try.of(Either.left(t));
	}

	@Override
	public <B> Try<B> flatMap(Function<? super A, ? extends Monad<Try<?>, B>> f) {
		return new Try<>(value
				.flatMap(a -> ((Try<B>) f.apply(a)).value));
	}

	@Override
	public <B> Try<B> pure(B value) {
		return Try.just(value);
	}

	@Override
	public A get() {
		return value.getValue()
				.fold(Exceptions::throwNow, Function.identity())
				.<Identity<A>> cast()
				.get();
	}

	public Throwable getException() {
		return value.getValue()
				.fold(Function.identity(), Exceptions.throwingFunction(NoSuchElementException::new))
				.<Identity<Throwable>> cast()
				.get();
	}

	public Either<A, Throwable> swap() {
		return value.swap()
				.getValue()
				.<Identity<EitherT.Choice<A, Throwable>>> cast()
				.get()
				.fold(Either::left, Either::right);
	}

	public <R> R fold(Function<Throwable, R> onException, Function<A, R> onSuccess) {
		return value.fold(onException, onSuccess)
				.<Identity<R>> cast()
				.get();
	}

}
