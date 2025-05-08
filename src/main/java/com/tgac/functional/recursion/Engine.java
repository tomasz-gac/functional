package com.tgac.functional.recursion;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Engine<A> extends Supplier<A> {
	boolean step(Consumer<? super A> sink);

	boolean run(int iterations, Consumer<? super A> sink);
	void run(Consumer<? super A> sink);
	Optional<A> run(int iterations);
}
