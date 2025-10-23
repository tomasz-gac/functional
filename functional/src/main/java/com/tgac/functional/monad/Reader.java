package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Reader<R, A> implements Monad<Reader<R, ?>, A>, Function<R, A> {
	State<R, A> value;

	public static <S, R> Reader<S, R> just(R value) {
		return Reader.of(State.of(s -> State.StateAndResult.of(s, value)));
	}

	@Override
	public <B> Reader<R, B> flatMap(Function<? super A, ? extends Monad<Reader<R, ?>, B>> f) {
		return Reader.of(value
				.flatMap(a -> f.apply(a)
						.<Reader<R, B>> cast()
						.value)
				.cast());
	}

	@Override
	public <B> Reader<R, B> pure(B value) {
		return Reader.of(this.value.pure(value));
	}

	@Override
	public A apply(R r) {
		return value.apply(r).getResult();
	}
}
