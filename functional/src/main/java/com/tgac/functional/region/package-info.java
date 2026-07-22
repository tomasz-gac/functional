// ABOUTME: Termination detection for cooperatively-scheduled work: regions of
// ABOUTME: counted fibers with parked subscribers, sealed at quiescence.

/**
 * Termination detection as a value: a {@link com.tgac.functional.region.Region}
 * pairs a monotonically growing {@link com.tgac.functional.region.MonotoneCell}
 * (whose growth wakes parked subscribers) with a
 * {@link com.tgac.functional.region.WorkLedger} (running work counted
 * Dijkstra–Scholten style; sleeping work recorded with WHERE it sleeps), and a
 * seal — the CAS'd-once declaration that the value is final. The sleeper
 * places form a wait-for graph; the seal cascade and the group seal are cycle
 * detection over that graph, and sealing a quiescent cycle is the sound
 * completion of a monotone fixpoint ("no more growth is possible"), waking its
 * sleepers into finality.
 *
 * <p><b>Which waiting mechanism to use — the three cases:</b>
 * <ul>
 * <li><b>External completion</b> (I/O, another process): the waited-for event
 * completes on its own, independent of any fiber in this drive. No cycle can
 * form through the wait, so no detection is needed — represent the work as an
 * externally-completed fiber (with a timeout regime; an exceptional completion
 * fails the branch: incompleteness, never unsoundness). Do NOT enroll such
 * waits here.</li>
 * <li><b>Internal, monotone</b> (accumulating answers, growing knowledge —
 * anything with upward-closed wake conditions over a grow-only substrate):
 * enroll in a Region. Cyclic waits are FIXPOINTS, not errors; quiescence
 * detection completes them soundly. This is the only legal way for one piece
 * of in-drive work to wait on another.</li>
 * <li><b>Internal, non-monotone</b> (a cycle through an operator whose result
 * depends on a COMPLETED sub-search — aggregation, negation): no sound
 * completion value exists; such cycles must be detected and refused
 * (stratification), never quiescence-completed. A Region's seal must not be
 * used to break them.</li>
 * </ul>
 *
 * <p>Blocking a thread until a fiber completes is never legal: in a
 * cooperative drive the awaited fiber may need that thread. Waiting is always
 * parked data (a subscriber in a cell, a sleeper in a ledger) or an external
 * completion — never thread state.
 */
package com.tgac.functional.region;
