package com.tgac.functional.transformer;

import com.tgac.functional.category.Monad;
import java.util.concurrent.Callable;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class TryMT<M extends Monad<M, ?>, A> implements Monad<TryMT<M, ?>, A> {
	EitherMT<M, Throwable, A> value;

	public static <M extends Monad<M, ?>, A> TryMT<M, A> of(
			Callable<Monad<M, A>> callable,
			Function<Throwable, ? extends Monad<M, Throwable>> pure) {
		try {
			return success(callable.call());
		} catch (Throwable t) {
			return failure(pure.apply(t));
		}
	}

	public static <M extends Monad<M, ?>, A> TryMT<M, A> success(Monad<M, A> ma) {
		return TryMT.of(EitherMT.right(ma));
	}

	public static <M extends Monad<M, ?>, A> TryMT<M, A> failure(Monad<M, Throwable> mt) {
		return TryMT.of(EitherMT.left(mt));
	}

	@Override
	public <B> TryMT<M, B> flatMap(Function<? super A, ? extends Monad<TryMT<M, ?>, B>> f) {
		return new TryMT<>(value
				.flatMap(a -> {
					try {
						return f.apply(a)
								.<TryMT<M, B>> cast()
								.getValue();
					} catch (Throwable t) {
						return TryMT.<M, B> failure(value.getValue().pure(t))
								.getValue();
					}
				}));
	}

	@Override
	public <B> TryMT<M, B> pure(B value) {
		return TryMT.of(this.value.pure(value));
	}

	public EitherMT<M, A, Throwable> swap() {
		return value.swap();
	}

	public <R> Monad<M, R> fold(Function<Throwable, R> onException, Function<A, R> onSuccess) {
		return value.fold(onException, onSuccess);
	}

}
