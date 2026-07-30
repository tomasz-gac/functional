package com.tgac.functional.fibers.interpreter;

// ABOUTME: Pins the work ledger: drainedSnapshot is the walk's admission read -
// ABOUTME: null while work runs, else counters plus the places sleepers wait at.

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class WorkLedgerTest {

	@Test
	public void aFreshLedgerHasNoSnapshot() {
		// started == 0 means the region's work has not begun — completing an
		// entry whose master is about to run would be unsound
		WorkLedger<String, String> ledger = new WorkLedger<>();
		assertThat(ledger.drainedSnapshot()).isNull();
	}

	@Test
	public void aSnapshotNeedsEveryStartMatchedByAFinish() {
		WorkLedger<String, String> ledger = new WorkLedger<>();
		ledger.started();
		assertThat(ledger.drainedSnapshot()).isNull();
		ledger.started();
		ledger.finished();
		assertThat(ledger.drainedSnapshot()).isNull();
		ledger.finished();
		assertThat(ledger.drainedSnapshot()).isNotNull();
	}

	@Test
	public void theSnapshotCarriesTheSleepersPlacesForTheWalkToJudge() {
		WorkLedger<String, String> ledger = new WorkLedger<>();
		ledger.started();
		ledger.finished();
		ledger.blocked("consumer", "someEntry");

		// the ledger does not judge wakeability - it hands the walk the
		// places, and membership decides which records are home
		WorkLedger.Snapshot<String> snapshot = ledger.drainedSnapshot();
		assertThat(snapshot).isNotNull();
		assertThat(snapshot.blockedAt).containsExactly("someEntry");
	}

	@Test
	public void wakingRemovesTheSleeper() {
		WorkLedger<String, String> ledger = new WorkLedger<>();
		ledger.started();
		ledger.finished();
		ledger.blocked("consumer", "someEntry");
		ledger.unblocked("consumer");

		assertThat(ledger.drainedSnapshot().blockedAt).isEmpty();
	}

	@Test
	public void theSnapshotCounterIsTheReVerifyHandle() {
		WorkLedger<String, String> ledger = new WorkLedger<>();
		ledger.started();
		ledger.finished();

		WorkLedger.Snapshot<String> snapshot = ledger.drainedSnapshot();
		assertThat(snapshot.started).isEqualTo(ledger.startedCount());

		// a spawn after the snapshot moves the monotone counter: the walk's
		// two-phase re-verify sees the mismatch and aborts
		ledger.started();
		assertThat(ledger.startedCount()).isNotEqualTo(snapshot.started);
	}

}
