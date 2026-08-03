package com.tgac.functional.fibers.schedulers;

// ABOUTME: The pool drive's contract: a failing frame cancels the whole drive
// ABOUTME: cooperatively; step-counted runs refuse; advance drives by poll window.

import static com.tgac.functional.category.Nothing.nothing;
import static com.tgac.functional.fibers.Fiber.done;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

public class ForkJoinSchedulerTest {

	@Test
	public void failureCancelsSiblingWork() throws Exception {
		AtomicLong siblingTicks = new AtomicLong();
		@SuppressWarnings("rawtypes")
		Fiber[] spin = new Fiber[1];
		spin[0] = Fiber.defer(() -> {
			siblingTicks.incrementAndGet();
			return spin[0];
		});
		Fiber<Nothing> thrower = Fiber.defer(() -> {
			throw new IllegalStateException("boom");
		});
		@SuppressWarnings("unchecked")
		Fiber<Nothing> program = Fiber.fork(Arrays.asList((Fiber<Nothing>) spin[0], thrower));
		ForkJoinScheduler<Nothing> driver = new ForkJoinScheduler<>(program, new ForkJoinPool(2));

		assertThatThrownBy(driver::get).hasMessageContaining("boom");

		// the failure is the whole drive's failure: the spinning sibling
		// must see the cancellation at its next yield and stop
		Thread.sleep(50);
		long afterFailure = siblingTicks.get();
		Thread.sleep(100);
		assertThat(siblingTicks.get())
				.as("sibling work continued after the drive failed")
				.isEqualTo(afterFailure);
	}

	@Test
	public void stepCountedRunRefusesOnAPoolDrive() {
		ForkJoinScheduler<Nothing> driver =
				new ForkJoinScheduler<>(done(nothing()), new ForkJoinPool(1));
		// a pool drive counts no steps — the step-counted contract belongs
		// to the stepping drivers; asking for it here must refuse loudly
		assertThatThrownBy(() -> driver.run(64, v -> {
		}))
				.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void advanceDrivesThePoolByItsOwnQuantum() {
		Object[] box = new Object[1];
		ForkJoinScheduler<Nothing> driver =
				new ForkJoinScheduler<>(done(nothing()), new ForkJoinPool(1));
		while (!driver.advance(v -> box[0] = v)) {
			// keep polling until the trivial program completes
		}
		assertThat(box[0]).isEqualTo(nothing());
	}
}
