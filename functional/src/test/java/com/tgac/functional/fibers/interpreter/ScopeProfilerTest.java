package com.tgac.functional.fibers.interpreter;

// ABOUTME: The scope profiler: steps bucketed by the stepped frame's owning
// ABOUTME: scope, labeled by the scope's name or its construction site.

import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.schedulers.BreadthFirstScheduler;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ScopeProfilerTest {

	@Test
	public void originCapturesTheConstructionSite() {
		Scope[] minted = new Scope[1];
		OriginCapture.within(() -> minted[0] = Scope.scope());
		Scope scope = minted[0];
		boolean found = false;
		for (StackTraceElement frame : scope.origin().getStackTrace()) {
			if (frame.getClassName().contains("ScopeProfilerTest")) {
				found = true;
			}
		}
		assertThat(found).isTrue();
	}

	@Test
	public void stepsBucketByScopeNameAndRoot() {
		ScopeProfiler profiler = new ScopeProfiler("com.tgac.functional.");
		Scope work = Scope.scope("work");
		Fiber<Nothing> program = Fiber.claim(work, countdown(50))
				.flatMap(_0 -> Fiber.sealed(work));
		new BreadthFirstScheduler<>(program).withListener(profiler).run(v -> {
		});
		Map<String, Long> counts = profiler.counts();
		assertThat(counts.get("work")).isGreaterThan(10L);
		assertThat(counts.get("root")).isGreaterThan(0L);
	}

	@Test
	public void anAnonymousScopeDerivesItsClientFrame() {
		ScopeProfiler profiler = new ScopeProfiler();
		Scope[] minted = new Scope[1];
		OriginCapture.within(() -> minted[0] = Scope.scope());
		Scope anonymous = minted[0];
		Fiber<Nothing> program = Fiber.claim(anonymous, countdown(50))
				.flatMap(_0 -> Fiber.sealed(anonymous));
		new BreadthFirstScheduler<>(program).withListener(profiler).run(v -> {
		});
		assertThat(profiler.counts().keySet())
				.anyMatch(label -> label.contains("ScopeProfilerTest"));
	}

	private static Fiber<Nothing> countdown(int n) {
		return n == 0 ? Fiber.done(Nothing.nothing())
				: Fiber.defer(() -> countdown(n - 1));
	}
}
