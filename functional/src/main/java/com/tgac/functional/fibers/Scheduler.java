package com.tgac.functional.fibers;

import com.tgac.functional.fibers.interpreter.StepListener;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Scheduler<A> extends Supplier<A>, AutoCloseable {
	boolean step(Consumer<? super A> sink);

	/**
	 * Drive by an explicit STEP COUNT — the stepping drivers' contract. A
	 * driver that cannot count steps (a pool drive) refuses loudly; callers
	 * that just want bounded progress use {@link #advance}.
	 */
	boolean run(int iterations, Consumer<? super A> sink);

	/**
	 * Advance the computation by this scheduler's own bounded quantum — a
	 * step batch on stepping drivers, a poll window on pool drivers — and
	 * report whether the computation has completed. The quantum belongs to
	 * the scheduler: callers ask for progress, never for a unit count.
	 */
	default boolean advance(Consumer<? super A> sink) {
		return run(64, sink);
	}

	void run(Consumer<? super A> sink);

	Optional<A> run(int iterations);

	/**
	 * Install a step observer. Schedulers driven by the shared step
	 * interpreter honour it; one not built on the interpreter has nothing
	 * per-step to report and leaves it a no-op.
	 */
	default Scheduler<A> withListener(StepListener listener) {
		return this;
	}
}
