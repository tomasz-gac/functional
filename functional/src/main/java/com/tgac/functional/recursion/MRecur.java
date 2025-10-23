package com.tgac.functional.recursion;

import com.tgac.functional.Reference;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class MRecur<A> implements Supplier<Option<A>> {
	private final Recur<Option<A>> recur;

	public static <A> MRecur<A> mdone(A v) {
		return MRecur.of(Recur.done(Option.of(v)));
	}

	public static <A> MRecur<A> none() {
		return MRecur.of(Recur.done(Option.none()));
	}

	public static <A> MRecur<A> mrecur(Supplier<MRecur<A>> supplier) {
		return MRecur.of(Recur.recur(() -> supplier.get().recur));
	}

	public static <A> MRecur<A> ofRecur(Recur<A> r) {
		return MRecur.of(r.map(Option::of));
	}

	public <B> MRecur<B> flatMap(Function<A, MRecur<B>> f) {
		return MRecur.of(recur
				.flatMap(o -> o.map(f)
						.map(r -> r.recur)
						.getOrElse(() -> Recur.done(Option.none()))));
	}

	public MRecur<A> filter(Predicate<A> test) {
		return this.flatMap(v ->
				Option.of(v)
						.filter(test)
						.map(MRecur::mdone)
						.getOrElse(MRecur::none));
	}

	public MRecur<A> orElse(Supplier<MRecur<A>> other) {
		return MRecur.of(recur
				.flatMap(a -> a.map(MRecur::mdone)
						.getOrElse(other)
						.recur));
	}

	public Recur<A> resumeWith(Supplier<A> other) {
		return recur.map(a -> a.getOrElse(other));
	}

	public <B> MRecur<B> ifElse(
			Function<A, MRecur<B>> then,
			Supplier<MRecur<B>> orElse) {
		return MRecur.of(getRecur()
				.flatMap(a -> a.isDefined() ?
						then.apply(a.get()).getRecur() :
						orElse.get().getRecur()));
	}

	public static <T> MRecur<T> mcache(Supplier<MRecur<T>> r) {
		Reference<T> cache = Reference.empty();
		return mrecur(() -> mdone(cache.get()))
				.filter(Objects::nonNull)
				.orElse(r)
				.map(v -> {
					cache.set(v);
					return v;
				});
	}

	public <B> MRecur<B> map(Function<A, B> f) {
		return MRecur.of(recur.map(o -> o.map(f)));
	}

	@Override
	public Option<A> get() {
		return recur.get();
	}

	public Recur<A> getOrElse(Supplier<A> s) {
		return recur.map(o -> o.getOrElse(s));
	}

	public Recur<A> getOrElse(A v) {
		return recur.map(o -> o.getOrElse(v));
	}

	public static <A, B> MRecur<Tuple2<A, B>> zip(MRecur<A> lhs, MRecur<B> rhs) {
		return lhs.flatMap(l -> rhs.map(r -> Tuple.of(l, r)));
	}
}
