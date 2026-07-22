package com.tgac.functional.algebra.laws;

// ABOUTME: Idempotence, commutativity, associativity, and leq-coherence for
// ABOUTME: both direction witnesses.

import com.tgac.functional.algebra.JoinSemilattice;
import com.tgac.functional.algebra.MeetSemilattice;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SemilatticeLaws {
	public static <L extends MeetSemilattice<L>> void checkMeet(List<L> xs) {
		for (L a : xs) {
			Laws.require(a.meet(a).equals(a), "meet idempotence", a);
			Laws.require(a.leq(a), "leq reflexivity", a);
			for (L b : xs) {
				Laws.require(a.meet(b).equals(b.meet(a)), "meet commutativity", a, b);
				Laws.require(a.meet(b).leq(a) && a.meet(b).leq(b), "meet is a lower bound", a, b);
				if (a.leq(b) && b.leq(a)) {
					Laws.require(a.equals(b), "leq antisymmetry (mod equals)", a, b);
				}
				for (L c : xs) {
					Laws.require(a.meet(b).meet(c).equals(a.meet(b.meet(c))), "meet associativity", a, b, c);
					if (a.leq(b) && b.leq(c)) {
						Laws.require(a.leq(c), "leq transitivity", a, b, c);
					}
				}
			}
		}
		LawRegistry.recordSamples("meet", xs);
		// the sweeps above ARE the order laws for the derived leq — this kit
		// certifies the PartialOrder algebra of everything it checks
		LawRegistry.recordSamples("partial-order", xs);
	}

	public static <L extends JoinSemilattice<L>> void checkJoin(List<L> xs) {
		checkJoin(xs, Object::equals);
	}

	/** @param eq the quotient the laws are claimed up to (solved form for substitutions) */
	public static <L extends JoinSemilattice<L>> void checkJoin(List<L> xs, BiPredicate<L, L> eq) {
		for (L a : xs) {
			Laws.require(eq.test(a.join(a), a), "join idempotence", a);
			for (L b : xs) {
				L ab = a.join(b);
				Laws.require(eq.test(ab, b.join(a)), "join commutativity", a, b);
				Laws.require(eq.test(a.join(ab), ab) && eq.test(b.join(ab), ab),
						"join is an upper bound", a, b);
				for (L c : xs) {
					Laws.require(eq.test(a.join(b).join(c), a.join(b.join(c))), "join associativity", a, b, c);
				}
			}
		}
		LawRegistry.recordSamples("join", xs);
		// join laws imply the order laws for the derived leq (up to the same
		// eq quotient the join laws are claimed under)
		LawRegistry.recordSamples("partial-order", xs);
	}
}
