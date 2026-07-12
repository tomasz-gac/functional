package com.tgac.functional.fibers;

import static com.tgac.functional.fibers.Fiber.defer;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * ABOUTME: Tests for Fiber.FlatMap - verifying sequential composition and continuation handling.
 * ABOUTME: Ensures flatMap chains execute in correct order and avoid stack overflow through scheduler continuation stack.
 */
public class FlatMapTest {

	@Test
	public void shouldFlatMapDoneToDone() {
		Integer result = done(5)
				.flatMap(x -> done(x * 2))
				.get();
		assertThat(result).isEqualTo(10);
	}

	@Test
	public void shouldChainMultipleFlatMaps() {
		Integer result = done(1)
				.flatMap(x -> done(x + 1))
				.flatMap(x -> done(x * 2))
				.flatMap(x -> done(x + 10))
				.get();
		assertThat(result).isEqualTo(14); // ((1 + 1) * 2) + 10
	}

	@Test
	public void shouldHandleVeryLongFlatMapChain() {
		Fiber<Integer> chain = done(0);
		for (int i = 0; i < 10_000; i++) {
			chain = chain.flatMap(x -> done(x + 1));
		}
		Integer result = chain.get();
		assertThat(result).isEqualTo(10_000);
	}

	@Test
	public void shouldExecuteFlatMapsInOrder() {
		List<Integer> executionOrder = new ArrayList<>();

		Integer result = done(1)
				.flatMap(x -> {
					executionOrder.add(1);
					return done(x + 1);
				})
				.flatMap(x -> {
					executionOrder.add(2);
					return done(x * 2);
				})
				.flatMap(x -> {
					executionOrder.add(3);
					return done(x + 10);
				})
				.get();

		assertThat(result).isEqualTo(14);
		assertThat(executionOrder).containsExactly(1, 2, 3);
	}

	@Test
	public void shouldFlatMapDoneToDeferred() {
		Integer result = done(5)
				.flatMap(x -> defer(() -> done(x * 2)))
				.get();
		assertThat(result).isEqualTo(10);
	}

	@Test
	public void shouldFlatMapDeferredToDone() {
		Integer result = defer(() -> done(5))
				.flatMap(x -> done(x * 2))
				.get();
		assertThat(result).isEqualTo(10);
	}

	@Test
	public void shouldFlatMapDeferredToDeferred() {
		Integer result = defer(() -> done(3))
				.flatMap(x -> defer(() -> done(x * 3)))
				.get();
		assertThat(result).isEqualTo(9);
	}

	@Test
	public void shouldHandleNestedFlatMapsWithDefer() {
		Integer result = defer(() -> done(2))
				.flatMap(a -> defer(() -> done(3))
						.flatMap(b -> defer(() -> done(4))
								.flatMap(c -> done(a + b + c))))
				.get();
		assertThat(result).isEqualTo(9);
	}

	@Test
	public void shouldPreserveTypesAcrossFlatMap() {
		String result = done(42)
				.flatMap(i -> done("Number: " + i))
				.flatMap(s -> done(s.toUpperCase()))
				.get();
		assertThat(result).isEqualTo("NUMBER: 42");
	}

	@Test
	public void shouldHandleFlatMapReturningCompoundStructure() {
		class Result {
			final int value;
			final String label;

			Result(int value, String label) {
				this.value = value;
				this.label = label;
			}
		}

		Result result = done(10)
				.flatMap(x -> done(new Result(x * 2, "doubled")))
				.flatMap(r -> done(new Result(r.value + 5, r.label + "-adjusted")))
				.get();

		assertThat(result.value).isEqualTo(25);
		assertThat(result.label).isEqualTo("doubled-adjusted");
	}

	@Test
	public void shouldHandleFlatMapWithBranching() {
		Integer result = done(10)
				.flatMap(x -> {
					if (x > 5) {
						return done(x * 2);
					} else {
						return done(x + 10);
					}
				})
				.get();
		assertThat(result).isEqualTo(20);
	}

	@Test
	public void shouldHandleFlatMapThatReturnsOriginalValue() {
		Integer result = done(42)
				.flatMap(x -> done(x))
				.flatMap(x -> done(x))
				.get();
		assertThat(result).isEqualTo(42);
	}

	@Test
	public void shouldHandleMixedMapAndFlatMap() {
		Integer result = done(5)
				.map(x -> x + 1)
				.flatMap(x -> done(x * 2))
				.map(x -> x + 3)
				.flatMap(x -> done(x - 1))
				.get();
		assertThat(result).isEqualTo(14); // ((5 + 1) * 2 + 3) - 1
	}

	@Test
	public void shouldHandleFlatMapWithRecursion() {
		Integer result = factorial(5).get();
		assertThat(result).isEqualTo(120);
	}

	private Fiber<Integer> factorial(int n) {
		if (n <= 1) {
			return done(1);
		}
		return defer(() -> factorial(n - 1))
				.flatMap(rest -> done(n * rest));
	}

	@Test
	public void shouldHandleDeepRecursiveFlatMap() {
		Integer result = factorial(100).get();
		// Just verify it completes without stack overflow
		assertThat(result).isNotNull();
	}

	@Test
	public void shouldPassValuesCorrectlyThroughChain() {
		String result = done("start")
				.flatMap(s -> done(s + "-1"))
				.flatMap(s -> done(s + "-2"))
				.flatMap(s -> done(s + "-3"))
				.get();
		assertThat(result).isEqualTo("start-1-2-3");
	}

	@Test
	public void shouldHandleFlatMapWithZip() {
		Integer result = done(5)
				.flatMap(a -> done(10)
						.map(b -> a + b))
				.get();
		assertThat(result).isEqualTo(15);
	}

	@Test
	public void shouldSupportMonadicLaws() {
		// Left identity: pure(a).flatMap(f) == f(a)
		Integer leftIdentity1 = done(5).flatMap(x -> done(x * 2)).get();
		Integer leftIdentity2 = done(5 * 2).get();
		assertThat(leftIdentity1).isEqualTo(leftIdentity2);

		// Right identity: m.flatMap(pure) == m
		Integer rightIdentity1 = done(5).flatMap(Fiber::done).get();
		Integer rightIdentity2 = done(5).get();
		assertThat(rightIdentity1).isEqualTo(rightIdentity2);

		// Associativity: m.flatMap(f).flatMap(g) == m.flatMap(x -> f(x).flatMap(g))
		Integer assoc1 = done(5)
				.flatMap(x -> done(x * 2))
				.flatMap(x -> done(x + 3))
				.get();
		Integer assoc2 = done(5)
				.flatMap(x -> done(x * 2).flatMap(y -> done(y + 3)))
				.get();
		assertThat(assoc1).isEqualTo(assoc2);
	}

	@Test
	public void shouldHandleExtremeFlatMapDepth() {
		Fiber<Integer> deep = buildFlatMapChain(50_000);
		Integer result = deep.get();
		assertThat(result).isEqualTo(50_000);
	}

	private Fiber<Integer> buildFlatMapChain(int depth) {
		Fiber<Integer> fiber = done(0);
		for (int i = 0; i < depth; i++) {
			fiber = fiber.flatMap(x -> done(x + 1));
		}
		return fiber;
	}

	@Test
	public void shouldHandleFlatMapWithMultipleTypes() {
		class Person {
			final String name;
			final int age;

			Person(String name, int age) {
				this.name = name;
				this.age = age;
			}
		}

		String result = done(new Person("Alice", 30))
				.flatMap(p -> done(p.age))
				.flatMap(age -> done("Age: " + age))
				.get();

		assertThat(result).isEqualTo("Age: 30");
	}
}
