package com.tgac.functional.fibers;

import com.tgac.functional.fibers.schedulers.StepListener;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Scheduler<A> extends Supplier<A>, AutoCloseable {
	boolean step(Consumer<? super A> sink);

	boolean run(int iterations, Consumer<? super A> sink);

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
