package com.tgac.functional.recursion;

import com.tgac.functional.Reference;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
public class Recur<A> implements Supplier<A> {
	protected final Either<A, Either<
			Supplier<Recur<A>>, FlatMap<Object, A>>> eval;

	public static <A> Recur<A> done(A v) {
		return Recur.of(Either.left(v));
	}

	public static <A> Recur<A> recur(Supplier<Recur<A>> rec) {
		return Recur.of(
				Either.right(
						Either.left(rec)));
	}

	@Override
	@SneakyThrows
	public A get() {
		return toEngine().get();
	}

	public <B> Recur<B> flatMap(Function<A, Recur<B>> f) {
		return Recur.of(
				Either.right(
						Either.right(FlatMap.of(f, this))));
	}

	public <B> Recur<B> map(Function<A, B> f) {
		return flatMap(v -> done(f.apply(v)));
	}

	public <B> B to(Function<Recur<A>, B> f) {
		return f.apply(this);
	}

	public boolean isDone() {
		return eval.isLeft();
	}

	@SuppressWarnings("unchecked")
	public Engine<A> toEngine() {
		return new Engine<>((Recur<Object>) this);
	}

	@Getter
	@ToString
	@EqualsAndHashCode
	@AllArgsConstructor
	@FieldDefaults(makeFinal = true, level = AccessLevel.MODULE)
	protected static class FlatMap<A, B> implements Function<A, Recur<B>> {

		Function<A, Recur<B>> f;
		Recur<A> arg;

		@SuppressWarnings("unchecked")
		public static <C, D> FlatMap<Object, D> of(Function<C, Recur<D>> f, Recur<C> r) {
			return new FlatMap<>(o -> f.apply((C) o), (Recur<Object>) r);
		}

		@Override
		public Recur<B> apply(A v) {
			return f.apply(v);
		}
	}

	public static <A, B> Recur<Tuple2<A, B>> zip(Recur<A> lhs, Recur<B> rhs) {
		return lhs.flatMap(l -> rhs.map(r -> Tuple.of(l, r)));
	}

	public static <T> Recur<T> cache(Supplier<Recur<T>> r) {
		Reference<T> cache = Reference.empty();
		return recur(() -> done(cache.get()))
				.flatMap(h -> Objects.nonNull(h) ? done(h) : r.get())
				.map(v -> {
					cache.set(v);
					return v;
				});
	}

	public static <A> Option<Recur<Iterable<A>>> lift(Iterable<Recur<A>> iterable) {
		return Stream.ofAll(iterable)
				.map(v -> v.map(Stream::of))
				.reduceOption((acc, item) -> Recur.zip(acc, item.map(Stream::head))
						.map(lr -> lr.apply(Stream::append)))
				// cast
				.map(r -> r.map(v -> v));
	}
}
