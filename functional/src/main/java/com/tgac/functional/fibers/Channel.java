package com.tgac.functional.fibers;

import com.tgac.functional.category.Nothing;
import io.vavr.Tuple2;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;

/**
 * A rendezvous channel for synchronizing producer/consumer communication between Fibers.
 *
 * <p>This is a <em>synchronous</em> (unbuffered) channel where both producer and consumer
 * must wait for each other. Unlike buffered channels, values cannot be queued - each put()
 * must rendezvous with a corresponding take().</p>
 *
 * <h2>Semantics</h2>
 * <ul>
 *   <li>{@link #take()} suspends until a producer puts a value</li>
 *   <li>{@link #put(T)} suspends until a consumer takes the value</li>
 *   <li>Handoff is atomic - exactly one consumer receives each value</li>
 *   <li>FIFO ordering - first waiting consumer gets first available value</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * Channel<String> channel = new Channel<>();
 *
 * // Consumer suspends until value available
 * Fiber<Nothing> consumer = channel.take().await()
 *     .flatMap(msg -> {
 *         System.out.println("Received: " + msg);
 *         return channel.take().await();
 *     })
 *     .flatMap(msg2 -> done(Nothing.nothing()));
 *
 * // Producer suspends until consumer ready
 * Fiber<Nothing> producer = channel.put("Hello").await()
 *     .flatMap(_ -> channel.put("World").await());
 *
 * // Both must run concurrently for handoff to occur
 * Fiber.fork(Arrays.asList(producer, consumer), r -> {}).get();
 * }</pre>
 *
 * <h2>Comparison to Other Patterns</h2>
 * <ul>
 *   <li><strong>Go channels:</strong> Similar to Go's unbuffered channels (make(chan T))</li>
 *   <li><strong>Haskell MVars:</strong> Similar to MVars but always empty (rendezvous only)</li>
 *   <li><strong>CSP:</strong> Implements Communicating Sequential Processes synchronization</li>
 * </ul>
 *
 * <p><strong>Thread-safety:</strong> All operations are thread-safe and can be called
 * from multiple Fibers concurrently.</p>
 *
 * @param <T> The type of values communicated through this channel
 */
public class Channel<T> {
	// Consumers waiting for producers to send values
	private final Deque<CompletableFuture<T>> waitingTakes = new ArrayDeque<>();

	// Producers waiting for consumers to receive values (value + acknowledgment future)
	private final Deque<Tuple2<T, CompletableFuture<Nothing>>> waitingPuts = new ArrayDeque<>();

	// Lock ensuring atomic poll-or-enqueue operations
	private final Object lock = new Object();

	/**
	 * Receive a value from the channel, suspending until a producer sends one.
	 *
	 * <p>If a producer is already waiting, the handoff is immediate and this returns
	 * a completed future. Otherwise, this operation suspends by returning a pending
	 * future that will be completed when a producer calls {@link #put(T)}.</p>
	 *
	 * <p><strong>Ordering:</strong> Values are received in FIFO order - the first
	 * consumer to call take() will receive the first value put().</p>
	 *
	 * @return An Awaitable that completes with the received value
	 */
	public Awaitable<T> take() {
		return () -> {
			T valueToDeliver = null;
			CompletableFuture<Nothing> producerAck = null;

			synchronized (lock) {
				// FAST-PATH: producer already waiting – single poll
				Tuple2<T, CompletableFuture<Nothing>> prod = waitingPuts.pollFirst();
				if (prod != null) {
					valueToDeliver = prod._1;
					producerAck = prod._2;
				} else {
					// SLOW-PATH: enqueue exactly once
					CompletableFuture<T> fut = new CompletableFuture<>();
					waitingTakes.addLast(fut);
					return fut; // return the one future created
				}
			}

			// Complete outside the lock to avoid re-entrancy into the queue
			if (producerAck != null) producerAck.complete(Nothing.nothing());
			return CompletableFuture.completedFuture(valueToDeliver);
		};
	}

	/**
	 * Send a value through the channel, suspending until a consumer receives it.
	 *
	 * <p>If a consumer is already waiting, the handoff is immediate and this returns
	 * a completed future. Otherwise, this operation suspends by returning a pending
	 * future that will be completed when a consumer calls {@link #take()}.</p>
	 *
	 * <p><strong>Ordering:</strong> Values are delivered in FIFO order - the first
	 * producer to call put() will have their value received by the first waiting consumer.</p>
	 *
	 * @param value The value to send through the channel
	 * @return An Awaitable that completes when the value has been received by a consumer
	 */
	public Awaitable<Nothing> put(T value) {
		return () -> {
			CompletableFuture<T> waitingConsumer = null;

			synchronized (lock) {
				// FAST-PATH: consumer already waiting – single poll
				waitingConsumer = waitingTakes.pollFirst();
				if (waitingConsumer == null) {
					// SLOW-PATH: enqueue exactly once
					CompletableFuture<Nothing> ack = new CompletableFuture<>();
					waitingPuts.addLast(new Tuple2<>(value, ack));
					return ack;
				}
			}

			// Complete outside the lock to avoid re-entrancy into the queue
			waitingConsumer.complete(value);
			return CompletableFuture.completedFuture(Nothing.nothing());
		};
	}

	/**
	 * Get the number of consumers currently suspended waiting for values.
	 *
	 * <p>This is primarily useful for debugging and testing. The count may change
	 * immediately after this method returns due to concurrent operations.</p>
	 *
	 * @return The number of pending take() operations
	 */
	public int pendingConsumers() {
		synchronized (lock) {
			return waitingTakes.size();
		}
	}

	/**
	 * Get the number of producers currently suspended waiting for consumers.
	 *
	 * <p>This is primarily useful for debugging and testing. The count may change
	 * immediately after this method returns due to concurrent operations.</p>
	 *
	 * @return The number of pending put() operations
	 */
	public int pendingProducers() {
		synchronized (lock) {
			return waitingPuts.size();
		}
	}
}