package com.tgac.functional.recursion; // Assuming same package as Recur and Engine

import com.tgac.functional.category.Nothing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Consumer;
import java.util.function.Function;
// Supplier is part of the Engine interface

/**
 * A hierarchical engine for the Recur monad that processes computations level by level,
 * inspired by BFS principles, to better handle nested Recur.ForEach structures.
 * It aims to minimize heap allocations for individual stack operations.
 * Optimized to avoid unnecessary scans when re-adding tasks to their current processing level.
 *
 * Note: This engine instance is designed for a single full execution of the initial Recur.
 * Once `run` or `get` completes and all tasks are processed, the engine is "spent".
 * To re-run the computation, a new engine instance should be created.
 */
@SuppressWarnings("unchecked") // Due to generic Recur<Object> and casts
public final class HierarchicalEngine<A> implements Engine<A> {

    private final PriorityQueue<LevelTasks> levels;
    private final Recur<A> initialRecur;
    private boolean initialRecurScheduled = false;

    /**
     * Represents a collection of tasks (EngineStack instances) at a specific depth.
     * Tasks within a level are processed in FIFO order.
     */
    private static class LevelTasks implements Comparable<LevelTasks> {
        final int depth;
        final ArrayDeque<EngineStack> tasks = new ArrayDeque<>(); // FIFO queue

        public LevelTasks(int depth) {
            this.depth = depth;
        }

        @Override
        public int compareTo(LevelTasks other) {
            return Integer.compare(this.depth, other.depth);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LevelTasks that = (LevelTasks) o;
            return depth == that.depth;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(depth);
        }
    }

    /**
     * Internal representation of a computation stack frame.
     */
    private static class EngineStack {
        Recur<Object> computation;
        final Deque<Function<Object, Recur<Object>>> fs = new ArrayDeque<>();
        final int depth; // Depth of this stack in the computation tree

        // --- Fields for when this stack is a ForEach parent/controller ---
        int forEachChildrenPending;
        Consumer<Object> forEachItemSink; // User-provided sink for individual items of ForEach

        // --- Field for when this stack is a child of a ForEach ---
        EngineStack forEachParentController = null; // Reference to the parent stack controlling the ForEach

        public EngineStack(Recur<Object> computation, int depth) {
            this.computation = computation;
            this.depth = depth;
        }

        /** Configures this stack to act as a controller for a ForEach node. */
        public void configureAsForEachParentController(Recur.ForEach<Object> forEachNode) {
            if (forEachNode == null || forEachNode.getOptions() == null) {
                this.forEachChildrenPending = 0;
            } else {
                this.forEachChildrenPending = forEachNode.getOptions().size();
            }
            this.forEachItemSink = forEachNode.getSink();
        }

        /** Assigns a parent controller to this stack, indicating it's a ForEach child. */
        public void assignParentController(EngineStack parentController) {
            this.forEachParentController = parentController;
        }
    }

    public static <A> HierarchicalEngine<A> of(Recur<A> recur) {
        if (recur == null) {
            throw new NullPointerException("Initial Recur cannot be null");
        }
        return new HierarchicalEngine<>(recur);
    }

    private HierarchicalEngine(Recur<A> initialRecur) {
        this.initialRecur = initialRecur;
        this.levels = new PriorityQueue<>();
    }

    /**
     * Finds an existing LevelTasks for the given depth or creates a new one.
     * If a new LevelTasks is created, it's added to the 'levels' PriorityQueue.
     * @param targetDepth The depth for which to find or create LevelTasks.
     * @return The LevelTasks object for the targetDepth.
     */
    private LevelTasks findOrCreateLevel(int targetDepth) {
        // Linear scan to find an existing level.
        // This is still used when moving to a *different* depth or if a level was completely emptied.
        for (LevelTasks lt : levels) {
            if (lt.depth == targetDepth) {
                return lt;
            }
        }
        // Not found, create a new one and add it to the PQ
        LevelTasks newLevel = new LevelTasks(targetDepth);
        levels.offer(newLevel);
        return newLevel;
    }

    /**
     * Initializes the engine with the initial computation if not already done.
     */
    private void ensureInitialized() {
        if (!initialRecurScheduled && levels.isEmpty()) {
            // For initialization, findOrCreateLevel is appropriate.
            LevelTasks initialLevelTasks = findOrCreateLevel(0);
            EngineStack rootStack = new EngineStack((Recur<Object>) initialRecur, 0);
            initialLevelTasks.tasks.addLast(rootStack);
            initialRecurScheduled = true;
        } else if (initialRecurScheduled && levels.isEmpty()) {
            // Engine has run to completion before. It's "spent".
        }
    }

    @Override
    public boolean run(int iterations, Consumer<? super A> sink) {
        ensureInitialized();
        for (int i = 0; i < iterations; ++i) {
            if (levels.isEmpty()) {
                return true; // All work done
            }
            if (this.step(sink)) { // step returns true if all work is done
                return true;
            }
        }
        return levels.isEmpty(); // True if all done, false if iterations ran out
    }

    @Override
    public void run(Consumer<? super A> sink) {
        ensureInitialized();
        while (true) {
            if (levels.isEmpty() || this.step(sink)) {
                break; // All work done
            }
        }
    }

    @Override
    public Optional<A> run(int iterations) {
        final Object[] resultBox = new Object[1];
        boolean fullyCompleted = run(iterations, value -> resultBox[0] = value);

        if (fullyCompleted && resultBox[0] != null) {
            return Optional.of((A) resultBox[0]);
        } else if (fullyCompleted) {
            return Optional.empty(); // Completed but no value (e.g., Recur<Nothing>)
        }
        return Optional.empty(); // Not fully completed within iterations
    }

