package com.tgac.functional.transformer;

import com.tgac.functional.category.Monad;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class OptionT<M extends Monad<M, ?>, A> implements Monad<OptionT<M, ?>, A> {
	Monad<M, Optional<A>> value;

	public static <M extends Monad<M, ?>, A> OptionT<M, A> just(Monad<M, A> ma) {
		return new OptionT<>(ma.map(Optional::ofNullable));
	}

	public static <M extends Monad<M, ?>, A> OptionT<M, A> none(Function<Optional<A>, Monad<M, Optional<A>>> pure) {
		return new OptionT<>(pure.apply(Optional.empty()));
	}

	@Override
	public <B> OptionT<M, B> flatMap(Function<? super A, ? extends Monad<OptionT<M, ?>, B>> f) {
		return new OptionT<>(
				value.flatMap(o ->
						o.map(a -> f.apply(a)
										.<OptionT<M, B>> cast()
										.getValue())
								.orElseGet(() -> value.pure(Optional.empty()))));
	}

	@Override
	public <B> OptionT<M, B> map(Function<? super A, B> f) {
		return new OptionT<>(value.map(o -> o.map(f)));
	}

	@Override
	public <B> Monad<OptionT<M, ?>, B> pure(B value) {
		return new OptionT<>(this.value.pure(Optional.of(value)));
	}
}
