package com.tgac.functional;

import static com.tgac.functional.Consumers.consumer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConsumersTest {
	@Test
	public void shouldBuildFunctions() {
		Reference<String> result = Reference.empty();
		consumer(() -> result.set(null))
				.accept();
		Assertions.assertThat(result.get())
				.isEqualTo(null);
		consumer(result::set).accept("1");
		Assertions.assertThat(result.get())
				.isEqualTo("1");
		consumer((a, b) -> result.set(a.toString() + b)).accept(1, 2);
		Assertions.assertThat(result.get())
				.isEqualTo("12");
		consumer((a, b, c) -> result.set(a.toString() + b + c)).accept(1, 2, 3);
		Assertions.assertThat(result.get())
				.isEqualTo("123");
	}

	@Test
	public void shouldApplyPartial() {
		Reference<String> result = Reference.empty();
		consumer((a, b, c) -> result.set(a.toString() + b + c))
				.partial(1)
				.accept(2, 3);
		Assertions.assertThat(result.get())
				.isEqualTo("123");
	}

	@Test
	public void shouldApplyPartialRight() {
		Reference<String> result = Reference.empty();
		consumer((a, b, c) -> result.set(a.toString() + b + c))
				.partialRight(3)
				.accept(1, 2);
		Assertions.assertThat(result.get())
				.isEqualTo("123");
	}

	@Test
	public void shouldApplyPartialByNumber() {
		Reference<String> result = Reference.empty();
		consumer((a, b, c) -> result.set(a.toString() + b + c))
				.partial1(2)
				.accept(1, 3);
		Assertions.assertThat(result.get())
				.isEqualTo("123");
	}
}
