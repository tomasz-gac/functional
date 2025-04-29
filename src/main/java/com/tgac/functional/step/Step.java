package com.tgac.functional.step;

import com.tgac.functional.Exceptions;
import com.tgac.functional.category.Monad;
import com.tgac.functional.recursion.Recur;
import io.vavr.collection.Array;
import io.vavr.control.Option;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
public interface Step<A> extends Monad<Step<?>, A> {

	@Override
	default <B> Step<B> pure(B value) {
		return Single.of(value);
	}

	<R> R accept(Visitor<A, R> visitor);

	boolean isEmpty();

	<B> Step<B> flatMap(Function<? super A, ? extends Monad<Step<?>, B>> f);

	<B> Step<B> map(Function<? super A, B> f);

	Step<A> append(Step<A> rhs);

	Recur<Step<A>> interleave(Array<Step<A>> rest);

	Recur<Step<A>> bind(Function<A, Step<A>> f);

	default Stream<A> stream() {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(
						new StepIterator<>(this),
						Spliterator.NONNULL & Spliterator.ORDERED),
				false);
	}

	static <A> Step<A> empty() {
		return Empty.instance();
	}

	static <A> Step<A> single(A v) {
		return Single.of(v);
	}

	static <A> Step<A> incomplete(Supplier<Recur<Step<A>>> s) {
		return Incomplete.of(s);
	}

	static <A> Cons<A> cons(A head, Step<A> tail) {
		return Cons.of(head, tail);
	}

	static <A> Step<A> ofAll(List<A> items) {
		return IntStream.range(0, items.size())
				.mapToObj(i -> items.get(items.size() - 1 - i))
				.reduce(Step.empty(),
						(tail, head) -> Step.cons(head, tail),
						Exceptions.throwingBiOp(UnsupportedOperationException::new));
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	static <A> Step<A> of(Optional<A> opt) {
		return opt.map(Step::single).orElseGet(Step::empty);
	}

	static <A> Step<A> of(Option<A> opt) {
		return opt.map(Step::single).getOrElse(Step::empty);
	}

	interface Visitor<A, R> {
		R visit(Empty<A> empty);

		R visit(Incomplete<A> inc);

		R visit(Single<A> single);

		R visit(Cons<A> cons);
	}
}
