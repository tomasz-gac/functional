package org.clauseway.functional.category;

// ABOUTME: Pins Nothing's semilattice instance: the one-point lattice, where
// ABOUTME: combine returns the singleton and every law holds by uniqueness.

import static org.clauseway.functional.category.Nothing.nothing;
import static org.assertj.core.api.Assertions.assertThat;

import org.clauseway.functional.algebra.Semilattice;
import org.junit.jupiter.api.Test;

public class NothingTest {

	@Test
	public void nothingIsTheOnePointSemilattice() {
		Semilattice<Nothing> unit = nothing();

		assertThat(unit.combine(nothing())).isSameAs(nothing());
	}
}
