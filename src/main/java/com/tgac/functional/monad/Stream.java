package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import com.tgac.functional.transformer.StreamT;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Stream<A> implements Monad<Stream<?>, A>, Supplier<java.util.stream.Stream<A>> {
	StreamT<Identity<?>, A> value;

	public static <A> Stream<A> of(java.util.stream.Stream<A> values) {
		return new Stream<>(StreamT.of(Identity.of(values)));
	}

	public static <A> Stream<A> just(A value) {
		return new Stream<>(StreamT.just(Identity.of(value)));
	}

	@Override
	public <B> Stream<B> flatMap(Function<? super A, ? extends Monad<Stream<?>, B>> f) {
		return new Stream<>(value.flatMap(a -> ((Stream<B>) f.apply(a)).value));
	}

	@Override
	public <B> Monad<Stream<?>, B> pure(B value) {
		return of(java.util.stream.Stream.of(value));
	}

	@Override
	public java.util.stream.Stream<A> get() {
		return ((Identity<java.util.stream.Stream<A>>) value.getValue()).getValue();
	}

	public java.util.stream.Stream<A> toJavaStream() {
		return value.getValue()
				.<Identity<java.util.stream.Stream<A>>> cast()
				.get();
	}
}
