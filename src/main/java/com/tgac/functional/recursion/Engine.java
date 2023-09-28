package com.tgac.functional.recursion;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.tgac.functional.recursion.Recur.done;

@RequiredArgsConstructor
public class Engine<A> implements Supplier<A> {
	@NonNull
	Recur<Object> result;
	private final Deque<Function<Object, Recur<Object>>> fs = new ArrayDeque<>();

	@SuppressWarnings("unchecked")
	protected Option<A> computeResult(int iterations,
			Either<Supplier<Recur<Object>>, Recur.FlatMap<Object, Object>> eval) {
		result = eval.fold(Supplier::get, f -> processFlatMap(fs, f));
		int i = 0;
		while (i < iterations && (!fs.isEmpty() || result.eval.fold(l -> false, r -> true))) {
			result = result.eval.fold(
					val -> fs.isEmpty() ? done(val) : fs.pollLast().apply(val),
					more -> more.fold(
							Supplier::get,
							f -> processFlatMap(fs, f)));
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
		return result.eval.fold(
						l -> Option.of((A) l),
						r -> computeResult(iterations, r))
				.map(Either::<Engine<A>, A>right)
				.getOrElse(() -> Either.left(this));
	}

	@Override
	public A get() {
		Either<Engine<A>, A> result = Either.left(this);
		while (result.isLeft()) {
			result = result.getLeft().run(Integer.MAX_VALUE);
		}
		return result.get();
	}
}
