package com.tgac.functional.fibers.schedulers;

// ABOUTME: Depth-first scheduler: steps the most-recently-forked frame to completion before
// ABOUTME: its siblings. Prolog-order search — a driver over FiberStep backed by a LIFO stack.

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Source;
import com.tgac.functional.fibers.Scheduler;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DepthFirstScheduler<A> implements Scheduler<A>, FiberStep.Effects, SearchInspectable {

	private final Deque<Entry> entries;
	private final AwaitBoundary<Entry> awaits = new AwaitBoundary<>();
	private StepListener stepListener = StepListener.NO_OP;

	// the entry being stepped and the sink of the current step() call
	private Entry current;
	private Consumer<? super A> rootSink;
	private boolean currentCompleted;

	@Override
	public DepthFirstScheduler<A> withListener(StepListener listener) {
		this.stepListener = listener == null ? StepListener.NO_OP : listener;
		return this;
	}

	public static <A> DepthFirstScheduler<A> of(Fiber<A> fiber) {
		Deque<Entry> entries = new ArrayDeque<>();
		entries.addFirst(new Entry(new FiberStep.Frame(fiber), null, 0));
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
		awaits.drainInto(entries::addLast);
		if (entries.isEmpty()) {
			awaits.refuseStranded();
			return true;
		}

		current = entries.peekFirst();
		rootSink = sink;
		currentCompleted = false;

		FiberStep.step(current.frame, this, stepListener);

		return currentCompleted && entries.isEmpty();
	}

	@Override
	public void completed(Object value) {
		entries.pollFirst();
		if (current.sink != null) {
			current.sink.accept(value);
		} else {
			rootSink.accept((A) value);
		}
		currentCompleted = true;
	}

	@Override
	public void forked(Fiber.Forked<Object> fork) {
		entries.pollFirst();

		Entry parent = current;
		AtomicInteger pending = new AtomicInteger(fork.getOptions().size());
		Consumer<Object> notifyParent = result -> {
			if (pending.decrementAndGet() == 0) {
				parent.frame.computation = doneNothing();
				entries.addFirst(parent); // re-introduce the parent node
			}
			fork.getSink().accept(result);
		};

		// push options so the first is stepped first — depth-first, in clause order
		List<Fiber<Object>> options = fork.getOptions();
		for (int i = options.size() - 1; i >= 0; i--) {
			entries.addFirst(new Entry(new FiberStep.Frame(options.get(i), current.frame.scope), notifyParent, current.depth + 1));
		}
	}

	@Override
	public void detached(Fiber<?> child, Source<?> into) {
		// runs independently; its result is discarded, and it does not preempt the current branch
		entries.addLast(new Entry(new FiberStep.Frame(child, into), value -> {
		}, current.depth));
	}

	@Override
	public Await.Waiter<Object> resumeHandle(Scope<?> owner) {
		return awaits.resumeHandle(current, current.frame, owner);
	}

	@Override
	public void suspending(Source<?> at) {
		awaits.held(current, at);
		entries.pollFirst();
	}

	@Override
	public void suspendCancelled() {
		awaits.cancelled(current);
		entries.addFirst(current);
	}

	private static Fiber<Object> doneNothing() {
		return (Fiber<Object>) (Fiber<?>) Fiber.done(Nothing.nothing());
	}

	@Override
	public SearchSnapshot snapshot() {
		SearchSnapshot.Builder b = new SearchSnapshot.Builder();
		for (Entry entry : entries) {
			b.add(entry.getDepth(), entry.frame.computation);
		}
		return b.build();
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
