package com.tgac.functional.fibers;

// ABOUTME: The Named node: names the dynamic extent of its body for observers —
// ABOUTME: steps bill under the name, children inherit it, completion restores.

import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.interpreter.ScopeProfiler;
import com.tgac.functional.fibers.schedulers.BreadthFirstScheduler;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class NamedNodeTest {

	@Test
	public void stepsInsideTheBodyBillUnderTheName() {
		ScopeProfiler profiler = new ScopeProfiler();
		Fiber<Nothing> program = Fiber.named(origin -> "region", countdown(20))
				.flatMap(_0 -> countdown(20));
		new BreadthFirstScheduler<>(program).withListener(profiler).run(v -> {
		});
		Map<String, Long> counts = profiler.counts();
		assertThat(counts.get("region")).isGreaterThan(10L);
		assertThat(counts.get("root")).isGreaterThan(10L);
	}

	@Test
	public void nestedNamesRestoreTheEnclosingOne() {
		ScopeProfiler profiler = new ScopeProfiler();
		Fiber<Nothing> program = Fiber.named(origin -> "outer",
				countdown(10)
						.flatMap(_0 -> Fiber.named(origin -> "inner", countdown(10)))
						.flatMap(_0 -> countdown(10)));
		new BreadthFirstScheduler<>(program).withListener(profiler).run(v -> {
		});
		Map<String, Long> counts = profiler.counts();
		assertThat(counts.get("outer")).isGreaterThan(10L);
		assertThat(counts.get("inner")).isGreaterThan(5L);
	}

	@Test
	public void forkedChildrenInheritTheName() {
		ScopeProfiler profiler = new ScopeProfiler();
		Fiber<Nothing> program = Fiber.named(origin -> "region",
				Fiber.fork(Arrays.asList(countdown(20), countdown(20))));
		new BreadthFirstScheduler<>(program).withListener(profiler).run(v -> {
		});
		assertThat(profiler.counts().get("region")).isGreaterThan(20L);
	}

	private static Fiber<Nothing> countdown(int n) {
		return n == 0 ? Fiber.done(Nothing.nothing())
				: Fiber.defer(() -> countdown(n - 1));
	}
}
