package com.tgac.functional.fibers.schedulers;

import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.Fiber;
import com.tgac.functional.fibers.Scheduler;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BredthFirstScheduler<A> implements Scheduler<A> {
	@NonNull
	private PriorityQueue<Stacks> stacksPerDepth;
	private final int iterationsForPromotion;
	private final AtomicInteger parkedCount = new AtomicInteger(0);

	public BredthFirstScheduler(Fiber<A> recur) {
		this(recur, 10_000);
	}

	public BredthFirstScheduler(Fiber<A> recur, int iterationsForPromotion) {
		this.stacksPerDepth = new PriorityQueue<>(Comparator.comparingInt(Stacks::getDepth));
		ArrayList<Stack> stacks = new ArrayList<>(1);
		stacks.add(Stack.of(recur, null));
		stacksPerDepth.add(new Stacks(stacks, 0, -1, 0));
		this.iterationsForPromotion = iterationsForPromotion;
	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		for (int step = 0; step < iterations; ++step) {
			if (step(sink))
				return true;
		}
		return stacksPerDepth.isEmpty() && parkedCount.get() == 0;
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
		if (stacksPerDepth.isEmpty()) {
			return parkedCount.get() == 0;
		}

		Stacks stacks = stacksPerDepth.peek();
		++stacks.iterations;
		stacks.index = (stacks.index + 1) % stacks.stacks.size();
		Stack stack = stacks.stacks.get(stacks.index);
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
				removeCurrentStackItem(stacks);

				if (stack.originInterleaveSink != null) {
					stack.originInterleaveSink.accept(value);
				} else {
					sink.accept((A) value);
				}
				return stacksPerDepth.isEmpty() && parkedCount.get() == 0;
			}

			stack.computation = stack.fs.pollLast().apply(value);
			return false;

		} else if (computation instanceof Fiber.Suspended) {
			handleSuspended(stack, stacks);
			return stacksPerDepth.isEmpty() && parkedCount.get() == 0;

		} else if (computation instanceof Fiber.ForEach) {
			Fiber.ForEach<Object> forEach = (Fiber.ForEach<Object>) computation;

			// Remove the interleaving stack, defer resuming until all options complete
			removeCurrentStackItem(stacks);

			AtomicInteger counter = new AtomicInteger(forEach.getOptions().size());

			Consumer<Object> notifyParent = result -> {
				if (counter.decrementAndGet() == 0) {
					stack.computation = Fiber.done(Nothing.nothing());
					add(stack);

				}
				forEach.getSink().accept(result);
			};

			addAll(stacks.depth + 1, forEach.getOptions().stream()
					.map(s -> Stack.of(s, notifyParent))
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

	private <W> void handleSuspended(Stack stack, Stacks stacks) {
		Fiber.Suspended<W, Object> suspended = (Fiber.Suspended<W, Object>) stack.computation;

		parkedCount.incrementAndGet();

		Stack capturedStack = stack;
		int capturedDepth = stacks.depth;
		suspended.getFuture().thenAccept(value -> {
			Fiber<Object> work = suspended.getResume().apply(value);
			capturedStack.computation = work;

			synchronized(stacksPerDepth) {
				addAll(capturedDepth, Collections.singletonList(capturedStack));
			}

			parkedCount.decrementAndGet();
		});

		// Remove from active (parking)
		removeCurrentStackItem(stacks);
	}

	private void tryPromote() {
		Stacks current = stacksPerDepth.peek();
		if (current != null && current.iterations > iterationsForPromotion && stacksPerDepth.size() > 1) {
			Iterator<Stacks> it = stacksPerDepth.iterator();
			Stacks first = it.next();
			Stacks second = it.next();
			second.stacks.addAll(first.stacks);
			stacksPerDepth.poll();
		}
	}

	private void addAll(int depth, List<Stack> stack) {
		if (stack.isEmpty()) {
			return;
		}
		if (!stacksPerDepth.isEmpty() && stacksPerDepth.peek().depth == depth) {
			stacksPerDepth.peek().stacks.addAll(stack);
		} else {
			stacksPerDepth.offer(new Stacks(stack, depth, -1, 0)); // re-introduce the parent node
		}
	}

	private void add(Stack stack) {
		if (stacksPerDepth.isEmpty()) {
			List<Stack> stacks1 = new ArrayList<>(1);
			stacks1.add(stack);
			stacksPerDepth.offer(new Stacks(stacks1, 0, -1, 0)); // re-introduce the parent node
		} else {
			stacksPerDepth.peek().stacks.add(stack);
		}
	}

	private void removeCurrentStackItem(Stacks stacks) {
		Collections.swap(stacks.stacks, stacks.index, stacks.stacks.size() - 1);
		stacks.stacks.remove(stacks.stacks.size() - 1);
		if (stacks.stacks.isEmpty()) {
			stacksPerDepth.remove(stacks);
		} else {
			tryPromote();
		}
	}

	@AllArgsConstructor
	static class Stack {
		private Fiber<Object> computation;
		private Consumer<Object> originInterleaveSink;
		private final Deque<Function<Object, Fiber<Object>>> fs = new ArrayDeque<>();

		public static <A> Stack of(Fiber<A> recur, Consumer<A> sink) {
			return new Stack((Fiber<Object>) recur, (Consumer<Object>) sink);
		}
	}

	@AllArgsConstructor
	static class Stacks {
		List<Stack> stacks;
		@Getter
		int depth;
		int index;
		int iterations;
	}
}
