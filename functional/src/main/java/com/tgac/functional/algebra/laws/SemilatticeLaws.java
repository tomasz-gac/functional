package com.tgac.functional.algebra.laws;

// ABOUTME: Idempotence, commutativity, associativity, and leq-coherence for
// ABOUTME: both direction witnesses.

import com.tgac.functional.algebra.JoinSemilattice;
import com.tgac.functional.algebra.MeetSemilattice;
import java.util.List;
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
				for (L c : xs) {
					Laws.require(a.meet(b).meet(c).equals(a.meet(b.meet(c))), "meet associativity", a, b, c);
				}
			}
		}
	}

	public static <L extends JoinSemilattice<L>> void checkJoin(List<L> xs) {
		for (L a : xs) {
			Laws.require(a.join(a).equals(a), "join idempotence", a);
			for (L b : xs) {
				Laws.require(a.join(b).equals(b.join(a)), "join commutativity", a, b);
				Laws.require(a.leq(a.join(b)) && b.leq(a.join(b)), "join is an upper bound", a, b);
				for (L c : xs) {
					Laws.require(a.join(b).join(c).equals(a.join(b.join(c))), "join associativity", a, b, c);
				}
			}
		}
	}
}
