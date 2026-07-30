package com.tgac.functional.fibers.schedulers;

// ABOUTME: The default scheduler: depth-bucketed frames stepped round-robin within the
// ABOUTME: shallowest bucket, with long-running buckets promoted to keep disjunction fair.

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.interpreter.AwaitBoundary;
import com.tgac.functional.fibers.interpreter.Frame;
import com.tgac.functional.fibers.interpreter.ResumeHandle;
import com.tgac.functional.fibers.interpreter.Scope;
import com.tgac.functional.fibers.interpreter.StepListener;
import com.tgac.functional.fibers.Await;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Source;
import com.tgac.functional.fibers.Scheduler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
public final class BreadthFirstScheduler<A> implements Scheduler<A>, Frame.Effects<BreadthFirstScheduler.Entry>, SearchInspectable {

	private static final Consumer<Object> DISCARD = value -> {
	};

	private final PriorityQueue<Bucket> buckets;
	private final int iterationsForPromotion;
	private StepListener stepListener = StepListener.NO_OP;

	private final AwaitBoundary<Entry> awaits = new AwaitBoundary<>();

	@Override
	public BreadthFirstScheduler<A> withListener(StepListener listener) {
		this.stepListener = listener == null ? StepListener.NO_OP : listener;
		return this;
	}

	// the entry being stepped and the sink of the current step() call
	private int currentDepth;
	private Consumer<? super A> rootSink;
	private boolean currentCompleted;

	public BreadthFirstScheduler(Fiber<A> fiber) {
		this(fiber, 10_000);
	}

	public BreadthFirstScheduler(Fiber<A> fiber, int iterationsForPromotion) {
		this.buckets = new PriorityQueue<>(Comparator.comparingInt(Bucket::getDepth));
		ArrayList<Entry> entries = new ArrayList<>(1);
		entries.add(new Entry(new Frame(fiber), null));
		buckets.add(new Bucket(entries, 0, -1, 0));
		this.iterationsForPromotion = iterationsForPromotion;
	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		for (int step = 0; step < iterations; ++step) {
			if (step(sink))
				return true;
		}
		return buckets.isEmpty() && awaits.quiet();
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
		awaits.drainInto(this::add);
		if (buckets.isEmpty()) {
			awaits.refuseStranded();
			return true;
		}

		Bucket bucket = buckets.peek();
		++bucket.iterations;
		bucket.index = (bucket.index + 1) % bucket.entries.size();

		currentDepth = bucket.depth;
		// take the entry OUT before stepping - callbacks never remove, the
		// loop re-adds a still-runnable frame
		Collections.swap(bucket.entries, bucket.index, bucket.entries.size() - 1);
		Entry entry = bucket.entries.remove(bucket.entries.size() - 1);
		if (bucket.entries.isEmpty()) {
			buckets.remove(bucket);
		} else {
			tryPromote();
		}
		rootSink = sink;
		currentCompleted = false;

		if (entry.frame.step(entry, this, stepListener)) {
			addAll(currentDepth, new ArrayList<>(Collections.singletonList(entry)));
		}

		return currentCompleted && buckets.isEmpty();
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
		addAll(currentDepth + 1, children.stream()
				.map(child -> new Entry(child, DISCARD))
				.collect(Collectors.toList()));
	}

	@Override
	public void detached(Entry entry, Frame child) {
		// runs independently; its result is discarded
		addAll(currentDepth,
				new ArrayList<>(Collections.singletonList(new Entry(child, DISCARD))));
	}

	@Override
	public ResumeHandle resumeHandle(Entry entry, Scope owner) {
		return awaits.resumeHandle(entry, entry.frame, owner);
	}

	@Override
	public void suspended(Entry entry, Object at) {
		awaits.held(entry, at);
	}

	private void tryPromote() {
		Bucket current = buckets.peek();
		if (current != null && current.iterations > iterationsForPromotion && buckets.size() > 1) {
			Iterator<Bucket> it = buckets.iterator();
			Bucket first = it.next();
			Bucket second = it.next();
			second.entries.addAll(first.entries);
			buckets.poll();
		}
	}

	private void addAll(int depth, List<Entry> entries) {
		if (entries.isEmpty()) {
			return;
		}
		if (!buckets.isEmpty() && buckets.peek().depth == depth) {
			buckets.peek().entries.addAll(entries);
		} else {
			buckets.offer(new Bucket(entries, depth, -1, 0)); // re-introduce the parent node
		}
	}

	private void add(Entry entry) {
		if (buckets.isEmpty()) {
			List<Entry> entries = new ArrayList<>(1);
			entries.add(entry);
			buckets.offer(new Bucket(entries, 0, -1, 0)); // re-introduce the parent node
		} else {
			buckets.peek().entries.add(entry);
		}
	}

	@Override
	public SearchSnapshot snapshot() {
		SearchSnapshot.Builder b = new SearchSnapshot.Builder();
		for (Bucket bucket : buckets) {
			for (Entry entry : bucket.entries) {
				b.add(bucket.depth, entry.frame.computation());
			}
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

	@AllArgsConstructor
	private static final class Bucket {
		final List<Entry> entries;
		@Getter
		final int depth;
		int index;
		int iterations;
	}
}
