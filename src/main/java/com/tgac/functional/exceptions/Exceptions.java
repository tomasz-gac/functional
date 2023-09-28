package com.tgac.functional.exceptions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
/**
 * @author TGa
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Exceptions {

    public static <T> Supplier<T> format(Function<String, T> factory, String text, Object... args) {
        return () -> factory.apply(String.format(text, args));
    }

    public static <T> Supplier<T> throwingSupplier(Supplier<? extends Throwable> throwable) {
        return () -> throwNow(throwable.get());
    }

    public static <T, R> Function<T, R> throwingFunction(Supplier<? extends Throwable> throwable) {
        return v -> throwNow(throwable.get());
    }

    public static <T, U, R> BiFunction<T, U, R> throwingBiFunction(Supplier<? extends Throwable> throwable) {
        return (v, u) -> throwNow(throwable.get());
    }

    public static <T> UnaryOperator<T> throwingOp(Supplier<? extends Throwable> throwable) {
        return v -> throwNow(throwable.get());
    }

    public static <T> BinaryOperator<T> throwingBiOp(Supplier<? extends Throwable> throwable) {
        return (v, u) -> throwNow(throwable.get());
    }

    public static <A> A throwNow(Throwable throwable) {
        try {
            throw throwable;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
