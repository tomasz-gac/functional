package com.tgac.functional;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface Reference<T> {
	T get();

	void set(T v);

	default Reference<T> update(UnaryOperator<T> f) {
		set(f.apply(get()));
		return this;
	}

	static <T> Reference<T> to(T v) {
		return new Reference<T>() {
			private T value = v;

			@Override
			public T get() {
				return value;
			}

			@Override
			public void set(T v) {
				value = v;
			}
		};
	}

	static <T> Reference<T> defer(Supplier<T> s, Consumer<T> c) {
		return new Reference<T>() {
			@Override
			public T get() {
				return s.get();
			}

			@Override
			public void set(T v) {
				c.accept(v);
			}
		};
	}

	static <T> Reference<T> empty() {
		return to(null);
	}
}
