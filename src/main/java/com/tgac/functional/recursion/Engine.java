package com.tgac.functional.recursion;

import com.tgac.functional.reflection.Types;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Engine<A> implements Supplier<List<A>> {
	int index = -1;
	private final List<Stack> stacks;

	public static <A> Engine<A> of(List<Recur<A>> recurs) {
		return new Engine<>(recurs.stream()
				.map(Types.<Recur<Object>> cast())
				.map(Stack::new)
				.collect(Collectors.toList()));
	}

	public static <A> Engine<A> interleave(List<Engine<A>> engines) {
		return new Engine<>(engines.stream()
				.flatMap(s -> s.stacks.stream())
				.collect(Collectors.toList()));
	}

	public boolean run(int iterations, Consumer<? super A> sink) {
		for (int step = 0; step < iterations; ++step) {
			if (step(sink)) {
				return true;
			}
		}
		return stacks.isEmpty();
	}

	public boolean runUntilResult(Consumer<? super A> sink) {
		AtomicBoolean emitted = new AtomicBoolean(false);
		while (true) {
			if (step(v -> {
				emitted.set(true);
				sink.accept(v);
			})) {
				return true;
			}
			if (emitted.get()) {
				return false;
			}
		}
	}

	public boolean step(Consumer<? super A> sink) {
		index = (index + 1) % stacks.size();
		Stack stack = stacks.get(index);
		if (stack.computation instanceof Recur.More) {
			stack.computation = ((Recur.More<Object>) stack.computation).getRec().get();
		} else if (stack.computation instanceof Recur.FlatMap) {
			Recur.FlatMap<Object, Object> flat = (Recur.FlatMap<Object, Object>) stack.computation;
			stack.fs.addLast(flat.getF());
			stack.computation = flat.getArg();
		} else if (stack.computation instanceof Recur.Done) {
			Object value = ((Recur.Done<Object>) stack.computation).getValue();
			if (stack.fs.isEmpty()) {
				sink.accept((A) value);
				stacks.remove(index);
				return stacks.isEmpty();
			}
			stack.computation = stack.fs.pollLast().apply(value);
		} else {
			throw new IllegalStateException("Unknown Recur subclass: " + stack.computation.getClass());
		}
		return false;
	}

	@SuppressWarnings("StatementWithEmptyBody")
	public void run(Consumer<? super A> sink) {
		while (!run(Integer.MAX_VALUE, sink)) {
		}
	}

	public Spliterator<A> spliterator() {
		return new EngineSpliterator<>(this);
	}

	public Stream<A> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	public Result<A> run(int iterations) {
		List<A> results = new ArrayList<>();
		return Result.of(results,
				run(iterations, results::add));
	}

	@Override
	public List<A> get() {
		List<A> values = new ArrayList<>();
		run(values::add);
		return values;
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	public static class Result<A> {
		List<A> values;
		boolean done;
	}

	@AllArgsConstructor
	private static class Stack {
		private Recur<Object> computation;
		private final Deque<Function<Object, Recur<Object>>> fs = new ArrayDeque<>();
	}
}
