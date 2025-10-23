package com.tgac.functional.monad;

import com.tgac.functional.Exceptions;
import com.tgac.functional.category.Monad;
import com.tgac.functional.transformer.EitherT;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Either<L, R> implements Monad<Either<L, ?>, R> {
	EitherT<Identity<?>, L, R> value;

	public static <L, R> Either<L, R> left(L left) {
		return new Either<>(EitherT.left(Identity.of(left)));
	}

	public static <L, R> Either<L, R> right(R right) {
		return new Either<>(EitherT.right(Identity.of(right)));
	}

	@Override
	public <R1> Either<L, R1> flatMap(Function<? super R, ? extends Monad<Either<L, ?>, R1>> f) {
		return new Either<>(
				value.flatMap(r -> ((Either<L, R1>) f.apply(r)).value));
	}

	@Override
	public <R1> Either<L, R1> pure(R1 value) {
		return new Either<>(this.value.pure(value));
	}

	public <S> S fold(
			Function<? super L, ? extends S> mapLeft,
			Function<? super R, ? extends S> mapRight) {
		return ((Identity<EitherT.Choice<L, R>>) value.getValue())
				.get()
				.fold(mapLeft, mapRight);
	}

	public boolean isRight() {
		return fold(l -> false, r -> true);
	}

	public boolean isLeft() {
		return !isRight();
	}

	public R get() {
		return fold(Exceptions.throwingFunction(IllegalStateException::new), Function.identity());
	}

	public L getLeft() {
		return fold(Function.identity(), Exceptions.throwingFunction(IllegalStateException::new));
	}

	public Either<R, L> swap() {
		return new Either<>(value.swap());
	}
}
