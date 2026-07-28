# Completion — awaiting and sealing, as coded

STATUS: AS BUILT (July 2026) for the fibers runtime and await phases 1–2
(`fibers`, `fibers/schedulers`); §8 names the two phase-3 obligations. This
doc LIFTS the completion story out of tabling: logic's `table-completion.md`
keeps the domain theorem — which call events exist, the coat, the two-edge
graph — and this doc is the substrate that theorem runs on. Every term here
is a code name. The runtime lives in `fibers.schedulers` (Scope,
WorkLedger, MonotoneCell package-private; Fixpoint the public entry);
`fibers.primitives` keeps only the JoinMap value. Read with those open.

## 1. The parts, by class

**`WorkLedger<S, P>`** (package-private) — one scope's outstanding work, two halves under the
ledger's monitor:
- the counters `started`/`finished`, both monotone, incremented by
  `started()`/`finished()`;
- the `blocked` map (`HashMap<S, P>`) — each blocked piece mapped to the
  place it waits at — written by `blocked(sleeper, at)` and
  `unblocked(sleeper)`.

Reads: `drained()` ⟺ `started > 0 ∧ finished == started` (the
`started > 0` conjunct is load-bearing — §7.4); `quiescent(cannotWake)` ⟺
`drained()` ∧ every place in `blocked` passes the predicate;
`startedCount()` (monotone — two equal reads bracket an interval with no
`started()` in it); `blockedAt()`; `drainedSnapshot()` — drained-ness,
`started`, and the blocked places read in ONE monitor hold, null when not
drained (§7.3). `counted(work, onFinished)` is the pairing discipline for
non-frame work: `started()` synchronously at wrap time, then
`work.flatMap(finished(); onFinished.get())` — the `finished()` and the
caller's hook run as the work's own continuation. Exactly-once law (from
the class javadoc): a leaked `started`/`finished` pair never completes —
the seal never fires, sound but useless; a doubled one completes early — a
seal under live work, unsound.

**`MonotoneCell<V extends Semilattice<V>, S>`** (package-private) — the value, under the
cell's monitor:
- `grow(delta)`: `value.combine(delta)`; a result equal to `value` returns
  `Option.none()` — the delta was absorbed, strict ascent is a law of the
  `Semilattice`, not caller discipline; otherwise swap the value, clear
  `parked`, return the drained subscribers — whoever grew the value wakes
  them.
- `park(subscriber, caughtUp)`: re-tests `caughtUp` under the monitor;
  false refuses — the park/grow race resolves toward reading, never toward
  sleeping past data; true appends to `parked`.
- `read()` (the value is persistent — safe to use after the monitor is
  released), `drainParked()`, `parkedCount()`.

**The token (PHASE-3 TARGET: the `Source` itself).** As built through
phase 2, `WorkScope` is a zero-method interface the `Fiber` ADT mentions
(`detachTo`, `Source.scope()`, `Fiber.Detached`) and the interpreter
downcasts (`FiberStep.Frame.own`, throwing on foreign implementations).
Phase 3 deletes it: A SOURCE IS A MONOTONE VALUE PLUS THE WORKFORCE
PRODUCING IT, so the Source serves as the token — `detachTo(source,
work)` records work as producing into the source, blocked records hold
the source a frame waits at, `Source.scope()` disappears, and a foreign
`Source` implementation (no ledger the runtime owns) is AUTOMATICALLY the
external flavor instead of a `scope() == null` convention. Adopted
constraint (Tom, July 2026): one source per workforce — a production
publishing two values models them as one source of a product value; the
two-cells-one-workforce shape is deliberately inexpressible. The class
that embodies the definition is `MonotoneCell` (Tom, July 2026): the cell
implements `Source`, owns its `Scope` privately, and is the one public
runtime object — the interpreter resolves the token via instanceof
MonotoneCell. `Fixpoint` is an interim delegate over the cell for the
data-subscriber path and is DELETED when tabling migrates to Fiber.await;
Scope then slims to ledger + sealed + sealCascade + groupSeal (ownerOf,
drainOnSeal, awaitingSeal, respawn, onSealed all die — frame-waiters are
resumed by the seal, so owner rechecks ride their finished() calls), and
MonotoneCell<V, S> loses its S parameter.

