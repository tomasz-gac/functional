package com.tgac.functional.monad;

import com.tgac.functional.category.Monad;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class IdentityM<A> implements Monad<IdentityM<?>, A>, Supplier<A> {
	A value;

	@Override
	public <B> IdentityM<B> flatMap(Function<? super A, ? extends Monad<IdentityM<?>, B>> f) {
		return f.apply(value).cast();
	}

	@Override
	public <B> IdentityM<B> pure(B value) {
		return IdentityM.of(value);
	}

	@Override
	public A get() {
		return value;
	}
}
