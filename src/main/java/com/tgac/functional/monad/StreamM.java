package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import com.tgac.functional.transformer.StreamMT;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamM<A> implements Monad<StreamM<?>, A>, Supplier<Stream<A>> {
	StreamMT<IdentityM<?>, A> value;

	public static <A> StreamM<A> of(Stream<A> values) {
		return new StreamM<>(StreamMT.of(IdentityM.of(values)));
	}

	public static <A> StreamM<A> just(A value) {
		return new StreamM<>(StreamMT.just(IdentityM.of(value)));
	}

	@Override
	public <B> StreamM<B> flatMap(Function<? super A, ? extends Monad<StreamM<?>, B>> f) {
		return new StreamM<>(value.flatMap(a -> ((StreamM<B>) f.apply(a)).value));
	}

	@Override
	public <B> Monad<StreamM<?>, B> pure(B value) {
		return of(Stream.of(value));
	}

	@Override
	public Stream<A> get() {
		return ((IdentityM<Stream<A>>) value.getValue()).getValue();
	}

	public Stream<A> toJavaStream() {
		return value.getValue()
				.<IdentityM<Stream<A>>> cast()
				.get();
	}
}
