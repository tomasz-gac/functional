package com.tgac.functional.recursion;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class Engine<A> implements Supplier<A> {
	@NonNull
	Recur<Object> result;
	private final Deque<Function<Object, Recur<Object>>> fs = new ArrayDeque<>();

	@SuppressWarnings("unchecked")
	protected Option<A> computeResult(int iterations,
			Recur<Object> eval) {
		Recur.Visitor<Object, Recur<Object>> visitor = new Recur.Visitor<Object, Recur<Object>>() {
			@Override
			public Recur<Object> visit(Recur.Done<Object> done) {
				return done;
			}
			@Override
			public Recur<Object> visit(Recur.More<Object> more) {
				return more.getRec().get();
			}
			@Override
			public <B> Recur<Object> visit(Recur.FlatMap<B, Object> flatMap) {
				return processFlatMap(fs, (Recur.FlatMap<Object, Object>) flatMap);
			}
		};
		result = eval.accept(visitor);
		int i = 0;
		while (i < iterations && (!fs.isEmpty() || !result.isDone())) {
			result = result.accept(
					new Recur.Visitor<Object, Recur<Object>>() {
						@Override
						public Recur<Object> visit(Recur.Done<Object> done) {
							return fs.isEmpty() ? done : fs.pollLast().apply(done.get());
						}
						@Override
						public Recur<Object> visit(Recur.More<Object> more) {
							return more.getRec().get();
						}
						@Override
						public <B> Recur<Object> visit(Recur.FlatMap<B, Object> flatMap) {
							return processFlatMap(fs, (Recur.FlatMap<Object, Object>) flatMap);
						}
					}
			);
			++i;
		}
		final int j = i;
		return Option.of(result)
				.filter(__ -> j < iterations)
				.map(r -> (A) r.get());
	}

	protected static Recur<Object> processFlatMap(Deque<Function<Object, Recur<Object>>> fs, Recur.FlatMap<Object, Object> f) {
		fs.add(f.f);
		return f.getArg();
	}

	@SuppressWarnings("unchecked")
	public Either<Engine<A>, A> run(int iterations) {
		return result.accept(new Recur.Visitor<Object, Option<A>>() {
					@Override
					public Option<A> visit(Recur.Done<Object> done) {
						return Option.of((A) done.get());
					}
					@Override
					public Option<A> visit(Recur.More<Object> more) {
						return computeResult(iterations, more);
					}
					@Override
					public <B> Option<A> visit(Recur.FlatMap<B, Object> flatMap) {
						return computeResult(iterations, flatMap);
					}
				}).
				map(Either::<Engine<A>, A>right)
				.getOrElse(() -> Either.left(this));
	}
	;
	@Override
	public A get() {
		Either<Engine<A>, A> result = Either.left(this);
		while (result.isLeft()) {
			result = result.getLeft().run(Integer.MAX_VALUE);
		}
		return result.get();
	}
}
