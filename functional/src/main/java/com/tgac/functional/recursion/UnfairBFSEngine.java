package com.tgac.functional.recursion;

import com.tgac.functional.category.Nothing;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnfairBFSEngine<A> implements Engine<A> {
	private final PriorityQueue<Stack> stacks;

	public static <A> UnfairBFSEngine<A> of(Fiber<A> recur) {
		PriorityQueue<Stack> table = new PriorityQueue<>(Comparator.comparingInt(Stack::getDepth));
		table.add(Stack.of(recur, null, 0));
		return new UnfairBFSEngine<>(table);
	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		for (int step = 0; step < iterations; ++step) {
			if (step(sink))
				return true;
		}
		return stacks.isEmpty();
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
		if (stacks.isEmpty())
			return true;

		Stack stack = stacks.peek();
		Fiber<Object> computation = stack.computation;

		if (computation instanceof Fiber.More) {
			stack.computation = ((Fiber.More<Object>) computation).getRec().get();
			return false;

		} else if (computation instanceof Fiber.FlatMap) {
			Fiber.FlatMap<Object, Object> flat = (Fiber.FlatMap<Object, Object>) computation;
			stack.fs.addLast(flat.getF());
			stack.computation = flat.getArg();
			return false;

		} else if (computation instanceof Fiber.Done) {
			Object value = ((Fiber.Done<Object>) computation).getValue();

			if (stack.fs.isEmpty()) {
				stacks.poll();

				if (stack.originInterleaveSink != null) {
					stack.originInterleaveSink.accept(value);
				} else {
					sink.accept((A) value);
				}
				return stacks.isEmpty(); // true if this was the last one
			}

			stack.computation = stack.fs.pollLast().apply(value);
			return false;

		} else if (computation instanceof Fiber.ForEach) {
			Fiber.ForEach<Object> forEach = (Fiber.ForEach<Object>) computation;

			// Remove the interleaving stack, defer resuming until all options complete
			stacks.poll();

			AtomicInteger counter = new AtomicInteger(forEach.getOptions().size());

			Consumer<Object> notifyParent = result -> {
				if (counter.decrementAndGet() == 0) {
					stack.computation = Fiber.done(Nothing.nothing());
					stacks.offer(stack); // re-introduce the parent node
				}
				forEach.getSink().accept(result);
			};

			stacks.addAll(forEach.getOptions().stream()
					.map(s -> Stack.of(s, notifyParent, stack.depth + 1))
					.collect(Collectors.toList()));

			return false;
		} else {
			throw new IllegalStateException("Unknown Fiber subclass: " + computation.getClass());
		}
	}

	@Override
	public void close() throws Exception {
		// empty by design
	}

	@AllArgsConstructor
	static class Stack {
		private Fiber<Object> computation;
		private Consumer<Object> originInterleaveSink;
		@Getter
		private int depth;
		private final Deque<Function<Object, Fiber<Object>>> fs = new ArrayDeque<>();

		public static <A> Stack of(Fiber<A> recur, Consumer<A> sink, int depth) {
			return new Stack((Fiber<Object>) recur, (Consumer<Object>) sink, depth);
		}
	}
}
