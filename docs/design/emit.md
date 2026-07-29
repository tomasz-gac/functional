# emit — production as an effect; Scope and Channel unfused

**STATUS: DESIGN (July 2026, from conversations with Tom). Not built. Written
on branch `suspension-simplified`, where its prerequisites already landed
(always-park `Source.suspend`, fork completing immediately) and where the four
exhaustion consumers are consequently broken — §7 records that state honestly.
Read `await.md` for the suspend/wake contract this doc builds on; nothing
here changes it.**

---

## 1. The three failures this answers

Three independent findings from the fork/suspension work converge on one
missing concept.

**fork.flatMap was a conditional guarantee.** Code in `Aggregate`, `Conda`,
`Condu` and `Trace` runs a sub-search fiber and flatMaps after it, reading
completion as "the sub-search is exhausted." Under the fork countdown that
held — but only for suspension-free subtrees, where a child's first yield is
its completion and the property composes transitively. The first suspension
anywhere in the subtree silently weakens "exhausted" to "gone quiet for now."
A guarantee conditional on a global non-property — *nothing in this subtree
suspends* — is a trap: whoever adds the first suspension breaks contracts
they have never heard of. Fork now completes immediately (it is a control
scatter; its completion carries nothing), which turned the conditional lie
into an unconditional and visible one: the four consumers fold empty answer
sets. They need the honest primitive this document specifies.

**drained needed a Scope and could only buy one inside a dead cell.** The
exhaustion those consumers actually need is quiescence of a workforce — a
`Scope` sealing. But Scope is fused into `MonotoneCell`: the only way to
obtain a workforce is to mint a value cell nobody will ever grow, and the
only way to await its seal is a value-await with a never-true predicate — a
control question asked on the value channel.

**detachTo and grow are uncorrelated.** `detachTo(A, fiber)` bills the fiber
to A's workforce; nothing stops the fiber calling `B.grow(delta)`. That
producer is invisible to B's ledger, so B can seal while it still runs. The
failure is racy: a grow landing before B's seal is silently absorbed, after
it throws `grow on a sealed source`. Scheduling-dependent soundness. Tabling
maintains the discipline today by construction; nothing enforces it.

One diagnosis covers all three: **the runtime has channels (values that grow
and complete) and workforces (fibers whose quiescence completes them), fused
into one object and bridged by an unenforced convention.** Unfuse them, and
make production an act that cannot disagree with membership.

## 2. The model

