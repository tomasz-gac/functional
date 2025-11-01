package com.tgac.functional.fibers;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * ABOUTME: Represents something that can be awaited to produce a value, similar to C++'s Awaitable concept.
 * ABOUTME: Provides clean API for suspending fiber execution until external events complete.
 * ABOUTME: Use flatMap to chain multiple awaits without nesting.
 */
@FunctionalInterface
public interface Awaitable<T> {
	/**
	 * Prepare this awaitable by creating and registering a CompletableFuture.
	 * The implementation is responsible for:
	 * - Creating the CompletableFuture
	 * - Registering it with whatever external system needs to hold it
	 * - Ensuring the future will be completed when the awaited event occurs
	 *
	 * @return The CompletableFuture that will be completed with the awaited value
	 */
	CompletableFuture<T> prepare();

	/**
	 * Suspend execution until this Awaitable produces a value.
	 *
	 * Returns a Fiber that will complete with the awaited value.
	 * Use flatMap to chain multiple awaits without nesting:
	 *
	 * <pre>
	 * awaitable1.await()
	 *     .flatMap(val1 -> awaitable2.await())
	 *     .flatMap(val2 -> done(result));
	 * </pre>
	 *
	 * @return A Fiber.Suspended that will resume with the awaited value
	 */
	default Fiber<T> await() {
		CompletableFuture<T> future = prepare();
		return Fiber.suspend(future, Fiber::done);
	}

	/**
	 * Suspend execution until this Awaitable produces a value,
	 * then continue with the given continuation function.
	 *
	 * This is useful when you need custom logic in the continuation.
	 * For simple chaining, prefer using await() with flatMap.
	 *
	 * @param continuation The function to call with the awaited value
	 * @param <A> The result type of the continuation
	 * @return A Fiber.Suspended that will resume when the value is available
	 */
	default <A> Fiber<A> await(Function<T, Fiber<A>> continuation) {
		CompletableFuture<T> future = prepare();
		return Fiber.suspend(future, continuation);
	}
}
