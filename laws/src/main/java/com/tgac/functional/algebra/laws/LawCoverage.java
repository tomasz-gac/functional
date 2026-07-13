package com.tgac.functional.algebra.laws;

// ABOUTME: The reusable coverage gate: discovers algebraic implementors on the main
// ABOUTME: classpath and verifies each is claimed by a @LawsFor-annotated test.

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	@SafeVarargs
	public static void verify(Path mainClasses, Path testClasses,
			Class<? extends java.lang.annotation.Annotation>... afterHooks) throws IOException {
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
			if (testClass.getAnnotation(LawsFor.class) != null && !hasAfterHook(testClass, afterHooks)) {
				throw new AssertionError(testClass.getName()
						+ " claims laws but has no @AfterClass/@AfterAll calling verifyClaimsExercised");
			}
		}
		List<String> unclaimed = new ArrayList<>();
		for (Class<?> c : loadAll(mainClasses)) {
			if (c.isInterface() || Modifier.isAbstract(c.getModifiers()) || checkedAlgebrasOf(c).isEmpty()) {
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
			// audit only the classes THIS test claims: foreign receipts are the
			// claiming test's business, so hook verdicts cannot depend on
			// which test classes happened to run earlier
			boolean underClaims = false;
			for (Class<?> at = exercised; at != null; at = at.getEnclosingClass()) {
				for (Class<?> claimed : claim.value()) {
					if (at.equals(claimed)) {
						underClaims = true;
					}
				}
			}
			if (!underClaims) {
				continue;
			}
			Set<String> kits = LawRegistry.kitsFor(exercised);
			for (Class<?> algebra : checkedAlgebrasOf(exercised)) {
				String[] accepted = algebra.getAnnotation(com.tgac.functional.algebra.CheckedBy.class).value();
				boolean matched = false;
				for (String kit : accepted) {
					if (kits.contains(kit)) {
						matched = true;
					}
				}
				if (!matched) {
					problems.add(exercised.getName() + " implements " + algebra.getSimpleName()
							+ " but no matching kit ran (needs one of "
							+ java.util.Arrays.toString(accepted) + ", has: " + kits + ")");
				}
			}
		}
		if (!problems.isEmpty()) {
			throw new AssertionError(testClass.getName() + ": " + problems);
		}
	}


	/** Every @CheckedBy-annotated interface in c's hierarchy — c's algebras. */
	private static List<Class<?>> checkedAlgebrasOf(Class<?> c) {
		List<Class<?>> algebras = new ArrayList<>();
		collectAlgebras(c, algebras);
		return algebras;
	}

	private static void collectAlgebras(Class<?> c, List<Class<?>> out) {
		if (c == null) {
			return;
		}
		for (Class<?> i : c.getInterfaces()) {
			if (i.getAnnotation(com.tgac.functional.algebra.CheckedBy.class) != null && !out.contains(i)) {
				out.add(i);
			}
			collectAlgebras(i, out);
		}
		collectAlgebras(c.getSuperclass(), out);
	}

	private static boolean isClaimed(Class<?> c, Set<Class<?>> claims) {
		for (Class<?> at = c; at != null; at = at.getEnclosingClass()) {
			if (claims.contains(at)) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasAfterHook(Class<?> testClass,
			Class<? extends java.lang.annotation.Annotation>[] afterHooks) {
		for (java.lang.reflect.Method m : testClass.getDeclaredMethods()) {
			for (Class<? extends java.lang.annotation.Annotation> hook : afterHooks) {
				if (m.isAnnotationPresent(hook)) {
					return true;
				}
			}
		}
		return false;
	}

	/** The code source of a class — target/classes in a reactor build, the jar otherwise. */
	public static Path codeSource(Class<?> anchor) {
		try {
			return Path.class.cast(Paths.get(
					anchor.getProtectionDomain().getCodeSource().getLocation().toURI()));
		} catch (java.net.URISyntaxException e) {
			throw new IllegalStateException("cannot locate code source of " + anchor, e);
		}
	}

	private static List<Class<?>> loadAll(Path root) throws IOException {
		if (root.toString().endsWith(".jar")) {
			try (java.nio.file.FileSystem jar = java.nio.file.FileSystems.newFileSystem(root, (ClassLoader) null)) {
				return loadAllFrom(jar.getPath("/"));
			}
		}
		return loadAllFrom(root);
	}

	private static List<Class<?>> loadAllFrom(Path root) throws IOException {
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
