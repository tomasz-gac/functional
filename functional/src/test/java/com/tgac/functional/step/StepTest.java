package com.tgac.functional.step;

import static com.tgac.functional.fibers.Fiber.done;

import com.tgac.functional.fibers.Fiber;
import io.vavr.collection.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class StepTest {
	private static final List<Integer> ITEMS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

	@Test
	public void shouldCreateList() {
		Step<Integer> steps = Step.ofAll(ITEMS);

		Assertions.assertThat(steps.stream().collect(Collectors.toList()))
				.isEqualTo(ITEMS);
	}

	@Test
	public void shouldMapEmpty() {
		Assertions.assertThat(
						Step.<Integer> empty()
								.map(i -> i + 1)
								.stream()
								.collect(Collectors.toList()))
				.isEqualTo(Collections.emptyList());
	}

	@Test
	public void shouldFlatMapEmpty() {
		Assertions.assertThat(
						Step.<Integer> empty()
								.flatMap(i -> Cons.of(i, Step.single(i + 1)))
								.stream()
								.collect(Collectors.toList()))
				.isEqualTo(Collections.emptyList());
	}

	private static Fiber<Step<Integer>> ints(int i, Step<Integer> tail) {
		if (i == 0) {
			return done(tail);
		} else {
			return Fiber.defer(() -> ints(i - 1, Cons.of(i, tail)));
		}
	}

	@Test
	public void shouldBuildIncomplete() {
		Assertions.assertThat(
						Step.incomplete(() -> ints(10, Step.empty()))
								.stream()
								.collect(Collectors.toList()))
				.isEqualTo(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
	}

	@Test
	public void shouldMapIncomplete() {
		Assertions.assertThat(
						Step.incomplete(() -> ints(10, Step.empty()))
								.map(i -> i + 1)
								.stream()
								.collect(Collectors.toList()))
				.isEqualTo(Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
	}

	@Test
	public void shouldFlatMapIncomplete() {
		Assertions.assertThat(
						Step.incomplete(() -> ints(10, Step.empty()))
								.flatMap(i -> Cons.of(i, Step.single(i + 1)))
								.stream()
								.collect(Collectors.toList()))
				.isEqualTo(Arrays.asList(1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11));
	}

	@Test
	public void shouldMapSingle() {
		Assertions.assertThat(
						Step.single(1)
								.map(i -> i + 1)
								.stream()
								.collect(Collectors.toList()))
				.isEqualTo(Collections.singletonList(2));
	}

	@Test
	public void shouldFlatMapSingle() {
		Assertions.assertThat(
						Step.single(1)
								.flatMap(i -> Cons.of(i, Step.single(i + 1)))
								.stream()
								.collect(Collectors.toList()))
				.isEqualTo(Arrays.asList(1, 2));
	}

	@Test
	public void shouldMapCons() {
		Assertions.assertThat(
						Step.ofAll(ITEMS)
								.map(i -> i + 1)
								.stream()
								.collect(Collectors.toList()))
				.isEqualTo(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
	}

	@Test
	public void shouldFlatMapCons() {
		Assertions.assertThat(
						Step.ofAll(ITEMS)
								.flatMap(i -> Cons.of(i, Step.single(i + 1)))
								.stream()
								.collect(Collectors.toList()))
				.isEqualTo(Arrays.asList(
						0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10));
	}

	@Test
	public void shouldInterleave() {
		Assertions.assertThat(
						Step.<Integer> empty().interleave(
										Array.of(
												Step.single(0),
												Step.ofAll(ITEMS),
												Step.incomplete(() -> ints(10, Step.empty()))))
								.get()
								.stream()
								.collect(Collectors.toList()))
				.isEqualTo(Arrays.asList(0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10));
	}

}