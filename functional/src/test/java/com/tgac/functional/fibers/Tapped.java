package com.tgac.functional.fibers;

// ABOUTME: Test helper: observe forked children's completion values by tapping
// ABOUTME: each fiber — the tap fires at child completion, in completion order.

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fork observes nothing about its children's values — a fork completes when
 * control drains, not when data arrives. Tests that want to see each child's
 * completion value tap the children themselves.
 */
public final class Tapped {

	private Tapped() {
	}

	public static <A> List<Fiber<A>> tapped(List<Fiber<A>> fibers, Consumer<A> tap) {
		List<Fiber<A>> out = new ArrayList<>(fibers.size());
		for (Fiber<A> fiber : fibers) {
			out.add(fiber.map(v -> {
				tap.accept(v);
				return v;
			}));
		}
		return out;
	}
}
