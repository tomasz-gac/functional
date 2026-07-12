package com.tgac.functional.fibers;

import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * ABOUTME: Tests for Fiber.Done - verifying immediate completion and value retrieval semantics.
 * ABOUTME: Ensures Done fibers correctly handle values, mapping, flatMapping, and composition.
 */
public class DoneTest {

	@Test
	public void shouldReturnValueImmediately() {
		Integer result = done(42).get();
		assertThat(result).isEqualTo(42);
	}

	@Test
	public void shouldReturnStringValue() {
		String result = done("hello").get();
		assertThat(result).isEqualTo("hello");
	}

	@Test
	public void shouldMapValue() {
		Integer result = done(10)
				.map(x -> x * 2)
				.get();
		assertThat(result).isEqualTo(20);
	}

	@Test
	public void shouldChainMultipleMaps() {
		Integer result = done(5)
				.map(x -> x + 1)
				.map(x -> x * 2)
				.map(x -> x - 3)
				.get();
		assertThat(result).isEqualTo(9);  // (5 + 1) * 2 - 3 = 9
	}

	@Test
	public void shouldFlatMapToAnotherDone() {
		Integer result = done(10)
				.flatMap(x -> done(x * 2))
				.get();
		assertThat(result).isEqualTo(20);
	}

	@Test
	public void shouldChainMultipleFlatMaps() {
		Integer result = done(2)
				.flatMap(x -> done(x + 3))
				.flatMap(x -> done(x * 4))
				.flatMap(x -> done(x - 1))
				.get();
		assertThat(result).isEqualTo(19);  // ((2 + 3) * 4) - 1 = 19
	}

	@Test
	public void shouldMixMapAndFlatMap() {
		String result = done(5)
				.map(x -> x * 2)
				.flatMap(x -> done("value: " + x))
				.map(String::toUpperCase)
				.get();
		assertThat(result).isEqualTo("VALUE: 10");
	}

	@Test
	public void shouldIndicateIsDone() {
		Fiber<Integer> fiber = done(100);
		assertThat(fiber.isDone()).isTrue();
	}

	@Test
	public void shouldPreserveNullableValues() {
		// Done requires NonNull, but after creation we can map to produce null results
		String result = done("test")
				.map(s -> (String) null)
				.get();
		assertThat(result).isNull();
	}

	@Test
	public void shouldHandleComplexObjectGraphs() {
		class Person {
			final String name;
			final int age;

			Person(String name, int age) {
				this.name = name;
				this.age = age;
			}
		}

		Person result = done(new Person("Alice", 30))
				.map(p -> new Person(p.name, p.age + 1))
				.flatMap(p -> done(new Person(p.name.toUpperCase(), p.age)))
				.get();

		assertThat(result.name).isEqualTo("ALICE");
		assertThat(result.age).isEqualTo(31);
	}

	@Test
	public void shouldWorkWithPureMethod() {
		Fiber<Integer> fiber = done(10);
		Integer result = fiber.pure(20).get();
		assertThat(result).isEqualTo(20);
	}

	@Test
	public void shouldHandleNestedDoneInFlatMap() {
		Integer result = done(1)
				.flatMap(a -> done(2)
						.flatMap(b -> done(3)
								.flatMap(c -> done(a + b + c))))
				.get();
		assertThat(result).isEqualTo(6);
	}
}