**`Scope<S> implements WorkScope`** (package-private, in
`fibers.schedulers` — reachable only through `Fixpoint` and the
interpreter):
- fields: `ledger` (a `WorkLedger<Object, WorkScope>` — blocked keys are
  opaque: domain subscribers S or `FiberStep.Frame`s; places are
  `WorkScope`), `sealed` (`AtomicBoolean`), `ownerOf : S → Scope<S>` (the
  one domain input: which scope a subscriber's work belongs to; null =
  top-level, recorded nowhere, gating nothing), `awaitingSeal`
  (`ArrayList<S>` of subscribers waiting only for the seal), `onSealed`
  (hook, inert by default), `drainOnSeal` (supplier of the dead — defaults
  to draining `awaitingSeal`; `Fixpoint` redirects it to
  `cell::drainParked`).
- the ledger methods, each a one-line delegation to the same-named
  `WorkLedger` method: `started()`, `finished()` (plus
  `Fiber.defer(this::sealCascade)` as the returned fiber),
  `blocked(sleeper, at)`, `unblocked(sleeper)`; `isSealed()` →
  `sealed.get()`. Called by the interpreter and by `Fixpoint` — nothing
  else can reach them.
- own methods: `awaitSeal(subscriber)`; `respawn(sleeper, work)` (§3);
  `onSealed(hook)`; `seal()` (manual — tests and external certificates; no
  cascade, no drain); `sealCascade()` (§4).

**`Fixpoint<V extends Semilattice<V>, S>`** — a `MonotoneCell` and a
`Scope`, with the mechanism in its shape: subscribers are parked
continuations, and the constructor-injected
`feed : (S, V) → Fiber<Nothing>` is how growth pushes the grown value into
them. The constructor maps `ownerOf : S → Fixpoint<?, S>` onto the
underlying scopes and redirects `scope.drainOnSeal(cell::drainParked)`.
Methods: `read()`, `grow(delta)` (§3), `master(work)` =
`Fiber.detachTo(scope, work)`, `park(subscriber, caughtUp)` (§3), and the
pass-throughs `onSealed`, `isSealed`, `seal`, `parkedCount`.

**`Source<V>` / `Await` / `Fiber.Awaiting`** (await phases 1–2):
`Source.scope()` names the `WorkScope` whose seal is the source's negative
completion — the place recorded for every waiter held here; null for
sources with no seal (externally completed work, timeout regime).
`Source.suspend(ready, waiter)` atomically returns an `Await.Result` —
`more(value)` or `sealed(value)`, the sealed one carrying the FINAL value
— or holds the `Await.Waiter` and returns null.
`Await.Waiter.complete(result)` is the scheduler-owned resume handle,
exactly-once; completions arrive on the scheduler's own thread today, and
the injection queue is the designated publication point when foreign-thread
completers (external sources) land. `Fiber.await(source, ready)` builds the
`Awaiting` node.

**`FiberStep`** — `Frame` = `computation` + `scope` + the `ks`
continuation deque; the `Frame(fiber, scope)` constructor calls
`scope.started()` at construction. `Effects` (the scheduler contract):
`completed`, `forked`, `detached(child, scope)`, and the await triple
`resumeHandle(owner)`, `suspending(at)`, `suspendCancelled()` — the
triple's defaults throw `UnsupportedOperationException`, so a scheduler
without await support refuses loudly.

**`AwaitBoundary<E>`** — the queue schedulers' shared await state:
`injections` (`ConcurrentLinkedQueue<E>` of resumed entries),
`outstanding` (synchronized `LinkedHashMap<E, Source<?>>` of held
entries). Methods: `resumeHandle(entry, frame, owner)`, `held(entry, at)`,
`cancelled(entry)`, `drainInto(requeue)`, `quiet()`, `refuseStranded()`.
`BreadthFirstScheduler`, `DepthFirstScheduler`, `RoundRobin`, and
`UnfairBreadthFirstScheduler` each own one; `ForkJoinScheduler` is bespoke
(§6).

## 2. The two waiting mechanisms, honestly

