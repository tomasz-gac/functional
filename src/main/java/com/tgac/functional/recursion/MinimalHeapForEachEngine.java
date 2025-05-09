package com.tgac.functional.recursion; // Assuming same package as Recur and Engine

import com.tgac.functional.category.Nothing;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An engine for the Recur monad that implements the Engine interface and aims to minimize heap allocations when processing Recur.ForEach nodes.
 *
 * It achieves this by having the EngineStack directly manage ForEach child completion counts and parent notification, avoiding intermediate lambdas or AtomicIntegers per ForEach operation.
 *
 * Note: This engine instance is designed for a single full execution of the initial Recur. Once `run` or `get` completes and all tasks are processed, the engine is "spent". To re-run the computation,
 * a new engine instance should be created with `MinimalHeapForEachEngine.of(recur)`.
 */
@SuppressWarnings("unchecked") // Due to generic Recur<Object> and casts
public final class MinimalHeapForEachEngine<A> implements Engine<A> {

	private int currentIndex = -1; // Start at -1, so first (currentIndex+1)%size is 0
	private final List<EngineStack> activeStacks;
	private final Recur<A> initialRecur; // Store the initial computation
	private boolean initialRecurAddedToStacks = false; // Tracks if the initial recur has been set up

	/**
	 * Creates a new engine for the given recursive computation.
	 *
	 * @param recur
	 * 		The initial recursive computation.
	 * @param <A>
	 * 		The type of the result of the computation.
	 * @return A new MinimalHeapForEachEngine instance.
	 */
	public static <A> MinimalHeapForEachEngine<A> of(Recur<A> recur) {
		if (recur == null) {
			throw new NullPointerException("Initial Recur cannot be null");
		}
		return new MinimalHeapForEachEngine<>(recur);
	}

	private MinimalHeapForEachEngine(Recur<A> initialRecur) {
		this.initialRecur = initialRecur;
		this.activeStacks = new ArrayList<>();
	}

	/**
	 * Initializes the engine with the initial computation if not already done. This is called at the beginning of each `run` method.
	 *
	 * @param rootSink
	 * 		The sink for the outermost computation.
	 */
	private void ensureInitialized(Consumer<? super A> rootSink) {
		if (!initialRecurAddedToStacks && activeStacks.isEmpty()) {
			// The cast to Consumer<Object> is for internal EngineStack use.
			// The EngineStack's sink will ultimately provide an Object, which,
			// if it's the final result for the rootSink, will be cast to A.
			activeStacks.add(EngineStack.newRootComputation(initialRecur, (Consumer<Object>) rootSink));
			initialRecurAddedToStacks = true;
		} else if (initialRecurAddedToStacks && activeStacks.isEmpty()) {
			// Engine has run to completion before. To run again, a new instance should be made.
			// This state indicates it's "spent".
		}
		// If activeStacks is not empty, it's an ongoing computation.
	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		ensureInitialized(sink);

		for (int i = 0; i < iterations; ++i) {
			if (activeStacks.isEmpty()) {
				return true; // All work is definitively done
			}
			if (this.step(sink)) {
				// A root computation completed AND it was the last active stack.
				return true;
			}
		}
		return activeStacks.isEmpty(); // True if all done, false if iterations ran out
	}

	@Override
	public void run(Consumer<? super A> sink) {
		ensureInitialized(sink);

		while (true) {
			if (activeStacks.isEmpty()) {
				break; // All work done
			}
			if (this.step(sink)) {
				// A root computation completed AND it was the last active stack.
				break;
			}
		}
	}

	@Override
	public Optional<A> run(int iterations) {
		final Object[] resultBox = new Object[1]; // Captures the result
		boolean fullyCompleted = run(iterations, value -> resultBox[0] = value);

		if (fullyCompleted && resultBox[0] != null) {
			return Optional.of((A) resultBox[0]);
		} else if (fullyCompleted) {
			// Computation finished, but no value was set (e.g. Recur<Nothing>).
			return Optional.empty();
		}
		return Optional.empty(); // Not fully completed within iterations
	}

	@Override
	public A get() {
		final Object[] resultBox = new Object[1];
		// Ensure initialization for get() if no run method was called prior.
		// The sink here is just to capture the result for get().
		ensureInitialized(value -> resultBox[0] = value);

		run(value -> resultBox[0] = value); // Run to full completion

		if (resultBox[0] == null) {
			// This can happen if the computation results in Nothing or if A is a type that can be null
			// and the computation explicitly returns null (though Recur.done(null) is disallowed by @NonNull).
			// Depending on strictness, one might throw an exception or return null.
			// For Recur.done(@NonNull A v), value should not be null.
			// If Recur<Nothing> is used, resultBox[0] might remain null or hold Nothing.nothing().
		}
		return (A) resultBox[0];
	}

