package com.tgac.functional.fibers.schedulers;

// ABOUTME: Depth-ordered scheduler that always steps the shallowest frame.
// ABOUTME: A driver over FiberStep — unfair because a shallow frame can starve deeper ones.

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Scheduler;
import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnfairBreadthFirstScheduler<A> implements Scheduler<A>, FiberStep.Effects {

	private final PriorityQueue<Entry> entries;
	private StepListener stepListener = StepListener.NO_OP;

	public UnfairBreadthFirstScheduler<A> withListener(StepListener listener) {
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

	public static <A> UnfairBreadthFirstScheduler<A> of(Fiber<A> fiber) {
		PriorityQueue<Entry> entries = new PriorityQueue<>(Comparator.comparingInt(Entry::getDepth));
		entries.add(new Entry(new FiberStep.Frame(fiber), null, 0));
		return new UnfairBreadthFirstScheduler<>(entries);
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

		current = entries.peek();
		rootSink = sink;
		currentCompleted = false;

		FiberStep.step(current.frame, this);

		return currentCompleted && entries.isEmpty();
	}

	@Override
	public void completed(Object value) {
		entries.poll();
		if (current.sink != null) {
			current.sink.accept(value);
		} else {
			rootSink.accept((A) value);
		}
		currentCompleted = true;
	}

	@Override
	public void forked(Fiber.Forked<Object> fork) {
		entries.poll();

		Entry parent = current;
		AtomicInteger pending = new AtomicInteger(fork.getOptions().size());
		Consumer<Object> notifyParent = result -> {
			if (pending.decrementAndGet() == 0) {
				parent.frame.computation = doneNothing();
				entries.offer(parent); // re-introduce the parent node
			}
			fork.getSink().accept(result);
		};

		for (Fiber<Object> option : fork.getOptions()) {
			entries.offer(new Entry(new FiberStep.Frame(option), notifyParent, current.depth + 1));
		}
	}

	@Override
	public void detached(Fiber<?> child) {
		// runs independently; its result is discarded
		entries.offer(new Entry(new FiberStep.Frame(child), value -> {}, current.depth));
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
		@Getter
		final int depth;
	}
}
