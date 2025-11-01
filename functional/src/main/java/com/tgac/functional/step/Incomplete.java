package com.tgac.functional.step;

import static com.tgac.functional.recursion.Fiber.done;
import static com.tgac.functional.recursion.Fiber.defer;

import com.tgac.functional.category.Monad;
import com.tgac.functional.recursion.Fiber;
import io.vavr.collection.Array;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Incomplete<A> implements Step<A> {
	@NonNull
	private Fiber<Step<A>> rest;

	public static <A> Incomplete<A> of(Supplier<Fiber<Step<A>>> inc) {
		return new Incomplete<>(defer(inc));
	}

	@Override
	public <R> R accept(Visitor<A, R> visitor) {
		return visitor.visit(this);
	}

	public synchronized Step<A> getOrEvaluate() {
		if (!rest.isDone()) {
			rest = done(eval(rest.get()));
		}
		return rest.get();
	}

	private static <T> Step<T> eval(Step<T> stream) {
		while (stream instanceof Incomplete) {
			stream = ((Incomplete<T>) stream).getOrEvaluate();
		}
		return stream;
	}

	@Override
	public boolean isEmpty() {
		return getOrEvaluate().isEmpty();
	}

	@Override
	public <B> Step<B> flatMap(Function<? super A, ? extends Monad<Step<?>, B>> f) {
		return Incomplete.of(() -> rest.map(s -> s.flatMap(f)));
	}

	@Override
	public <B> Step<B> map(Function<? super A, B> f) {
		return Incomplete.of(() -> rest.map(s -> s.map(f)));
	}

	@Override
	public Step<A> append(Step<A> rhs) {
		return Incomplete.of(() -> rest.map(rest -> rest.append(rhs)));
	}

	@Override
	public Fiber<Step<A>> interleave(Array<Step<A>> rest) {
		return getRest()
				.flatMap(s -> s.interleave(rest));
	}

	@Override
	public Fiber<Step<A>> bind(Function<A, Step<A>> f) {
		return getRest()
				.flatMap(s -> s.bind(f));
	}
}
