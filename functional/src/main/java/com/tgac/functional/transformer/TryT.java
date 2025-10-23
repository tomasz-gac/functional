package com.tgac.functional.transformer;

import com.tgac.functional.category.Monad;
import java.util.concurrent.Callable;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class TryT<M extends Monad<M, ?>, A> implements Monad<TryT<M, ?>, A> {
	EitherT<M, Throwable, A> value;

	public static <M extends Monad<M, ?>, A> TryT<M, A> of(
			Callable<Monad<M, A>> callable,
			Function<Throwable, ? extends Monad<M, Throwable>> pure) {
		try {
			return success(callable.call());
		} catch (Throwable t) {
			return failure(pure.apply(t));
		}
	}

	public static <M extends Monad<M, ?>, A> TryT<M, A> success(Monad<M, A> ma) {
		return TryT.of(EitherT.right(ma));
	}

	public static <M extends Monad<M, ?>, A> TryT<M, A> failure(Monad<M, Throwable> mt) {
		return TryT.of(EitherT.left(mt));
	}

	@Override
	public <B> TryT<M, B> flatMap(Function<? super A, ? extends Monad<TryT<M, ?>, B>> f) {
		return new TryT<>(value
				.flatMap(a -> {
					try {
						return f.apply(a)
								.<TryT<M, B>> cast()
								.getValue();
					} catch (Throwable t) {
						return TryT.<M, B> failure(value.getValue().pure(t))
								.getValue();
					}
				}));
	}

	@Override
	public <B> TryT<M, B> pure(B value) {
		return TryT.of(this.value.pure(value));
	}

	public EitherT<M, A, Throwable> swap() {
		return value.swap();
	}

	public <R> Monad<M, R> fold(Function<Throwable, R> onException, Function<A, R> onSuccess) {
		return value.fold(onException, onSuccess);
	}

}
