package com.tgac.codegen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that triggers the generation of function functional interfaces.
 * <p>
 * This annotation should be placed on a class to indicate that the annotation
 * processor should generate a set of functional interfaces for functions with
 * a specific number of arguments.
 * <p>
 * The annotation processor, {@link FunctionProcessor}, will detect this
 * annotation and generate the corresponding function interfaces (e.g.,
 * {@code Function1<T1, R>}, {@code Function2<T1, T2, R>}, etc.) in the specified
 * package.
 *
 * @see FunctionProcessor
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface GenerateFunctions {
	// No members
}
