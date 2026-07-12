package com.tgac.functional.algebra;

// ABOUTME: The coverage gate: discovers every class implementing an algebraic
// ABOUTME: interface and fails unless its laws are registered — then runs them.

import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.algebra.laws.BottomedLaws;
import com.tgac.functional.algebra.laws.CommutativeMonoidLaws;
import com.tgac.functional.algebra.laws.FoldLaws;
import com.tgac.functional.algebra.laws.LatticeLaws;
import com.tgac.functional.algebra.laws.Lattices;
import com.tgac.functional.algebra.laws.MonoidLaws;
import com.tgac.functional.algebra.laws.Monoids;
import com.tgac.functional.algebra.laws.SemiringLaws;
import com.tgac.functional.algebra.laws.Semirings;
import com.tgac.functional.algebra.laws.StarLaws;
import com.tgac.functional.algebra.laws.SuperiorityLaws;
import io.vavr.collection.List;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class AlgebraicLawCoverageTest {

	private static final Class<?>[] ALGEBRAS = {
			MeetSemilattice.class, JoinSemilattice.class, Lattice.class,
			Monoid.class, CommutativeMonoid.class, Semiring.class, Bottomed.class};

	private static final java.util.List<Long> SMALL = Arrays.asList(0L, 1L, 2L, 3L, 7L);
	private static final java.util.List<Long> BOUNDARY = Arrays.asList(
			0L, 1L, 2L, Long.MAX_VALUE / 2, Long.MAX_VALUE - 1, Long.MAX_VALUE);
	private static final java.util.List<Boolean> BOOLS = Arrays.asList(true, false);
	private static final java.util.List<Long> COSTS = Arrays.asList(0L, 1L, 5L, 12L, Long.MAX_VALUE);

	/** The one hand-maintained part: meaningful samples cannot be synthesized. */
	private static final Map<Class<?>, Runnable> LAWS = new LinkedHashMap<>();

	static {
		register(Semirings.COUNTING, () -> SemiringLaws.check(Semirings.COUNTING, SMALL));
		register(Semirings.BOOLEAN, () -> {
			SemiringLaws.check(Semirings.BOOLEAN, BOOLS);
			StarLaws.check(Semirings.BOOLEAN, BOOLS);
			SuperiorityLaws.check(Semirings.BOOLEAN, BOOLS);
		});
		register(Semirings.MIN_PLUS, () -> {
			SemiringLaws.check(Semirings.MIN_PLUS, COSTS);
			StarLaws.check(Semirings.MIN_PLUS, COSTS);
			SuperiorityLaws.check(Semirings.MIN_PLUS, COSTS);
		});
		register(Semirings.SATURATING, () -> SemiringLaws.check(Semirings.SATURATING, BOUNDARY));
		register(Monoids.LONG_SUM, () -> {
			CommutativeMonoidLaws.check(Monoids.LONG_SUM, SMALL);
			FoldLaws.check(Monoids.LONG_SUM, Arrays.asList(1L, 2L), Arrays.asList(3L, 4L));
		});
		register(Monoids.LONG_MIN, () -> {
			CommutativeMonoidLaws.check(Monoids.LONG_MIN, SMALL);
			FoldLaws.check(Monoids.LONG_MIN, Arrays.asList(5L, 2L), Arrays.asList(9L));
		});
		register(Monoids.LONG_MAX, () -> {
			CommutativeMonoidLaws.check(Monoids.LONG_MAX, SMALL);
			FoldLaws.check(Monoids.LONG_MAX, Arrays.asList(5L, 2L), Arrays.asList(9L));
		});
		register(Monoids.<Long> list(), () -> {
			MonoidLaws.check(Monoids.<Long> list(),
					Arrays.asList(List.of(1L), List.of(2L, 3L), List.empty()));
			FoldLaws.check(Monoids.<Long> list(),
					Arrays.asList(List.of(1L), List.of(2L)), Arrays.asList(List.of(3L)));
		});
		// the derived monoid views (one anonymous class each, shared by all semirings)
		register(Semirings.COUNTING.additive(),
				() -> CommutativeMonoidLaws.check(Semirings.COUNTING.additive(), SMALL));
		register(Semirings.COUNTING.multiplicative(),
				() -> MonoidLaws.check(Semirings.COUNTING.multiplicative(), SMALL));
		LAWS.put(Lattices.Mask.class, () -> {
			java.util.List<Lattices.Mask> xs = Arrays.asList(
					Lattices.Mask.of(0b0000L), Lattices.Mask.of(0b1010L),
					Lattices.Mask.of(0b0110L), Lattices.Mask.of(0b1111L));
			LatticeLaws.check(xs);
			BottomedLaws.check(xs);
		});
		LAWS.put(Lattices.Range.class, () -> {
			java.util.List<Lattices.Range> xs = Arrays.asList(
					Lattices.Range.of(0, 10), Lattices.Range.of(3, 5),
					Lattices.Range.of(8, 12), Lattices.Range.of(1, 0));
			LatticeLaws.checkInflationary(xs);
			BottomedLaws.check(xs);
		});
	}

	private static void register(Object witness, Runnable laws) {
		LAWS.put(witness.getClass(), laws);
	}

	@Test
	public void everyDeclaredAlgebraicInstanceHasItsLawsRegisteredAndPassing() throws IOException {
		java.util.List<Class<?>> instances = discoverImplementors();
		assertThat(instances).isNotEmpty();
		for (Class<?> instance : instances) {
			Runnable laws = LAWS.get(instance);
			assertThat(laws)
					.describedAs("no law registration for %s — add samples to "
							+ "AlgebraicLawCoverageTest.LAWS", instance.getName())
					.isNotNull();
			try {
				laws.run();
			} catch (AssertionError e) {
				throw new AssertionError(instance.getName() + ": " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Walks this module's compiled classes — main scope only, so test-local
	 * deliberately-broken witnesses never trip the gate.
	 */
	private static java.util.List<Class<?>> discoverImplementors() throws IOException {
		Path root = Paths.get("target", "classes");
		java.util.List<Class<?>> found = new ArrayList<>();
		try (Stream<Path> paths = Files.walk(root)) {
			paths.filter(p -> p.toString().endsWith(".class"))
					.forEach(p -> {
						String name = root.relativize(p).toString()
								.replace(java.io.File.separatorChar, '.')
								.replaceAll("\\.class$", "");
						try {
							Class<?> c = Class.forName(name);
							if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) {
								return;
							}
							for (Class<?> algebra : ALGEBRAS) {
								if (algebra.isAssignableFrom(c)) {
									found.add(c);
									return;
								}
							}
						} catch (ClassNotFoundException | LinkageError e) {
							// unloadable class files are not algebraic instances
						}
					});
		}
		return found;
	}
}
