package com.tgac.functional.step;

import static com.tgac.functional.recursion.Recur.done;

import com.tgac.functional.category.Monad;
import com.tgac.functional.recursion.Recur;
import io.vavr.collection.Array;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Empty<A> implements Step<A> {
	@SuppressWarnings("rawtypes")
	private static final Empty INSTANCE = new Empty();

	@SuppressWarnings("unchecked")
	public static <A> Empty<A> instance() {
		return INSTANCE;
	}

	@Override
	public <R> R accept(Visitor<A, R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public <B> Step<B> flatMap(Function<? super A, ? extends Monad<Step<?>, B>> f) {
		return Empty.instance();
	}

	@Override
	public <B> Step<B> map(Function<? super A, B> f) {
		return instance();
	}

	@Override
	public Step<A> append(Step<A> rhs) {
		return rhs;
	}

	@Override
	public Recur<Step<A>> interleave(Array<Step<A>> rest) {
		if (rest.isEmpty()) {
			return Recur.done(this);
		} else {
			return Recur.recur(() -> rest.head().interleave(rest.tail()));
		}
	}

	@Override
	public Recur<Step<A>> bind(Function<A, Step<A>> f) {
		return done(Empty.instance());
	}

}
