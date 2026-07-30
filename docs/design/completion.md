# Completion — how the engine knows a workforce is finished

STATUS: AS BUILT (July 2026, branch `emit`). This doc is the REASONING
behind the completion machinery: why the seal rule is what it is, how the
group seal builds a virtual scope, and why the whole thing needs no locks
beyond leaf monitors. `await.md` is the waiting model, `emit.md` the
production discipline; the class-by-class surface is javadoc's job. Logic's
`table-completion.md` keeps the domain theorem (call events, the coat, the
two-edge graph); this is the substrate it runs on.

## 1. The question, and why it is hard

A consumer awaits a channel that may never grow again. Somebody must
eventually tell it "there is no more" — the negative completion. No
producer can: whether the channel can ever grow again is a GLOBAL property
of everything still running or parked. So the runtime must compute it.

The computation is termination detection, and it is hard for one reason:
the answer must be decided at a point in time, but the world it describes
is concurrent. Work spawns work; parked work wakes. Any rule that reads
"nothing is happening" must argue that nothing CAN happen — not just that
nothing was happening when it looked.

The engine's answer has three layers, each solving the failure mode of the
one below:

1. **counters** — is any member running?
2. **the singleton seal rule** — counters plus parked members, one scope;
3. **the group seal** — rings of scopes that are each quiescent but 
    have work awaiting others: a VIRTUAL SCOPE assembled at detection time.

## 2. The ledger: running work as two monotone counters

Every scope carries a `WorkLedger`: `started` and `finished`, both
monotone, plus a map of blocked members to the place each waits at.

The counters are Dijkstra–Scholten counting: every unit of work passes
through exactly one started/finished pair, and `drained()` ⟺
`started > 0 ∧ finished == started`. The exactly-once law is the whole
soundness story at this layer — a leaked pair means the seal never fires
(sound, useless); a doubled pair means it fires early (unsound). That is
why billing is the INTERPRETER'S, not a calling convention: a frame is
billed in its constructor (no interval where it exists unbilled), its
`finished()` runs as its own final continuation, and fork children are
minted under the forking frame's still-open pair — the open pair is a
SHIELD that holds the counters above zero across every spawn, so no
quiescence read can land in the middle.

The `started > 0` conjunct is load-bearing: a scope nobody ever claimed
must not read as drained, or a consumer parking before the master's claim
would seal an empty table. Its dual is deliberate: a never-claimed scope
never seals, and its waiters are named loudly at exhaustion.

Counters alone cannot answer the question, because a drained scope may
still have PARKED members — and parked work is exactly the work that a
future event might turn back into running work.

## 3. The singleton seal rule: why "drained + every record home" is enough

A parked member is recorded as a blocked edge: *this sleeper waits at that
scope's channel*. The seal rule:

> Seal when the counters are drained AND every blocked record points HOME
> (at this scope itself).

The reasoning is a two-step exclusion, and it only works because both
steps quantify over things the ledger can see:

- To wake a home-parked waiter, the home channel must GROW (waiters'
  predicates are upward-closed; only growth or seal completes them).
- To grow the home channel, some member must be RUNNING — production is
  billed by construction (`emit.md`: the emit step verifies the emitting
  frame belongs to the channel's workforce, so an unbilled producer is
  unrepresentable).
- The counters just said nothing is running. Therefore nothing can ever
  wake the home records: they are dead, the scope's world is finished, and
  sealing them with the final value is the honest EOF.

Note what carried the proof: the emit discipline. If anything outside the
ledger could grow the channel — the old public `grow` — step two fails and
the seal is a guess. The production rules and the completion rules are one
design; each is sound only with the other.

A record at a FOREIGN scope breaks the argument at step two: waking it
needs growth THERE, and this scope's counters say nothing about that
workforce. So the singleton rule defers — correctly, because the foreign
workforce may still be running. The interesting case is when it defers
forever for no good reason.

## 4. The group seal: a virtual scope, assembled at detection time

