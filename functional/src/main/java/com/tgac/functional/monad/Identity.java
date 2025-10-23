package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class Identity<A> implements Monad<Identity<?>, A>, Supplier<A> {
	A value;

	@Override
	public <B> Identity<B> flatMap(Function<? super A, ? extends Monad<Identity<?>, B>> f) {
		return f.apply(value).cast();
	}

	@Override
	public <B> Identity<B> pure(B value) {
		return Identity.of(value);
	}

	@Override
	public A get() {
		return value;
	}
}
