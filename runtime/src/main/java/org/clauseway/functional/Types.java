package org.clauseway.functional;

import io.vavr.control.Option;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author TGa
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Types {
	public static <T> Option<T> cast(Object v, Class<T> cls) {
		return cls.isInstance(v) ?
				Option.of(cls.cast(v)) :
				Option.none();
	}

	@SuppressWarnings("unchecked")
	public static <T> Option<T> castAs(Object v, Class<?> cls) {
		return cast(v, cls)
				.map(u -> (T) u);
	}

	public static <T> Function<Object, Option<T>> castAs(Class<?> cls) {
		return v -> Types.castAs(v, cls);
	}

	public static <T> Function<Object, Option<T>> cast(Class<T> cls) {
		return v -> cast(v, cls);
	}

	@SuppressWarnings("unchecked")
	public static <U> Function<Object, U> cast() {
		return v -> (U) v;
	}
}
