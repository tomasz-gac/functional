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
public class State<S, R> implements Monad<State<S, ?>, R>, Function<S, State.StateAndResult<S, R>> {
	Function<S, StateAndResult<S, R>> f;

	public static <S, R> State<S, R> just(R value) {
		return State.of(s -> State.StateAndResult.of(s, value));
	}

	@Override
	public <B> Monad<State<S, ?>, B> flatMap(Function<? super R, ? extends Monad<State<S, ?>, B>> f) {
		return State.of(s -> {
			StateAndResult<S, R> nextState = this.f.apply(s);
			return f.apply(nextState.getResult())
					.<State<S, B>> cast()
					.f.apply(nextState.getState());
		});
	}

	@Override
	public <B> State<S, B> pure(B value) {
		return State.of(s -> StateAndResult.of(s, value));
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
