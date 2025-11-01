package com.tgac.functional.fibers.schedulers;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Scheduler;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoundRobin<A> implements Scheduler<A> {
	int index = -1;
	private final List<Stack> stacks;
	private final AtomicInteger parkedCount = new AtomicInteger(0);

	public static <A> RoundRobin<A> of(Fiber<A> recur) {
		ArrayList<Stack> table = new ArrayList<>();
		table.add(Stack.of(recur, null));
		return new RoundRobin<>(table);
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

	public boolean step(Consumer<? super A> sink) {
		if (stacks.isEmpty())
			return parkedCount.get() == 0;

		index = (index + 1) % stacks.size();
		Stack stack = stacks.get(index);
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
				Collections.swap(stacks, index, stacks.size() - 1); // avoids shuffling
				stacks.remove(stacks.size() - 1);

				if (stack.originChoiceSink != null) {
					stack.originChoiceSink.accept(value);
				} else {
					sink.accept((A) value);
				}
				return stacks.isEmpty() && parkedCount.get() == 0;
			}

			stack.computation = stack.fs.pollLast().apply(value);
			return false;

		} else if (computation instanceof Fiber.Suspended) {
			handleSuspended(stack, index);
			return stacks.isEmpty() && parkedCount.get() == 0;

		} else if (computation instanceof Fiber.ForEach) {
			Fiber.ForEach<Object> forEach = (Fiber.ForEach<Object>) computation;

			// Remove the interleaving stack, defer resuming until all options complete
			stacks.remove(index);

			Stack parentStack = stack;
			AtomicInteger counter = new AtomicInteger(forEach.getOptions().size());

			Consumer<Object> notifyParent = result -> {
				if (counter.decrementAndGet() == 0) {
					parentStack.computation = Fiber.done(Nothing.nothing());
					stacks.add(parentStack);
					index = stacks.size() - 1;
				}
				forEach.getSink().accept(result);
			};

			stacks.addAll(forEach.getOptions().stream()
					.map(s -> Stack.of(s, notifyParent))
					.collect(Collectors.toList()));
			index = -1;

			return false;
		} else {
			throw new IllegalStateException("Unknown Fiber subclass: " + computation.getClass());
		}
	}

	@Override
	public void close() throws Exception {
		// empty by design
	}

	@SuppressWarnings("unchecked")
	private <W> void handleSuspended(Stack stack, int idx) {
		Fiber.Suspended<W, Object> suspended = (Fiber.Suspended<W, Object>) stack.computation;

		parkedCount.incrementAndGet();

		Stack capturedStack = stack;
		suspended.getFuture().thenAccept(value -> {
			Fiber<Object> work = suspended.getResume().apply(value);
			capturedStack.computation = work;

			synchronized(stacks) {
				stacks.add(capturedStack);
			}

			parkedCount.decrementAndGet();
		});

		// Remove from active (parking)
		Collections.swap(stacks, idx, stacks.size() - 1);
		stacks.remove(stacks.size() - 1);
		index = Math.max(-1, index - 1);
	}

	@AllArgsConstructor
	static class Stack {
		private Fiber<Object> computation;
		private Consumer<Object> originChoiceSink;
		private final Deque<Function<Object, Fiber<Object>>> fs = new ArrayDeque<>();

		public static <A> Stack of(Fiber<A> recur, Consumer<A> sink) {
			return new Stack((Fiber<Object>) recur, (Consumer<Object>) sink);
		}
	}
}
