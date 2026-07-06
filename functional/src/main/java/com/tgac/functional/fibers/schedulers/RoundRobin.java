package com.tgac.functional.fibers.schedulers;

// ABOUTME: The simplest scheduler: a flat list of frames stepped in rotation.
// ABOUTME: A driver over FiberStep — all it owns is the queue and the fork join.

import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Scheduler;
import com.tgac.functional.category.Nothing;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoundRobin<A> implements Scheduler<A>, FiberStep.Effects {

	private final List<Entry> entries;
	private int index = -1;
	private StepListener stepListener = StepListener.NO_OP;

	@Override
	public RoundRobin<A> withListener(StepListener listener) {
		this.stepListener = listener == null ? StepListener.NO_OP : listener;
		return this;
	}

	@Override
	public StepListener listener() {
		return stepListener;
	}

	// the entry being stepped and the sink of the current step() call
	private Entry current;
	private Consumer<? super A> rootSink;
	private boolean currentCompleted;

	public static <A> RoundRobin<A> of(Fiber<A> fiber) {
		ArrayList<Entry> entries = new ArrayList<>();
		entries.add(new Entry(new FiberStep.Frame(fiber), null));
		return new RoundRobin<>(entries);
	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		for (int step = 0; step < iterations; ++step) {
			if (step(sink))
				return true;
		}
		return entries.isEmpty();
	}

	@Override
	public void run(Consumer<? super A> sink) {
		while (true) {
			if (run(Integer.MAX_VALUE, sink)) {
				break;
			}
		}
	}

	@Override
	public Optional<A> run(int iterations) {
		Object[] box = new Object[1];
		return run(iterations, v -> box[0] = v) ?
				Optional.of((A) box[0]) :
				Optional.empty();
	}

	@Override
	public A get() {
		AtomicReference<A> result = new AtomicReference<>();
		run(result::set);
		return result.get();
	}

	@Override
	public boolean step(Consumer<? super A> sink) {
		if (entries.isEmpty())
			return true;

		index = (index + 1) % entries.size();
		current = entries.get(index);
		rootSink = sink;
		currentCompleted = false;

		FiberStep.step(current.frame, this);

		return currentCompleted && entries.isEmpty();
	}

	@Override
	public void completed(Object value) {
		Collections.swap(entries, index, entries.size() - 1); // avoids shuffling
		entries.remove(entries.size() - 1);
		if (current.sink != null) {
			current.sink.accept(value);
		} else {
			rootSink.accept((A) value);
		}
		currentCompleted = true;
	}

	@Override
	public void forked(Fiber.Forked<Object> fork) {
		entries.remove(index);

		Entry parent = current;
		AtomicInteger pending = new AtomicInteger(fork.getOptions().size());
		Consumer<Object> notifyParent = result -> {
			if (pending.decrementAndGet() == 0) {
				parent.frame.computation = doneNothing();
				entries.add(parent);
				index = entries.size() - 1;
			}
			fork.getSink().accept(result);
		};

		for (Fiber<Object> option : fork.getOptions()) {
			entries.add(new Entry(new FiberStep.Frame(option), notifyParent));
		}
		index = -1;
	}

	@Override
	public void detached(Fiber<?> child) {
		// runs independently; its result is discarded
		entries.add(new Entry(new FiberStep.Frame(child), value -> {}));
	}

	private static Fiber<Object> doneNothing() {
		return (Fiber<Object>) (Fiber<?>) Fiber.done(Nothing.nothing());
	}

	@Override
	public void close() {
		// empty by design
	}

	@RequiredArgsConstructor
	private static final class Entry {
		final FiberStep.Frame frame;
		final Consumer<Object> sink; // null delivers to the root sink
	}
}
