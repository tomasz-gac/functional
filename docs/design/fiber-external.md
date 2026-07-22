# Fiber.external — external completions in a cooperative-fiber engine

**STATUS: DESIGN (July 2026, from conversations with Tom). Not built. The
Region machinery this doc leans on IS shipped (`com.tgac.functional.fibers.primitives`,
whose package javadoc carries the same doctrine in short form). This document
is the full rationale and the decision guide.**

---

## 1. The problem

Fibers are cooperatively scheduled: a drive steps frames from a worklist, and
a fiber that blocks its thread until another fiber completes deadlocks the
drive — the awaited fiber may need that very thread. That rule is absolute
and this design does not relax it. But real work must sometimes wait for the
OUTSIDE world — a database row, an HTTP response — and the outside world
completes on its own schedule, on its own threads. The question is how that
waiting enters a system whose one law is "waiting is never thread state."

An earlier design (since removed) answered with an await-on-future primitive
plus a completion queue. It deadlocked — not by blocking threads, but by
BLINDING QUIESCENCE: parked work was invisible to the machinery that decides
"all work is done," so the engine could neither terminate nor detect why not.
The lesson is structural and drives everything below: every wait must either
be visible to a detector, or be provably unable to form a cycle.

## 2. The design

**One new fiber node.** `Fiber.external(CompletionStage<T>)` SUSPENDS a live
fiber: the shared interpreter hands `(stage, continuation)` to the drive's
boundary and returns control. The fiber does not end — which is the load-
bearing choice: any accounting wrapper already around it (a counted unit of
work in a `WorkLedger`) simply has not finished, so every quiescence question
downstream ("may this region seal?", "is this solve done?") is answered by
the EXISTING drained rule with zero new records. The historical blindness is
structurally impossible: suspended work was never torn off its fiber, so it
was never invisible.

**Drive-side, three small pieces:**
- an **injection queue**: completions enqueue the continuation from the I/O
  thread; the drive drains the queue at the top of EVERY iteration
  (nonblocking poll). Injection is continuous, never a phase — any
  drain-then-harvest boundary conditioned on quiescence is unreachable when
  the worklist contains unbounded work, and unbounded work is a normal
  citizen of a fair engine;
- an **outstanding counter**: incremented at suspension, decremented at
  injection;
- the **idle endgame**: when the worklist is empty AND outstanding > 0, the
  THREAD (not any fiber) parks on the queue's blocking take. Exhaustion
  becomes the honest conjunction: worklist empty ∧ outstanding zero.

**Liveness is fairness, never emptiness.** An injected continuation competes
in the fair queue like any other frame; a fair drive steps it within one
rotation regardless of infinite neighbors. Depth-first drives keep their
documented unfairness — they were never fairness-safe for unbounded work,
with or without I/O.

**Failure is a completion.** Timeouts and I/O errors complete the stage
exceptionally; the resumed fiber fails its branch. Semantically that degrades
to INCOMPLETENESS (fewer results), never unsoundness. Every external stage
should carry a timeout regime; a stage that can hang forever holds the
endgame open forever.

## 3. When to use which — the decision guide

Every wait is an edge in a wait-for graph ("my completion depends on that
event"); deadlock is a cycle with no external escape. Whether a cycle is a
bug or a fixpoint depends on the SUBSTRATE being waited on, which yields
exactly three cases and one absolute rule:

| you are waiting on… | use | on cycle |
|---|---|---|
| the outside world (I/O, another process) | `Fiber.external` + timeout | cycles are invisible by construction; the timeout fails one branch — incompleteness, never unsoundness |
| in-engine state that GROWS monotonically (accumulating answers, widening knowledge — any upward-closed wake condition over a grow-only substrate) | enroll in a `Region`: park the continuation as data (a sleeper in a ledger, a subscriber in a cell) | a FIXPOINT, not an error: quiescence detection completes it soundly ("no more growth is possible") |
| a COMPLETED sub-computation through a non-monotone operator (aggregation over the wait's own progress, negation) | nothing — forbidden | no sound completion value exists; detect and REFUSE (stratification), never quiescence-complete |
| another fiber, by blocking a thread | never | — |

Rules of thumb, in the order to ask them:

1. **Is the completer outside every drive that transitively depends on this
   one?** Then `Fiber.external`. This is a CONTRACT, not a preference: a
   stage completed by in-drive work is the banned fiber-awaits-fiber
   deadlock laundered through a CompletionStage — both parties suspend
   before any guard can look, and the violation surfaces only as a solve
   that never reaches its endgame (diagnosable via the outstanding
   registry, not preventable).
2. **Is the substrate monotone?** Then Region enrollment is the only legal
   in-engine wait, and cycles cost nothing but the fixpoint they denote.
   Region is precisely a deadlock detector doubling as a fixpoint
   completer: the sleeping map is the wait-for graph, the counters are the
   no-runnable-work half, the group-seal walk is cycle (SCC) detection, and
   the seal is the completion policy — sound BECAUSE of monotonicity.
3. **Neither?** The design is wrong. Restructure so the dependency is
   external or monotone, or stratify so the cycle cannot form.

One composition rule follows: work counted in a region must not internally
wait on that region's OWN progress through an external-looking stage — the
region's ledger sees a running unit that never finishes, its seal never
fires, and the solve hangs. (In the logic engine's terms: inside a tabled
body, waits must be genuinely external.)

## 4. What this deliberately is not

Not a general await, and not a re-litigation of park-as-data: parking exists
for work that must OUTLIVE its fiber and serve many wakers (a table's
answers); suspension is for work that merely pauses, holding exactly one
continuation with a guaranteed completion. The two could be unified — one
wait-for graph spanning all wait primitives, one detector, two flavors
(external/unenrolled, internal/enrolled-monotone) — and that unification is
due when something first needs an internal wait OUTSIDE the machinery that
already enrolls (tabling). Until then, external-only holds the line, and this
node stays as small as it looks: one interpreter case, a queue, a counter,
and a contract.
