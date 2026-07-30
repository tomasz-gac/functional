# Fiber.await — the condition variable over a monotone value

**STATUS: AS BUILT (July 2026, branch `emit`). §1 is the model and stands;
§2–§3 describe the shipped shape; §4 the invariants as enforced. §5–§7 are
HISTORICAL — the pre-build gate analysis and the lineage of what this
replaced — kept for motivation, not semantics. `completion.md` is the
as-coded reference for the substrate this doc models; `emit.md` is the
production side.**

---

## 1. The model

A fiber may wait, in-engine, on exactly one kind of thing: a CHANNEL — a
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
whether the channel can ever grow again is a global property of everything
running or waiting. So the runtime computes it: each channel belongs to a
SCOPE (a workforce with a ledger), and when the scope's ledger proves
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
precedent: the park decision fused into one atomic call under the channel
monitor, the `Result` a frame wakes with as `await_resume`'s payload, and
single-wake re-armed by flatMap as `co_await` in a loop. The dividing line
across all of them: every industrial precedent ASSERTS completion
(`onComplete`, `countDown`, `arriveAndDeregister`) — someone must know they
are last. Computed completion over a monotone store exists only in the
research systems (LVars; SLG completion in the tabling literature, logic's
`group-seal.md`). That is why the vocabulary is reusable and the libraries
are not.

## 2. The abstraction

A recorded, suspendable task: one identity, always in exactly one state —
RUNNING, BLOCKED(at a channel or a scope), or DONE. The interpreter owns
every transition and the scope's ledger counts them; no transition is a
calling convention (`completion.md` §3 is the write-by-write table).

