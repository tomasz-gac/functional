package com.tgac.functional.algebra.laws;

// ABOUTME: Claims law coverage for algebraic implementors: a test class annotated
// ABOUTME: with the implementor (or its enclosing class, for anonymous witnesses).

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Placed on a test class whose {@code @Test} methods run the law kits for the
 * named classes. A claim covers the named class itself AND every implementor
 * declared inside it — which is how anonymous witnesses
 * ({@code Semirings$1}…) are claimable by class literal.
 * {@code LawCoverage.verify} is the gate that matches claims against
 * discovered implementors.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LawsFor {
	Class<?>[] value();
}
