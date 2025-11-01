package com.tgac.functional.fibers;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Scheduler<A> extends Supplier<A>, AutoCloseable {
	boolean step(Consumer<? super A> sink);

	boolean run(int iterations, Consumer<? super A> sink);

	void run(Consumer<? super A> sink);

	Optional<A> run(int iterations);
}
