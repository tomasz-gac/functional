package com.tgac.functional;

import static com.tgac.functional.Tuples.tuple;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Streams {
	public static <A, B, C> Stream<C> zip(Stream<? extends A> a, Stream<? extends B> b, BiFunction<? super A, ? super B, ? extends C> f) {
		Spliterator<? extends A> spliteratorA = a.spliterator();
		Spliterator<? extends B> spliteratorB = b.spliterator();

		int characteristics = spliteratorA.characteristics() & spliteratorB.characteristics();

		Iterator<A> iteratorA = Spliterators.iterator(spliteratorA);
		Iterator<B> iteratorB = Spliterators.iterator(spliteratorB);

		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(
						new Iterator<C>() {
							@Override
							public boolean hasNext() {
								return iteratorA.hasNext() && iteratorB.hasNext();
							}

							@Override
							public C next() {
								return f.apply(iteratorA.next(), iteratorB.next());
							}
						}, characteristics),
				false);
	}

	public static <A> Function<A, Tuples._2<Long, A>> enumerateLong() {
		AtomicLong i = new AtomicLong(0);
		return v -> tuple(i.getAndIncrement(), v);
	}

	public static <A> Function<A, Tuples._2<Integer, A>> enumerate() {
		AtomicInteger i = new AtomicInteger(0);
		return v -> tuple(i.getAndIncrement(), v);
	}
}
