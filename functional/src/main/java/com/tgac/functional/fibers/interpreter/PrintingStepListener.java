package com.tgac.functional.fibers.interpreter;

// ABOUTME: A StepListener that prints a concise per-reduction trace to a sink.
// ABOUTME: A ready-made scheduler trace; subclass or filter for less noise.

import com.tgac.functional.fibers.Fiber;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Prints one line per reduction and per completion/fork/detach event. Wire it
 * with {@code scheduler.withListener(new PrintingStepListener())}. Every fiber
 * step is verbose by design; override or wrap to filter.
 */
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class PrintingStepListener implements StepListener {

	Consumer<String> out;

	public PrintingStepListener() {
		this(System.out::println);
	}

	@Override
	public void onStep(Fiber<?> node, Scope scope, String name) {
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

	@Override
	public void onAwaiting(Fiber.Awaiting<?> awaiting) {
		out.accept("awaiting " + awaiting.getChannel());
	}

	@Override
	public void onSealed(Fiber.Sealed sealedOn) {
		out.accept("sealed-wait " + sealedOn.getScope());
	}

	@Override
	public void onEmit(Fiber.Emit<?> emit) {
		out.accept("emit     " + emit.getDelta());
	}
}
