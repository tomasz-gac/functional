package com.tgac.functional.monad;

import static com.tgac.functional.recursion.Fiber.done;
import static com.tgac.functional.recursion.Fiber.defer;

import com.tgac.functional.category.Monad;
import com.tgac.functional.recursion.Fiber;
import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public interface Cont<T, R> extends
		Monad<Cont<?, R>, T>,
		Fiber.Fn<Fiber.Fn<T, R>, R> {

	@Override
	default <B> Cont<B, R> flatMap(Function<? super T, ? extends Monad<Cont<?, R>, B>> f) {
		return k ->
				Fiber.defer(() -> apply(a ->
						Fiber.defer(() -> f.apply(a).<Cont<B, R>> cast()
								.apply(k))));
	}

	/**
	 * Helper class that allows the resuming function to be used regardless of the current flatMap return type.
	 */
	@Value
	@RequiredArgsConstructor(access = AccessLevel.MODULE, staticName = "of")
	class Resume<T, R> implements Serializable {
		Function<T, Fiber<R>> k;

		public <U> Cont<U, R> with(T value) {
			return aborted -> k.apply(value);
		}
	}

	/**
	 * <pre>
	 * Call-with-current-continuation.
	 * Allows to abort execution of code block using the supplied abort function.
	 * callCC :: ((a -> Cont r b) -> Cont r a) -> Cont r a
	 * </pre>
	 */
	static <A, R> Cont<A, R> callCC(Function<Resume<A, R>, Cont<A, R>> body) {
		return suspend(cc -> body.apply(Resume.of(cc)).apply(cc));
	}

	default Fiber<R> runRec(Fiber.Fn<T, R> cont) {
		return apply(cont);
	}

	default Fiber<R> run(Function<T, @NonNull R> cont) {
		return apply(v -> done(cont.apply(v)));
	}

	@Override
	default <B> Cont<B, R> pure(B value) {
		return just(value);
	}

	static <T, R> Cont<T, R> suspend(Fiber.Fn<Fiber.Fn<T, R>, R> f) {
		return f::apply;
	}

	static <T, R> Cont<T, R> complete(R value) {
		return suspend(k -> done(value));
	}

	static <T, R> Cont<T, R> defer(Supplier<Fiber<Cont<T, R>>> deferred) {
		return k -> deferred.get().flatMap(c -> c.apply(k));
	}

	static <T, R> Cont<T, R> just(T value) {
		return k -> k.apply(value);
	}
}
