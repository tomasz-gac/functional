package com.tgac.functional;

import static com.tgac.functional.Tuples.tuple;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TupleTest {

	@Test
	public void shouldBuildTuples() {
		Tuples._0 t0 = tuple();
		Tuples._1<Integer> t1 = tuple(1);
		Tuples._2<Integer, String> t2 = tuple(1, "str");
	}

	@Test
	public void shouldConcatTuples() {
		Assertions.assertThat(tuple()
						.concat(tuple("1", 3))
						.concat(tuple(true)))
				.isEqualTo(tuple("1", 3, true));
	}

	@Test
	public void shouldRemoveFromTuple() {
		Assertions.assertThat(tuple("1", 3, true)
						.remove1())
				.isEqualTo(tuple("1", true));
	}

	@Test
	public void shouldApplyTuple() {
		String apply = tuple("1", 3, true)
				.apply((a, b, c) -> a + b + c);
		Assertions.assertThat(apply)
				.isEqualTo("13true");
	}
}
