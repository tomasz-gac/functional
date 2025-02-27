package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import com.tgac.functional.transformer.OptionMT;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionM<A> implements Monad<OptionM<?>, A>, Supplier<Optional<A>> {
	OptionMT<IdentityM<?>, A> value;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static <A> OptionM<A> of(Optional<A> f) {
		return new OptionM<>(OptionMT.of(IdentityM.of(f)));
	}

	public static <A> OptionM<A> just(A value) {
		return of(Optional.ofNullable(value));
	}

	public static <A> OptionM<A> none() {
		return of(Optional.empty());
	}

	@Override
	public <B> OptionM<B> flatMap(Function<? super A, ? extends Monad<OptionM<?>, B>> f) {
		return new OptionM<>(value
				.flatMap(a -> ((OptionM<B>) f.apply(a)).value));
	}

	@Override
	public <B> OptionM<B> pure(B value) {
		return OptionM.just(value);
	}

	@Override
	public Optional<A> get() {
		return ((IdentityM<Optional<A>>) value.getValue()).getValue();
	}

	public <R> R fold(Supplier<R> onEmpty, Function<A, R> onSome) {
		return ((IdentityM<Optional<A>>) value.getValue()).getValue()
				.map(onSome)
				.orElseGet(onEmpty);
	}
}
