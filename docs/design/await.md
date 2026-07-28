# Fiber.await — the condition variable over a monotone source

**STATUS: DESIGN (July 2026, from conversations with Tom). Not built. §1–§6
are the specification and stand alone; §7 (lineage) records what this design
replaces and why — read it for motivation, not for semantics. This is the
unification `fiber-external.md` §4 declared "due when something first needs an
internal wait outside the machinery that already enrolls (tabling)";
aggregation is that first thing. Read `fiber-external.md` for the
wait-for-graph doctrine this doc inherits.**

---

## 1. The model

A fiber may wait, in-engine, on exactly one kind of thing: a SOURCE — a
shared value that only grows. The wait is a condition variable with two
upgrades over the pthreads shape:

- **Monotonicity kills lost wakeups.** The waiter's readiness predicate is
  upward-closed (once true, true forever — a cursor against an append-only
  log, a bound against a narrowing domain). A wakeup can never be missed,
  only reveal more; spurious wakeups are absorbed by the recheck.
- **Run-once, re-armed by flatMap.** One await fires at most once. The loop
  `await → consume what you were handed → await again` is `while (!ready)
  cv.wait()`, built from ordinary sequencing. No multi-shot machinery in the
  node.

And one addition no condition variable has, the single novel element of the
design: the **negative completion**. A producer cannot know it is the last —
whether the source can ever grow again is a global property of everything
running or waiting. So the runtime computes it: each source belongs to a
SCOPE (a `WorkScope` with a ledger), and when the scope's ledger proves
quiescence — no running work, every waiter provably unable to be woken — the
scope SEALS and every pending await completes with `sealed`. POSIX pairs a
CV with a producer-set done flag; here the done flag is a theorem.

