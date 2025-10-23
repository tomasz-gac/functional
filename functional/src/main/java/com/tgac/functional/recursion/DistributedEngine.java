package com.tgac.functional.recursion;

import com.tgac.functional.category.Nothing;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An Engine implementation designed to work with distributed executors like Hazelcast or Apache Ignite.
 * It uses a distributed state model, where the state of the computation is stored in a shared map
 * and tasks are serializable and operate on this distributed state.
 *
 * @param <A> The type of the final result of the computation.
 */
@SuppressWarnings({"unchecked"})
public final class DistributedEngine<A> implements Engine<A> {

	private final Recur<A> initialRecur;
	private final ExecutorService executorService;
	private final ConcurrentMap<String, byte[]> stackStore; // Simulates a distributed IMap<String, byte[]>

	private final CompletableFuture<A> finalResultFuture = new CompletableFuture<>();
	private volatile boolean processingStarted = false;

	/**
	 * Represents the serializable state of a computation stack.
	 * In a real distributed environment, this object would be serialized and sent across the network.
	 */
	private static class State implements Serializable {
		private static final long serialVersionUID = 1L;

		Recur<Object> computation;
		final Deque<Function<Object, Recur<Object>>> continuationStack = new ArrayDeque<>();
		final boolean isRootStack;
		final String parentStackId;

		// Fields for ForEach parent state
		final Consumer<Object> itemConsumer;
		final AtomicInteger childrenPendingCount;

		// Constructor for a new computation
		State(Recur<Object> computation, boolean isRootStack, String parentStackId) {
			this.computation = computation;
			this.isRootStack = isRootStack;
			this.parentStackId = parentStackId;
			this.itemConsumer = null;
			this.childrenPendingCount = null;
		}

		// Constructor for creating a ForEach parent
		State(Recur.ForEach<Object> forEachNode, State originalState) {
			this.computation = forEachNode;
			this.continuationStack.addAll(originalState.continuationStack);
			this.isRootStack = originalState.isRootStack;
			this.parentStackId = originalState.parentStackId;
			this.itemConsumer = forEachNode.getSink();
			this.childrenPendingCount = new AtomicInteger(forEachNode.getOptions().size());
		}
	}

	/**
	 * A serializable task that processes a single step of a computation.
	 */
	private static class ProcessTask implements Runnable, Serializable {
		private static final long serialVersionUID = 1L;
		private final String stackId;
		private transient DistributedEngine<?> engine;

		ProcessTask(String stackId, DistributedEngine<?> engine) {
			this.stackId = stackId;
			this.engine = engine;
		}

		@Override
		public void run() {
			byte[] stateBytes = engine.stackStore.get(stackId);
			if (stateBytes == null)
				return; // State was already processed or removed
			State state = deserialize(stateBytes);

			Recur<Object> computation = state.computation;

			if (computation instanceof Recur.More) {
				state.computation = ((Recur.More<Object>) computation).getRec().get();
				engine.stackStore.put(stackId, serialize(state));
				engine.executorService.submit(this); // Resubmit for the next step
			} else if (computation instanceof Recur.FlatMap) {
				Recur.FlatMap<Object, Object> flatMapNode = (Recur.FlatMap<Object, Object>) computation;
				state.continuationStack.addLast(flatMapNode.getF());
				state.computation = flatMapNode.getArg();
				engine.stackStore.put(stackId, serialize(state));
				engine.executorService.submit(this); // Resubmit for the next step
			} else if (computation instanceof Recur.Done) {
				handleDone(state, ((Recur.Done<Object>) computation).getValue());
			} else if (computation instanceof Recur.ForEach) {
				handleForEach(state, (Recur.ForEach<Object>) computation);
			} else {
				engine.finalResultFuture.completeExceptionally(new IllegalStateException("Unknown Recur subclass"));
			}
		}

		private void handleDone(State state, Object result) {
			if (!state.continuationStack.isEmpty()) {
				state.computation = state.continuationStack.pollLast().apply(result);
				engine.stackStore.put(stackId, serialize(state));
				engine.executorService.submit(this); // Continue with the next computation
			} else {
				engine.stackStore.remove(stackId); // Clean up state
				if (state.parentStackId != null) {
					engine.executorService.submit(new NotifyParentTask(state.parentStackId, result, engine));
				} else if (state.isRootStack) {
					engine.completeRootComputation(result);
				}
			}
		}

