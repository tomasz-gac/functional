package com.tgac.functional.fibers;

import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.schedulers.BreadthFirstScheduler;
import com.tgac.functional.fibers.schedulers.ForkJoinScheduler;
import com.tgac.functional.fibers.schedulers.RoundRobin;
import com.tgac.functional.fibers.schedulers.SearchInspectable;
import com.tgac.functional.fibers.schedulers.SearchSnapshot;
import com.tgac.functional.fibers.schedulers.UnfairBreadthFirstScheduler;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class SearchSnapshotTest {

	private final List<Function<Fiber<Nothing>, SearchInspectable>> inspectables = Arrays.asList(
			BreadthFirstScheduler::new,
			RoundRobin::of,
			UnfairBreadthFirstScheduler::of);

	private Fiber<Nothing> forkOfThree() {
		return Fiber.fork(Arrays.asList(done(1), done(2), done(3)), v -> {
				})
				.map(_0 -> Nothing.nothing());
	}

	@Test
	public void shouldShowTheRootFrameBeforeStepping() {
		for (Function<Fiber<Nothing>, SearchInspectable> factory : inspectables) {
			SearchInspectable scheduler = factory.apply(forkOfThree());

			SearchSnapshot snap = scheduler.snapshot();

			assertThat(snap.getFrameCount()).isEqualTo(1);
			// the root is fork(...).map(...) — a FlatMap
			assertThat(snap.getNodeTypes()).containsKey("FlatMap");
		}
	}

	@Test
	public void shouldBeEmptyAfterTheSearchCompletes() {
		for (Function<Fiber<Nothing>, SearchInspectable> factory : inspectables) {
			SearchInspectable scheduler = factory.apply(forkOfThree());

			((Scheduler<Nothing>) scheduler).get();

			assertThat(scheduler.snapshot().getFrameCount()).isZero();
		}
	}

	@Test
	public void shouldRevealTheForkedBranchesAtPeakBreadth() {
		for (Function<Fiber<Nothing>, SearchInspectable> factory : inspectables) {
			SearchInspectable scheduler = factory.apply(forkOfThree());
			Scheduler<Nothing> driver = (Scheduler<Nothing>) scheduler;

			int peak = 0;
			while (!driver.run(1, v -> {
			})) {
				peak = Math.max(peak, scheduler.snapshot().getFrameCount());
			}

			// the three fork options are alive simultaneously at some point
			assertThat(peak).isGreaterThanOrEqualTo(3);
		}
	}

	@Test
	public void shouldRenderAReadableDump() {
		SearchInspectable scheduler = new BreadthFirstScheduler<>(forkOfThree());

		assertThat(scheduler.snapshot().toString()).contains("live frames: 1");
	}

	@Test
	public void shouldNotMakeTheParallelSchedulerInspectable() {
		Scheduler<Nothing> parallel = new ForkJoinScheduler<>(forkOfThree());

		assertThat(parallel).isNotInstanceOf(SearchInspectable.class);
	}
}
