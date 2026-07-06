package com.tgac.functional.fibers.schedulers;

// ABOUTME: The default scheduler: depth-bucketed frames stepped round-robin within the
// ABOUTME: shallowest bucket, with long-running buckets promoted to keep disjunction fair.

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Scheduler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
public final class BreadthFirstScheduler<A> implements Scheduler<A>, FiberStep.Effects {

	private final PriorityQueue<Bucket> buckets;
	private final int iterationsForPromotion;
	private StepListener stepListener = StepListener.NO_OP;

	public BreadthFirstScheduler<A> withListener(StepListener listener) {
		this.stepListener = listener == null ? StepListener.NO_OP : listener;
		return this;
	}

	@Override
	public StepListener listener() {
		return stepListener;
	}

	// the entry being stepped and the sink of the current step() call
	private Bucket currentBucket;
	private Entry current;
	private Consumer<? super A> rootSink;
	private boolean currentCompleted;

	public BreadthFirstScheduler(Fiber<A> fiber) {
		this(fiber, 10_000);
	}

	public BreadthFirstScheduler(Fiber<A> fiber, int iterationsForPromotion) {
		this.buckets = new PriorityQueue<>(Comparator.comparingInt(Bucket::getDepth));
		ArrayList<Entry> entries = new ArrayList<>(1);
		entries.add(new Entry(new FiberStep.Frame(fiber), null));
		buckets.add(new Bucket(entries, 0, -1, 0));
		this.iterationsForPromotion = iterationsForPromotion;
	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		for (int step = 0; step < iterations; ++step) {
			if (step(sink))
				return true;
		}
		return buckets.isEmpty();
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
		if (buckets.isEmpty()) {
			return true;
		}

		Bucket bucket = buckets.peek();
		++bucket.iterations;
		bucket.index = (bucket.index + 1) % bucket.entries.size();

		currentBucket = bucket;
		current = bucket.entries.get(bucket.index);
		rootSink = sink;
		currentCompleted = false;

		FiberStep.step(current.frame, this);

		return currentCompleted && buckets.isEmpty();
	}

	@Override
	public void completed(Object value) {
		removeCurrentEntry(currentBucket);
		if (current.sink != null) {
			current.sink.accept(value);
		} else {
			rootSink.accept((A) value);
		}
		currentCompleted = true;
	}

	@Override
	public void forked(Fiber.Forked<Object> fork) {
		removeCurrentEntry(currentBucket);

		Entry parent = current;
		AtomicInteger pending = new AtomicInteger(fork.getOptions().size());
		Consumer<Object> notifyParent = result -> {
			if (pending.decrementAndGet() == 0) {
				parent.frame.computation = doneNothing();
				add(parent);
			}
			fork.getSink().accept(result);
		};

		addAll(currentBucket.depth + 1, fork.getOptions().stream()
				.map(option -> new Entry(new FiberStep.Frame(option), notifyParent))
				.collect(Collectors.toList()));
	}

	@Override
	public void detached(Fiber<?> child) {
		// runs independently; its result is discarded
		addAll(currentBucket.depth,
				new ArrayList<>(Collections.singletonList(
						new Entry(new FiberStep.Frame(child), value -> {}))));
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

	private void removeCurrentEntry(Bucket bucket) {
		Collections.swap(bucket.entries, bucket.index, bucket.entries.size() - 1);
		bucket.entries.remove(bucket.entries.size() - 1);
		if (bucket.entries.isEmpty()) {
			buckets.remove(bucket);
		} else {
			tryPromote();
		}
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

	@AllArgsConstructor
	private static final class Bucket {
		final List<Entry> entries;
		@Getter
		final int depth;
		int index;
		int iterations;
	}
}
