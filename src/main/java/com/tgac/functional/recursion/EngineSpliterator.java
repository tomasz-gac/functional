package com.tgac.functional.recursion;

import java.util.Spliterator;
import java.util.function.Consumer;

final class EngineSpliterator<A> implements Spliterator<A> {
	private final Engine<A> engine;

	public EngineSpliterator(Engine<A> engine) {
		this.engine = engine;
	}

	@Override
	public boolean tryAdvance(Consumer<? super A> action) {
		return !engine.runUntilResult(v -> {
			System.out.println(v);
			action.accept(v);
		});
	}

	@Override
	public Spliterator<A> trySplit() {
		// Not parallelizable — no splitting
		return null;
	}

	@Override
	public long estimateSize() {
		return Long.MAX_VALUE; // unknown
	}

	@Override
	public int characteristics() {
		return NONNULL | ORDERED;
	}
}