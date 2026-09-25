package org.clauseway.functional;

// ABOUTME: Optional combinators Java 8 lacks: firstPresent tries alternatives
// ABOUTME: in order and answers the first non-empty one.

import java.util.Optional;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Optionals {

	/** The first present alternative, evaluated lazily in order; empty when none is. */
	@SafeVarargs
	public static <T> Optional<T> firstPresent(Supplier<Optional<T>>... alternatives) {
		for (Supplier<Optional<T>> alternative : alternatives) {
			Optional<T> candidate = alternative.get();
			if (candidate.isPresent()) {
				return candidate;
			}
		}
		return Optional.empty();
	}
}
