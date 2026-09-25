package org.clauseway.functional;

import java.util.Optional;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author TGa
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Types {
	public static <T> Optional<T> cast(Object v, Class<T> cls) {
		return cls.isInstance(v) ?
				Optional.of(cls.cast(v)) :
				Optional.empty();
	}

	@SuppressWarnings("unchecked")
	public static <T> Optional<T> castAs(Object v, Class<?> cls) {
		return cast(v, cls)
				.map(u -> (T) u);
	}

	public static <T> Function<Object, Optional<T>> castAs(Class<?> cls) {
		return v -> Types.castAs(v, cls);
	}

	public static <T> Function<Object, Optional<T>> cast(Class<T> cls) {
		return v -> cast(v, cls);
	}

	@SuppressWarnings("unchecked")
	public static <U> Function<Object, U> cast() {
		return v -> (U) v;
	}
}
