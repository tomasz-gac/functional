package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import com.tgac.functional.transformer.OptionT;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Option<A> implements Monad<Option<?>, A>, Supplier<Optional<A>> {
	OptionT<Identity<?>, A> value;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static <A> Option<A> of(Optional<A> f) {
		return new Option<>(OptionT.of(Identity.of(f)));
	}

	public static <A> Option<A> just(A value) {
		return of(Optional.ofNullable(value));
	}

	public static <A> Option<A> none() {
		return of(Optional.empty());
	}

	@Override
	public <B> Option<B> flatMap(Function<? super A, ? extends Monad<Option<?>, B>> f) {
		return new Option<>(value
				.flatMap(a -> ((Option<B>) f.apply(a)).value));
	}

	@Override
	public <B> Option<B> pure(B value) {
		return Option.just(value);
	}

	@Override
	public Optional<A> get() {
		return ((Identity<Optional<A>>) value.getValue()).getValue();
	}

	public <R> R fold(Supplier<R> onEmpty, Function<A, R> onSome) {
		return ((Identity<Optional<A>>) value.getValue()).getValue()
				.map(onSome)
				.orElseGet(onEmpty);
	}
}