		private void handleForEach(State state, Recur.ForEach<Object> forEachNode) {
			List<Recur<Object>> options = forEachNode.getOptions();
			if (options == null || options.isEmpty()) {
				state.computation = Recur.done(Nothing.nothing());
				engine.stackStore.put(stackId, serialize(state));
				engine.executorService.submit(this); // Process the new Done node
				return;
			}

			State parentState = new State(forEachNode, state);
			engine.stackStore.put(stackId, serialize(parentState));

			for (Recur<Object> option : options) {
				String childId = UUID.randomUUID().toString();
				State childState = new State(option, false, stackId);
				engine.stackStore.put(childId, serialize(childState));
				engine.executorService.submit(new ProcessTask(childId, engine));
			}
		}
	}

	/**
	 * A serializable task to notify a parent ForEach stack that a child has completed.
	 */
	private static class NotifyParentTask implements Runnable, Serializable {
		private static final long serialVersionUID = 1L;
		private final String parentStackId;
		private final Object childResult;
		private transient DistributedEngine<?> engine;

		NotifyParentTask(String parentStackId, Object childResult, DistributedEngine<?> engine) {
			this.parentStackId = parentStackId;
			this.childResult = childResult;
			this.engine = engine;
		}

		@Override
		public void run() {
			byte[] stateBytes = engine.stackStore.get(parentStackId);
			if (stateBytes == null)
				return;
			State parentState = deserialize(stateBytes);

			if (parentState.itemConsumer != null) {
				parentState.itemConsumer.accept(childResult);
			}

			if (parentState.childrenPendingCount.decrementAndGet() == 0) {
				parentState.computation = Recur.done(Nothing.nothing());
				engine.stackStore.put(parentStackId, serialize(parentState));
				engine.executorService.submit(new ProcessTask(parentStackId, engine));
			} else {
				engine.stackStore.put(parentStackId, serialize(parentState));
			}
		}
	}

	public DistributedEngine(Recur<A> initialRecur, ExecutorService executorService, ConcurrentMap<String, byte[]> stackStore) {
		if (initialRecur == null || executorService == null || stackStore == null) {
			throw new NullPointerException();
		}
		this.initialRecur = initialRecur;
		this.executorService = executorService;
		this.stackStore = stackStore;
	}

	@Override
	public A get() {
		startRootProcessing(null);
		try {
			return finalResultFuture.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Engine.get() interrupted", e);
		} catch (java.util.concurrent.ExecutionException e) {
			if (e.getCause() instanceof RuntimeException)
				throw (RuntimeException) e.getCause();
			if (e.getCause() instanceof Error)
				throw (Error) e.getCause();
			throw new RuntimeException("Exception in engine computation", e.getCause());
		}
	}

	@Override
	public void run(Consumer<? super A> sink) {
		startRootProcessing(sink);
		try {
			finalResultFuture.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean step(Consumer<? super A> sink) {
		if (!processingStarted) {
			startRootProcessing(sink);
		}
		return finalResultFuture.isDone();
	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		startRootProcessing(sink);
		try {
			finalResultFuture.get(iterations, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			// Ignore timeout, cancellation - just check if done.
		}
		return finalResultFuture.isDone();
	}

	@Override
	public Optional<A> run(int iterations) {
		run(iterations, null);
		if (finalResultFuture.isDone()) {
			try {
				return Optional.ofNullable(finalResultFuture.getNow(null));
			} catch (Exception e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	@Override
	public void close() {
		finalResultFuture.cancel(true);
	}

	private void startRootProcessing(Consumer<? super A> sink) {
		synchronized (this) {
			if (processingStarted)
				return;
			processingStarted = true;
		}

		finalResultFuture.whenComplete((result, error) -> {
			if (error == null && sink != null) {
				sink.accept(result);
			}
		});

		String rootStackId = UUID.randomUUID().toString();
		State rootState = new State((Recur<Object>) initialRecur, true, null);
		stackStore.put(rootStackId, serialize(rootState));
		executorService.submit(new ProcessTask(rootStackId, this));
	}

	private void completeRootComputation(Object result) {
		if (!finalResultFuture.isDone()) {
			finalResultFuture.complete((A) result);
		}
	}

	private static byte[] serialize(State state) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(state);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to serialize state", e);
		}
	}

	private static State deserialize(byte[] bytes) {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(bis)) {
			return (State) ois.readObject();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize state", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to deserialize state", e);
		}
	}
}