- **`Scope`** — a workforce: fibers billed to it (started/finished counters,
  blocked records), a seal (CAS'd once, upward-closed), the seal cascade and
  group seal exactly as in `completion.md`. First-class and value-free:
  mintable bare, no cell required.

- **`MonotoneCell<V>`** — a channel: a lattice value, held value-waiters, and
  the name of the Scope that closes it. Constructed *against* a scope
  (`new MonotoneCell<>(initial, scope)`), not owning one. The scope keeps the
  list of channels it closes; at quiescence it seals them all (today's single
  `completeWaitersOnSeal` hook, generalized to a list).

- **`emit`** — the ONE producer primitive: an instruction in the fiber tree
  that folds a delta into the channel of the workforce the executing frame
  belongs to. It takes no channel argument; production has no addressing
  mode. `grow` leaves the public API.

- **`await(source, ready)`** — the ONE consumer primitive, unchanged from
  `await.md`: completes `more(value)` at the first satisfying growth,
  `sealed(value)` at the seal. Both arms are honest answers to a value
  question ("here is more" / "never more"). The sealed arm is EOF, and EOF
  belongs in-band on the data channel (read() returns −1 on the stream,
  Rx delivers onComplete to the subscriber): a parked reader whose cell
  seals must be completed, or it is abandoned — a permanent blocked record
  that deadlocks its owner's seal in turn. `drained` cannot replace this
  arm; it serves non-readers. The two-arm completion is also the minimal
  race primitive between "data arrived" and "data ended" — removing it
  forces a select primitive that would reinvent the same atomicity.

- **`drained(scope)`** — the ONE control-await: completes `Nothing` when the
  scope seals. The control question asked on the control object. The scope
  tracks its drain-waiters separately from any cell's value-waiters: different
  payload (`Nothing` vs `V`), different wake condition (seal-only vs
  growth-or-seal), different audience.

- **`fork`** — the control scatter, as already landed: inject children into
  the ambient scope, complete immediately, promise nothing.

The two waiting channels answer the two different questions that were
previously disguised as one: *has this value grown past my cursor?* (await)
and *is this workforce finished?* (drained).

## 3. The API

```java
public final class Scope {
	public static Scope scope();          // mint a workforce; no public methods —
}                                         // ledger and seal stay runtime-internal

public class MonotoneCell<V extends Semilattice<V>> implements Source<V> {
	public MonotoneCell(V initial, Scope closedBy);
	// suspend(ready, waiter) as in await.md; read(); Scope scope();
	// grow: package-private (the runtime's fold, the test door — §6)
}

public interface Emitter<V> {
	Fiber<Nothing> emit(V delta);         // fold delta into the ambient channel
}

// Fiber statics
static <A> Fiber<Nothing> plant(Scope into, Fiber<A> tree);       // channel-less plant, ONCE per scope
static Fiber<Nothing> drained(Scope scope);                        // Nothing at the seal
static <V> Fiber<Nothing> produceTo(MonotoneCell<V> cell,
		Function<Emitter<V>, Fiber<Nothing>> body);                // the plant, ONCE per cell's scope
```

`produceTo(cell, emit -> tree)` PLANTS `tree` as `cell.scope()`'s workforce
and hands it the cell's typed emitter. **Planting is once-only, CAS-guarded
per scope**: the second plant of a workforce throws, deterministically —
it races the first plant, never the seal (§3's membership passage). A second
produceTo on the same cell is also never *needed*: inside the tree the
emitter is already in scope, and forks inherit the workforce — "more
production" is spelled `fork`, from within. The channel-less form (`plant`)
carries the same once-CAS for pure `drained` workforces. The tree is
`Fiber<Nothing>` throughout — fork, Conde, all existing control plumbing
untouched; values travel only through emits.

**Emission semantics.** An emit is an ADT node carrying the delta. The
interpreter steps it holding `frame.scope`: it verifies the frame belongs to
the emitter's channel's scope, folds the delta (the lattice join; an inert
join wakes nobody, per `await.md`), and completes `nothing()`.

**Why the shape survives the three attacks raised against it:**

- *Abort*: a producer that finds nothing emits nothing and completes.
  Failure remains what it always was in this engine — silence.
- *Many values*: emit repeatedly, anywhere in the tree; consumers wake at
  each effective fold. Production stays incremental; the recursive-feedback
  loop in tabling streams exactly as today.
- *Cross-production* (`detachTo(A, defer(() -> B.grow(1)))`): inexpressible,
  twice over. `grow` is not public, and `emit` names no target — the only
  address is ambient. The residual dynamic case is a *leaked* emitter (stored,
  run inside a foreign workforce): the interpreter's one identity check at
  the emit step — the only place production executes, reading the one fact
  it already holds — refuses it loudly and deterministically. No ThreadLocal,
  no guard bolted on.
- *Unbound emit*: lexically unaskable. There is no free-floating
  `Fiber.emit`; an emitter exists only inside a `produceTo`. (The leaked case
  is the check above.)

**Compile-time typing** rides the capability: `Emitter<V>` is typed against
the cell, so a mistyped delta is a compile error, not a fold-time surprise.

**Awaits inside a produce tree** are the engine's core loop, not a special
case: a producing frame that awaits leaves a blocked record in its
workforce's ledger, the seal defers on it exactly as `completion.md`
specifies, and the resumed frame emits on. Parking at the channel you feed
is the home-record case the seal rule already counts.

**Nested handlers.** `produceTo(a, ea -> produceTo(b, eb -> tree))` bills
`tree` to b's workforce. `eb` folds; `ea` inside the tree is REFUSED by the
membership check — and rightly so: the outer body completed at planting, so
a's workforce drains and a seals while b's tree still runs; an `ea` emit
there is the detachTo/grow race restated. The sound spelling of "one
computation feeding two channels" is two cells sharing one workforce,
planted ONCE with both emitters minted together (plant-once forbids
obtaining the second emitter by nesting):

```java
Scope s = Scope.scope();
MonotoneCell<A> a = new MonotoneCell<>(a0, s);
MonotoneCell<B> b = new MonotoneCell<>(b0, s);
produceTo(a, b, (ea, eb) -> tree)      // the multi-cell plant — YAGNI-gated:
                                       // nothing today needs two output channels
```

Both membership checks pass, both channels fold, and both seal together at
s's quiescence — the honest completion for co-produced channels. The check
refuses exactly the unsound configuration (distinct workforces) and admits
exactly the sound one (a shared workforce).

**Membership is prepaid — and plant-once is the mechanism.** The seal is a
point decision — "the workforce is drained now" — sound only if the
workforce is closed-world at that point. The workforce lifecycle under
plant-once:

- *Dormant*: a scope with no members has no seal trigger either — the seal
  cascade only fires from a member's `finished()`. Zero members, zero
  triggers: a dormant scope cannot seal. Nothing to race.
- *The plant*: exactly one, CAS-guarded. A second plant races the FIRST
  PLANT, not the seal, and the CAS resolves it deterministically — the loser
  throws, independent of scheduling.
- *After the plant*: every path onto the roster is FROM WITHIN — a running
  member forking or detaching into its own scope, safe by the open-pair
  shield (the spawner's started-unit holds the counters above zero, so the
  scope cannot read as drained mid-spawn).

The outsider — a fiber billed elsewhere joining a scope that may already
have drained, the cross-production hole's sibling — has no remaining
spelling: the only outside door is the plant, and it is spent. The TOCTOU
sliver between the singleton seal's quiescence check and its CAS still
exists in the code, but the only unshielded `started()` that could exploit
it was the outsider's, and outsiders are now refused by a CAS that never
touches the counter. The law is upgraded to a mechanism, with no lock on
the engine's hottest path. (`started()` on a sealed scope still throws, as
a backstop tripwire.)

**Prior art in this codebase**: `TableEntry.tryBecomeMaster` is exactly this
CAS — one producer-planting per entry, adopted when two masters racing
production into one cell forced it. The plant-once rule generalizes the
idiom tabling already needed; the produceTo CAS absorbs `tryBecomeMaster`
(§5).

**Nested workforces and the seal rule.** A workforce planted from within
another and awaited by `drained` is a NESTING, and nestings resolve
BOTTOM-UP by singleton cascades: the inner scope drains, seals itself,
wakes the waiter, and the outer scope seals later at true quiescence. The
group seal (group-seal.md) exists only for the genuinely unrelated peer
rings of tabling, and its ring argument quantifies over wake conditions —
so the blocked entry must carry the wake condition. In graph terms, the
walk treats the two edge kinds oppositely. A CELL-EDGE is neutralized by
inclusion: annex the target into the closure, and sealing it hands the
waiter its terminal EOF — no new work for the holder. A DRAIN-EDGE
poisons its holder wherever it points and no closure can neutralize it:
if the target is inside, the group's own seal is the waiter's wake — a
member resumes with pending work on its sealed home; if outside, that
seal may land later and wake the member just as unsoundly. So the walk
refuses on contact, with no membership check — the refusal is a deferral,
retried by the resumed member's own finished() once the target seals by
its own machinery. A drain-wait on a target that never seals strands its
holder (reported at exhaustion), exactly as a cell-wait on a dead place
does; its degenerate form — draining your own workforce — is refused at
the await. The invariant in one line: a pending drain-wait marks its
holder unfinished; unfinished members refuse their group; refusals retry
on the very wake that resolves them.

The temporal summary: READS ARE TIME-FREE by monotonicity — a late awaiter
of a sealed cell completes immediately with `sealed(finalValue)`, losing
nothing; an awaiter of a never-planted cell is stranded and named loudly at
exhaustion. WRITES ARE TIME-BOUNDED by prepaid membership. Every shape
outside those two refuses loudly. A channel reference being globally
reachable is harmless: what is global is only the right to read.

## 4. Precedents

The primitive is unusual for a threading substrate because it is not a
threading primitive — it is an effect, and every effectful tradition has it:

- **Writer monad**: `tell` accumulates into an ambient monoid; `runWriter`
  binds the accumulator. `emit`/`produceTo` is Writer with a lattice for the
  monoid, concurrency in the tree, and seal-on-quiescence for the run's end.
- **Algebraic effect handlers** (OCaml 5, Koka): `emit` is a performed
  effect, `produceTo` the nearest enclosing handler. "What if no handler?"
  is the canonical question there too — a type error under effect types,
  `Effect.Unhandled` at perform-time without them. Our lexical-capability
  answer sits between the two.
- **Kotlin `flow { emit(x) }`**: emit is a method on the builder's receiver —
  syntactically impossible outside the builder. Same for generators' `yield`.
- **Unix stdout**: a process writes to a descriptor it never opened; the
  parent bound it (`produceTo` is redirection); writing with nobody there is
  SIGPIPE — loud.

Against `grow`, `emit` differs on exactly one axis. Power over *when and
whether* to produce is identical — necessarily, since a search cannot know
its answer count before running; the per-answer act must be dynamic, and
"decide before committing" is the completion-as-value shape that abort and
multi-emission killed. Power over *where* is removed entirely. That removed
power was precisely the unsound one.

## 5. The consumers, as instances

One pattern — produce into a lattice channel, await value-or-seal — with a
lattice per construct:

- **Tabling produce**: `produceTo(entry.cell(), emit -> body)`. `addAnswer`
  dissolves into delta construction + emit: dedup is the inert join
  (`JoinMap.append`), residue entailment is lattice absorption, star-tabling's
  `capture` wraps delta construction before the emit. `tryBecomeMaster`
  dissolves too — master selection IS the plant-once CAS, through its
  try-form: tabling's second caller is a legitimate race whose loser becomes
  a consumer, so the entry path uses `tryProduceTo` (CAS-or-false) where the
  §5 constructs use `produceTo` (CAS-or-throw; a second plant there is a
  bug). Consumers are unchanged (`await` on the cell, `more`/`sealed` arms
  as today).
- **findall / count / fold** (`Aggregate`): produce the sub-search into a
  fresh channel (bag/counter/fold lattice), `drained`, read the fold. This
  is #76's aggregation-folds-on-seal, landed at the fiber layer. A
  sub-search that parks at a tabled entry keeps the scope open until the
  entry seals — findall over a tabled goal waits for the table instead of
  folding a partial set. The aggregation-probe pins will surface this
  change loudly; the #68 refusals stand.
- **Conda / Condu**: produce the head sub-search into a channel and await
  value-or-seal: commit on the value arm, move on at the seal arm. The exact
  lattice per construct (first answer vs answer set) is an implementation
  decision — §8.
- **Trace**: the exploration's box-model Exit/Fail ports fire after
  `drained` on the exploration's scope.
- **Pure exhaustion** (`drained` alone): the degenerate instance — a scope
  with no channel at all. No dumb cell.

## 6. The external door

`grow` goes package-private, which draws a deliberate boundary:

- **Tests** construct cells and grow them directly from the test thread —
  package-private access, outside any drive, unchanged in practice.
- **External sources** (`fiber-external.md`; task #64) are completed from
  outside the fiber world and have no workforce: nothing about them changes,
  and the emit check does not apply (no ambient scope ever runs them).
- **In-drive growth from outside the closing workforce** — the racy hole —
  is exactly what no longer has a public spelling.

## 7. Where the branch stands (honesty section)

`suspension-simplified` currently carries, committed: always-park
(`b61a649`), the JoinMap growth journal (`c317a8f`, consume-side unwired,
pending the parked streaming-tabling decision), and fork-completes-immediately
with the countdown machinery deleted (uncommitted at the time of writing,
functional suite green at 197). The logic suite is red — 19 failures + 2
errors in `SortingTest`/`AggregateTest`/`TraceCompositionTest` (the four
consumers reading fork completion as exhaustion) plus the parked
`WeightedTablingTest` min-plus failure. That red is this document's §1; it is
the correct pressure and stays until §5 lands. Nothing is pushed or merged.

## 8. Open decisions for the review

1. **Naming**: `emit`/`Emitter`/`produceTo` — or `tell`, or `yield`-adjacent
   names are taken/keyworded; propose better if these grate.
2. **Conda/Condu lattices**: first-answer only vs committed-clause answer
   set — semantics of committed choice under the new primitive, decided
   against the existing tests.
3. **Multiple channels per scope** are expressible and sound (the emitter
   names the fold target; the check verifies only membership) — two cells
   sharing one workforce, planted once with both emitters minted together,
   is the spelling for co-produced channels, sealing together at the shared
   quiescence. The multi-cell plant overload is YAGNI-gated; §3's nesting
   passage has the trace.
4. **Sequencing**: (a) Scope unfused + `drained` + the four consumers
   rewired (turns both suites green); (b) `produceTo`/`emit` + tabling's
   produce path migrated + `grow` withdrawn from the public API; (c) the
   leaked-emitter check. Each stage a commit checkpoint with both suites and
   the stress drill. (a) before (b) so green is restored before the deeper
   cut — or (a)+(b) in one motion if you prefer fewer intermediate shapes.
