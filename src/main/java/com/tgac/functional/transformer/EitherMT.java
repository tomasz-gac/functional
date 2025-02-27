package com.tgac.functional.transformer;

import com.tgac.functional.category.Monad;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EitherMT<M extends Monad<M, ?>, L, R> implements Monad<EitherMT<M, L, ?>, R> {
	Monad<M, Choice<L, R>> value;

	public interface Choice<L, R> {
		<S> S fold(Function<? super L, ? extends S> lhs, Function<? super R, ? extends S> rhs);
	}

	public static <M extends Monad<M, ?>, L, R> EitherMT<M, L, R> left(Monad<M, L> left) {
		return new EitherMT<>(left.map(Left::of));
	}

	public static <M extends Monad<M, ?>, L, R> EitherMT<M, L, R> right(Monad<M, R> right) {
		return new EitherMT<>(right.map(Right::of));
	}

	public <R1> Monad<M, R1> fold(
			Function<? super L, R1> onLeft,
			Function<? super R, R1> onRight) {
		return value.map(e -> e.fold(onLeft, onRight));
	}

	@Override
	public <R1> EitherMT<M, L, R1> flatMap(Function<? super R, ? extends Monad<EitherMT<M, L, ?>, R1>> f) {
		return new EitherMT<>(value
				.flatMap(either -> either.fold(
						l -> value.pure(Left.of(l)),
						r -> f.apply(r).<EitherMT<M, L, R1>> cast().value
				)));
	}

	@Override
	public <R1> EitherMT<M, L, R1> pure(R1 value) {
		return new EitherMT<>(this.value.pure(Right.of(value)));
	}

	public EitherMT<M, R, L> swap() {
		return new EitherMT<>(value.map(e -> e.fold(Right::of, Left::of)));
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	private static class Left<L, R> implements EitherMT.Choice<L, R> {
		L value;

		@Override
		public <S> S fold(Function<? super L, ? extends S> lhs, Function<? super R, ? extends S> rhs) {
			return lhs.apply(value);
		}
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	private static class Right<L, R> implements EitherMT.Choice<L, R> {
		R value;

		@Override
		public <S> S fold(Function<? super L, ? extends S> lhs, Function<? super R, ? extends S> rhs) {
			return rhs.apply(value);
		}
	}
}
