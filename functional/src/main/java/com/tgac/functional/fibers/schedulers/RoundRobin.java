package com.tgac.functional.fibers.schedulers;

// ABOUTME: The simplest scheduler: a flat list of frames stepped in rotation.
// ABOUTME: A driver over Frame — all it owns is the queue.

import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Scheduler;
import com.tgac.functional.fibers.interpreter.AwaitBoundary;
import com.tgac.functional.fibers.interpreter.Frame;
import com.tgac.functional.fibers.interpreter.ResumeHandle;
import com.tgac.functional.fibers.interpreter.Scope;
import com.tgac.functional.fibers.interpreter.StepListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoundRobin<A> implements Scheduler<A>, Frame.Effects<RoundRobin.Entry>, SearchInspectable {

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
		entries.add(new Entry(new Frame(fiber), null));
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
		currentCompleted = true;
	}

	@Override
	public void forked(Entry entry, List<Frame> children) {
		for (Frame child : children) {
			entries.add(new Entry(child, DISCARD));
		}
		index = -1;
	}

	@Override
	public void detached(Entry entry, Frame child) {
		// runs independently; its result is discarded
		entries.add(new Entry(child, DISCARD));
	}

	@Override
	public ResumeHandle resumeHandle(Entry entry, Scope owner) {
		return awaits.resumeHandle(entry, entry.frame, owner);
	}

	@Override
	public void suspended(Entry entry, Object at) {
		awaits.held(entry, at);
	}

	@Override
	public SearchSnapshot snapshot() {
		SearchSnapshot.Builder b = new SearchSnapshot.Builder();
		for (Entry entry : entries) {
			b.add(0, entry.frame.computation());
		}
		return b.build();
	}

	@Override
	public void close() {
		// empty by design
	}

	@RequiredArgsConstructor
	static final class Entry {
		final Frame frame;
		final Consumer<Object> sink; // null delivers to the root sink
	}
}
