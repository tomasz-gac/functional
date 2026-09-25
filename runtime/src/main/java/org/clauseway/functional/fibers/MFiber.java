package org.clauseway.functional.fibers;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.clauseway.functional.Reference;
import org.clauseway.functional.tuples.Tuple;
import org.clauseway.functional.tuples.Tuple2;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class MFiber<A> {
	private final Fiber<Optional<A>> fiber;

	public static <A> MFiber<A> mdone(A v) {
		return MFiber.of(Fiber.done(Optional.of(v)));
	}

	public static <A> MFiber<A> none() {
		return MFiber.of(Fiber.done(Optional.empty()));
	}

	public static <A> MFiber<A> mdefer(Supplier<MFiber<A>> supplier) {
		return MFiber.of(Fiber.defer(() -> supplier.get().fiber));
	}

	public static <A> MFiber<A> ofFiber(Fiber<A> r) {
		return MFiber.of(r.map(Optional::of));
	}

	/** The loud extractor, delegated: requires the underlying fiber Done. */
	public Optional<A> getDone(String context) {
		return fiber.getDone(context);
	}

	/** The sanctioned nesting door, delegated: pure fibers only. */
	@Deprecated
	public Optional<A> ground() {
		return fiber.ground();
	}

	public <B> MFiber<B> flatMap(Function<A, MFiber<B>> f) {
		return MFiber.of(fiber
				.flatMap(o -> o.map(f)
						.map(r -> r.fiber)
						.orElseGet(() -> Fiber.done(Optional.empty()))));
	}

	public MFiber<A> filter(Predicate<A> test) {
		return this.flatMap(v ->
				Optional.of(v)
						.filter(test)
						.map(MFiber::mdone)
						.orElseGet(MFiber::none));
	}

	public MFiber<A> orElse(Supplier<MFiber<A>> other) {
		return MFiber.of(fiber
				.flatMap(a -> a.map(MFiber::mdone)
						.orElseGet(other)
						.fiber));
	}

	public Fiber<A> resumeWith(Supplier<A> other) {
		return fiber.map(a -> a.orElseGet(other));
	}

	public <B> MFiber<B> ifElse(
			Function<A, MFiber<B>> then,
			Supplier<MFiber<B>> orElse) {
		return MFiber.of(getFiber()
				.flatMap(a -> a.isPresent() ?
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

	public Fiber<A> getOrElse(Supplier<A> s) {
		return fiber.map(o -> o.orElseGet(s));
	}

	public Fiber<A> getOrElse(A v) {
		return fiber.map(o -> o.orElse(v));
	}

	public static <A, B> MFiber<Tuple2<A, B>> zip(MFiber<A> lhs, MFiber<B> rhs) {
		return lhs.flatMap(l -> rhs.map(r -> Tuple.of(l, r)));
	}
}
