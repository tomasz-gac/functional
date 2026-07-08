package com.tgac.functional.fibers;

// ABOUTME: Pins the Worklist contract: FIFO drain, discovered work appended, stop
// ABOUTME: short-circuits, deep queues are stack-safe, and stepping is fair.

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class WorklistTest {

	@Test
	public void drainsToQuiescenceAppendingDiscoveredWork() {
		// each item n < 3 discovers n+1; sums everything processed
		Integer sum = Worklist.drain(0, Collections.singletonList(0),
						(acc, n) -> n < 3 ?
								Worklist.Step.proceed(acc + n, Collections.singletonList(n + 1)) :
								Worklist.Step.proceed(acc + n))
				.get();
		assertThat(sum).isEqualTo(0 + 1 + 2 + 3);
	}

	@Test
	public void processesInFifoOrder() {
		StringBuilder order = new StringBuilder();
		Worklist.drain("", Arrays.asList("a", "b"),
						(acc, w) -> {
							order.append(w);
							return "a".equals(w) ?
									Worklist.Step.proceed(acc, Arrays.asList("c")) :
									Worklist.Step.<String, String> proceed(acc);
						})
				.get();
		// discovered work queues BEHIND existing work
		assertThat(order.toString()).isEqualTo("abc");
	}

	@Test
	public void stopShortCircuits() {
		Integer processed = Worklist.drain(0, Arrays.asList(1, 2, 3, 4),
						(acc, n) -> n == 2 ?
								Worklist.Step.stop(acc + n) :
								Worklist.Step.proceed(acc + n))
				.get();
		assertThat(processed).isEqualTo(3);
	}

	@Test
	public void deepQueuesAreStackSafe() {
		Integer count = Worklist.drain(0, Collections.singletonList(0),
						(acc, n) -> n < 100_000 ?
								Worklist.Step.proceed(acc + 1, Collections.singletonList(n + 1)) :
								Worklist.Step.proceed(acc + 1))
				.get();
		assertThat(count).isEqualTo(100_001);
	}

	@Test
	public void stepsAreSchedulable() {
		// one item per engine step: the drain is preemptible between items
		Scheduler<Integer> engine = Worklist
				.drain(0, Arrays.asList(1, 2, 3), (acc, n) -> Worklist.Step.<Integer, Integer> proceed(acc + n))
				.toEngine();
		assertThat(engine.run(2)).isEmpty();          // not done after 2 steps
		assertThat(engine.run(10)).contains(6);       // finishes with room to spare
	}
}
