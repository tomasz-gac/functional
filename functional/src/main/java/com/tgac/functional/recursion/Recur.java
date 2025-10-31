package com.tgac.functional.recursion;

import com.tgac.functional.Reference;
import com.tgac.functional.category.Monad;
import com.tgac.functional.category.Nothing;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.var;

public interface Recur<A> extends Monad<Recur<?>, A>, Supplier<A> {
	interface Fn<T, R> extends Function<T, Recur<R>> {
	}

	static <A> Recur<A> done(@NonNull A v) {
		return Done.of(v);
	}

	static <A> Recur<A> recur(Supplier<Recur<A>> rec) {
		return More.of(rec);
	}

	static <W, A> Recur<A> suspend(CompletableFuture<W> future, Function<W, Recur<A>> resume) {
		return new Suspended<>(future, resume);
	}

	@Override
	default <B> Recur<B> flatMap(Function<? super A, @NonNull ? extends Monad<Recur<?>, B>> f) {
		return FlatMap.of(f, this);
	}

	@Override
	default <B> Recur<B> map(Function<? super A, @NonNull B> f) {
		return flatMap(v -> done(f.apply(v)));
	}

	@Override
	default <B> Recur<B> pure(B value) {
		return done(value);
	}

	@Override
	@SneakyThrows
	default A get() {
		try (var e = toEngine()) {
			return e.get();
		}
	}

	default boolean isDone() {
		return false;
	}

	default Engine<A> toEngine() {
		return new BFSEngine<>(this);
	}

	static <A, B> Recur<Tuple2<A, B>> zip(Recur<A> lhs, Recur<B> rhs) {
		return lhs.flatMap(l -> rhs.map(r -> Tuple.of(l, r)));
	}

	static <T> Recur<T> cache(Supplier<Recur<T>> r) {
		Reference<T> cache = Reference.empty();
		return (Recur<T>) recur(() -> done(cache.get()))
				.flatMap(h -> Objects.nonNull(h) ? done(h) : r.get())
				.map(v -> {
					cache.set(v);
					return v;
				});
	}

	static <A> Option<Recur<Iterable<A>>> lift(Iterable<Recur<A>> iterable) {
		return Stream.ofAll(iterable)
				.map(v -> v.map(Stream::of))
				.reduceOption((acc, item) -> Recur.zip(acc, item.map(Stream::head))
						.map(lr -> lr.apply(Stream::append)))
				// cast
				.map(r -> r.map(v -> v));
	}

	static <A> Recur<Nothing> forEach(List<Recur<A>> tasks, Consumer<A> sink) {
		return new ForEach<A>(tasks, sink)
				.map(_0 -> Nothing.nothing());
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	class Done<A> implements Recur<A> {
		A value;

		@Override
		public A get() {
			return value;
		}

		@Override
		public <B> Recur<B> flatMap(Function<? super A, ? extends Monad<Recur<?>, B>> f) {
			return (Recur<B>) f.apply(value);
		}

		@Override
		public <B> Recur<B> map(Function<? super A, B> f) {
			return Done.of(f.apply(value));
		}

		@Override
		public boolean isDone() {
			return true;
		}
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	class More<A> implements Recur<A> {
		Supplier<Recur<A>> rec;
	}

	@Getter
	@RequiredArgsConstructor(staticName = "of")
	class ForEach<A> implements Recur<A> {
		private final List<Recur<A>> options;
		private final Consumer<A> sink;
	}

	/**
	 * ABOUTME: Represents a suspended computation awaiting external completion via CompletableFuture.
	 * ABOUTME: When the future completes with a value, the resume function is called to produce the next computation.
	 */
	@Getter
	@RequiredArgsConstructor
	class Suspended<W, A> implements Recur<A> {
		private final CompletableFuture<W> future;
		private final Function<W, Recur<A>> resume;
	}

	@Getter
	@ToString
	@EqualsAndHashCode
	@AllArgsConstructor
	@FieldDefaults(makeFinal = true, level = AccessLevel.MODULE)
	class FlatMap<A, B> implements Recur<B>, Function<A, Recur<B>> {
		Function<A, Recur<B>> f;
		Recur<A> arg;

		@Override
		public Recur<B> apply(A v) {
			return f.apply(v);
		}

		@SuppressWarnings("unchecked")
		public static <C, D> FlatMap<Object, D> of(Function<? super C, ? extends Monad<Recur<?>, D>> f, Recur<C> r) {
			return new FlatMap<>(o -> (Recur<D>) f.apply((C) o), (Recur<Object>) r);
		}
	}
}