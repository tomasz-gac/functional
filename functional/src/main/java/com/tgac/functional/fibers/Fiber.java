package com.tgac.functional.fibers;

import com.tgac.functional.Reference;
import com.tgac.functional.category.Monad;
import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.schedulers.BreadthFirstScheduler;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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
		return Deferred.of(rec);
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

	default Scheduler<A> toEngine() {
		return new BreadthFirstScheduler<>(this);
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

	/**
	 * Fork the tasks as independent frames in the calling fiber's scope. A
	 * CONTROL primitive: the fork completes when control has drained out of
	 * every child — each has either finished or parked itself at a
	 * {@link Source}, fully recorded (a child yields exactly once, and done
	 * is the final yield). Completion promises NOTHING about the children's
	 * values: a parked child lives on, resumed by its source, and may keep
	 * producing after the fork has completed. Work that must observe "all
	 * results are in" awaits a source's seal instead — quiescence of the
	 * producing workforce is the only honest end-of-stream.
	 */
	static <A> Fiber<Nothing> fork(List<Fiber<A>> tasks) {
		return new Forked<A>(tasks)
				.map(_0 -> Nothing.nothing());
	}

	/**
	 * Detach a fiber to run independently without blocking the caller's completion.
	 * The detached fiber runs in the background and the caller continues immediately.
	 * The child runs UNOWNED — no scope records it; use {@link #detachTo} to re-parent.
	 *
	 * @param fiber The fiber to detach
	 * @return A fiber that completes immediately while the detached fiber runs independently
	 */
	static <A> Fiber<Nothing> detach(Fiber<A> fiber) {
		return new Detached<>(fiber, null);
	}

	/**
	 * Detach a fiber PRODUCING INTO {@code into}: the child runs independently
	 * and its work is recorded in the source's workforce — the one legal escape
	 * from ambient inheritance (a tabling master belongs to its entry, not to
	 * whichever caller spawned it). A foreign Source has no workforce the
	 * runtime can count: the child runs unowned.
	 */
	static <A> Fiber<Nothing> detachTo(Source<?> into, Fiber<A> fiber) {
		return new Detached<>(fiber, into);
	}

	/**
	 * Suspend until {@code ready} holds of {@code source}'s value or the
	 * source's scope seals — the condition variable over a monotone source
	 * (docs/design/await.md). The fiber does not end while blocked: its
	 * scope's started/finished pair converts into a blocked record, so every
	 * quiescence question stays answerable. Run-once: one await completes at
	 * most once ({@code more} or {@code sealed}); re-arm with flatMap.
	 */
	static <V> Fiber<Await.Result<V>> await(Source<V> source, Predicate<V> ready) {
		return new Awaiting<>(source, ready);
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
	class Deferred<A> implements Fiber<A> {
		Supplier<Fiber<A>> rec;
	}

	@Getter
	@RequiredArgsConstructor(staticName = "of")
	class Forked<A> implements Fiber<A> {
		private final List<Fiber<A>> options;
	}

	/**
	 * ABOUTME: Represents a detached fiber that runs independently in the background.
	 * ABOUTME: The parent fiber completes immediately while the detached fiber continues execution.
	 */
	@Value
	@RequiredArgsConstructor
	class Detached<A> implements Fiber<Nothing> {
		private final Fiber<A> fiber;
		private final Source<?> into;
	}

	/** A fiber suspended on a {@link Source} until ready or sealed. */
	@Value
	@RequiredArgsConstructor
	class Awaiting<V> implements Fiber<Await.Result<V>> {
		private final Source<V> source;
		private final Predicate<V> ready;
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