package org.clauseway.functional.tuples;

// ABOUTME: The flat tuple beyond arity 8: an array-backed member of the family
// ABOUTME: carrying the same structural contract, componentwise equality.

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The family's unbounded member: where the typed classes stop at arity 8,
 * this one holds any wider row flat. No accessors and no apply — a caller
 * wide enough to need it speaks the structural contract directly.
 */
public final class TupleN implements Tuple {

	private final Object[] members;

	TupleN(Object[] members) {
		this.members = members.clone();
	}

	@Override
	public int arity() {
		return members.length;
	}

	@Override
	public Object get(int i) {
		if (i < 1 || i > members.length) {
			throw new IllegalArgumentException(
					"no member " + i + " in a tuple of " + members.length);
		}
		return members[i - 1];
	}

	@Override
	public Tuple withMembers(Object[] replacement) {
		if (replacement.length != members.length) {
			throw new IllegalArgumentException("a tuple of " + members.length
					+ " rebuilds from " + members.length + " members, got "
					+ replacement.length);
		}
		return new TupleN(replacement);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof TupleN && Arrays.equals(members, ((TupleN) o).members);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(members);
	}

	@Override
	public String toString() {
		return Arrays.stream(members)
				.map(String::valueOf)
				.collect(Collectors.joining(", ", "(", ")"));
	}
}
