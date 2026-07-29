package com.tgac.functional.fibers.schedulers;

// ABOUTME: The simplest scheduler: a flat list of frames stepped in rotation.
// ABOUTME: A driver over FiberStep — all it owns is the queue and the fork countdown.

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Source;
import com.tgac.functional.fibers.Scheduler;
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
public final class RoundRobin<A> implements Scheduler<A>, FiberStep.Effects<RoundRobin.Entry>, SearchInspectable {

	private static final Consumer<Object> DISCARD = value -> {
	};

	private final List<Entry> entries;
	private final AwaitBoundary<Entry> awaits = new AwaitBoundary<>();
	private int index = -1;
	private StepListener stepListener = StepListener.NO_OP;

	@Override
	public RoundRobin<A> withListener(StepListener listener) {
		this.stepListener = listener == null ? StepListener.NO_OP : listener;
		return this;
	}

	private Consumer<? super A> rootSink;
	private boolean currentCompleted;

	public static <A> RoundRobin<A> of(Fiber<A> fiber) {
		ArrayList<Entry> entries = new ArrayList<>();
		entries.add(new Entry(new FiberStep.Frame(fiber), null, null));
		return new RoundRobin<>(entries);
	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		for (int step = 0; step < iterations; ++step) {
			if (step(sink))
				return true;
		}
		return entries.isEmpty() && awaits.quiet();
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
		awaits.drainInto(entries::add);
		if (entries.isEmpty()) {
			awaits.refuseStranded();
			return true;
		}

		index = (index + 1) % entries.size();
		Collections.swap(entries, index, entries.size() - 1);
		Entry entry = entries.remove(entries.size() - 1);
		rootSink = sink;
		currentCompleted = false;

		if (entry.frame.step(entry, this, stepListener)) {
			entries.add(entry);
		}

		return currentCompleted && entries.isEmpty();
	}

	@Override
	public void completed(Entry entry, Object value) {
		if (entry.sink != null) {
			entry.sink.accept(value);
		} else {
			rootSink.accept((A) value);
		}
		entry.yielded();
		currentCompleted = true;
	}

	@Override
	public void forked(Entry entry, Fiber.Forked<Object> fork) {
		Entry parent = entry;
		AtomicInteger pending = new AtomicInteger(fork.getOptions().size());
		// the countdown counts CONTROL YIELDS - a suspended child yields without a value
		Runnable childYielded = () -> {
			if (pending.decrementAndGet() == 0) {
				parent.frame.computation = doneNothing();
				entries.add(parent);
				index = entries.size() - 1;
			}
		};

		for (Fiber<Object> option : fork.getOptions()) {
			entries.add(new Entry(new FiberStep.Frame(option, entry.frame.scope), DISCARD, childYielded));
		}
		index = -1;
	}

	@Override
	public void detached(Entry entry, Fiber<?> child, Source<?> into) {
		// runs independently; its result is discarded
		entries.add(new Entry(new FiberStep.Frame(child, into), DISCARD, null));
	}

	@Override
	public ResumeHandle resumeHandle(Entry entry, Scope owner) {
		return awaits.resumeHandle(entry, entry.frame, owner);
	}

	@Override
	public void suspended(Entry entry, Source<?> at) {
		awaits.held(entry, at);
		entry.yielded();
	}

	private static Fiber<Object> doneNothing() {
		return (Fiber<Object>) (Fiber<?>) Fiber.done(Nothing.nothing());
	}

	@Override
	public SearchSnapshot snapshot() {
		SearchSnapshot.Builder b = new SearchSnapshot.Builder();
		for (Entry entry : entries) {
			b.add(0, entry.frame.computation);
		}
		return b.build();
	}

	@Override
	public void close() {
		// empty by design
	}

	@RequiredArgsConstructor
	static final class Entry {
		final FiberStep.Frame frame;
		final Consumer<Object> sink; // null delivers to the root sink
		final Runnable countdown; // the fork's countdown - control-yield, not value
		private boolean yielded;

		/** Fire the countdown exactly once: completion and suspension both yield control. */
		void yielded() {
			if (yielded) {
				return;
			}
			yielded = true;
			if (countdown != null) {
				countdown.run();
			}
		}
	}
}
