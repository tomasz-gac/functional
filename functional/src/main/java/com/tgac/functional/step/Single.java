package com.tgac.functional.step;

import static com.tgac.functional.recursion.Recur.done;

import com.tgac.functional.category.Monad;
import com.tgac.functional.recursion.Recur;
import io.vavr.collection.Array;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class Single<A> implements Step<A> {
	A head;

	@Override
	public <R> R accept(Visitor<A, R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public <B> Step<B> flatMap(Function<? super A, ? extends Monad<Step<?>, B>> f) {
		return f.apply(head).cast();
	}

	@Override
	public <B> Step<B> map(Function<? super A, B> f) {
		return Single.of(f.apply(head));
	}

	@Override
	public Step<A> append(Step<A> rhs) {
		return Step.cons(head, rhs);
	}

	@Override
	public Recur<Step<A>> interleave(Array<Step<A>> rest) {
		if (rest.isEmpty()) {
			return Recur.done(this);
		} else {
			return Recur.done(
					Step.cons(head,
							Step.incomplete(() -> rest.head().interleave(rest.tail()))));
		}
	}

	@Override
	public Recur<Step<A>> bind(Function<A, Step<A>> f) {
		return done(f.apply(head));
	}
}
