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

public interface Fiber<A> extends Monad<Fiber<?>, A>, Supplier<A> {
	interface Fn<T, R> extends Function<T, Fiber<R>> {
	}

	static <A> Fiber<A> done(@NonNull A v) {
		return Done.of(v);
	}

	static <A> Fiber<A> defer(Supplier<Fiber<A>> rec) {
		return More.of(rec);
	}

	static <W, A> Fiber<A> suspend(CompletableFuture<W> future, Function<W, Fiber<A>> resume) {
		return new Suspended<>(future, resume);
	}

	@Override
	default <B> Fiber<B> flatMap(Function<? super A, @NonNull ? extends Monad<Fiber<?>, B>> f) {
		return FlatMap.of(f, this);
	}

	@Override
	default <B> Fiber<B> map(Function<? super A, @NonNull B> f) {
		return flatMap(v -> done(f.apply(v)));
	}

	@Override
	default <B> Fiber<B> pure(B value) {
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

	static <A, B> Fiber<Tuple2<A, B>> zip(Fiber<A> lhs, Fiber<B> rhs) {
		return lhs.flatMap(l -> rhs.map(r -> Tuple.of(l, r)));
	}

	static <T> Fiber<T> cache(Supplier<Fiber<T>> r) {
		Reference<T> cache = Reference.empty();
		return (Fiber<T>) defer(() -> done(cache.get()))
				.flatMap(h -> Objects.nonNull(h) ? done(h) : r.get())
				.map(v -> {
					cache.set(v);
					return v;
				});
	}

	static <A> Option<Fiber<Iterable<A>>> lift(Iterable<Fiber<A>> iterable) {
		return Stream.ofAll(iterable)
				.map(v -> v.map(Stream::of))
				.reduceOption((acc, item) -> Fiber.zip(acc, item.map(Stream::head))
						.map(lr -> lr.apply(Stream::append)))
				// cast
				.map(r -> r.map(v -> v));
	}

	static <A> Fiber<Nothing> forEach(List<Fiber<A>> tasks, Consumer<A> sink) {
		return new ForEach<A>(tasks, sink)
				.map(_0 -> Nothing.nothing());
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	class Done<A> implements Fiber<A> {
		A value;

		@Override
		public A get() {
			return value;
		}

		@Override
		public <B> Fiber<B> flatMap(Function<? super A, ? extends Monad<Fiber<?>, B>> f) {
			return (Fiber<B>) f.apply(value);
		}

		@Override
		public <B> Fiber<B> map(Function<? super A, B> f) {
			return Done.of(f.apply(value));
		}

		@Override
		public boolean isDone() {
			return true;
		}
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	class More<A> implements Fiber<A> {
		Supplier<Fiber<A>> rec;
	}

	@Getter
	@RequiredArgsConstructor(staticName = "of")
	class ForEach<A> implements Fiber<A> {
		private final List<Fiber<A>> options;
		private final Consumer<A> sink;
	}

	/**
	 * ABOUTME: Represents a suspended computation awaiting external completion via CompletableFuture.
	 * ABOUTME: When the future completes with a value, the resume function is called to produce the next computation.
	 */
	@Getter
	@RequiredArgsConstructor
	class Suspended<W, A> implements Fiber<A> {
		private final CompletableFuture<W> future;
		private final Function<W, Fiber<A>> resume;
	}

	@Getter
	@ToString
	@EqualsAndHashCode
	@AllArgsConstructor
	@FieldDefaults(makeFinal = true, level = AccessLevel.MODULE)
	class FlatMap<A, B> implements Fiber<B>, Function<A, Fiber<B>> {
		Function<A, Fiber<B>> f;
		Fiber<A> arg;

		@Override
		public Fiber<B> apply(A v) {
			return f.apply(v);
		}

		@SuppressWarnings("unchecked")
		public static <C, D> FlatMap<Object, D> of(Function<? super C, ? extends Monad<Fiber<?>, D>> f, Fiber<C> r) {
			return new FlatMap<>(o -> (Fiber<D>) f.apply((C) o), (Fiber<Object>) r);
		}
	}
}