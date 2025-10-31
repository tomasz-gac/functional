package com.tgac.functional.recursion;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * ABOUTME: Represents something that can be awaited to produce a value, similar to C++'s Awaitable concept.
 * ABOUTME: Provides clean API for suspending fiber execution until external events complete.
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
	 * Suspend execution until this Awaitable produces a value,
	 * then continue with the given continuation function.
	 *
	 * This is the primary way to use an Awaitable - it hides the
	 * CompletableFuture boilerplate and provides a clean API similar
	 * to async/await in other languages.
	 *
	 * @param continuation The function to call with the awaited value
	 * @param <A> The result type of the continuation
	 * @return A Recur.Suspended that will resume when the value is available
	 */
	default <A> Recur<A> await(Function<T, Recur<A>> continuation) {
		CompletableFuture<T> future = prepare();
		return Recur.suspend(future, continuation);
	}
}
