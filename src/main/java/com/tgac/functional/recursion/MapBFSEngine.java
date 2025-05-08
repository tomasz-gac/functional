package com.tgac.functional.recursion;

import com.tgac.functional.category.Nothing;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapBFSEngine<A> implements Engine<A> {
	private final Map<Integer, Stacks> stacksPerDepth;

	public static <A> MapBFSEngine<A> of(Recur<A> recur) {
		Map<Integer, Stacks> stacks = new TreeMap<>();
		stacks.put(0, new Stacks(new ArrayList<>(1), 0));
		stacks.get(0).stacks.add(Stack.of(recur, null));
		return new MapBFSEngine<>(stacks);
	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		for (int step = 0; step < iterations; ++step) {
			if (step(sink))
				return true;
		}
		return stacksPerDepth.isEmpty();
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

	private void putStack(Stack stack, int depth) {
		stacksPerDepth.computeIfAbsent(depth, i ->
				new Stacks(new ArrayList<>(1), depth));
		stacksPerDepth.get(depth).stacks.add(stack);
	}

	@Override
	public A get() {
		AtomicReference<A> result = new AtomicReference<>();
		run(result::set);
		return result.get();
	}

	public boolean step(Consumer<? super A> sink) {
		if (stacksPerDepth.isEmpty())
			return true;

		Stacks stacks = stacksPerDepth.values().stream()
				.filter(s -> !s.stacks.isEmpty())
				.findFirst()
				.get();
		stacks.index = (stacks.index + 1) % stacks.stacks.size();
		Stack stack = stacks.stacks.get(stacks.index);
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
				Collections.swap(stacks.stacks, stacks.index, stacks.stacks.size() - 1); // avoids shuffling
				stacks.stacks.remove(stacks.stacks.size() - 1);
				if(stacks.stacks.isEmpty()){
					stacksPerDepth.remove(stacks.depth);
				}

				if (stack.originChoiceSink != null) {
					stack.originChoiceSink.accept(value);
				} else {
					sink.accept((A) value);
				}
				return stacksPerDepth.isEmpty(); // true if this was the last one
			}

			stack.computation = stack.fs.pollLast().apply(value);
			return false;

		} else if (computation instanceof Recur.ForEach) {
			Recur.ForEach<Object> forEach = (Recur.ForEach<Object>) computation;

			// Remove the interleaving stack, defer resuming until all options complete
			stacks.stacks.remove(stacks.index);

			Stack parentStack = stack;
			AtomicInteger counter = new AtomicInteger(forEach.getOptions().size());

			Consumer<Object> notifyParent = result -> {
				if (counter.decrementAndGet() == 0) {
					parentStack.computation = Recur.done(Nothing.nothing());
					putStack(parentStack, stacks.depth);
				}
				forEach.getSink().accept(result);
			};

			forEach.getOptions().stream()
					.map(s -> Stack.of(s, notifyParent))
					.forEach(s -> putStack(s, stacks.depth + 1));

			return false;
		} else {
			throw new IllegalStateException("Unknown Recur subclass: " + computation.getClass());
		}
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

	private static class Stacks {
		ArrayList<Stack> stacks;
		int depth;
		int index = -1;

		public Stacks(ArrayList<Stack> stacks, int depth) {
			this.stacks = stacks;
			this.depth = depth;
		}
	}
}
