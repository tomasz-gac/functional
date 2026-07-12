package com.tgac.functional.algebra.laws;

// ABOUTME: The reusable coverage gate: discovers algebraic implementors on the main
// ABOUTME: classpath and verifies each is claimed by a @LawsFor-annotated test.

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LawCoverage {

	/**
	 * Fails unless every non-abstract implementor of the given algebras found
	 * under {@code mainClasses} is claimed by some {@link LawsFor} annotation
	 * found under {@code testClasses} — either directly or via an enclosing
	 * class (anonymous witnesses). The claimed tests themselves run as
	 * ordinary test classes; this verifies only that the mapping is total.
	 */
	public static void verify(Path mainClasses, Path testClasses, Class<?>... algebras) throws IOException {
		Set<Class<?>> claims = new HashSet<>();
		for (Class<?> testClass : loadAll(testClasses)) {
			LawsFor claim = testClass.getAnnotation(LawsFor.class);
			if (claim != null) {
				for (Class<?> claimed : claim.value()) {
					claims.add(claimed);
				}
			}
		}
		for (Class<?> testClass : loadAll(testClasses)) {
			// nested classes are fixtures, not law tests — enforcement is top-level
			if (testClass.getEnclosingClass() != null) {
				continue;
			}
			if (testClass.getAnnotation(LawsFor.class) != null && !hasAfterHook(testClass)) {
				throw new AssertionError(testClass.getName()
						+ " claims laws but has no @AfterClass/@AfterAll calling verifyClaimsExercised");
			}
		}
		List<String> unclaimed = new ArrayList<>();
		for (Class<?> c : loadAll(mainClasses)) {
			if (c.isInterface() || Modifier.isAbstract(c.getModifiers()) || !isAlgebraic(c, algebras)) {
				continue;
			}
			if (!isClaimed(c, claims)) {
				unclaimed.add(c.getName());
			}
		}
		if (!unclaimed.isEmpty()) {
			throw new AssertionError("algebraic implementors without a @LawsFor test: " + unclaimed);
		}
	}

	/**
	 * Per-test-class guard (wire from @AfterClass/@AfterAll): every class
	 * claimed by the test's {@link LawsFor} was exercised by at least one kit,
	 * and every exercised class was exercised by the kits its ALGEBRAS demand —
	 * a claimed MeetSemilattice touched only by BottomedLaws fails here.
	 */
	public static void verifyClaimsExercised(Class<?> testClass) {
		LawsFor claim = testClass.getAnnotation(LawsFor.class);
		if (claim == null) {
			throw new AssertionError(testClass.getName() + " has no @LawsFor claim to verify");
		}
		List<String> problems = new ArrayList<>();
		for (Class<?> claimed : claim.value()) {
			boolean touched = false;
			for (Class<?> exercised : LawRegistry.exercisedClasses()) {
				for (Class<?> at = exercised; at != null; at = at.getEnclosingClass()) {
					if (at.equals(claimed)) {
						touched = true;
					}
				}
			}
			if (!touched) {
				problems.add("claimed but never exercised by any kit: " + claimed.getName());
			}
		}
		for (Class<?> exercised : LawRegistry.exercisedClasses()) {
			Set<String> kits = LawRegistry.kitsFor(exercised);
			requireKit(problems, exercised, "com.tgac.functional.algebra.MeetSemilattice",
					kits, "meet", "lattice", "lattice-inflationary");
			requireKit(problems, exercised, "com.tgac.functional.algebra.JoinSemilattice",
					kits, "join", "lattice", "join-inflationary");
			requireKit(problems, exercised, "com.tgac.functional.algebra.Bottomed",
					kits, "bottomed");
			requireKit(problems, exercised, "com.tgac.functional.algebra.Semiring",
					kits, "semiring");
			requireKit(problems, exercised, "com.tgac.functional.algebra.CommutativeMonoid",
					kits, "commutative");
			requireKit(problems, exercised, "com.tgac.functional.algebra.Monoid",
					kits, "monoid", "commutative");
		}
		if (!problems.isEmpty()) {
			throw new AssertionError(testClass.getName() + ": " + problems);
		}
	}

	private static void requireKit(
			List<String> problems, Class<?> exercised, String algebraName,
			Set<String> kits, String... accepted) {
		if (!implementsAlgebra(exercised, algebraName)) {
			return;
		}
		for (String kit : accepted) {
			if (kits.contains(kit)) {
				return;
			}
		}
		problems.add(exercised.getName() + " implements " + algebraName
				+ " but no matching kit ran (has: " + kits + ")");
	}

	private static boolean implementsAlgebra(Class<?> c, String algebraName) {
		for (Class<?> at = c; at != null; at = at.getSuperclass()) {
			if (interfacesOf(at, algebraName)) {
				return true;
			}
		}
		return false;
	}

	private static boolean interfacesOf(Class<?> c, String algebraName) {
		for (Class<?> i : c.getInterfaces()) {
			if (i.getName().equals(algebraName) || interfacesOf(i, algebraName)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isAlgebraic(Class<?> c, Class<?>[] algebras) {
		for (Class<?> algebra : algebras) {
			if (algebra.isAssignableFrom(c)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isClaimed(Class<?> c, Set<Class<?>> claims) {
		for (Class<?> at = c; at != null; at = at.getEnclosingClass()) {
			if (claims.contains(at)) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasAfterHook(Class<?> testClass) {
		for (java.lang.reflect.Method m : testClass.getDeclaredMethods()) {
			for (java.lang.annotation.Annotation a : m.getAnnotations()) {
				String name = a.annotationType().getSimpleName();
				if (name.equals("AfterAll") || name.equals("AfterClass")) {
					return true;
				}
			}
		}
		return false;
	}

	private static List<Class<?>> loadAll(Path root) throws IOException {
		List<Class<?>> out = new ArrayList<>();
		try (Stream<Path> paths = Files.walk(root)) {
			paths.filter(p -> p.toString().endsWith(".class"))
					.forEach(p -> {
						String name = root.relativize(p).toString()
								.replace(java.io.File.separatorChar, '.')
								.replaceAll("\\.class$", "");
						try {
							out.add(Class.forName(name));
						} catch (ClassNotFoundException | LinkageError e) {
							// unloadable class files are not candidates
						}
					});
		}
		return out;
	}
}