**Precedent.** The nearest relative is LVars (Kuper & Newton): lattice-valued
variables with THRESHOLD READS — blocking until the value passes an
upward-closed threshold, exactly the `ready` predicate — and quiescence
detection plus freeze as the definitive answer, exactly the seal; their
quasi-determinism theorem is this design's soundness story. The JDK's own
ledger machinery is ForkJoin's quiescence family (`awaitQuiescence`,
`helpQuiesce`, `Phaser`'s registration/arrival counting) — Dijkstra–Scholten
counting of outstanding tasks, which is what `WorkLedger` is. Reactive
Streams' `onNext`/`onComplete` is the same two-arm completion as
`more`/`sealed`. And C++ coroutines' awaitable contract
(`await_ready`/`await_suspend`/`await_resume`) is the closest SHAPE
precedent: `Source.suspend` is ready and suspend fused into one atomic call
(C++ splits them and patches the check-then-park race by letting
`await_suspend` refuse; our immediate return is the same move with the
atomicity guaranteed by the source's monitor), the `Result` a frame wakes
with is `await_resume`'s payload, and single-wake re-armed by flatMap is
`co_await` in a loop. The dividing line across all of them: every industrial
precedent ASSERTS completion (`onComplete`, `countDown`,
`arriveAndDeregister`) — someone must know they are last. Computed completion
over a monotone store exists only in the research systems (LVars; SLG
completion in the tabling literature, `group-seal.md`). That is why the
vocabulary is reusable and the libraries are not.

## 2. The abstraction

A recorded, suspendable task: one identity, always in exactly one state —
RUNNING, BLOCKED(at source), or DONE. The interpreter owns every transition
and the scope's ledger counts them; no transition is a calling convention.

THE SEAL THEOREM (unchanged from the shipped Scope, restated over frames): an
scope seals when its counters are drained (every started frame finished)
and every frame blocked on its behalf is blocked at itself or at an
already-sealed source — waking would need growth there, growth needs running
work there, just ruled out. Sealing completes the dead waiters, each dead
waiter's owner loses an obstruction and is rechecked: the cascade. Rings of
mutually-waiting scopes seal as one unit via the group walk (the merge
rule, `group-seal.md`). Same Dijkstra–Scholten counters, same walk, same
soundness argument as today — relocated into the interpreter.

## 3. API proposal

New surface in `com.tgac.functional.fibers` (interpreter-visible, beside
`WorkScope`):

```java
/** A monotone value fibers can await. Implementations: MonotoneCell;
 *  the external-completion boundary (Fiber.external's stage adapter). */
public interface Source<V> {

    /**
     * THE SOURCE IS THE TOKEN: a Source is a monotone value PLUS the
     * workforce producing it. Fiber.detachTo(source, work) records work as
     * producing into the source; blocked records hold the source a frame
     * waits at; the seal is the quiescence of the source's own workforce.
     * A foreign Source implementation has no workforce the runtime can
     * count, so it is AUTOMATICALLY the external flavor: never sealed,
     * timeout regime. One source per workforce — a production publishing
     * two values models them as one source of a product value.
     */

    /**
     * Attempt to suspend a waiter. ATOMIC with growth and seal: either
     * the answer is already available — {@code ready} holds of the current
     * value, or the source is sealed — and the immediate Result is
     * returned; or the waiter is HELD and null is returned. A held waiter
     * is completed exactly once: at the first growth satisfying its
     * predicate (sealed=false), or at seal (sealed=true). Must not block.
     */
    Await.Result<V> suspend(Predicate<V> ready, Await.Waiter<V> waiter);
}

public final class Await {

    /** The completion. {@code value} is the source's value at completion
     *  time — on sealed it is the FINAL value, so a waiter never re-reads
     *  ("did anything slip in before the seal?" is answered by construction). */
    public static final class Result<V> {
        public final V value;
        public final boolean sealed;
    }

    /** The scheduler-owned resume handle a Source completes. */
    public interface Waiter<V> {
        void complete(Result<V> result);
    }
}
```

**Why `Waiter` is not `CompletableFuture`.** The tempting collapse —
`cell.next(ready)` returns a `CompletionStage`, `Fiber.await` desugars to
`Fiber.external(stage)` — fails on the design's own center: a CF-based wait
is INVISIBLE to the ledger, and for the internal flavor that is circularly
fatal — `sealed` is what completes the wait, but `sealed` is computed from
waits being visible as blocked edges. Hide the edge inside a CF and neither
ever happens. The ledger-edge registration is the irreducible part; no JDK
type carries it. CF is also more surface than it saves: blocking `get`,
`obtrude`, callbacks running inline on the completing thread — each exactly
wrong in a cooperative engine and fenceable only by convention. What IS
shared with CF-land: the suspension mechanics — `external` becomes "await
with no blocked record and no `sealed` arm," one interpreter case and one
injection queue serving both flavors; and a `Waiter` may wrap a CF
internally at the external boundary.

**The external flavor — `Fiber.external` collapses into a Source.** A
mailbox IS a monotone source: the value is the log of messages received so
far, the cursor is how much a consumer has read, `ready` is "log longer than
my cursor," and an append from an I/O thread is a `grow` — the waiter
handles are thread-safe and route into the scheduler's injection queue already.
What a foreign producer changes is semantic, not mechanical: no ledger
covers it, so `sealed` is uncomputable — exactly the `scope() == null`
flavor. `Fiber.external` (its design doc predates this one) therefore
shrinks to a CompletionStage-to-Source adapter plus a timeout regime in
place of the seal. ONE SEAM MUST BE BUILT WITH IT: the exhaustion rule is
flavor-dependent. The stranded-waiter refusal is correct exactly while
every outstanding source is sealable (a seal releases its waiters, so a
leftover proves a bug); a scheduler holding waiters of a null-scope source
must instead take the parked-thread exhaustion rule — worklist empty ∧ outstanding
> 0 → the THREAD blocks on the injection queue, timeouts being the only
completion guarantee. Selecting the refusal there would kill legitimate
waits; selecting the block for internal sources would hang on real bugs.

One new Fiber node and factory:

```java
/** Suspend until {@code ready} holds of {@code source}'s value or the
 *  source's scope seals. The fiber does NOT end while blocked - every
 *  counted wrapper around it simply has not finished. */
public static <V> Fiber<Await.Result<V>> await(Source<V> source, Predicate<V> ready);
```

**Interpreter case** (FiberStep, beside the existing node branches): on
`Awaiting`, call `source.suspend(ready, scheduler.waiterFor(frame))`. Non-null:
continue immediately with the Result — no suspension happened. Null: the
frame is BLOCKED — it leaves the run queue without a `finished` tick, and
the ledger records the edge (this frame's scope is blocked at
`source.scope()`). The `Waiter` the scheduler hands out does three things,
atomically enough for the ledger (started-before-unblocked is internal to
it): mark the frame running again, set its computation to the Result, and
enqueue it — via the scheduler's injection queue, the same boundary
`fiber-external.md` §2 specifies, drained nonblockingly at the top of every
scheduler iteration.

**Driver contract** (all five): provide `waiterFor(frame)` and the injection
queue; drop blocked frames; drain injections every iteration. Single-threaded
schedulers may use a plain queue; ForkJoin needs the thread-safe version. The
exhaustion rule conjunction: a scheduler is exhausted when the worklist is empty ∧
external outstanding is zero ∧ every remaining blocked frame is dead (its
source sealed — see open question 2).

**Source implementation**: `MonotoneCell` implements `Source` — it holds
waiters (predicate + handle) instead of domain subscribers; `grow` completes
every waiter whose predicate the grown value satisfies (`sealed=false`); the
scope's seal completes the rest (`sealed=true, value=read()`). `Fixpoint`
reduces to cell + scope + seal wiring; growth no longer schedules anything
itself.

**Consumer shape** (the logic engine's tabled consumer, sketch):

```java
consume(entry, cursor):
    deliver answers[cursor..] per unification, firing k per success
    then Fiber.await(entry.cell(), v -> v.size() > cursor)
        .flatMap(r -> r.sealed && r.value.size() <= cursor
            ? mode.caughtUp(entry)          // honest end of this branch
            : consume(entry, from r.value)) // re-arm: run-once + flatMap
```

Aggregation needs no shape at all — that is the point: `findall` runs the
subgoal with a collecting continuation and flatMaps the fold after it; since
a sub-tree containing awaits does not complete until its awaits do, the fold
runs exactly when the collection is complete. Negation is the match arm
`sealed ∧ collection empty`; ifte is "first `more` wins or `sealed` picks
else".

## 4. Invariants

1. **Suspend is atomic with growth and seal.** No window where a value moves
   past a predicate between check and hold (the shipped cell's park CAS
   discipline, kept).
2. **Exactly-once completion per held waiter**; a completed waiter is no
   longer held. A leaked waiter holds the exhaustion rule open (sound, diagnosable);
   a doubled completion is unsound — same law as the ledger's counted pairs.
3. **Billed-before-unblocked**, inside the Waiter: the frame re-enters the
   running ledger before its blocked edge is removed, so a racing seal never
   reads quiescence in the gap. This invariant is machine-internal — no
   caller can violate it.
4. **`sealed` results carry the final value.** A waiter completed by seal
   never re-reads the source.
5. **Cursor freshness is sound.** A completion may carry a value fresher
   than the growth that triggered it; upward-closed predicates only reveal
   more.
6. **No fiber-awaits-fiber, no thread blocking** — unchanged absolutes. An
   await is legal exactly because it is an edge the ledger sees.

## 5. Impact estimate

**Reuse: a re-seating, not a rewrite.** Sorting the shipped primitives into
THEOREM vs PROTOCOL code: the theorem transfers near-verbatim — `WorkLedger`
entirely (already generic; only the key type changes), Scope's seal half
(`sealCascade`, `sealIfQuiescent`, `groupSeal` with the two-phase
monotone-counter snapshot — the hardest-won ~150 lines) untouched, the
cell's park-vs-growth atomicity discipline kept with waiters in place of
subscribers, `FiberStep`'s frame/ks/dispatch machinery kept whole plus one
case. What is discarded is precisely the protocol code — respawn, the feed
wiring, the defunctionalized re-entry — the compensations themselves. Of
~600 lines in `primitives`, roughly 400 survive near-verbatim, 150 die, 100
are new.

| area | change | size | risk |
|---|---|---|---|
| `Fiber` | node + factory + `Await`/`Source` types | ~100 new | low |
| `FiberStep` | Awaiting case, ledger transitions, injection drain, exhaustion rule | ~120 | **high** — the core interpreter |
| five drivers | waiterFor + injection queue + drop/requeue | ~20 each; ForkJoin ~80 | medium; ForkJoin high (cross-thread requeue) |
| `Scope`/`WorkLedger` | transitions absorbed into interpreter; respawn/awaitSeal/enclose/`Fiber.scoped`/ScopeRestore deleted | net −150 | medium — the seal theorem relocates, tests pin it |
| `MonotoneCell` | implements Source, waiter list replaces subscriber list | ~60 rework | medium |
| `Fixpoint` | grow completes waiters; feed/respawn scheduling deleted | −80/+40 | medium |
| logic consume path | await loop; parkWhenCaughtUp and feed wiring deleted; Registration slims or dissolves (gate 1) | ~200 changed | **high** — stress suite + tabling guard tests are the net |
| `weight/Closed` | `sealed()` hook deleted; solve moves to first sealed-woken `caughtUp`; stash unchanged | ~80 changed | medium — the star guard tests pin it |
| aggregate | workarounds deleted; three pins flip 0→2/3/3 | ~50, mostly deletion | low |
| tests | await unit tests, one-shot-equivalence, scheduler-equivalence rerun, Fixpoint/Scope tests reshaped | ~300 | — |

Total ≈ +900/−450 across both repos. Comparable in line count to the ambient
scope change (#77); strictly deeper in risk, because it touches the run-queue
discipline of every driver. Phased landing:

0. the gates (§6) — done; gate 1's answer decides Registration's fate;
1. types + node + BreadthFirst + unit tests (nothing else migrated);
2. remaining drivers; scheduler-equivalence suite green;
3. consume migrates (feed path coexists until green, then deleted);
4. Closed re-seated;
5. aggregation simplified, pins flipped;
6. deletions (respawn, enclose, scoped, ScopeRestore) + the Scope rename
   (`Account`? `Production`? — decided here, once it is only a scope).

## 6. The gates, worked

**1. Closed tabling's emit targets — SETTLED: self-service, and the hook
dies.** Reading `Closed` end to end dissolves the question. What its seal
hook actually does with drained readers: stash the top-level non-fragment
ones as REPLAY RECIPES (k, pkg, argsTerm — replayed from index 0 with solved
values), trigger the joint solve of the closure, release stashes. Under
await none of that needs readers handed to a hook: a reader woken with
`sealed` is a LIVE frame holding its own k/pkg/args — it finishes consuming
(invariant 4 hands it the final answers) and reports itself to the mode via
`caughtUp`, whose stash-or-replay logic already exists verbatim
(`Life.caughtUp`). The joint solve moves from the seal hook to the first
sealed-woken reader's `caughtUp` — sound on both counts it needs:
SEALED ⟹ SOLVABLE holds because the group walk marks every member before
completing any waiter (the shipped ordering, kept); and the lazy window
where a coated reader consumes a sealed-but-unsolved entry is already
covered by Closed's own documented agreement — an edge to a solved entry
folds to exactly the inline value, so recording the edge is equally correct.
An entry sealed with no top-level readers never solves — correct, nothing
wanted values.

Two consequences. First, the `Registration` that survives is Closed's stash
entry — and it is legitimately data, not a suspended task: a fragment chain
honestly ENDS (its answers arrive via the replayed valued twin, new work
spawned at solve time, recorded to the live reader driving it). The
defunctionalized-continuation role dies; the replay-recipe role, which was
always the honest half, stays — likely as a Closed-internal record. Second,
`onSealed` loses its last consumer and goes: the cascade completes waiters,
nothing else.

**2. End-of-scheduler — PROVED (sketch), plus a cheap runtime assert.** Claim:
worklist empty ∧ injections drained ∧ external outstanding zero ⟹ no live
blocked frame remains. Sketch: blocked edges partition scopes into
components; a cascade walk from any member inspects its whole component
(edges define membership — a blocked edge "out of" a component contradicts
its definition). Every frame completion ticks `finished` then attempts the
cascade as its own tail, so consider a component's LAST finish event: at
that moment all member counters are drained (externals appear as unfinished
counted work, so a pending external keeps its component out of scope here —
the external exhaustion rule covers it), and every member's blocked frame is blocked
home or within the component — so the singleton rule or the group walk seals
it, completing its waiters, which re-queues frames — contradicting "last"
unless the completions are the scheduler's next work. Parallel schedulers race
last-finishes; the shipped two-phase snapshot and per-member CAS arbitrate,
unchanged. One PRECONDITION surfaces, and it is load-bearing: `drained()`
requires `started > 0` — deliberately, else a reader parking before its
master spawns would seal an empty scope — so a scope that never
receives work strands its waiters forever. That is a domain obligation
(tabling satisfies it by construction: entry creation and master detach are
one code path), and the scheduler's exhaustion rule turns it into a loud check: on
worklist-empty, assert every remaining blocked frame's source is sealed;
a violation names the never-started scope. Assert, not sweep.

**3. One-shot equivalence — reduced to a pin list.** (a) The
scheduler-equivalence suite reruns across all five drivers, unchanged.
(b) Exactly-once delivery: producers racing a consumer's re-arm loop —
every answer delivered once per consumer, none dropped, none doubled when
one wake carries two growths (the cursor loop absorbs the fresher value).
(c) The seal boundary: growth and seal racing a suspend — the `sealed`
result carries the final value, the consumer's tail-consume delivers what
slipped in (the shipped re-read dance, now an invariant). (d) Fairness: a
re-queued frame is stepped within one rotation of a fair scheduler, infinite
neighbors notwithstanding — same law `fiber-external.md` states for
injected continuations.

With gate 1 settled by analysis, the migration is DIRECT (no half-step);
the phase list in §5 stands, with phase 0 done — this section is its
record.

## 7. Lineage — what this replaces, and why

*(Historical. Everything below describes the pre-await engine; nothing here
is needed to understand §1–§6.)*

The shipped tabling engine could not represent BLOCKED, and compensated. A
consumer out of answers stored its continuation as data (`Registration` — a
defunctionalized closure: `(k, pkg, argsTerm, cursor)` plus `consume` as the
apply function) and returned done — control returned, reading as completion.
One logical task oscillated across three ledger representations (ambient
frame ticks; a manual blocked-map entry; a manual counted wrapper at respawn)
and two continuation representations (live stack; Registration), every
transition a hand-called protocol with documented ordering invariants
(blocked-before-park, started-before-unblocked). §4's invariant 3 is that
protocol, made a machine fact.

The visible wound was aggregation: `findall` over a cold tabled goal folded
after its sub-tree "completed" — which the park lie made spuriously true —
deterministically collecting nothing (`count=0`, negation's answer, from an
ledger artifact; pinned in `AggregateTablingPinTest`). The queued fixes —
fold-on-seal hooks, an enclosing scope per findall, a run-vs-drained
completion split (`Cont.drained`) — were all second completion signals routed
around the lie. Honest completion makes them corollaries: the fold is
ordinary sequencing (§3), and run vs drained collapses because there is one
completion and it is honest.

Also dissolved: the injected `feed` (growth completes waiters instead of
re-entering domain code), `respawn` and the public `blocked`/`unblocked`
verbs, the seal-time re-read dance (invariant 4 answers it), and
`Fiber.scoped`/`ScopeRestore` (zero production callers at time of writing;
extent semantics were never the used part of scoping — frame inheritance and
`detachTo` were). Recursion through aggregation, which yielded a wrong 0,
becomes an explicit cycle in the blocked graph — the stratification guard is
a self-edge check there, and wrong-answer becomes loud refusal, fulfilling
`fiber-external.md`'s forbidden third row with a detector instead of a
doctrine.

## 8. What this deliberately is not

Not a general fiber-awaits-fiber (a cycle of those is a deadlock, not a
fixpoint; still banned), not thread blocking (still never), and not a new
theorem — the seal rule, cascade, and group walk transfer verbatim from the
shipped Scope. One node, two flavors (monotone source with `sealed`; external
source with timeout), one wait-for graph, one detector.

**Not a cross-engine bus: one scheduler per Source.** Two engines
sharing a source would get correct wake ROUTING (a waiter handle is bound
to the scheduler that minted it, whichever thread completes it) and safe
BILLING (the ledger doors are monitor-guarded, already exercised
cross-thread by ForkJoin). What breaks is the ENDGAME: the strand check
reasons per-scheduler — "my worklist is empty and no task of mine can complete
these waiters, so they are dead" — while the theorem is global. Engine A
exhausted while engine B still grows the shared source is a FALSE strand,
refused loudly. Nothing in production shares a source across engines (the
table is per-solve, aggregation runs inside the same fiber tree,
solveParallel isolates solves), and the violation mode is a thrown refusal
naming the source — loud, never silent — so the boundary is a documented
contract, not code. When a real consumer arrives (standing solves,
cross-solve persistence), the fix is engine-aware exhaustion rules — "block, don't
refuse, while any foreign engine can still grow this source" — which
requires source-side engine registration; design it then, with the
consumer in hand.
