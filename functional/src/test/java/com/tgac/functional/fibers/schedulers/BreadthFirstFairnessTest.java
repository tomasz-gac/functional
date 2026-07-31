package com.tgac.functional.fibers.schedulers;

// ABOUTME: Pins the fairness valve: a same-depth sibling stuck behind a busy
// ABOUTME: fragment is rescued by promotion - starvation is bounded, not forever.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

public class BreadthFirstFairnessTest {

	private static Fiber<Nothing> spin() {
		return Fiber.defer(BreadthFirstFairnessTest::spin);
	}

	@Test
	public void aSameDepthSiblingOfABusyFragmentEventuallySteps() {
		// two forks at the same parent depth build SIBLING depth+2 buckets
		// (addAll checks only the peek - deliberate fragmentation); the
		// first fragment spins forever, so only the promotion valve can let
		// the second fragment's frame run. Bounded starvation, pinned
		AtomicBoolean rescued = new AtomicBoolean(false);
		Fiber<Nothing> program = Fiber.fork(Arrays.asList(
				Fiber.fork(Collections.singletonList(spin())),
				Fiber.fork(Collections.singletonList(Fiber.defer(() -> {
					rescued.set(true);
					return done(nothing());
				})))));

		BreadthFirstScheduler<Nothing> engine = new BreadthFirstScheduler<>(program, 100);
		engine.run(50_000, value -> {
		});

		assertThat(rescued).isTrue();
	}
}
