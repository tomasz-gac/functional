package com.tgac.functional.monad;

import com.tgac.functional.Exceptions;
import com.tgac.functional.category.Monad;
import com.tgac.functional.transformer.EitherMT;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EitherM<L, R> implements Monad<EitherM<L, ?>, R> {
	EitherMT<IdentityM<?>, L, R> value;

	public static <L, R> EitherM<L, R> left(L left) {
		return new EitherM<>(EitherMT.left(IdentityM.of(left)));
	}

	public static <L, R> EitherM<L, R> right(R right) {
		return new EitherM<>(EitherMT.right(IdentityM.of(right)));
	}

	@Override
	public <R1> EitherM<L, R1> flatMap(Function<? super R, ? extends Monad<EitherM<L, ?>, R1>> f) {
		return new EitherM<>(
				value.flatMap(r -> ((EitherM<L, R1>) f.apply(r)).value));
	}

	@Override
	public <R1> EitherM<L, R1> pure(R1 value) {
		return new EitherM<>(this.value.pure(value));
	}

	public <S> S fold(
			Function<? super L, ? extends S> mapLeft,
			Function<? super R, ? extends S> mapRight) {
		return ((IdentityM<EitherMT.Choice<L, R>>) value.getValue())
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

	public EitherM<R, L> swap() {
		return new EitherM<>(value.swap());
	}
}