	/**
	 * Executes a single step of a recursive computation.
	 *
	 * @param sink
	 * 		Sink for the root computation if it completes in this step. This sink expects a value of type A.
	 * @return true if a root computation completed AND it was the last active stack, false otherwise.
	 */
	@Override
	public boolean step(Consumer<? super A> sink) {
		if (activeStacks.isEmpty()) {
			return true; // Nothing to process
		}

		currentIndex = (currentIndex + 1) % activeStacks.size();
		EngineStack currentStack = activeStacks.get(currentIndex);
		Recur<Object> computation = currentStack.computation;

		if (computation == null) {
			// This should ideally not happen if logic is correct.
			// It might indicate a ForEach parent was not correctly re-added or a stack was corrupted.
			activeStacks.remove(currentIndex); // Remove problematic stack
			if (!activeStacks.isEmpty())
				currentIndex = (currentIndex - 1 + activeStacks.size()) % activeStacks.size(); // Adjust index
			else
				currentIndex = -1;
			return activeStacks.isEmpty();
		}

		if (computation instanceof Recur.More) {
			currentStack.computation = ((Recur.More<Object>) computation).getRec().get();
			return false;

		} else if (computation instanceof Recur.FlatMap) {
			Recur.FlatMap<Object, Object> flatMapNode = (Recur.FlatMap<Object, Object>) computation;
			currentStack.fs.addLast(flatMapNode.getF());
			currentStack.computation = flatMapNode.getArg();
			return false;

		} else if (computation instanceof Recur.Done) {
			Object value = ((Recur.Done<Object>) computation).getValue();

			if (currentStack.fs.isEmpty()) { // Base case of this stack's recursion/flatMap chain
				EngineStack completedStack = activeStacks.get(currentIndex);

				// Efficient removal:
				if (activeStacks.size() == 1) {
					activeStacks.clear();
					currentIndex = -1;
				} else {
					activeStacks.set(currentIndex, activeStacks.get(activeStacks.size() - 1));
					activeStacks.remove(activeStacks.size() - 1);
					// Index will be recalculated at the start of the next step, effectively processing
					// the element that was swapped into current_index's place next, or wrapping around.
					// To prevent skipping the swapped element in the current cycle if currentIndex was the last element:
					if (currentIndex >= activeStacks.size() && !activeStacks.isEmpty()) {
						currentIndex = activeStacks.size() - 1; // or 0, or -1 to force restart from 0
					} else if (activeStacks.isEmpty()) {
						currentIndex = -1;
					}
					// A simpler approach for round-robin after removal is to decrement currentIndex,
					// so that (currentIndex+1) % size in the next step correctly picks the item
					// that followed the removed one (or the one swapped into its place if it wasn't last).
					// However, the current (idx+1)%size at the beginning of step handles most cases.
					// Let's adjust to ensure fairness if the removed item was not the last one.
					// If current_index was removed, the next item to process is at the *new* current_index (if list not empty)
					// The (current_index + 1) % new_size will be calculated at the start of the next step.
					// To ensure the element swapped into current_index's spot isn't skipped immediately if current_index
					// was not the last element, we can decrement current_index.
					if (currentIndex > 0 && currentIndex >= activeStacks.size() && !activeStacks.isEmpty()) {
						// If the removed element was the last one, and currentIndex pointed to it,
						// then currentIndex might be out of bounds for the new size.
						// The modulo at the start of next step handles this.
						// No, if current_index was removed, the next element to process is at the *new* current_index
						// (which holds the previously last element).
						// To ensure the element that was at `currentIndex` (now holding the swapped element)
						// is processed in the next turn of the round robin, we can decrement `currentIndex`.
						// This way, `(currentIndex + 1)` in the next step will point to it.
						currentIndex = (currentIndex - 1 + activeStacks.size()) % Math.max(1, activeStacks.size());
						// The Math.max(1,..) is to avoid modulo by zero if list becomes empty.
						// If list becomes empty, currentIndex will be -1 (set earlier).
					} else if (activeStacks.isEmpty()) {
						currentIndex = -1;
					}
					// If currentIndex was not the last, and it was removed, then the element at current_index
					// is the one that was previously at the end. The next step's (c_idx+1)%size will handle it.
					// The primary concern is if currentIndex was, say, 0 in a list of 3. Item 0 is removed.
					// Item 2 moves to 0. Next step's (0+1)%2 = 1. Item at new index 1 (old 1) is processed. Ok.
				}

				if (completedStack.forEachParentController != null) {
					EngineStack parent = completedStack.forEachParentController;
					if (parent.forEachItemSink != null) {
						parent.forEachItemSink.accept(value); // This sink is Consumer<Object>
					}
					parent.forEachChildrenPending--;

					if (parent.forEachChildrenPending == 0) {
						parent.computation = Recur.done(Nothing.nothing());
						parent.forEachItemSink = null;
						activeStacks.add(parent); // Reactivate parent
						// Resetting currentIndex can help process the reactivated parent sooner
						if (!activeStacks.isEmpty())
							currentIndex = activeStacks.size() - 2; // To make next (idx+1)%size hit the newly added
					}
				} else {
					// This was a root-level stack or a regular non-ForEach child stack.
					// Use the sink provided to this step() call.
					if (sink != null) {
						sink.accept((A) value); // Cast to A for the external sink
					}
				}
				return activeStacks.isEmpty();
			}

			currentStack.computation = currentStack.fs.pollLast().apply(value);
			return false;

		} else if (computation instanceof Recur.ForEach) {
			Recur.ForEach<Object> forEachNode = (Recur.ForEach<Object>) computation;
			List<Recur<Object>> options = forEachNode.getOptions();

			if (options.isEmpty()) {
				currentStack.computation = Recur.done(Nothing.nothing());
				return false;
			}

			// Current stack becomes the ForEach parent controller
			currentStack.forEachChildrenPending = options.size();
			currentStack.forEachItemSink = forEachNode.getSink(); // This is Consumer<Object>

			// Remove parent controller from active stacks temporarily.
			EngineStack parentControllerStack = activeStacks.get(currentIndex);
			if (activeStacks.size() == 1) {
				activeStacks.clear();
				currentIndex = -1;
			} else {
				activeStacks.set(currentIndex, activeStacks.get(activeStacks.size() - 1));
				activeStacks.remove(activeStacks.size() - 1);
				// Adjust index after removal to ensure fair processing of the swapped element
				currentIndex = (currentIndex - 1 + activeStacks.size()) % Math.max(1, activeStacks.size());
				if (activeStacks.isEmpty())
					currentIndex = -1;
			}

			for (Recur<Object> option : options) {
				activeStacks.add(EngineStack.newForEachChild(option, parentControllerStack));
			}

			// Reset currentIndex to ensure new children are picked up fairly in round-robin
			if (!activeStacks.isEmpty()) {
				// Point before the first of the newly added children, effectively.
				// If children were added at the end, and list size is S, new children are at S-numChildren to S-1
				// To start processing them, set currentIndex to S-numChildren-1 (or -1 if that's simpler)
				currentIndex = -1; // Simplest way to restart scan from beginning
			}

			return false;
		} else {
			throw new IllegalStateException("Unknown Recur subclass: " + computation.getClass().getName());
		}
	}

