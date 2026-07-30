package com.tgac.functional.fibers.schedulers;

// ABOUTME: Depth-first scheduler: steps the most-recently-forked frame to completion before
// ABOUTME: its siblings. Prolog-order search — a driver over Frame backed by a LIFO stack.

import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Scheduler;
import com.tgac.functional.fibers.interpreter.AwaitBoundary;
import com.tgac.functional.fibers.interpreter.Frame;
import com.tgac.functional.fibers.interpreter.ResumeHandle;
import com.tgac.functional.fibers.interpreter.Scope;
import com.tgac.functional.fibers.interpreter.StepListener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DepthFirstScheduler<A> implements Scheduler<A>, Frame.Effects<DepthFirstScheduler.Entry>, SearchInspectable {

	private static final Consumer<Object> DISCARD = value -> {
	};

	private final Deque<Entry> entries;
	private final AwaitBoundary<Entry> awaits = new AwaitBoundary<>();
	private StepListener stepListener = StepListener.NO_OP;

	private Consumer<? super A> rootSink;
	private boolean currentCompleted;

	@Override
	public DepthFirstScheduler<A> withListener(StepListener listener) {
		this.stepListener = listener == null ? StepListener.NO_OP : listener;
		return this;
	}

	public static <A> DepthFirstScheduler<A> of(Fiber<A> fiber) {
		Deque<Entry> entries = new ArrayDeque<>();
		entries.addFirst(new Entry(new Frame(fiber), null, 0));
		return new DepthFirstScheduler<>(entries);
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
		// injected resumes do not preempt the current branch - like detached
		// a resumed frame PREEMPTS, like a planted one: the woken box's
		// delivery must finish before its spawner's siblings run
		awaits.drainInto(entries::addFirst);
		if (entries.isEmpty()) {
			awaits.refuseStranded();
			return true;
		}

		Entry entry = entries.pollFirst();
		rootSink = sink;
		currentCompleted = false;

		if (entry.frame.step(entry, this, stepListener)) {
			entries.addFirst(entry);
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
		// push so the first child is stepped first — depth-first, in clause order
		for (int i = children.size() - 1; i >= 0; i--) {
			entries.addFirst(new Entry(children.get(i), DISCARD, entry.depth + 1));
		}
	}

	@Override
	public void detached(Entry entry, Frame child) {
		// a planted workforce PREEMPTS: depth-first order must descend into
		// the detached body (a traced box's exploration, a master's produce)
		// before the spawner's siblings run - Prolog order for the trace
		entries.addFirst(new Entry(child, DISCARD, entry.depth));
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
			b.add(entry.getDepth(), entry.frame.computation());
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
		@Getter
		final int depth;
	}
}
