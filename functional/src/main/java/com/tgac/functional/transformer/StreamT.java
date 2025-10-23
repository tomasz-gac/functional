package com.tgac.functional.transformer;

import com.tgac.functional.category.Monad;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class StreamT<M extends Monad<M, ?>, A> implements Monad<StreamT<M, ?>, A> {
	Monad<M, Stream<A>> value;

	public static <M extends Monad<M, ?>, A> StreamT<M, A> just(Monad<M, A> v) {
		return new StreamT<>(v.map(Stream::of));
	}

	public static <M extends Monad<M, ?>, A> StreamT<M, A> empty(Function<Stream<A>, Monad<M, Stream<A>>> pure) {
		return new StreamT<>(pure.apply(Stream.empty()));
	}

	@Override
	public <B> StreamT<M, B> flatMap(Function<? super A, ? extends Monad<StreamT<M, ?>, B>> f) {
		return new StreamT<>(value
				.flatMap(stream -> stream
						.map(a -> f.apply(a)
								.<StreamT<M, B>> cast()
								.value)
						.reduce((lhs, rhs) -> lhs
								.flatMap(l -> rhs
										.flatMap(r -> value.pure(Stream.concat(l, r)))))
						.orElseGet(() -> value.pure(Stream.empty()))));
	}

	@Override
	public <B> StreamT<M, B> pure(B value) {
		return new StreamT<>(this.value.pure(Stream.of(value)));
	}
}
