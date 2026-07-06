package com.tgac.functional.fibers;

import static com.tgac.functional.fibers.Fiber.defer;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.schedulers.BreadthFirstScheduler;
import com.tgac.functional.fibers.schedulers.ForkJoinScheduler;
import com.tgac.functional.fibers.schedulers.StepListener;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

/**
 * The step seam observes reductions uniformly across schedulers.
 */
public class StepListenerTest {

	private static final class Recorder implements StepListener {
		final AtomicInteger steps = new AtomicInteger();
		final AtomicInteger forks = new AtomicInteger();
		final AtomicInteger detaches = new AtomicInteger();
		final java.util.List<Object> completed = new java.util.concurrent.CopyOnWriteArrayList<>();

		@Override
		public void onStep(Fiber<?> node) {
			steps.incrementAndGet();
		}

		@Override
		public void onCompleted(Object value) {
			completed.add(value);
		}

		@Override
		public void onForked(Fiber.Forked<?> fork) {
			forks.incrementAndGet();
		}

		@Override
		public void onDetached(Fiber<?> child) {
			detaches.incrementAndGet();
		}
	}

	@Test
	public void shouldObserveStepsCompletionsAndForks() {
		Recorder recorder = new Recorder();
		Fiber<Nothing> program = Fiber.fork(Arrays.asList(done(1), defer(() -> done(2))), v -> {})
				.map(_0 -> Nothing.nothing());

		new BreadthFirstScheduler<>(program).withListener(recorder).get();

		assertThat(recorder.steps.get()).isGreaterThan(0);
		assertThat(recorder.forks.get()).isEqualTo(1);
		assertThat(recorder.completed).contains(1, 2);
	}

	@Test
	public void shouldObserveDetach() {
		Recorder recorder = new Recorder();
		Fiber<Nothing> program = Fiber.detach(done(99)).map(_0 -> Nothing.nothing());

		new BreadthFirstScheduler<>(program).withListener(recorder).get();

		assertThat(recorder.detaches.get()).isEqualTo(1);
		assertThat(recorder.completed).contains(99);
	}

	@Test
	public void shouldObserveTheSameEventsUnderTheParallelScheduler() {
		Recorder recorder = new Recorder();
		Fiber<Nothing> program = Fiber.fork(Arrays.asList(done(1), done(2)), v -> {})
				.map(_0 -> Nothing.nothing());

		new ForkJoinScheduler<>(program).withListener(recorder).get();

		assertThat(recorder.forks.get()).isEqualTo(1);
		assertThat(recorder.completed).contains(1, 2);
	}

	@Test
	public void shouldCostNothingWhenAbsent() {
		// the default listener is NO_OP — programs run unchanged without one
		AtomicReference<Long> result = new AtomicReference<>();
		Fiber<Long> program = sum(1000);

		result.set(new BreadthFirstScheduler<>(program).get());

		assertThat(result.get()).isEqualTo(500500L);
	}

	private Fiber<Long> sum(long n) {
		return sum(n, 0L);
	}

	private Fiber<Long> sum(long n, long acc) {
		return n == 0 ? done(acc) : defer(() -> sum(n - 1, acc + n));
	}
}
