package com.tgac.functional.category;

import com.tgac.functional.Exceptions;
import com.tgac.functional.monad.Either;
import com.tgac.functional.monad.Future;
import com.tgac.functional.monad.Stream;
import com.tgac.functional.transformer.EitherT;
import com.tgac.functional.transformer.FutureT;
import com.tgac.functional.transformer.OptionT;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.experimental.ExtensionMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@ExtensionMethod(MonadUtils.class)
class MonadTest {

	@Test
	public void testFutureMT() {
		FutureT<Stream<?>, Integer> fut = FutureT.of(
				IntStream.range(0, 10)
						.boxed()
						.map(delay(100))
						.asMonad());

		List<Integer> collect =
				fut.flatMap(i -> FutureT.just(
								IntStream.range(0, i)
										.boxed()
										.asMonad()))
						.getValue()
						.<Stream<CompletableFuture<Integer>>> cast()
						.get()
						.reduce(CompletableFuture.completedFuture(java.util.stream.Stream.<Integer> empty()),
								(acc, f) -> acc.thenCompose(v -> f.thenApply(j -> java.util.stream.Stream.concat(v, java.util.stream.Stream.of(j)))),
								Exceptions.throwingBiOp(UnsupportedOperationException::new))
						.join()
						.collect(Collectors.toList());

		System.out.println(collect);
		Assertions.assertThat(collect)
				.containsExactly(
						0,
						0, 1,
						0, 1, 2,
						0, 1, 2, 3,
						0, 1, 2, 3, 4,
						0, 1, 2, 3, 4, 5,
						0, 1, 2, 3, 4, 5, 6,
						0, 1, 2, 3, 4, 5, 6, 7,
						0, 1, 2, 3, 4, 5, 6, 7, 8);
	}

	private static <A> Function<A, CompletableFuture<A>> delay(int delayMs) {
		return i -> CompletableFuture.supplyAsync(() ->
		{
			try {
				Thread.sleep(delayMs);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return i;
		});
	}

	@Test
	public void testEitherMT() {
		EitherT<Stream<?>, String, Integer> eitherStream = EitherT.right(
				Stream.of(IntStream.range(0, 10).boxed()));

		List<String> evenOrNumber = eitherStream
				.flatMap(v -> v % 2 == 0 ?
						EitherT.right(java.util.stream.Stream.of(v).asMonad()) :
						EitherT.left(java.util.stream.Stream.of("Even number: " + v).asMonad()))
				.fold(String::valueOf, String::valueOf)
				.<Stream<String>> cast()
				.get()
				.collect(Collectors.toList());

		Stream<Either<String, Integer>> stream2 = Stream.of(
				IntStream.range(0, 10).boxed()
						.map(Either::right));

		List<String> collect = stream2
				.map(e -> e
						.flatMap(v -> v % 2 == 0 ?
								Either.right(v) :
								Either.left("Event number: " + v)))
				.map(e -> e.fold(String::valueOf, String::valueOf))
				.<Stream<String>> cast()
				.toJavaStream()
				.collect(Collectors.toList());

		System.out.println(evenOrNumber);
		Assertions.assertThat(evenOrNumber)
				.containsExactly(
						"0",
						"Even number: 1",
						"2",
						"Even number: 3",
						"4",
						"Even number: 5",
						"6",
						"Even number: 7",
						"8",
						"Even number: 9");
	}

	@Test
	public void testOptionMTPresent() {
		OptionT<Future<?>, Integer> futureMIntegerOptionMT = OptionT.just(
				MonadTest.<Integer> delay(100)
						.apply(1)
						.asMonad());

		Assertions.assertThat(futureMIntegerOptionMT
						.flatMap(i -> OptionT.just(Future.just(i + 4)))
						.getValue()
						.<Future<Optional<Integer>>> cast()
						.get()
						.join())
				.hasValue(5);
	}

	@Test
	public void testOptionMTEmpty() {
		OptionT<Future<?>, Integer> futureMIntegerOptionMT = OptionT.just(
				MonadTest.<Integer> delay(100)
						.apply(1)
						.asMonad());

		Assertions.assertThat(futureMIntegerOptionMT
						.flatMap(i -> OptionT.<Future<?>, Integer> none(Future::just))
						.getValue()
						.<Future<Optional<Integer>>> cast()
						.get()
						.join())
				.isEmpty();
	}
}