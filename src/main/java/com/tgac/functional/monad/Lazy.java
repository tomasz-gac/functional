package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class Lazy<A> implements Monad<Lazy<?>, A>, Supplier<A> {
	Either<Supplier<Lazy<A>>, A> value;

	public static <A> Lazy<A> more(Supplier<Lazy<A>> more) {
		return Lazy.of(Either.left(more));
	}

	public static <A> Lazy<A> defer(Supplier<A> deferred) {
		return Lazy.of(Either.left(() -> done(deferred.get())));
	}

	public static <A> Lazy<A> done(A value) {
		return Lazy.of(Either.right(value));
	}

	@Override
	public <B> Lazy<B> flatMap(Function<? super A, ? extends Monad<Lazy<?>, B>> f) {
		return Lazy.of(value
				.fold(
						l -> Either.left(() -> l.get().flatMap(f)),
						r -> {
							Monad<Lazy<?>, B> apply = f.apply(r);
							Either<Supplier<Lazy<B>>, B> value1 = apply.<Lazy<B>> cast().value;
							return value1;
						}));
	}

	@Override
	public <B> Lazy<B> pure(B value) {
		return done(value);
	}

	private boolean isLeft() {
		return value.isLeft();
	}

	@Override
	public A get() {
		Lazy<A> next = value.fold(Supplier::get, Lazy::done);
		while (next.isLeft()) {
			next = next.getValue().fold(Supplier::get, Lazy::done);
		}

		return next.value.get();
	}
}
