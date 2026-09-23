package org.clauseway.functional.tuples;

// ABOUTME: Receipts for the tuple family: vavr-shaped surface (of, accessors,
// ABOUTME: apply) plus the structural contract (arity, get, withMembers).

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TupleTest {

	@Test
	public void factoriesAccessorsAndArity() {
		Tuple1<String> one = Tuple.of("a");
		assertThat(one._1()).isEqualTo("a");
		assertThat(one.arity()).isEqualTo(1);

		Tuple2<String, Integer> two = Tuple.of("a", 1);
		assertThat(two._1()).isEqualTo("a");
		assertThat(two._2()).isEqualTo(1);
		assertThat(two._1).isEqualTo("a");
		assertThat(two.arity()).isEqualTo(2);

		Tuple8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> eight =
				Tuple.of(1, 2, 3, 4, 5, 6, 7, 8);
		assertThat(eight._8()).isEqualTo(8);
		assertThat(eight.arity()).isEqualTo(8);
	}

	@Test
	public void applyDestructures() {
		String one = Tuple.of("a").apply(s -> s + "!");
		assertThat(one).isEqualTo("a!");
		String two = Tuple.of("a", 1).apply((s, n) -> s + n);
		assertThat(two).isEqualTo("a1");
		Integer three = Tuple.of(1, 2, 3).apply((a, b, c) -> a + b + c);
		assertThat(three).isEqualTo(6);
		Integer eight = Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)
				.apply((a, b, c, d, e, f, g, h) -> a + b + c + d + e + f + g + h);
		assertThat(eight).isEqualTo(36);
	}

	@Test
	public void theStructuralContractRoundTrips() {
		List<Tuple> all = Arrays.asList(
				Tuple.of(1),
				Tuple.of(1, 2),
				Tuple.of(1, 2, 3),
				Tuple.of(1, 2, 3, 4),
				Tuple.of(1, 2, 3, 4, 5),
				Tuple.of(1, 2, 3, 4, 5, 6),
				Tuple.of(1, 2, 3, 4, 5, 6, 7),
				Tuple.of(1, 2, 3, 4, 5, 6, 7, 8));
		for (Tuple t : all) {
			int n = t.arity();
			Object[] members = new Object[n];
			for (int i = 1; i <= n; i++) {
				members[i - 1] = t.get(i);
				assertThat(members[i - 1]).isEqualTo(i);
			}
			assertThat(t.withMembers(members)).isEqualTo(t);

			Object[] bumped = new Object[n];
			for (int i = 0; i < n; i++) {
				bumped[i] = (Integer) members[i] + 100;
			}
			Tuple rebuilt = t.withMembers(bumped);
			assertThat(rebuilt.arity()).isEqualTo(n);
			for (int i = 1; i <= n; i++) {
				assertThat(rebuilt.get(i)).isEqualTo(i + 100);
			}
		}
	}

	@Test
	public void equalityIsComponentwise() {
		assertThat(Tuple.of("a", 1)).isEqualTo(Tuple.of("a", 1));
		assertThat(Tuple.of("a", 1).hashCode()).isEqualTo(Tuple.of("a", 1).hashCode());
		assertThat(Tuple.of("a", 1)).isNotEqualTo(Tuple.of("a", 2));
		assertThat((Object) Tuple.of(1)).isNotEqualTo(Tuple.of(1, 1));
	}

	@Test
	public void rendersLikeATuple() {
		assertThat(Tuple.of("a", 1).toString()).isEqualTo("(a, 1)");
		assertThat(Tuple.of(1).toString()).isEqualTo("(1)");
	}

	@Test
	public void theCompanionConstructsByArity() {
		for (int n = 1; n <= 12; n++) {
			Object[] members = new Object[n];
			for (int i = 0; i < n; i++) {
				members[i] = i + 1;
			}
			Tuple t = Tuple.ofAll(members);
			assertThat(t.arity()).isEqualTo(n);
			for (int i = 1; i <= n; i++) {
				assertThat(t.get(i)).isEqualTo(i);
			}
			Object[] bumped = new Object[n];
			for (int i = 0; i < n; i++) {
				bumped[i] = (Integer) members[i] + 100;
			}
			assertThat(t.withMembers(bumped).get(n)).isEqualTo(n + 100);
		}
		assertThatThrownBy(() -> Tuple.ofAll(new Object[0]))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void theUntypedDoorSpreadsAnArrayByArity() {
		// an Object[] argument is MEMBERS, never a 1-tuple holding the array
		Object[] cells = {"a", "b", "c"};
		Tuple t = Tuple.ofAll(cells);
		assertThat(t.arity()).isEqualTo(3);
		assertThat(t.get(1)).isEqualTo("a");
		assertThat((Object) Tuple.of("a")).isInstanceOf(Tuple1.class);
	}

	@Test
	public void dispatchIsDeterministicAtTheTypedBoundary() {
		// arity decides the class, so key equality never sees mixed shapes
		assertThat((Object) Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)).isInstanceOf(Tuple8.class);
		assertThat(Tuple.ofAll(1, 2, 3, 4, 5, 6, 7, 8, 9)).isInstanceOf(TupleN.class);
		assertThat(Tuple.ofAll(1, 2, 3, 4, 5, 6, 7, 8, 9))
				.isEqualTo(Tuple.ofAll(1, 2, 3, 4, 5, 6, 7, 8, 9));
		assertThat(Tuple.ofAll(1, 2, 3, 4, 5, 6, 7, 8, 9).toString())
				.isEqualTo("(1, 2, 3, 4, 5, 6, 7, 8, 9)");
	}

	@Test
	public void theContractRefusesOutOfRange() {
		assertThatThrownBy(() -> Tuple.of(1, 2).withMembers(new Object[] {1}))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> Tuple.of(1, 2).get(0))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> Tuple.of(1, 2).get(3))
				.isInstanceOf(IllegalArgumentException.class);
	}
}
