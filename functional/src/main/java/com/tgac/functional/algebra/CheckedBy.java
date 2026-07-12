package com.tgac.functional.algebra;

// ABOUTME: Declares which law-kit ids certify an algebraic interface — the
// ABOUTME: pairing LawCoverage verifies for every exercised implementor.

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * On an algebraic interface: the kit ids (as recorded by the law kits) any of
 * which certifies an implementor. An interface bearing this annotation IS an
 * algebra to the coverage gate — discovery, claiming and kit-matching all
 * derive from it, so a new algebra/kit pair needs no gate changes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckedBy {
	String[] value();
}
