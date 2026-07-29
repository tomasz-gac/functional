package com.tgac.functional.fibers.primitives;

// ABOUTME: JoinMap's growth journal: one entry per effective ascent — a fresh
// ABOUTME: key or a fold that moved a value — and none for an absorbed join.

import static org.assertj.core.api.Assertions.assertThat;

import com.tgac.functional.algebra.Semirings;
import org.junit.jupiter.api.Test;

public class JoinMapTest {

	private static JoinMap<String, Long> shortest(String key, long cost) {
		return JoinMap.<String, Long> empty(Semirings.MIN_PLUS).append(key, cost).get();
	}

	@Test
	public void aFreshKeyIsOneGrowth() {
		JoinMap<String, Long> map = shortest("d", 6L);

		assertThat(map.size()).isEqualTo(1);
		assertThat(map.growths()).isEqualTo(1);
		assertThat(map.growth(0)._1).isEqualTo("d");
		assertThat(map.growth(0)._2).isEqualTo(6L);
	}

	@Test
	public void anAscendingFoldIsAGrowthWithoutANewKey() {
		JoinMap<String, Long> map = shortest("d", 6L).join(shortest("d", 4L));

		// the key set did not grow, the knowledge did: min(6, 4) moved the value
		assertThat(map.size()).isEqualTo(1);
		assertThat(map.growths()).isEqualTo(2);
		assertThat(map.growth(1)._1).isEqualTo("d");
		assertThat(map.growth(1)._2).isEqualTo(4L);
	}

	@Test
	public void anAbsorbedFoldIsNoGrowth() {
		JoinMap<String, Long> map = shortest("d", 4L).join(shortest("d", 6L));

		assertThat(map.growths()).isEqualTo(1);
		assertThat(map.members.get("d").get()).isEqualTo(4L);
	}

	@Test
	public void aJournalledKeyReadsItsCurrentFold() {
		JoinMap<String, Long> map = shortest("d", 6L).join(shortest("d", 4L));

		// every journal entry hands the fold as of the read — monotone, so
		// re-delivery only ever improves on what the earlier entry delivered
		assertThat(map.growth(0)._2).isEqualTo(4L);
	}

	@Test
	public void appendAscentExtendsTheJournal() {
		JoinMap<String, Long> map = shortest("d", 6L).append("d", 4L).get();

		assertThat(map.growths()).isEqualTo(2);
	}

	@Test
	public void appendAbsorbedRefuses() {
		assertThat(shortest("d", 4L).append("d", 6L).isEmpty()).isTrue();
	}
}
