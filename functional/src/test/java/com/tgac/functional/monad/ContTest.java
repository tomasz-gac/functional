package com.tgac.functional.monad;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tgac.functional.Exceptions;
import com.tgac.functional.Reference;
import com.tgac.functional.recursion.Fiber;
import io.vavr.collection.List;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ContTest {
	@Test
	public void shouldNotBlowStack() {
		Cont<Integer, String> cont = Cont.just(0);
		for (int i = 0; i < 1_000_000; i++) {
			cont = cont.flatMap(n -> Cont.just(n + 1));
		}
		Assertions.assertThat(cont.run(Object::toString).get())
				.isEqualTo("1000000");
	}

	@Test
	public void shouldNotBlowStack2() {
		// ⚠️ This aborts without suspension!
		Function<Integer, Cont<Integer, String>> unsafe = n ->
				Cont.suspend(k -> k.apply(n + 1)); // ← direct apply, no recur

		Cont<Integer, String> c = Cont.just(0);

		for (int i = 0; i < 1_000_000; i++) {
			c = c.flatMap(unsafe);
		}

		Assertions.assertThat(c.run(Object::toString).get())
				.isEqualTo("1000000");
	}

	@Test
	public void shouldBlowStack() {
		Reference<Fiber.Fn<Integer, String>> bad = Reference.empty();

		AtomicInteger i = new AtomicInteger(0);
		bad.set(x -> {
			if (i.incrementAndGet() < 1_000_000) {
				return bad.get().apply(x);
			} else {
				return Fiber.done("done");
			}
		});

		Cont<Integer, String> c = Cont.suspend(cc -> bad.get().apply(0));

		assertThrows(StackOverflowError.class,
				() -> c.run(Object::toString).get());
	}

	@Test
	public void shouldNotBlowStack3() {
		Reference<Fiber.Fn<Integer, String>> good = Reference.empty();

		AtomicInteger i = new AtomicInteger(0);
		good.set(x -> Fiber.defer(() -> {
			if (i.incrementAndGet() < 1_000_000) {
				return good.get().apply(x);
			} else {
				return Fiber.done("done");
			}
		}));

		Cont<Integer, String> c = Cont.suspend(cc -> good.get().apply(0));

		Assertions.assertThat(c.run(Object::toString).get())
				.isEqualTo("done");
	}

	@Test
	public void shouldExitEarly() {
		Cont<Integer, String> cont = Cont.callCC(exit ->
				Cont.<Integer, String> just(5)
						.flatMap(i -> {
							if (i == 5) {
								return exit.with(123);
							} else {
								return Cont.just(3);
							}
						})
						.map(i -> i * 2)
						.cast());
		Assertions.assertThat(cont.run(String::valueOf).get())
				.isEqualTo("123");
	}

	@SafeVarargs
	static <T> Cont<T, List<T>> choose(T... values) {
		return k -> Arrays.stream(values)
				.reduce(Fiber.done(List.empty()),
						(acc, v) -> acc.flatMap(xs -> k.apply(v).map(xs::appendAll)),
						Exceptions.throwingBiOp(UnsupportedOperationException::new));
	}

	@Test
	public void shouldChoose() {
		Cont<String, List<String>> program =
				choose("a", "b").flatMap(x ->
						choose(x + "1", x + "2"));

		List<String> result = program.run(List::of).get();
		Assertions.assertThat(result)
				.containsExactly("a1", "a2", "b1", "b2");
	}

	@Test
	public void shouldContinueWithCC() {
		Assertions.assertThat(Cont.<Integer, String> callCC(exit ->
								Cont.<Integer, String> just(3)
										.map(i -> i * 5)
										.flatMap(i -> i == 15 ?
												exit.with(3) :
												Cont.just(i + 4))
										.map(i -> i * 3)
										.cast())
						.<Cont<Integer, String>> cast()
						.run(String::valueOf)
						.get())
				.isEqualTo("3");
	}

	@Test
	public void shouldContinueWithCC2() {
		Assertions.assertThat(Cont.<Integer, String> callCC(exit ->
								Cont.<Integer, String> just(3)
										.map(i -> i * 5)
										.flatMap(i -> i == 14 ?
												exit.with(3) :
												Cont.just(i + 4))
										.map(i -> i * 3)
										.cast())
						.<Cont<Integer, String>> cast()
						.run(String::valueOf)
						.get())
				.isEqualTo("57");
	}
}