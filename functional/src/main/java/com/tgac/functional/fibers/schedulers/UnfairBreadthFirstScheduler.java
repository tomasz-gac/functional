package com.tgac.functional.fibers.schedulers;

// ABOUTME: Depth-ordered scheduler that always steps the shallowest frame.
// ABOUTME: A driver over FiberStep — unfair because a shallow frame can starve deeper ones.

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Source;
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
public final class UnfairBreadthFirstScheduler<A> implements Scheduler<A>, FiberStep.Effects<UnfairBreadthFirstScheduler.Entry>, SearchInspectable {

	private final PriorityQueue<Entry> entries;
	private final AwaitBoundary<Entry> awaits = new AwaitBoundary<>();
	private StepListener stepListener = StepListener.NO_OP;

	@Override
	public UnfairBreadthFirstScheduler<A> withListener(StepListener listener) {
		this.stepListener = listener == null ? StepListener.NO_OP : listener;
		return this;
	}

	private Consumer<? super A> rootSink;
	private boolean currentCompleted;

	public static <A> UnfairBreadthFirstScheduler<A> of(Fiber<A> fiber) {
		PriorityQueue<Entry> entries = new PriorityQueue<>(Comparator.comparingInt(Entry::getDepth));
		entries.add(new Entry(new FiberStep.Frame(fiber), null, null, 0));
		return new UnfairBreadthFirstScheduler<>(entries);
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
		awaits.drainInto(entries::offer);
		if (entries.isEmpty()) {
			awaits.refuseStranded();
			return true;
		}

		Entry entry = entries.poll();
		rootSink = sink;
		currentCompleted = false;

		if (entry.frame.step(entry, this, stepListener)) {
			entries.offer(entry);
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
		entry.joined();
		currentCompleted = true;
	}

	@Override
	public void forked(Entry entry, Fiber.Forked<Object> fork) {
		Entry parent = entry;
		AtomicInteger pending = new AtomicInteger(fork.getOptions().size());
		// the join counts CONTROL YIELDS - a suspended child joins without a value
		Runnable childJoined = () -> {
			if (pending.decrementAndGet() == 0) {
				parent.frame.computation = doneNothing();
				entries.offer(parent); // re-introduce the parent node
			}
		};

		for (Fiber<Object> option : fork.getOptions()) {
			entries.offer(new Entry(new FiberStep.Frame(option, entry.frame.scope), fork.getSink(), childJoined, entry.depth + 1));
		}
	}

	@Override
	public void detached(Entry entry, Fiber<?> child, Source<?> into) {
		// runs independently; its result is discarded
		entries.offer(new Entry(new FiberStep.Frame(child, into), value -> {
		}, null, entry.depth));
	}

	@Override
	public Await.Waiter<Object> resumeHandle(Entry entry, Scope owner) {
		return awaits.resumeHandle(entry, entry.frame, owner);
	}

	@Override
	public void suspended(Entry entry, Source<?> at, Await.Waiter<Object> waiter) {
		((ResumeHandle) waiter).heldAt(FiberStep.Frame.own(at), () -> awaits.held(entry, at));
		entry.joined();
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
	static final class Entry {
		final FiberStep.Frame frame;
		final Consumer<Object> sink; // null delivers to the root sink
		final Runnable join; // the fork's countdown - control-yield, not value
		private boolean joined;

		/** Fire the join exactly once: completion and suspension both yield control. */
		void joined() {
			if (joined) {
				return;
			}
			joined = true;
			if (join != null) {
				join.run();
			}
		}
		@Getter
		final int depth;
	}
}
