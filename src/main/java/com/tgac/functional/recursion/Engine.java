package com.tgac.functional.recursion;

import com.tgac.functional.Streams;
import com.tgac.functional.category.Nothing;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Engine<A> implements Supplier<A> {
	int index = -1;
	private final List<Stack> stacks;

	public static <A> Engine<A> of(Recur<A> recur) {
		return new Engine<>(Stream.of(Stack.of(recur, null))
				.collect(Collectors.toList()));
	}

	public boolean run(int iterations, Consumer<? super A> sink) {
		for (int step = 0; step < iterations; ++step) {
			if (step(sink))
				return true;
		}
		return stacks.isEmpty();
	}

	public void run(Consumer<? super A> sink) {
		while (!run(Integer.MAX_VALUE, sink)) {
		}
	}

	public Optional<A> run(int iterations) {
		Object[] box = new Object[1];
		return run(iterations, v -> box[0] = v) ?
				Optional.of((A) box[0]) :
				Optional.empty();
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
				stacks.remove(index--); // current was processed

				if (stack.originChoiceSink != null) {
					stack.originChoiceSink.accept(value);
				} else {
					sink.accept((A) value);
				}
				return stacks.isEmpty(); // true if this was the last one
			}

			stack.computation = stack.fs.pollLast().apply(value);
			return false;

		} else if (computation instanceof Recur.Interleaved) {
			Recur.Interleaved<Object> interleaved = (Recur.Interleaved<Object>) computation;

			// Remove the interleaving stack, defer resuming until all options complete
			stacks.remove(index);

			Stack parentStack = stack;
			AtomicInteger counter = new AtomicInteger(interleaved.getOptions().size());

			Consumer<Object> notifyParent = result -> {
				if (counter.decrementAndGet() == 0) {
					parentStack.computation = Recur.done(Nothing.nothing());
					stacks.add(index + 1, parentStack); // resume parent at same position
				}
				interleaved.getSink().accept(result);
			};

			List<Stack> newStacks = interleaved.getOptions().stream()
					.map(s -> Stack.of(s, notifyParent))
					.collect(Collectors.toList());

			stacks.addAll(newStacks);
			index = -1;
			return false;

		} else {
			throw new IllegalStateException("Unknown Recur subclass: " + computation.getClass());
		}
	}

	@Override
	public A get() {
		AtomicReference<A> result = new AtomicReference<>();
		run(result::set);
		return result.get();
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	public static class Result<A> {
		List<A> values;
		boolean done;
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