	@Override
	public void close() throws Exception {

	}

	/**
	 * Internal representation of a computation stack frame.
	 */
	private static class EngineStack {
		Recur<Object> computation;
		final Deque<Function<Object, Recur<Object>>> fs = new ArrayDeque<>();
		Consumer<Object> rootSinkForThisStack; // Sink if this is a root computation

		// --- Fields for ForEach parent/controller ---
		int forEachChildrenPending;
		Consumer<Object> forEachItemSink; // User-provided sink for ForEach items (Consumer<Object>)

		// --- Field for ForEach child ---
		EngineStack forEachParentController = null;

		private EngineStack() {
		}

		/** Creates a stack for a root-level computation. */
		public static <T> EngineStack newRootComputation(Recur<T> recur, Consumer<Object> sink) {
			EngineStack es = new EngineStack();
			es.computation = (Recur<Object>) recur;
			es.rootSinkForThisStack = sink; // This sink is Consumer<Object>
			return es;
		}

		/** Creates a stack for a child of a ForEach node. */
		public static <T> EngineStack newForEachChild(Recur<T> childRecur, EngineStack parentController) {
			EngineStack es = new EngineStack();
			es.computation = (Recur<Object>) childRecur;
			es.forEachParentController = parentController;
			return es;
		}
	}
}
