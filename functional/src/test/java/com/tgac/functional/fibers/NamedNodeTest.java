package com.tgac.functional.fibers;

// ABOUTME: The Named node: names the dynamic extent of its body for observers —
// ABOUTME: steps bill under the name, children inherit it, completion restores.

import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.interpreter.Scope;
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
	public void nestedNamesBillTheJoinedChainAndRestore() {
		ScopeProfiler profiler = new ScopeProfiler();
		Fiber<Nothing> program = Fiber.named(origin -> "outer",
				countdown(10)
						.flatMap(_0 -> Fiber.named(origin -> "inner", countdown(10)))
						.flatMap(_0 -> countdown(10)));
		new BreadthFirstScheduler<>(program).withListener(profiler).run(v -> {
		});
		Map<String, Long> counts = profiler.counts();
		// the inner extent bills under the root-first chain, never bare
		assertThat(counts.get("outer;inner")).isGreaterThan(5L);
		assertThat(counts).doesNotContainKey("inner");
		// steps before AND after the inner extent bill to the restored outer
		assertThat(counts.get("outer")).isGreaterThan(15L);
	}

	@Test
	public void selfRepeatingExtentsCollapse() {
		ScopeProfiler profiler = new ScopeProfiler();
		new BreadthFirstScheduler<>(reenter("loop", 20)).withListener(profiler).run(v -> {
		});
		Map<String, Long> counts = profiler.counts();
		assertThat(counts.get("loop")).isGreaterThan(20L);
		assertThat(counts.keySet().stream()
				.anyMatch(label -> label.contains("loop;loop")))
				.isFalse();
	}

	@Test
	public void forkedChildrenInheritTheChain() {
		ScopeProfiler profiler = new ScopeProfiler();
		Fiber<Nothing> program = Fiber.named(origin -> "outer",
				Fiber.named(origin -> "inner",
						Fiber.fork(Arrays.asList(countdown(20), countdown(20)))));
		new BreadthFirstScheduler<>(program).withListener(profiler).run(v -> {
		});
		assertThat(profiler.counts().get("outer;inner")).isGreaterThan(20L);
	}

	/** A recursive extent re-minting the same name at every level. */
	private static Fiber<Nothing> reenter(String name, int n) {
		return Fiber.named(origin -> name,
				n == 0 ? Fiber.done(Nothing.nothing())
						: Fiber.defer(() -> reenter(name, n - 1)));
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

	@Test
	public void aNamedExtentInsideAWorkforceBillsTheComposite() {
		ScopeProfiler profiler = new ScopeProfiler();
		Scope work = Scope.scope("work");
		Fiber<Nothing> program = Fiber.named(origin -> "region",
				Fiber.claim(work, countdown(20))
						.flatMap(_0 -> Fiber.sealed(work)));
		new BreadthFirstScheduler<>(program).withListener(profiler).run(v -> {
		});
		assertThat(profiler.counts().get("region;work")).isGreaterThan(10L);
	}

	private static Fiber<Nothing> countdown(int n) {
		return n == 0 ? Fiber.done(Nothing.nothing())
				: Fiber.defer(() -> countdown(n - 1));
	}
}
