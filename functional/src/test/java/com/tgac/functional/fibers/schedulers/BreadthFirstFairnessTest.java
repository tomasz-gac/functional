package com.tgac.functional.fibers.schedulers;

// ABOUTME: Pins the scheduler contracts: honest BFS never demotes a healthy level
// ABOUTME: and crashes a dead one; the unfair scheduler joins starved siblings.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class BreadthFirstFairnessTest {

	private static Fiber<Nothing> spin() {
		return Fiber.defer(BreadthFirstFairnessTest::spin);
	}

	@Test
	public void aHealthyFrontierIsNeverDemoted() {
		// 150 shallow siblings, three interpreter nodes each, recording at
		// their LAST node - the frontier legitimately lives ~450 steps. BFS
		// must hold the whole way: the deeper frame runs only after the
		// frontier empties. A valve that pours a level down after a fixed
		// step budget lets the deep frame jump the queue mid-frontier.
		List<String> order = new ArrayList<>();
		List<Fiber<Nothing>> shallow = new ArrayList<>();
		// the deep fork goes FIRST: its second node (the actual fork) runs
		// one rotation in, so the deep bucket exists while the frontier is
		// still busy - the demotion window is wide open
		shallow.add(Fiber.fork(Collections.singletonList(Fiber.defer(() -> {
			order.add("deep");
			return done(nothing());
		}))));
		shallow.addAll(IntStream.range(0, 150)
				.<Fiber<Nothing>> mapToObj(id -> Fiber.defer(() -> Fiber.defer(() -> {
					order.add("s" + id);
					return done(nothing());
				})))
				.collect(Collectors.toList()));

		new BreadthFirstScheduler<>(Fiber.fork(shallow)).run(50_000, value -> {
		});

		assertThat(order).hasSize(151);
		assertThat(order.get(150)).isEqualTo("deep");
	}

	@Test
	public void aSameDepthSiblingOfABusyFragmentEventuallySteps() {
		// UNFAIR scheduler: five forks at the same parent depth build five
		// SIBLING buckets (deliberate fragmentation); four spin forever.
		// Whatever tie-order the heap picks, the reigning bucket is a
		// spinner within a pour or two, and only the promotion pour can
		// reach the rescue fragment - bounded starvation, pinned
		AtomicBoolean rescued = new AtomicBoolean(false);
		Fiber<Nothing> program = Fiber.fork(Arrays.asList(
				Fiber.fork(Collections.singletonList(spin())),
				Fiber.fork(Collections.singletonList(spin())),
				Fiber.fork(Collections.singletonList(Fiber.defer(() -> {
					rescued.set(true);
					return done(nothing());
				}))),
				Fiber.fork(Collections.singletonList(spin())),
				Fiber.fork(Collections.singletonList(spin()))));

		UnfairBreadthFirstScheduler<Nothing> engine = new UnfairBreadthFirstScheduler<>(program, 100);
		engine.run(5_000, value -> {
		});

		assertThat(rescued).isTrue();
	}

	@Test
	public void aDeadLevelCrashesThroughToItsDeeperSibling() {
		// a spinner alone on its level, deeper work waiting below, and no
		// same-depth sibling to join: only the crash can free the deep frame.
		// The threshold here is small so the test observes it; the default
		// sits at 10k because nothing can rescue a bad program anyway
		AtomicBoolean deepRan = new AtomicBoolean(false);
		Fiber<Nothing> program = Fiber.fork(Arrays.asList(
				spin(),
				Fiber.fork(Collections.singletonList(Fiber.defer(() -> {
					deepRan.set(true);
					return done(nothing());
				})))));

		new BreadthFirstScheduler<>(program, 500).run(3_000, value -> {
		});

		assertThat(deepRan).isTrue();
	}
}
