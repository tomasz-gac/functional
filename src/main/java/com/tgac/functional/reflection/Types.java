package com.tgac.functional.reflection;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author TGa
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Types {

    public static String resolveSimpleName(Type type) {
        return cast(type, Class.class)
                .map(Class::getSimpleName)
                .getOrElse(() -> cast(type, ParameterizedType.class)
                        .flatMap(pt -> cast(pt.getRawType(), Class.class)
                                .map(Class::getSimpleName)
                                .map(name -> name +
                                        String.format("<%s>", Arrays.stream(pt.getActualTypeArguments())
                                                // recursion, stack based
                                                .map(Types::resolveSimpleName)
                                                .collect(Collectors.joining(",")))))
                        .getOrElse(type::getTypeName));
    }

    public static <T> Option<T> cast(Object v, Class<T> cls) {
        return cls.isInstance(v) ?
                Option.of(cls.cast(v)) :
                Option.none();
    }

    public static <T> Option<T> castAs(Object v, Class<?> cls) {
        return cast(v, cls)
                .map(u -> (T) u);
    }

    public static <T> Function<Object, Option<T>> cast(Class<T> cls) {
        return v -> cast(v, cls);
    }

    public static Type[] resolveGenerics(Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        } else {
            return new Type[0];
        }
    }

    public static Class<?> getClass(Type type) {
        if (type instanceof ParameterizedType) {
            return ((Class<?>) ((ParameterizedType) type).getRawType());
        } else if (type instanceof Class) {
            return (Class<?>) type;
        } else {
            throw new IllegalArgumentException("Cannot handle type: " + type);
        }
    }

    public static <T> Option<ParameterizedType> getParametrizedType(Class<T> cls) {
        return Option.of(cls.getGenericSuperclass())
                .filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast);
    }
}
