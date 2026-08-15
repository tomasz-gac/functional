package com.tgac.functional.fibers.interpreter;

// ABOUTME: The eager budget: a small fixed default receipted against fresh
// ABOUTME: stacks, settable, bounding nesting; zero is pure laziness.

import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.fibers.Fiber;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class EagerBudgetTest {

	@Test
	public void aBudgetLengthChainOfRealAppliesFitsAFreshDefaultStack() throws InterruptedException {
		// the default's stack-exposure receipt: budget × real apply frames
		// on a fresh default-stack thread
		int budget = EngineGuard.eagerBudget();
		int[] reached = new int[1];
		Throwable[] failed = new Throwable[1];
		Thread t = new Thread(() -> {
			try {
				nest(0, budget, reached);
			} catch (Throwable e) {
				failed[0] = e;
			}
		}, "budget-receipt");
		t.start();
		t.join();
		assertThat(failed[0]).isNull();
		assertThat(reached[0]).isEqualTo(budget);
	}

	@Test
	public void heavyContinuationsSurviveTheBudget() throws InterruptedException {
		// continuations with fat frames bound the worst-case exposure
		int budget = EngineGuard.eagerBudget();
		long[] sum = new long[1];
		int[] reached = new int[1];
		Throwable[] failed = new Throwable[1];
		Thread t = new Thread(() -> {
			try {
				nestHeavy(0, budget, reached, sum);
			} catch (Throwable e) {
				failed[0] = e;
			}
		}, "budget-receipt-heavy");
		t.start();
		t.join();
		assertThat(failed[0]).isNull();
		assertThat(reached[0]).isEqualTo(budget);
	}

	@Test
	public void theBudgetBoundsNestingAndIsSettable() {
		int old = EngineGuard.eagerBudget();
		try {
			EngineGuard.setEagerBudget(7);
			assertThat(deep(20).isDone()).isFalse();
			EngineGuard.setEagerBudget(64);
			assertThat(deep(20).isDone()).isTrue();
		} finally {
			EngineGuard.setEagerBudget(old);
		}
	}

	@Test
	public void budgetZeroIsPureLazinessWithTheSameValue() {
		int old = EngineGuard.eagerBudget();
		Fiber<Integer> eager = deep(2);
		assertThat(eager.isDone()).isTrue();
		int eagerValue = eager.getDone("budget test");
		try {
			EngineGuard.setEagerBudget(0);
			Fiber<Integer> lazy = deep(2);
			assertThat(lazy.isDone()).isFalse();
			assertThat(lazy.ground()).isEqualTo(eagerValue);
		} finally {
			EngineGuard.setEagerBudget(old);
		}
	}

	private static void nest(int level, int limit, int[] reached) {
		if (level >= limit) {
			reached[0] = level;
			return;
		}
		Fiber.done(level).flatMap(v -> {
			nest(v + 1, limit, reached);
			return Fiber.done(v);
		});
	}

	private static void nestHeavy(int level, int limit, int[] reached, long[] sum) {
		if (level >= limit) {
			reached[0] = level;
			return;
		}
		Fiber.done(level).flatMap(v -> {
			long a = v, b = v + 1, c = v + 2, d = v + 3;
			long e = a * b, f = c * d, g = e + f, h = g ^ a;
			long[] pad = {a, b, c, d, e, f, g, h};
			nestHeavy(v + 1, limit, reached, sum);
			sum[0] += pad[(int) (h & 7)] + a + b + c + d + e + f + g;
			return Fiber.done(v);
		});
	}

	private static Fiber<Integer> deep(int n) {
		return n == 0 ? Fiber.done(0) : Fiber.done(n).flatMap(v -> deep(n - 1));
	}
}