Two scopes, each drained, each with one member parked at the other. Each
singleton rule defers on the foreign record; nothing is running anywhere;
nothing will ever change. This is tabling's peer ring (mutually recursive
tabled calls reading each other's tables), and it is not an error — it is
a FIXPOINT that nobody local can prove.

The insight: the singleton rule is already correct — it is merely being
applied to the wrong scope. If the ring were ONE scope, every one of those
records would point home and the rule would fire. So build that scope:

- **Nodes**: walk outward from a drained scope along its outgoing sleeper
  edges — each blocked record's place names the next scope. Every reached
  scope joins the MEMBERSHIP.
- **Merged ledger**: the virtual scope's counters are the sum of the
  members' (drained ⟺ every member drained), its blocked map the union.
- **Home, redefined**: an edge between members is now an internal edge of
  the virtual scope. The walk closes the set under edges precisely so
  that no edge leaves it — a record pointing out of the collected set
  would name a scope the walk would have collected.

Then apply the singleton rule to the merge, verbatim: all members drained,
all records home (internal) ⟹ nothing can wake anything ⟹ seal every
member. That is the whole group seal — the ring rule is the singleton rule
plus a membership construction (rationale and the SLG-completion lineage:
logic's `group-seal.md`).

In code the walk is the ONLY rule: the singleton seal is the walk that
collects nobody but its start — its home records are internal edges of a
membership of one. §3 is the explanation's ladder, not a separate
mechanism; a `drained()` guard in front keeps the common not-drained case
(every frame finish) to one monitor read.

The walk ABORTS rather than waits, and every abort names the reason the
virtual scope failed to close:

- a member that is not drained — running work exists; that member's own
  eventual `finished()` will retry;
- an edge to an already-SEALED place — a resume in flight (the sealed
  channel is completing its waiters right now); the resumed frame bills
  its own home, an unsealed member, and its `finished()` retries.

This is why there is no retry machinery and no periodic sweep: EVERY abort
is owned by a future `finished()`, and every `finished()` runs the seal
attempt as its own continuation. Completion detection rides completions.

## 5. The atomicity trick: monotone counters instead of locks

The virtual scope is assembled from per-member reads without ever holding
two ledger monitors at once — so what makes the assembled picture a
consistent snapshot rather than a smear of different instants?

One idea, used twice: **two equal reads of a monotone counter bracket an
interval in which the counter did not move.**

- **In the group walk**: each member contributes one atomic snapshot
  (drained-ness, `started`, blocked places — one monitor hold, or the
  interleavings hide exactly the edge that should abort the walk). After
  the walk, RE-VERIFY: re-read every member's `started`. Equal ⟹ no spawn
  landed anywhere in the membership between first and second read ⟹ there
  was a real instant at which the whole virtual scope was quiescent.
  `started` alone suffices because it is the only door INTO the roster —
  finishing only helps quiescence, and a resume bills `started` before its
  record is removed (§6).
- **In the ForkJoin endgame**: the strand check ("only parked frames
  remain, and nothing can wake them") is the same shape — a one-shot read
  of `pending == outstanding` races wake-storms and fired falsely under
  load; the shipped check demands two consecutive observations with an
  unmoved operation-epoch between them. Same theorem, different counter.

After the walk verifies, members are MARKED sealed one CAS at a time, all
marks before any waiter completes — so the first frame to wake reads the
whole ring as sealed (SEALED ⟹ SOLVABLE, which logic's joint solve relies
on). Racing group seals arbitrate per member by the CAS; losing a member
just means someone else already proved the same fact.

## 6. Billing at the boundaries: the invariants as protections

Every subtle ordering in the interpreter protects one thing: the meaning
of the counters at the instant a seal rule reads them.

- **The record lands before the pair closes** (parking): between a frame
  deciding to park and its `finished()` landing, the blocked record is
  already in place — so a seal attempt in that window sees the waiter, not
  a spuriously drained scope. The reverse order seals under live work.
- **`started` before `unblocked`** (resuming): the frame re-enters the
  running counters before its blocked record disappears — so a seal
  attempt in that window over-counts (defers, sound) instead of
  under-counting (seals under a waking frame, unsound). Both orderings
  live inside single interpreter/handle methods; no caller can misorder
  them.
- **The park closes AFTER the channel's offer**: a channel may complete a
  waiter synchronously inside `suspend`; the suspending frame's still-open
  pair shields the counters through that completion, so even an inline
  resume lands inside a never-drained interval.

## 7. The two park kinds: why seal-waiters are not records at all

A VALUE wait (`Fiber.await`) closes its started/finished pair and leaves a
blocked record: its seal-wake is the terminal EOF arm — a verdict about a
finished world — so the home may legitimately drain past it, and the
record exists exactly so the seal rules can judge it.

A SEAL wait (`Fiber.sealed`, exhaustion consumers) is the opposite: its
wake is a GREEN LIGHT — the waiter's continuation is arbitrary further
work of its home. The engine's encoding: THE LEDGER IS THE WORK. The
seal-waiter's pair simply stays open for the whole wait — no blocked
record, no re-billing at resume, no edge for the walk to classify. Its
home cannot drain past it, so no seal, singleton or group, can pass it by;
nestings of exhaustion resolve bottom-up as plain singleton cascades. An
earlier design classified seal-waits as a special edge kind with its own
walk rules; billing-through replaced all of it with the counters that
already existed. (The degenerate case — awaiting the seal of your own
workforce — is a wait for yourself and is refused at the step.)

## 8. The endgame: refuse, never guess

When a drive ends — the queue ran dry, or the root completed with nothing
left to run — every seal attempt that could run has already run: each rode
a `finished()`. So a frame still parked at that point is a STRAND, and the
only honest outputs are a clean end (nothing parked) or a loud refusal
naming the parked frames and their places. BOTH endings consult the held
registry (a root-completion exit that skipped it once dropped a deadlocked
seal-wait ring in silence, interleaving-dependently); the refusal
annotates places whose workforce was never claimed — provable exactly
there, because a claim that has not landed by then never will — and named
scopes and channels put the starved thing's name in the message. Re-walking at exhaustion would only re-evaluate rules whose
preconditions already failed — recovery theater masking a miscounted
ledger.

Strands have exactly three causes, and the refusal distinguishes a bug
from a contract violation: a workforce that was never claimed (`drained()`
requires `started > 0` — the domain must put creation and claim on one
code path, which `produce` does); a leaked pair (substrate bug); a channel
shared across schedulers (the one-scheduler contract: the strand check
reasons per-scheduler while the theorem is global, so a foreign engine
still growing a shared channel reads as a false strand — refused loudly,
by design, until a real cross-engine consumer motivates an engine-aware
rule). Externally-completed work (task #64) will need a fourth answer —
park the thread, trust timeouts — but no such wait exists today and the
seam for it was deliberately withdrawn (`emit.md` §6).

The parallel scheduler adds one tripwire on the happy path: completing the
root with parked frames outstanding is ALWAYS a bug (every held frame keeps
a pending unit open), so `p == 0` with a non-empty held registry refuses
rather than returning a partial fixpoint.

## 9. Where it lives

`Scope` (the seal rules, the claim CAS, the onSeal actions), `WorkLedger`
(counters + blocked map + the one-hold snapshot), `Channel` (growth wakes,
seal translates to EOF), `ResumeHandle` (the two billing doors), `Frame`
(every transition the ledger counts), `AwaitBoundary` + the drivers (held
registry, injection queue, strand refusal), `ForkJoinScheduler` (the
epoch-stabilized strand check) — all in `fibers.interpreter` and
`fibers.schedulers`. The javadocs carry the per-method contracts; this doc
is why those contracts add up to a theorem.

## 10. Reading order

`await.md` — the waiting model and its lineage. `emit.md` — the production
discipline whose membership guarantee §3 leans on. Logic's
`group-seal.md` — the ring rule against the SLG-completion literature.
Logic's `table-completion.md` — the domain theorem tabling builds on this
substrate.
