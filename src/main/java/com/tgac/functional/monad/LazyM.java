package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class LazyM<A> implements Monad<LazyM<?>, A>, Supplier<A> {
	EitherM<Supplier<LazyM<A>>, A> value;

	public static <A> LazyM<A> more(Supplier<LazyM<A>> more) {
		return LazyM.of(EitherM.left(more));
	}

	public static <A> LazyM<A> defer(Supplier<A> deferred) {
		return LazyM.of(EitherM.left(() -> done(deferred.get())));
	}

	public static <A> LazyM<A> done(A value) {
		return LazyM.of(EitherM.right(value));
	}

	@Override
	public <B> LazyM<B> flatMap(Function<? super A, ? extends Monad<LazyM<?>, B>> f) {
		return LazyM.of(value
				.fold(
						l -> EitherM.left(() -> l.get().flatMap(f)),
						r -> {
							Monad<LazyM<?>, B> apply = f.apply(r);
							EitherM<Supplier<LazyM<B>>, B> value1 = apply.<LazyM<B>> cast().value;
							return value1;
						}));
	}

	@Override
	public <B> LazyM<B> pure(B value) {
		return done(value);
	}

	private boolean isLeft() {
		return value.isLeft();
	}

	@Override
	public A get() {
		LazyM<A> next = value.fold(Supplier::get, LazyM::done);
		while (next.isLeft()) {
			next = next.getValue().fold(Supplier::get, LazyM::done);
		}

		return next.value.get();
	}
}
