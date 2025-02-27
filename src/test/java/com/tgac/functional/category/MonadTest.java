package com.tgac.functional.category;

import com.tgac.functional.Exceptions;
import com.tgac.functional.monad.EitherM;
import com.tgac.functional.monad.FutureM;
import com.tgac.functional.monad.StreamM;
import com.tgac.functional.transformer.EitherMT;
import com.tgac.functional.transformer.FutureMT;
import com.tgac.functional.transformer.OptionMT;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.experimental.ExtensionMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@ExtensionMethod(MonadUtils.class)
class MonadTest {

	@Test
	public void testFutureMT() {
		FutureMT<StreamM<?>, Integer> fut = FutureMT.of(
				IntStream.range(0, 10)
						.boxed()
						.map(delay(100))
						.asMonad());

		List<Integer> collect =
				fut.flatMap(i -> FutureMT.just(
								IntStream.range(0, i)
										.boxed()
										.asMonad()))
						.getValue()
						.<StreamM<CompletableFuture<Integer>>> cast()
						.get()
						.reduce(CompletableFuture.completedFuture(Stream.<Integer> empty()),
								(acc, f) -> acc.thenCompose(v -> f.thenApply(j -> Stream.concat(v, Stream.of(j)))),
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
		EitherMT<StreamM<?>, String, Integer> eitherStream = EitherMT.right(
				StreamM.of(IntStream.range(0, 10).boxed()));

		List<String> evenOrNumber = eitherStream
				.flatMap(v -> v % 2 == 0 ?
						EitherMT.right(Stream.of(v).asMonad()) :
						EitherMT.left(Stream.of("Even number: " + v).asMonad()))
				.fold(String::valueOf, String::valueOf)
				.<StreamM<String>> cast()
				.get()
				.collect(Collectors.toList());

		StreamM<EitherM<String, Integer>> stream2 = StreamM.of(
				IntStream.range(0, 10).boxed()
						.map(EitherM::right));

		List<String> collect = stream2
				.map(e -> e
						.flatMap(v -> v % 2 == 0 ?
								EitherM.right(v) :
								EitherM.left("Event number: " + v)))
				.map(e -> e.fold(String::valueOf, String::valueOf))
				.<StreamM<String>> cast()
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
		OptionMT<FutureM<?>, Integer> futureMIntegerOptionMT = OptionMT.just(
				MonadTest.<Integer> delay(100)
						.apply(1)
						.asMonad());

		Assertions.assertThat(futureMIntegerOptionMT
						.flatMap(i -> OptionMT.just(FutureM.just(i + 4)))
						.getValue()
						.<FutureM<Optional<Integer>>> cast()
						.get()
						.join())
				.hasValue(5);
	}

	@Test
	public void testOptionMTEmpty() {
		OptionMT<FutureM<?>, Integer> futureMIntegerOptionMT = OptionMT.just(
				MonadTest.<Integer> delay(100)
						.apply(1)
						.asMonad());

		Assertions.assertThat(futureMIntegerOptionMT
						.flatMap(i -> OptionMT.<FutureM<?>, Integer> none(FutureM::just))
						.getValue()
						.<FutureM<Optional<Integer>>> cast()
						.get()
						.join())
				.isEmpty();
	}
}