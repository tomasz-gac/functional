package com.tgac.functional.recursion;

import static com.tgac.functional.category.Unit.unit;

import com.tgac.functional.Reference;
import com.tgac.functional.category.Monad;
import com.tgac.functional.category.Unit;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
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

public interface Recur<A> extends Monad<Recur<?>, A>, Supplier<A> {
	interface Fn<T, R> extends Function<T, Recur<R>> {
	}

	static <A> Recur<A> done(@NonNull  A v) {
		return Done.of(v);
	}

	static <A> Recur<A> recur(Supplier<Recur<A>> rec) {
		return More.of(rec);
	}

	interface Visitor<A, R> {
		R visit(Done<A> done);

		R visit(More<A> more);

		<B> R visit(FlatMap<B, A> flatMap);
	}

	<R> R accept(Visitor<A, R> visitor);

	@Override
	default <B> Recur<B> flatMap(Function<? super A, ? extends Monad<Recur<?>, B>> f) {
		return FlatMap.of(f, this);
	}

	@Override
	default <B> Recur<B> map(Function<? super A, B> f) {
		return flatMap(v -> done(f.apply(v)));
	}

	@Override
	default <B> Recur<B> pure(B value) {
		return done(value);
	}

	@Override
	@SneakyThrows
	default A get() {
		return toEngine().get()
				.get(0);
	}

	default boolean isDone() {
		return false;
	}

	default Engine<A> toEngine() {
		return Engine.of(Collections.singletonList(this));
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

	static <A> Recur<Unit> interleave(List<Recur<A>> rs, Consumer<A> sink) {
		Engine<A> engine = Engine.of(rs);
		AtomicReference<Recur<Unit>> res = new AtomicReference<>();
		res.set(recur(() -> engine.step(sink) ? done(unit()) : res.get()));
		return res.get();
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	class Done<A> implements Recur<A> {
		A value;

		@Override
		public <R> R accept(Visitor<A, R> visitor) {
			return visitor.visit(this);
		}

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

		@Override
		public <R> R accept(Visitor<A, R> visitor) {
			return visitor.visit(this);
		}
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

		@Override
		public <R> R accept(Visitor<B, R> visitor) {
			return visitor.visit(this);
		}
	}
}