Two parking mechanisms coexist, both writing the same `WorkLedger`s:

- **Data subscribers (the S of `Fixpoint<V, S>`)** — tabling's
  `Registration` path. Parked by `Fixpoint.park`; woken by `grow` →
  `feed` → `Scope.respawn` — a FRESH detached fiber re-enters domain code
  from the subscriber's cursor. The fiber that parked is gone (it returned
  done); the `blocked` record is what keeps the scope from sealing under
  it.
- **Frames** — `Fiber.await`. The live `Frame` is held by a `Source`;
  `Await.Waiter.complete` resumes the SAME frame, `ks` intact, through
  `injections`.

Phase 3 migrates the first onto the second (`MonotoneCell` implements
`Source`; `feed` and `respawn` retire). Until then both are load-bearing
and this doc covers both.

## 3. Every ledger write, every path

The exactly-once law: a leaked `started`/`finished` pair never completes
(sound, useless — the seal just never fires); a doubled `finished`
completes early (unsound — a seal under live work). Every write path, as
coded:

| event | code path | ledger writes |
|---|---|---|
| frame constructed with a scope | `Frame(fiber, scope)` | `scope.started()` — at construction, so there is no interval in which the frame exists but the ledger does not know it |
| frame completes | `FiberStep` Done case, `ks` empty, scope non-null | `frame.scope = null`, then `computation = owner.finished().map(...)` — the `finished()` AND the `sealCascade` run as the frame's own continuation, same scheduler, same fairness |
| fork | `Effects.forked` — children constructed with `current.frame.scope` | each child's `Frame` constructor calls `started()`; the parent re-enters later with its own `started`/`finished` pair still open |
| detach | `Fiber.detach(f)` — scope null, no records anywhere; `Fiber.detachTo(scope, f)` — the child frame is constructed with `scope` | child `Frame` constructor calls `started()` iff scope non-null |
| `Scope.respawn(sleeper, work)` | `ledger.counted(work, this::sealCascade)` FIRST, `unblocked(sleeper)` second, `Fiber.detach(tracked)` third | `started` at wrap time, BEFORE the `blocked` record is removed (§7.2); `finished` + `sealCascade` when the work's fiber ends, via `counted` |
| `Fixpoint.park(subscriber, caughtUp)` | `ownerScope.blocked(subscriber, scope)` FIRST, `cell.park` second | the `blocked` record lands before the subscriber is parked (§7.1). `cell.park` false ⇒ `ownerScope.unblocked(subscriber)` and `Either.left(cell.read())` — keep reading, never poll. `cell.park` true ⇒ `Either.right(Fiber.defer(ownerScope::sealCascade))` — the owner's seal attempt runs as the parking branch's continuation, because parking may have been the owner's last obstruction |
| `Fixpoint.grow(delta)` | `cell.grow` → `Option.none()` ⇒ done. Else `grown = cell.read()` (a racing later `grow` may make this fresher — sound, subscribers read by cursor); per drained subscriber: `ownerOf.apply(subscriber)` null ⇒ `Fiber.detach(feed.apply(subscriber, grown))`, else `owner.respawn(subscriber, feed.apply(subscriber, grown))`; all composed by flatMap into the returned fiber | see `respawn` row |
| frame suspends | `FiberStep` Awaiting case, in order: `owner.blocked(frame, source.scope())` → `frame.scope = null` (after `source.suspend` holds the waiter, another thread may resume the frame — nothing may touch it past the offer) → `waiter = effects.resumeHandle(owner)` → `effects.suspending(source)` (register in `outstanding`, leave the run queue NOW) → `source.suspend(ready, waiter)` | held ⇒ `effects.detached(owner.finished(), null)`: the `finished()` + `sealCascade` run as detached work — the still-open pair until it runs only defers a seal, which is sound. Immediate ⇒ `effects.suspendCancelled()`, `owner.unblocked(frame)`, scope restored, frame continues |
| frame resumes | the `Await.Waiter` (from `AwaitBoundary.resumeHandle` or `ForkJoinScheduler`'s): `owner.started()` → `owner.unblocked(frame)` → `frame.scope = owner`, `frame.computation = done(result)` → add to `injections` | `started`-before-`unblocked` is internal to the handle — no caller can misorder it (§7.2) |
| `sealCascade` processes a dead sleeper | `ownerOf.apply(sleeper)` → `owner.unblocked(sleeper)`, owner re-queued | record removal only — a dead sleeper runs nothing, so nothing is started |

## 4. The seal, exactly

**`sealIfQuiescent`** (private — the singleton rule):
1. `ledger.quiescent(at -> at == this || at.isSealed())` — counters
   drained AND every place in `blocked` is this scope itself (waking would
   need growth here; growth needs running work here; just ruled out — the
   javadoc's HOME case) or an already-sealed scope (never grows again).
   The predicate runs under the ledger monitor but reads foreign
   `isSealed()` — an `AtomicBoolean`, not a monitor (§7.5).
2. `sealed.compareAndSet(false, true)` — exactly one caller wins; a loser
   returns null.
3. `drainOnSeal.get()` — `awaitingSeal` for a bare `Scope`,
   `cell.drainParked()` for a `Fixpoint`.
4. `emits.add(onSealed.apply(drained))` — the hook's fiber is collected,
   not run inline.

**`sealCascade`** (public — every trigger funnels here):
- a queue seeded with `this`; per polled scope: `sealIfQuiescent`; on
  refusal, if the scope is unsealed AND `ledger.drained()`, the
  obstruction must be a sleeper at a foreign unsealed scope — try
  `groupSeal`.
- per dead sleeper of a successful seal: `ownerOf.apply(sleeper)`,
  `owner.unblocked(sleeper)`, owner re-queued — seals propagate BACKWARDS
  along `blocked` records, leaves first.
- the collected `emits` are flatMap-composed in collection order into ONE
  fiber, the return value — which every trigger runs as a continuation,
  so the scheduler that stepped the finishing work also steps whatever
  the seal spawned. Monitors are taken one scope at a time, never two at
  once.

**Every `sealCascade` trigger:**
1. `Scope.finished()` — every frame completion (the Done case), and the
   suspend path via `effects.detached(owner.finished(), null)`.
2. `counted`'s `onFinished` — every `respawn`ed work unit's end.
3. `Fixpoint.park`'s accepted branch —
   `Fiber.defer(ownerScope::sealCascade)`.
4. Itself, transitively, along dead-sleeper records (the queue).

`seal()` and `awaitSeal` trigger NOTHING — a manual `seal()` is a
certificate, not an event; its callers arrange their own follow-up.

**`groupSeal`** (private — the ring rule; rationale in `group-seal.md`):
1. WALK: close `{start}` under the places in each member's `blocked` map.
   Per member ONE `drainedSnapshot()`; null (member not drained — running
   work) aborts the walk, and that member's own `finished` retries later.
   A place that is unsealed and not a `Scope` also aborts — a foreign
   `WorkScope` implementor cannot join the merge.
2. RE-VERIFY: after the walk, every member's `startedCount()` must equal
   its snapshot value — two equal reads of a monotone counter bracket an
   interval with no `started()`, a consistent cross-member snapshot with
   no nested monitors.
3. MARK ALL, THEN ANNOUNCE ALL: `sealed.compareAndSet` per member (a lost
   race skips that member's drain), all marks before any
   `drainOnSeal`/`onSealed` — at each hook the whole group already reads
   sealed. Logic's `Closed.Life.sealed` throws if this ordering ever
   breaks.
4. The concatenated dead return to the `sealCascade` loop for owner
   rechecks.

**What a seal drains, today vs phase 3.** Today `drainOnSeal` hands the
drained DATA subscribers to `onSealed` — and that is ALL a seal does: no
shipped `Source` is owned by a `Fixpoint`, so no seal completes a held
frame yet (test sources call their waiters by hand). Phase 3 makes
`MonotoneCell` implement `Source`; the seal then also completes every held
waiter with `Await.Result.sealed(read())` — under the §8 obligations.

## 5. The await path, exactly

`FiberStep.step`, the `Fiber.Awaiting` case, in code order:

    owner = frame.scope
    if (owner != null) owner.blocked(frame, source.scope())     // record BEFORE finished() can run
    frame.scope = null                                          // after the offer, another thread may
                                                                // resume the frame
    waiter = effects.resumeHandle(owner)
    effects.suspending(source)                                  // outstanding.put + leave the run queue NOW
    immediate = source.suspend(ready, waiter)
    if (immediate != null):
        effects.suspendCancelled(); owner.unblocked(frame); frame.scope = owner
        frame.computation = done(immediate); return true        // still runnable
    if (owner != null) effects.detached(owner.finished(), null) // finished() + sealCascade as detached work
    return false                                                // the frame is the Source's now

Scheduler duties (`Effects`): `suspending` removes the current entry from
the run queue and registers it — `AwaitBoundary.held`, or
`ForkJoinScheduler`'s `pending.incrementAndGet()` then `outstanding.put`
(that order: §6); `suspendCancelled` undoes both; `resumeHandle` binds the
`Await.Waiter` to the current entry. `injections` are drained at the TOP
of every `step()` via `drainInto`, so a resumed frame competes like any
other entry. `ForkJoinScheduler` resumes via `pool.execute` — a `fork()`
from a foreign thread would target the wrong pool.

## 6. Scheduler exhaustion, every case

What does it mean for `run()` to return true? Frame states: in the run
queue; in `injections`; in `outstanding` (held by a `Source` — in no
queue); completed.

**Queue schedulers** (`BreadthFirstScheduler`, `DepthFirstScheduler`,
`RoundRobin`, `UnfairBreadthFirstScheduler`): `run(iterations, sink)`
returns true ⟺ run queue empty ∧ `awaits.quiet()`; `step()` on an empty
run queue (after `drainInto`) calls `awaits.refuseStranded()` and returns
true.

**`ForkJoinScheduler`**: `pending` units instead of queues. A held frame
keeps one unit open (`suspending`: `pending.incrementAndGet()` BEFORE
`outstanding.put`, so a concurrent check may only read `pending` above
`outstanding.size()`, never a false equality). The scheduler's own private
`taskFinished()`: `p == 0` ⇒ `result.complete(rootValue)`;
`p > 0 ∧ p == outstanding.size()` ⇒ every remaining unit is a held frame
and no task is left that could complete one ⇒
`result.completeExceptionally(IllegalStateException)`. The `Await.Waiter`
orders `outstanding.remove` → `pending.incrementAndGet` + `pool.execute`
→ `taskFinished()`, so a resume in flight also never produces the
equality.

**At exhaustion, `outstanding` contains:**

| case | meaning | behavior |
|---|---|---|
| nothing | clean end | `run()` returns true |
| an entry whose source's scope can still seal | IMPOSSIBLE — induction below | — |
| an entry at a scope with `started == 0` | the scope never received work — `drained()` is false forever (§7.4) | `refuseStranded` throws, naming the source |
| an entry at a scope with a leaked `counted` pair | substrate bug | `refuseStranded` throws |
| an entry at a foreign Source (no ledger the runtime owns) | the external flavor (#64): no seal, by construction | needs a DIFFERENT exhaustion rule — the thread parks on `injections`, timeouts complete; UNBUILT, and today's throw is correct because no such wait exists in production yet |
| an entry at a source of another scheduler | the one-scheduler contract (`Source` javadoc, `await.md` §8): completers exist, but not here | `refuseStranded` throws — loud, never silent |

**The induction (why the second row is impossible).** The `blocked`
records partition scopes into connected components; a `sealCascade` from
any member reaches its whole component (a record pointing out of a
component contradicts the definition). Every completion runs `finished()`
then `sealCascade` as its own continuation — inside the scheduler, before
emptiness is observable. At a component's LAST `finished()`: all member
ledgers are drained, every held frame's record points within the component
— `sealIfQuiescent` or `groupSeal` seals it, completing its waiters, whose
`injections` contradict "the run queue is empty." Parallel schedulers race
last-`finished()`s; the two-phase `startedCount` re-verify and the
per-member `compareAndSet` arbitrate. A pending `Fiber.external`-style
wait keeps its component's counters undrained (the fiber does not end), so
such components never reach this question until the completion or the
timeout arrives.

**Why `refuseStranded` throws instead of retrying:** every `sealCascade`
that could run has run — each rode a `finished()`. Re-walking at
exhaustion could only re-evaluate rules whose preconditions already
failed, masking a miscounted ledger as recovery. The throw names the
sources.

**Root delivery is not exhaustion**: a scheduler may deliver its root
value and keep stepping detached frames; `get()` returns at exhaustion
(`ForkJoinScheduler` documents this; the queue schedulers behave the
same).

## 7. The invariants and the lock graph

1. **Record before `finished()`.** `Fixpoint.park`: `blocked` before
   `cell.park`. The Awaiting case: `blocked` before the `finished()` that
   rides `detached`. Reversed, the owner's ledger reads
   drained-with-empty-`blocked` while the waiter is in transit — it seals
   under live work.
2. **`started` before `unblocked`.** `respawn`: `counted` before
   `unblocked`. The `Await.Waiter`: `owner.started()` before
   `owner.unblocked(frame)`. Reversed, the interval between record
   removal and `started()` is a window in which `quiescent` passes.
   Over-counting for the instant only defers a seal — sound.
3. **`drainedSnapshot` is one hold.** Read separately, a racing `respawn`
   interleaves: `drained()` reads the old quiet state, the counter reads
   the post-`counted` value (so the re-verify compares it against
   itself), `blockedAt` reads post-`unblocked` — hiding the record that
   would have aborted the walk.
4. **`started > 0` inside `drained()`.** Without it, a subscriber parking
   before its scope's `master` arrives lets the scope seal EMPTY. The
   dual: a scope that never receives work never seals — its waiters
   strand, and `refuseStranded` names them. Domain rule: scope creation
   and master-spawn must be one code path (tabling's
   `Table.getOrCreateEntry` + `tryBecomeMaster` is).
5. **The lock graph.** The cell monitor and the ledger monitor are
   LEAVES — no method of either calls out while held (`quiescent`'s
   predicate reads `isSealed()`, an atomic). The `Scope` object monitor
   guards only `awaitingSeal`. `sealCascade` and `groupSeal` take one
   monitor at a time, never two ledgers at once. `AwaitBoundary` holds a
   `ConcurrentLinkedQueue` and a synchronized map, both leaves. No
   monitor is held across user code or a scheduler step. One direction +
   leaves = no cycle = no deadlock. A DISCIPLINE, not a type — re-check
   before adding any call under a monitor.
6. **`Source.suspend` atomicity.** Atomic with `grow` and `seal`, and it
   must never call `waiter.complete` synchronously inside itself.
   Violated, a wake is missed and the frame strands — a real strand,
   correctly thrown (found live: an unsynchronized test source under
   `ForkJoinScheduler`).

## 8. The two phase-3 obligations

Both at the step phase 3 builds — the seal completing held frames when
`MonotoneCell` implements `Source`:

1. **`started()` for all resumed waiters, then complete them.** A
   sealed-completion resumes a frame into its sealed arm and calls
   `owner.started()`. Until that lands, the frame's `blocked` record
   reads "at a sealed place" — `quiescent` on the OWNER passes while
   sealed-arm work is pending. The seal-completion must apply invariant 2
   across the whole drain: every resumed waiter's `started()` before any
   record can satisfy a `quiescent` predicate. (The pre-await reading —
   drained sleepers at a seal are dead forever — narrows to: never fed a
   NEW value; the sealed arm consumes the existing tail and terminates.)
2. **`grow` on a sealed source throws.** A sealed-resumed frame delivers
   its remaining answers; the continuations it fires could in principle
   reach `grow` on the sealed source. `table-completion.md`'s theorem
   says no new answer is derivable there — the cell must enforce it,
   because growing past a delivered `Await.Result.sealed(value)` would
   falsify it.

## 9. Reading order

`table-completion.md` (logic) — the domain theorem: call events, the coat,
master and sleeper edges. `group-seal.md` — the ring rule in depth.
`await.md` — the `Fiber.await` design: the model, the API, the impact
estimate, the external and cross-scheduler boundaries. This doc — the
substrate as coded.
