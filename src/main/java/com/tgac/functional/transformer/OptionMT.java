package com.tgac.functional.transformer;

import com.tgac.functional.category.Monad;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class OptionMT<M extends Monad<M, ?>, A> implements Monad<OptionMT<M, ?>, A> {
	Monad<M, Optional<A>> value;

	public static <M extends Monad<M, ?>, A> OptionMT<M, A> just(Monad<M, A> ma) {
		return new OptionMT<>(ma.map(Optional::ofNullable));
	}

	public static <M extends Monad<M, ?>, A> OptionMT<M, A> none(Function<Optional<A>, Monad<M, Optional<A>>> pure) {
		return new OptionMT<>(pure.apply(Optional.empty()));
	}

	@Override
	public <B> OptionMT<M, B> flatMap(Function<? super A, ? extends Monad<OptionMT<M, ?>, B>> f) {
		return new OptionMT<>(
				value.flatMap(o ->
						o.map(a -> f.apply(a)
										.<OptionMT<M, B>> cast()
										.getValue())
								.orElseGet(() -> value.pure(Optional.empty()))));
	}

	@Override
	public <B> OptionMT<M, B> map(Function<? super A, B> f) {
		return new OptionMT<>(value.map(o -> o.map(f)));
	}

	@Override
	public <B> Monad<OptionMT<M, ?>, B> pure(B value) {
		return new OptionMT<>(this.value.pure(Optional.of(value)));
	}
}
