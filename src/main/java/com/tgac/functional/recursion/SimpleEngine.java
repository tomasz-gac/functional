package com.tgac.functional.recursion;

import com.tgac.functional.category.Nothing;
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
public final class SimpleEngine<A> implements Engine<A> {
	int index = -1;
	private final List<Stack> stacks;

	public static <A> SimpleEngine<A> of(Recur<A> recur) {
		ArrayList<Stack> table = new ArrayList<>();
		table.add(Stack.of(recur, null));
		return new SimpleEngine<>(table);
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
			return true;

		index = (index + 1) % stacks.size();
		Stack stack = stacks.get(index);
		Recur<Object> computation = stack.computation;

		if (computation instanceof Recur.More) {
			stack.computation = ((Recur.More<Object>) computation).getRec().get();
			return false;

		} else if (computation instanceof Recur.FlatMap) {
			Recur.FlatMap<Object, Object> flat = (Recur.FlatMap<Object, Object>) computation;
			stack.fs.addLast(flat.getF());
			stack.computation = flat.getArg();
			return false;

		} else if (computation instanceof Recur.Done) {
			Object value = ((Recur.Done<Object>) computation).getValue();

			if (stack.fs.isEmpty()) {
				Collections.swap(stacks, index, stacks.size() - 1); // avoids shuffling
				stacks.remove(stacks.size() - 1);

				if (stack.originChoiceSink != null) {
					stack.originChoiceSink.accept(value);
				} else {
					sink.accept((A) value);
				}
				return stacks.isEmpty(); // true if this was the last one
			}

			stack.computation = stack.fs.pollLast().apply(value);
			return false;

		} else if (computation instanceof Recur.ForEach) {
			Recur.ForEach<Object> forEach = (Recur.ForEach<Object>) computation;

			// Remove the interleaving stack, defer resuming until all options complete
			stacks.remove(index);

			Stack parentStack = stack;
			AtomicInteger counter = new AtomicInteger(forEach.getOptions().size());

			Consumer<Object> notifyParent = result -> {
				if (counter.decrementAndGet() == 0) {
					parentStack.computation = Recur.done(Nothing.nothing());
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
			throw new IllegalStateException("Unknown Recur subclass: " + computation.getClass());
		}
	}

	@Override
	public void close() throws Exception {
		// empty by design
	}

	@AllArgsConstructor
	static class Stack {
		private Recur<Object> computation;
		private Consumer<Object> originChoiceSink;
		private final Deque<Function<Object, Recur<Object>>> fs = new ArrayDeque<>();

		public static <A> Stack of(Recur<A> recur, Consumer<A> sink) {
			return new Stack((Recur<Object>) recur, (Consumer<Object>) sink);
		}
	}
}
