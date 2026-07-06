package com.tgac.functional.step;

import com.tgac.functional.category.Monad;
import com.tgac.functional.fibers.Fiber;
import io.vavr.collection.Array;
import java.util.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(staticName = "of")
public class Cons<A> implements Step<A> {
	@NonNull
	A head;
	@NonNull
	Step<A> tail;

	@Override
	public <R> R accept(Visitor<A, R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public <B> Step<B> map(Function<? super A, B> f) {
		return Cons.of(f.apply(head), Step.incomplete(() -> Fiber.done(tail.map(f))));
	}

	@Override
	public Step<A> append(Step<A> rhs) {
		return Step.cons(head,
				Step.incomplete(() -> Fiber.done(tail.isEmpty() ? rhs : tail.append(rhs))));
	}

	@Override
	public <B> Step<B> flatMap(Function<? super A, ? extends Monad<Step<?>, B>> f) {
		return f.apply(head).<Step<B>> cast().append(Step.incomplete(() -> Fiber.done(tail.flatMap(f))));
	}

	@Override
	public Fiber<Step<A>> interleave(Array<Step<A>> rest) {
		if (rest.isEmpty()) {
			return Fiber.done(this);
		} else {
			return Fiber.done(Cons.of(head,
					Step.incomplete(() -> rest.head().interleave(rest.tail().append(tail)))));
		}
	}

	@Override
	public Fiber<Step<A>> bind(Function<A, Step<A>> f) {
		return f.apply(head)
				.interleave(Array.of(
						Incomplete.of(() -> tail.bind(f))));
	}
}