THE SEAL THEOREM: a scope seals when its counters are drained (every
started frame finished) and every blocked record is HOME — waking would
need growth here, growth needs running work here, just ruled out. Sealing
completes the held waiters; each resumed waiter's own `finished()` retries
its scope: the cascade. Rings of mutually-waiting scopes seal as one unit
via the group walk (logic's `group-seal.md`). A SEAL-waiter (`Fiber.sealed`)
appears in no blocked map at all — its started/finished pair stays open for
the whole wait, so its home simply is not drained (THE LEDGER IS THE WORK,
`emit.md`).

## 3. The API, as built

```java
// the consumer primitive
static <V extends Semilattice<V>> Fiber<AwaitResult<V>> await(
		Channel<V> channel, Predicate<V> ready);

/** more(value) — the channel grew past the waiter; sealed(value) — the
 *  channel's scope sealed, value FINAL, never re-read. */
@Value public class AwaitResult<V> { V value; boolean sealed; }
```

`Channel.suspend` is the interpreter's (package-private): under the channel
monitor it decides — sealed ⇒ `sealed(value)`, ready ⇒ `more(value)`, else
HOLD — and AN AWAIT ALWAYS PARKS: the "immediate" answer is a synchronous
completion of the already-parked frame through its `ResumeHandle`, re-queued
via the scheduler's injection queue, never a keep-running fast path. The
handle is the interpreter's own type — there is no `Waiter` interface and
no `Source` interface; the seam for foreign implementations was withdrawn
unexercised (`emit.md` §6), and externally-completed work (task #64) will
design its own door against a real use case.

**Why the resume handle is not `CompletableFuture`.** A CF-based wait is
INVISIBLE to the ledger, and that is circularly fatal — `sealed` is what
completes the wait, but `sealed` is computed from waits being visible as
blocked records. Hide the record inside a CF and neither ever happens. The
ledger-registration is the irreducible part; no JDK type carries it. CF is
also more surface than it saves: blocking `get`, `obtrude`, callbacks
running inline on the completing thread — each exactly wrong in a
cooperative engine and fenceable only by convention.

**The consumer shape** (the logic engine's tabled consumer, as shipped in
`Tabling.consume`):

```java
consume(entry, reader):
    deliver answers[cursor..] per unification, firing k per success
    then Fiber.await(entry.channel(), v -> v.size() > reader.nextIndex)
        .flatMap(r -> r.sealed && r.value.size() <= reader.nextIndex
            ? caughtUp(entry)               // honest end of this branch
            : consume(entry, from r.value)) // re-arm: run-once + flatMap
```

Aggregation needs no shape at all — that is the point: `Exhaustion.exhausted`
claims a fresh scope for the sub-search and awaits its seal
(`Fiber.sealed`); the fold after it runs exactly when the collection is
complete. Negation is the match arm `sealed ∧ collection empty`.

## 4. Invariants

1. **The park decision is atomic with growth and seal** under the channel
   monitor. No window where a value moves past a predicate between check
   and hold.
2. **Exactly-once completion per held waiter**; the channel removes a
   waiter under its monitor before completing it. A leaked handle strands
   the frame (loud at exhaustion); a doubled completion is unsound.
3. **Billed-before-unblocked**, inside `Scope.resumed`: the frame re-enters
   the running counters before its blocked record is removed, so a racing
   seal never reads quiescence in the gap. Machine-internal — no caller
   can violate it.
4. **`sealed` results carry the final value.** A waiter completed by seal
   never re-reads the channel.
5. **Cursor freshness is sound.** A completion may carry a value fresher
   than the growth that triggered it; upward-closed predicates only reveal
   more.
6. **No fiber-awaits-fiber, no thread blocking** — unchanged absolutes. An
   await is legal exactly because it is visible to the ledger: a value
   wait as a blocked record, a seal wait as its home's unfinished unit.

## 5. The gates, worked (HISTORICAL — pre-build analysis)

*(The build confirmed all three; kept as the record of why the migration
was judged safe. Vocabulary predates the renames.)*

**1. Closed tabling's emit targets — self-service, and the hook died.**
A reader woken with `sealed` is a LIVE frame holding its own k/pkg/args —
it finishes consuming and reports itself to the mode via `caughtUp`, whose
stash-or-replay logic already existed. The joint solve moved from the seal
hook to the first sealed-woken reader's `caughtUp` — sound because the
group walk marks every member before completing any waiter
(SEALED ⟹ SOLVABLE). `onSealed` lost its last consumer and went.

**2. End-of-scheduler — proved.** Worklist empty ∧ injections drained ⟹ no
live blocked frame remains; the component induction is now recorded in
`completion.md` §6. The load-bearing precondition — `drained()` requires
`started > 0`, so a never-claimed scope strands its waiters — became the
loud strand refusal, and the claim CAS made the domain obligation (creation
and claim in one code path) enforceable.

**3. One-shot equivalence — a pin list.** The scheduler-equivalence suite,
exactly-once delivery under racing growths, the seal boundary carrying the
final value, and fairness for re-queued frames — all pinned in the shipped
tests (`AwaitTest`, `ChannelTest`, `SchedulerEquivalenceTest`).

## 6. Lineage — what this replaced, and why (HISTORICAL)

The pre-await tabling engine could not represent BLOCKED, and compensated.
A consumer out of answers stored its continuation as data (`Registration` —
a defunctionalized closure) and returned done — control returned, reading
as completion. One logical task oscillated across three ledger
representations and two continuation representations, every transition a
hand-called protocol with documented ordering invariants. §4's invariant 3
is that protocol, made a machine fact.

The visible wound was aggregation: `findall` over a cold tabled goal folded
after its sub-tree "completed" — which the park lie made spuriously true —
deterministically collecting nothing. The queued fixes — fold-on-seal
hooks, an enclosing scope per findall, a run-vs-drained completion split —
were all second completion signals routed around the lie. Honest completion
made them corollaries: the fold is ordinary sequencing, and run vs drained
collapsed because there is one completion and it is honest.

Also dissolved: the injected `feed` (growth completes waiters instead of
re-entering domain code), `respawn` and the public `blocked`/`unblocked`
verbs, the seal-time re-read dance (invariant 4 answers it), `Fixpoint`,
`WorkScope`, `Fiber.scoped`/`ScopeRestore`, and — in the emit stage —
`detachTo`, the `Source` interface, and `Await.Waiter`. Recursion through
aggregation, which yielded a wrong 0, is an explicit cycle in the blocked
graph — wrong-answer became loud refusal.

## 7. What this deliberately is not

Not a general fiber-awaits-fiber (a cycle of those is a deadlock, not a
fixpoint; still banned), not thread blocking (still never), and not a new
theorem — the seal rule, cascade, and group walk transferred from the
pre-await Scope. One waiting mechanism, two park kinds (value wait,
billed-through seal wait), one wait-for graph, one detector.

**Not a cross-engine bus: one scheduler per channel** — the contract and
its failure mode are recorded in `completion.md` §8.