    @Override
    public A get() {
        final Object[] resultBox = new Object[1];
        ensureInitialized(); // Ensure setup if get() is called first
        run(value -> resultBox[0] = value); // Run to full completion
        return (A) resultBox[0];
    }

    @Override
    public boolean step(Consumer<? super A> sink) {
        if (levels.isEmpty()) {
            return true; // All computations are done
        }

        LevelTasks levelBeingProcessed = levels.peek(); // Get the shallowest level (without removing yet)
        if (levelBeingProcessed.tasks.isEmpty()) {
            // This level is empty, remove it and try stepping again.
            // This can happen if tasks were moved or completed leaving an empty level wrapper.
            levels.poll(); // Remove the empty level
            return step(sink); // Recursive call, effectively a loop until a task is found or levels is empty
        }

        EngineStack currentStack = levelBeingProcessed.tasks.pollFirst(); // Get next task from this level

        boolean levelWasEmptiedByPoll = false;
        if (levelBeingProcessed.tasks.isEmpty()) {
            levels.poll(); // Remove levelBeingProcessed from PQ as it's now empty
            levelWasEmptiedByPoll = true;
        }

        Recur<Object> computation = currentStack.computation;

        if (computation instanceof Recur.More ||
                computation instanceof Recur.FlatMap ||
                (computation instanceof Recur.Done && !currentStack.fs.isEmpty()) ||
                (computation instanceof Recur.ForEach && ( ((Recur.ForEach<Object>)computation).getOptions() == null || ((Recur.ForEach<Object>)computation).getOptions().isEmpty() ) )
        ) {
            // These cases involve reprocessing currentStack at the same depth.

            if (computation instanceof Recur.More) {
                currentStack.computation = ((Recur.More<Object>) computation).getRec().get();
            } else if (computation instanceof Recur.FlatMap) {
                Recur.FlatMap<Object, Object> flatMapNode = (Recur.FlatMap<Object, Object>) computation;
                currentStack.fs.addLast(flatMapNode.getF());
                currentStack.computation = flatMapNode.getArg();
            } else if (computation instanceof Recur.Done) { // Implies !currentStack.fs.isEmpty() due to outer if
                Object value = ((Recur.Done<Object>) computation).getValue();
                currentStack.computation = currentStack.fs.pollLast().apply(value);
            } else { // Implies Recur.ForEach with empty options
                currentStack.computation = Recur.done(Nothing.nothing());
            }

            LevelTasks targetLevelForReAdding;
            if (!levelWasEmptiedByPoll && levelBeingProcessed.depth == currentStack.depth) {
                // levelBeingProcessed is still in the PQ (or was the one we peeked and it's not yet removed by another thread/logic)
                // and it's the correct depth. We can reuse it.
                targetLevelForReAdding = levelBeingProcessed;
            } else {
                // levelBeingProcessed was removed because it became empty,
                // OR (defensively) its depth doesn't match currentStack's depth.
                // So, we must find or create the LevelTasks for currentStack.depth.
                targetLevelForReAdding = findOrCreateLevel(currentStack.depth);
            }
            targetLevelForReAdding.tasks.addFirst(currentStack); // Re-add for immediate reprocessing.

        } else if (computation instanceof Recur.Done) { // Implies currentStack.fs.isEmpty()
            Object value = ((Recur.Done<Object>) computation).getValue();
            // This is the final value for this EngineStack
            if (currentStack.forEachParentController != null) {
                // This stack was a child of a ForEach node
                EngineStack parent = currentStack.forEachParentController;
                if (parent.forEachItemSink != null) {
                    parent.forEachItemSink.accept(value); // Notify ForEach's item sink
                }
                parent.forEachChildrenPending--;

                if (parent.forEachChildrenPending == 0) {
                    // All children of this ForEach parent are done. Reactivate parent.
                    parent.computation = Recur.done(Nothing.nothing());
                    parent.forEachItemSink = null; // Reset
                    // Add parent back to its original level's task queue (at the end)
                    LevelTasks parentLevelTasks = findOrCreateLevel(parent.depth);
                    parentLevelTasks.tasks.addLast(parent);
                }
            } else {
                // This was a root-level computation (or not part of a ForEach)
                // Its result goes to the main sink.
                if (sink != null) {
                    sink.accept((A) value);
                }
            }
            // This stack is fully processed. Do not re-add it.

        } else if (computation instanceof Recur.ForEach) { // Implies options are not null and not empty
            Recur.ForEach<Object> forEachNode = (Recur.ForEach<Object>) computation;
            List<Recur<Object>> options = forEachNode.getOptions(); // Already checked not empty/null by outer if

            // CurrentStack (the ForEach node) becomes the parent controller.
            // It's not re-added to any queue now; it's "paused".
            // It will be reactivated when its children complete.
            currentStack.configureAsForEachParentController(forEachNode);

            int childDepth = currentStack.depth + 1;
            // findOrCreateLevel is appropriate here as we are moving to a new (or potentially existing other) level.
            LevelTasks childLevelTasks = findOrCreateLevel(childDepth);

            for (Recur<Object> option : options) {
                EngineStack childStack = new EngineStack(option, childDepth);
                childStack.assignParentController(currentStack); // Link child to parent
                childLevelTasks.tasks.addLast(childStack); // Add child to the next depth level
            }
        } else {
            // Should not be reached if all Recur subtypes are handled
            throw new IllegalStateException("Unknown Recur subclass: " + computation.getClass().getName());
        }
        return levels.isEmpty(); // Return true if all work is now complete
    }

    @Override
    public void close() throws Exception {
        // empty by design
    }
}
