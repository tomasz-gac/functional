package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StateM<S, R> implements Monad<StateM<S, ?>, R>, Function<S, StateM.StateAndResult<S, R>> {
	Function<S, StateAndResult<S, R>> f;

	public static <S, R> StateM<S, R> just(R value) {
		return StateM.of(s -> StateM.StateAndResult.of(s, value));
	}

	@Override
	public <B> Monad<StateM<S, ?>, B> flatMap(Function<? super R, ? extends Monad<StateM<S, ?>, B>> f) {
		return StateM.of(s -> {
			StateAndResult<S, R> nextState = this.f.apply(s);
			return f.apply(nextState.getResult())
					.<StateM<S, B>> cast()
					.f.apply(nextState.getState());
		});
	}

	@Override
	public <B> StateM<S, B> pure(B value) {
		return StateM.of(s -> StateAndResult.of(s, value));
	}

	@Override
	public StateAndResult<S, R> apply(S s) {
		return f.apply(s);
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	public static class StateAndResult<S, R> {
		S state;
		R result;
	}
}
