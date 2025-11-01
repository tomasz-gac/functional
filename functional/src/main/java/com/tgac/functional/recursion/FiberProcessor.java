package com.tgac.functional.recursion;

import com.tgac.functional.category.Nothing;
import java.util.List;

/**
 * Processes the different types of Fiber computations. This class encapsulates the business logic
 * of how to handle each step of a recursive computation.
 *
 * @param <A> The type of the final result of the computation.
 */
class FiberProcessor<A> {

	private final ExecutorServiceEngine<A> engine;

	FiberProcessor(ExecutorServiceEngine<A> engine) {
		this.engine = engine;
	}

	/**
	 * Processes the current computation on the stack.
	 *
	 * @param stack The current computation stack.
	 * @return The next stack to be processed, or null if the computation is paused or finished.
	 */
	ExecutorServiceEngine.EngineStack process(ExecutorServiceEngine.EngineStack stack) {
		Fiber<Object> computation = stack.computation;

		if (computation instanceof Fiber.More) {
			handleMore(stack, (Fiber.More<Object>) computation);
			return stack;
		} else if (computation instanceof Fiber.FlatMap) {
			handleFlatMap(stack, (Fiber.FlatMap<Object, Object>) computation);
			return stack;
		} else if (computation instanceof Fiber.Done) {
			return handleDone(stack, (Fiber.Done<Object>) computation);
		} else if (computation instanceof Fiber.Suspended) {
			return handleSuspended(stack, (Fiber.Suspended<?, Object>) computation);
		} else if (computation instanceof Fiber.ForEach) {
			return handleForEach(stack, (Fiber.ForEach<Object>) computation);
		} else {
			throw new IllegalStateException("Unknown Fiber subclass: " + computation.getClass().getName() + " in stack " + stack);
		}
	}

	/**
	 * Handles a More node by unwrapping the next computation.
	 */
	private void handleMore(ExecutorServiceEngine.EngineStack stack, Fiber.More<Object> more) {
		stack.computation = more.getRec().get();
	}

	/**
	 * Handles a FlatMap node by pushing the function onto the continuation stack and setting the next computation to the argument.
	 */
	private void handleFlatMap(ExecutorServiceEngine.EngineStack stack, Fiber.FlatMap<Object, Object> flatMap) {
		stack.continuationStack.addLast(flatMap.getF());
		stack.computation = flatMap.getArg();
	}

	/**
	 * Handles a Done node. If there are continuations, it applies the next one. Otherwise, it completes the computation.
	 *
	 * @return The next stack to process, or null to pause/finish.
	 */
	private ExecutorServiceEngine.EngineStack handleDone(ExecutorServiceEngine.EngineStack stack, Fiber.Done<Object> done) {
		Object result = done.getValue();
		if (!stack.continuationStack.isEmpty()) {
			stack.computation = stack.continuationStack.pollLast().apply(result);
			return stack; // Continue processing
		}

		// No more continuations, this computation is finished.
		if (stack.parent != null) {
			engine.handleDoneForChild(stack, result);
		} else if (stack.isRootStackForEngine) {
			engine.handleDoneForRoot(stack, result);
		}
		return null; // Stop processing
	}

	/**
	 * Handles a ForEach node by converting the current stack to a ForEachParentStack and spawning child tasks.
	 *
	 * @return The next stack to process, or null to pause.
	 */
	private ExecutorServiceEngine.EngineStack handleForEach(ExecutorServiceEngine.EngineStack stack, Fiber.ForEach<Object> forEachNode) {
		List<Fiber<Object>> options = forEachNode.getOptions();

		if (options == null || options.isEmpty()) {
			stack.computation = Fiber.done(Nothing.nothing());
			return stack; // Continue processing with an empty result.
		}

		// Convert the current stack to a parent stack that will wait for children.
		ExecutorServiceEngine.ForEachParentStack parentStack = new ExecutorServiceEngine.ForEachParentStack(forEachNode, stack);

		if (parentStack.forEachChildrenPendingCount.get() == 0) {
			stack.computation = Fiber.done(Nothing.nothing());
			return stack; // Continue processing with an empty result.
		}

		spawnChildrenTasks(parentStack, options);
		return null; // Pause this stack; it's now waiting for children to complete.
	}

	/**
	 * Handles a Suspended node by parking it and registering a callback for resumption.
	 *
	 * @return null to pause this stack - it will be resumed when the future completes.
	 */
	private <W> ExecutorServiceEngine.EngineStack handleSuspended(
			ExecutorServiceEngine.EngineStack stack,
			Fiber.Suspended<W, Object> suspended) {

		engine.incrementParkedCount();

		ExecutorServiceEngine.EngineStack capturedStack = stack;
		suspended.getFuture().thenAccept(value -> {
			if (engine.isCancelled()) {
				engine.decrementParkedCount();
				return;
			}

			Fiber<Object> work = suspended.getResume().apply(value);
			capturedStack.computation = work;

			engine.decrementParkedCount();

			// Re-submit the stack with the resumed computation
			engine.submitEngineTask(capturedStack);
		});

		// Pause this stack - it will be resumed via the callback
		return null;
	}

	/**
	 * Spawns child tasks for each option in a ForEach node.
	 */
	private void spawnChildrenTasks(ExecutorServiceEngine.ForEachParentStack parentStack, List<Fiber<Object>> options) {
		int submittedChildCount = 0;
		for (Fiber<Object> branchFiber : options) {
			if (engine.isCancelled()) {
				int skippedOrFailedCount = options.size() - submittedChildCount;
				parentStack.forEachChildrenPendingCount.addAndGet(-skippedOrFailedCount);
				break;
			}
			// Each option becomes a new computation stack.
			ExecutorServiceEngine.ComputationStack childStack = new ExecutorServiceEngine.ComputationStack(branchFiber, false, parentStack);
			engine.submitEngineTask(childStack);
			submittedChildCount++;
		}
	}
}