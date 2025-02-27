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
public class ReaderM<R, A> implements Monad<ReaderM<R, ?>, A>, Function<R, A> {
	StateM<R, A> value;

	public static <S, R> ReaderM<S, R> just(R value) {
		return ReaderM.of(StateM.of(s -> StateM.StateAndResult.of(s, value)));
	}

	@Override
	public <B> ReaderM<R, B> flatMap(Function<? super A, ? extends Monad<ReaderM<R, ?>, B>> f) {
		return ReaderM.of(value
				.flatMap(a -> f.apply(a)
						.<ReaderM<R, B>> cast()
						.value)
				.cast());
	}

	@Override
	public <B> ReaderM<R, B> pure(B value) {
		return ReaderM.of(this.value.pure(value));
	}

	@Override
	public A apply(R r) {
		return value.apply(r).getResult();
	}
}
