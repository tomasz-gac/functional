package com.tgac.functional.monad;

import com.tgac.functional.Exceptions;
import com.tgac.functional.category.Monad;
import com.tgac.functional.transformer.EitherMT;
import com.tgac.functional.transformer.TryMT;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TryM<A> implements Monad<TryM<?>, A>, Supplier<A> {
	TryMT<IdentityM<?>, A> value;

	public static <A> TryM<A> of(EitherM<Throwable, A> value) {
		return new TryM<>(TryMT.of(value.fold(
				t -> EitherMT.left(IdentityM.of(t)),
				a -> EitherMT.right(IdentityM.of(a)))));
	}

	public static <A> TryM<A> of(Callable<A> callable) {
		return new TryM<>(TryMT.<IdentityM<?>, A> of(
				() -> IdentityM.of(callable.call()),
				IdentityM::of));
	}

	public static <A> TryM<A> just(A value) {
		return TryM.of(EitherM.right(value));
	}

	public static <A> TryM<A> fail(Throwable t) {
		return TryM.of(EitherM.left(t));
	}

	@Override
	public <B> TryM<B> flatMap(Function<? super A, ? extends Monad<TryM<?>, B>> f) {
		return new TryM<>(value
				.flatMap(a -> ((TryM<B>) f.apply(a)).value));
	}

	@Override
	public <B> TryM<B> pure(B value) {
		return TryM.just(value);
	}

	@Override
	public A get() {
		return value.getValue()
				.fold(Exceptions::throwNow, Function.identity())
				.<IdentityM<A>> cast()
				.get();
	}

	public Throwable getException() {
		return value.getValue()
				.fold(Function.identity(), Exceptions.throwingFunction(NoSuchElementException::new))
				.<IdentityM<Throwable>> cast()
				.get();
	}

	public EitherM<A, Throwable> swap() {
		return value.swap()
				.getValue()
				.<IdentityM<EitherMT.Choice<A, Throwable>>> cast()
				.get()
				.fold(EitherM::left, EitherM::right);
	}

	public <R> R fold(Function<Throwable, R> onException, Function<A, R> onSuccess) {
		return value.fold(onException, onSuccess)
				.<IdentityM<R>> cast()
				.get();
	}

}
