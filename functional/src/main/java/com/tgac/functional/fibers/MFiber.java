package com.tgac.functional.fibers;

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
public class MFiber<A> implements Supplier<Option<A>> {
	private final Fiber<Option<A>> fiber;

	public static <A> MFiber<A> mdone(A v) {
		return MFiber.of(Fiber.done(Option.of(v)));
	}

	public static <A> MFiber<A> none() {
		return MFiber.of(Fiber.done(Option.none()));
	}

	public static <A> MFiber<A> mdefer(Supplier<MFiber<A>> supplier) {
		return MFiber.of(Fiber.defer(() -> supplier.get().fiber));
	}

	public static <A> MFiber<A> ofFiber(Fiber<A> r) {
		return MFiber.of(r.map(Option::of));
	}

	public <B> MFiber<B> flatMap(Function<A, MFiber<B>> f) {
		return MFiber.of(fiber
				.flatMap(o -> o.map(f)
						.map(r -> r.fiber)
						.getOrElse(() -> Fiber.done(Option.none()))));
	}

	public MFiber<A> filter(Predicate<A> test) {
		return this.flatMap(v ->
				Option.of(v)
						.filter(test)
						.map(MFiber::mdone)
						.getOrElse(MFiber::none));
	}

	public MFiber<A> orElse(Supplier<MFiber<A>> other) {
		return MFiber.of(fiber
				.flatMap(a -> a.map(MFiber::mdone)
						.getOrElse(other)
						.fiber));
	}

	public Fiber<A> resumeWith(Supplier<A> other) {
		return fiber.map(a -> a.getOrElse(other));
	}

	public <B> MFiber<B> ifElse(
			Function<A, MFiber<B>> then,
			Supplier<MFiber<B>> orElse) {
		return MFiber.of(getFiber()
				.flatMap(a -> a.isDefined() ?
						then.apply(a.get()).getFiber() :
						orElse.get().getFiber()));
	}

	public static <T> MFiber<T> mcache(Supplier<MFiber<T>> r) {
		Reference<T> cache = Reference.empty();
		return mdefer(() -> mdone(cache.get()))
				.filter(Objects::nonNull)
				.orElse(r)
				.map(v -> {
					cache.set(v);
					return v;
				});
	}

	public <B> MFiber<B> map(Function<A, B> f) {
		return MFiber.of(fiber.map(o -> o.map(f)));
	}

	@Override
	public Option<A> get() {
		return fiber.get();
	}

	public Fiber<A> getOrElse(Supplier<A> s) {
		return fiber.map(o -> o.getOrElse(s));
	}

	public Fiber<A> getOrElse(A v) {
		return fiber.map(o -> o.getOrElse(v));
	}

	public static <A, B> MFiber<Tuple2<A, B>> zip(MFiber<A> lhs, MFiber<B> rhs) {
		return lhs.flatMap(l -> rhs.map(r -> Tuple.of(l, r)));
	}
}
