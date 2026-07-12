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
