package com.tgac.functional.fibers.schedulers;

// ABOUTME: A StepListener that prints a concise per-reduction trace to a sink.
// ABOUTME: A ready-made scheduler trace; subclass or filter for less noise.

import com.tgac.functional.fibers.Fiber;
import java.util.function.Consumer;

/**
 * Prints one line per reduction and per completion/fork/detach event. Wire it
 * with {@code scheduler.withListener(new PrintingStepListener())}. Every fiber
 * step is verbose by design; override or wrap to filter.
 */
public final class PrintingStepListener implements StepListener {

	private final Consumer<String> out;

	public PrintingStepListener() {
		this(System.out::println);
	}

	public PrintingStepListener(Consumer<String> out) {
		this.out = out;
	}

	@Override
	public void onStep(Fiber<?> node) {
		out.accept("step     " + node.getClass().getSimpleName());
	}

	@Override
	public void onCompleted(Object value) {
		out.accept("completed " + value);
	}

	@Override
	public void onForked(Fiber.Forked<?> fork) {
		out.accept("forked   " + fork.getOptions().size() + " options");
	}

	@Override
	public void onDetached(Fiber<?> child) {
		out.accept("detached");
	}
}
