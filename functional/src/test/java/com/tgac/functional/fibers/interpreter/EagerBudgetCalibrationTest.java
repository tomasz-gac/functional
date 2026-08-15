package com.tgac.functional.fibers.interpreter;

// ABOUTME: The calibrated eager budget: probed from real stack capacity via the
// ABOUTME: actual Done.flatMap path, settable, and receipted against fresh stacks.

import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.fibers.Fiber;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class EagerBudgetCalibrationTest {

	@Test
	public void theProbeMeasuresThousandsOfRealAppliesOnADefaultStack() {
		// probe frames are real eager applies; any sane stack fits far more
		// than the floor times the margin
		assertThat(EngineGuard.probeCapacity()).isGreaterThanOrEqualTo(256);
	}

	@Test
	public void aBudgetLengthChainOfRealAppliesFitsAFreshDefaultStack() throws InterruptedException {
		// THE RECEIPT: the margin must leave a calibrated budget's worth of
		// real applies safely inside a default thread stack
		int budget = Math.max(64, EngineGuard.probeCapacity() / 4);
		int[] reached = new int[1];
		Throwable[] failed = new Throwable[1];
		Thread t = new Thread(() -> {
			try {
				nest(0, budget, reached);
			} catch (Throwable e) {
				failed[0] = e;
			}
		}, "budget-receipt");
		int pinned = EngineGuard.eagerBudget();
		try {
			// the receipt certifies the CALIBRATED budget, not the suite's pin
			EngineGuard.setEagerBudget(budget);
			t.start();
			t.join();
		} finally {
			EngineGuard.setEagerBudget(pinned);
		}
		assertThat(failed[0]).isNull();
		assertThat(reached[0]).isEqualTo(budget);
	}

	@Test
	public void heavyContinuationsSurviveTheBudget() throws InterruptedException {
		// continuations with fat frames bound the margin's weight assumption
		int budget = Math.max(64, EngineGuard.probeCapacity() / 4);
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
		int pinned = EngineGuard.eagerBudget();
		try {
			EngineGuard.setEagerBudget(budget);
			t.start();
			t.join();
		} finally {
			EngineGuard.setEagerBudget(pinned);
		}
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
		int eagerValue = deep(20).getDone("calibration test");
		try {
			EngineGuard.setEagerBudget(0);
			Fiber<Integer> lazy = deep(20);
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
