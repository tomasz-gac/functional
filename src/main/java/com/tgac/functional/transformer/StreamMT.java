package com.tgac.functional.transformer;

import com.tgac.functional.category.Monad;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class StreamMT<M extends Monad<M, ?>, A> implements Monad<StreamMT<M, ?>, A> {
	Monad<M, Stream<A>> value;

	public static <M extends Monad<M, ?>, A> StreamMT<M, A> just(Monad<M, A> v) {
		return new StreamMT<>(v.map(Stream::of));
	}

	public static <M extends Monad<M, ?>, A> StreamMT<M, A> empty(Function<Stream<A>, Monad<M, Stream<A>>> pure) {
		return new StreamMT<>(pure.apply(Stream.empty()));
	}

	@Override
	public <B> StreamMT<M, B> flatMap(Function<? super A, ? extends Monad<StreamMT<M, ?>, B>> f) {
		return new StreamMT<>(value
				.flatMap(stream -> stream
						.map(a -> f.apply(a)
								.<StreamMT<M, B>> cast()
								.value)
						.reduce((lhs, rhs) -> lhs
								.flatMap(l -> rhs
										.flatMap(r -> value.pure(Stream.concat(l, r)))))
						.orElseGet(() -> value.pure(Stream.empty()))));
	}

	@Override
	public <B> StreamMT<M, B> pure(B value) {
		return new StreamMT<>(this.value.pure(Stream.of(value)));
	}
}
