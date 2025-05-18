package com.tgac.functional.monad;

import com.tgac.functional.category.Functor;
import com.tgac.functional.category.Monad;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public abstract class Free<F extends Functor<F, ?>, A> implements Monad<Free<F, ?>, A> {

	@Override
	public <B> Free<F, B> pure(B b) {
		return new Pure<>(b);
	}

	public abstract <R> R accept(Visitor<F, A, R> visitor);

	public interface Visitor<F extends Functor<F, ?>, A, R>{
		R visit(Pure<F, A> pure);
		R visit(Suspend<F, A> pure);
	}

	public static <F extends Functor<F, ?>, A> Free<F, A> liftF(Functor<F, A> f){
		return new Suspend<>(f.map(Pure::new));
	}

	@Value
	@EqualsAndHashCode(callSuper = true)
	@RequiredArgsConstructor
	public static class Pure<F extends Functor<F, ?>, A> extends Free<F, A> {
		A value;

		public A get() {
			return value;
		}

		@Override
		public <B> Free<F, B> flatMap(Function<? super A, ? extends Monad<Free<F, ?>, B>> f) {
			return f.apply(value).cast();
		}

		@Override
		public <R> R accept(Visitor<F, A, R> visitor) {
			return visitor.visit(this);
		}
	}

	@Value
	@EqualsAndHashCode(callSuper = true)
	@RequiredArgsConstructor
	public static class Suspend<F extends Functor<F, ?>, A> extends Free<F, A> {
		Functor<F, Free<F, A>> suspended;

		@Override
		public <B> Free<F, B> flatMap(Function<? super A, ? extends Monad<Free<F, ?>, B>> f) {
			return new Suspend<>(suspended.map(freeA -> freeA.flatMap(f).cast()));
		}

		@Override
		public <R> R accept(Visitor<F, A, R> visitor) {
			return visitor.visit(this);
		}
	}
}