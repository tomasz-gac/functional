package org.clauseway.functional;

// ABOUTME: Pins firstPresent: order, laziness past the first hit, and the
// ABOUTME: all-empty case.

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;

public class OptionalsTest {

	@Test
	public void answersTheFirstPresentAlternative() {
		assertThat(Optionals.firstPresent(
				Optional::empty,
				() -> Optional.of("second"),
				() -> Optional.of("third")))
				.contains("second");
	}

	@Test
	public void laterAlternativesStayUnevaluatedPastTheFirstHit() {
		assertThat(Optionals.<String> firstPresent(
				() -> Optional.of("first"),
				() -> {
					throw new AssertionError("evaluated past the hit");
				}))
				.contains("first");
	}

	@Test
	public void emptyWhenEveryAlternativeIs() {
		assertThat(Optionals.<String> firstPresent(Optional::empty, Optional::empty))
				.isEmpty();
	}
}
