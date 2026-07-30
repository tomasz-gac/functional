# emit — production as an effect; Scope and Channel unfused

**STATUS: AS BUILT (July 2026, branch `emit`). §1 records the failures that
motivated the design; §2–§6 describe the shipped shape; §7 records what the
review decided and where the build diverged from the original sketch. Read
`await.md` for the suspend/wake contract this doc builds on; nothing here
changes it.**

---

## 1. The three failures this answered

Three independent findings from the fork/suspension work converged on one
missing concept.

**fork.flatMap was a conditional guarantee.** Code in `Aggregate`, `Conda`,
`Condu` and `Trace` ran a sub-search fiber and flatMapped after it, reading
completion as "the sub-search is exhausted." Under the fork countdown that
held — but only for suspension-free subtrees, where a child's first yield is
its completion and the property composes transitively. The first suspension
anywhere in the subtree silently weakened "exhausted" to "gone quiet for
now." A guarantee conditional on a global non-property — *nothing in this
subtree suspends* — is a trap: whoever adds the first suspension breaks
contracts they have never heard of. Fork now completes immediately (it is a
control scatter; its completion carries nothing), which turned the
conditional lie into an unconditional and visible one: the four consumers
folded empty answer sets until they were rewired onto the honest primitive.

**Exhaustion needed a Scope and could only buy one inside a dead cell.** The
exhaustion those consumers actually need is quiescence of a workforce — a
`Scope` sealing. But Scope was fused into the cell: the only way to obtain a
workforce was to mint a value cell nobody would ever grow, and the only way
to await its seal was a value-await with a never-true predicate — a control
question asked on the value channel.

**detachTo and grow were uncorrelated.** `detachTo(A, fiber)` billed the
fiber to A's workforce; nothing stopped the fiber calling `B.grow(delta)`.
That producer was invisible to B's ledger, so B could seal while it still
ran. The failure was racy: a grow landing before B's seal was silently
absorbed, after it threw. Scheduling-dependent soundness. Tabling maintained
the discipline by construction; nothing enforced it.

One diagnosis covers all three: **the runtime has channels (values that grow
and complete) and workforces (fibers whose quiescence completes them), fused
into one object and bridged by an unenforced convention.** Unfuse them, and
make production an act that cannot disagree with membership.

## 2. The model

- **`Scope`** — a workforce: fibers billed to it (started/finished counters,
  blocked records), a seal (CAS'd once, upward-closed), the seal cascade and
  group seal exactly as in `completion.md`. First-class and value-free:
  mintable bare (`Scope.scope()`), no channel required.

- **`Channel<V>`** — a channel: a lattice value, held value-waiters, and the
  name of the Scope that closes it. Constructed *against* a scope
  (`new Channel<>(initial, scope)`), or minting its own private one
  (`new Channel<>(initial)`). Each channel registers its EOF translation
  with the scope at construction; at quiescence the scope runs every
  registered seal action.

- **`emit`** — the ONE producer primitive: an instruction in the fiber tree
  that folds a delta into the channel of the workforce the executing frame
  belongs to. It takes no channel argument; production has no addressing
  mode. `grow` is package-private — only the interpreter grows a channel.

- **`await(channel, ready)`** — the ONE consumer primitive, unchanged from
  `await.md`: completes `more(value)` at the first satisfying growth,
  `sealed(value)` at the seal. Both arms are honest answers to a value
  question ("here is more" / "never more"). The sealed arm is EOF, and EOF
  belongs in-band on the data channel (read() returns −1 on the stream,
  Rx delivers onComplete to the subscriber): a parked reader whose channel
  seals must be completed, or it is abandoned — a permanent blocked record
  that deadlocks its owner's seal in turn. `sealed(scope)` cannot replace
  this arm; it serves non-readers. The two-arm completion is also the minimal
  race primitive between "data arrived" and "data ended" — removing it
  forces a select primitive that would reinvent the same atomicity.

- **`sealed(scope)`** — the ONE control-await: completes `Nothing` when the
  scope seals. The control question asked on the control object. A
  seal-waiter's started/finished pair STAYS OPEN for the whole wait — the
  ledger is the work, and a member that will wake with a green light is
  still its home's work, so the home cannot drain past it. (Value-waiters
  differ: their seal-wake is the terminal EOF arm, so their pair closes at
  the park and the seal may pass them by.)

- **`fork`** — the control scatter: inject children into the ambient scope,
  complete immediately, promise nothing.

The two waiting channels answer the two different questions that were
previously disguised as one: *has this value grown past my cursor?* (await)
and *is this workforce finished?* (sealed).

## 3. The API

```java
public final class Scope {
	public static Scope scope();          // mint a workforce
	public boolean tryClaim();            // the claim CAS - true exactly once
	public boolean isSealed();
	public void seal();                   // manual - external certificates
}                                         // ledger stays runtime-internal

public class Channel<V extends Semilattice<V>> {
	public Channel(V initial);            // a private workforce
	public Channel(V initial, Scope closedBy);
	public V read();  public Scope scope();  public boolean isSealed();
	// suspend and grow are the interpreter's - package-private
}

public interface Emitter<V> {
	Fiber<Nothing> emit(V delta);         // fold delta into the ambient channel
}

// Fiber statics
static <A> Fiber<Nothing> claim(Scope into, Fiber<A> tree);       // ONCE per scope; loser no-ops
static <A> Fiber<Nothing> claimOrElse(Scope into, Fiber<A> tree,
		Fiber<Nothing> orElse);                                    // loser runs orElse inline
static Fiber<Nothing> sealed(Scope scope);                         // Nothing at the seal
static <V> Fiber<Nothing> produce(Channel<V> cell,
		Function<Emitter<V>, Fiber<Nothing>> body);                // the claim, ONCE per cell's scope
static <V> Fiber<Nothing> produceOrElse(Channel<V> cell,
		Function<Emitter<V>, Fiber<Nothing>> body, Fiber<Nothing> orElse);
```

`produce(cell, emit -> tree)` CLAIMS `cell.scope()` for `tree` and hands the
tree the cell's typed emitter. **Claiming is once-only, CAS-guarded per
scope, and the CAS runs at the STEP** — the only place claiming actually
happens — so racing claimants are welcome and resolve deterministically. The
loser SILENTLY NO-OPS (and its body is never built); when the loser has work
of its own, the two-branch forms `claimOrElse`/`produceOrElse` run an
alternative fiber inline — a CAS, not a lock: nobody waits, nothing throws,
there are just the two branches. A second produce on the same cell is also
never *needed*: inside the tree the emitter is already in scope, and forks
inherit the workforce — "more production" is spelled `fork`, from within.
The channel-less form (`claim`) carries the same once-CAS for pure
`sealed`-awaited workforces. The tree is `Fiber<Nothing>` throughout — fork,
Conde, all existing control plumbing untouched; values travel only through
emits.

**Emission semantics.** An emit is an ADT node carrying the delta. The
interpreter steps it holding the frame's scope: it verifies the frame
belongs to the emitter's channel's scope, folds the delta (the lattice join;
an inert join wakes nobody, per `await.md`), and completes `nothing()`.

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
  `Fiber.emit`; an emitter exists only inside a `produce`. (The leaked case
  is the check above.)

**Compile-time typing** rides the capability: `Emitter<V>` is typed against
the cell, so a mistyped delta is a compile error, not a fold-time surprise.

**Awaits inside a produce tree** are the engine's core loop, not a special
case: a producing frame that awaits leaves a blocked record in its
workforce's ledger, the seal defers on it exactly as `completion.md`
specifies, and the resumed frame emits on. Parking at the channel you feed
is the home-record case the seal rule already counts.

**Nested handlers.** `produce(a, ea -> produce(b, eb -> tree))` bills
`tree` to b's workforce. `eb` folds; `ea` inside the tree is REFUSED by the
membership check — and rightly so: the outer body completed at claiming, so
a's workforce drains and a seals while b's tree still runs; an `ea` emit
there is the detachTo/grow race restated. The sound spelling of "one
computation feeding two channels" is two cells sharing one workforce,
claimed ONCE with both emitters minted together (claim-once forbids
obtaining the second emitter by nesting):

```java
Scope s = Scope.scope();
Channel<A> a = new Channel<>(a0, s);
Channel<B> b = new Channel<>(b0, s);
produce(a, b, (ea, eb) -> tree)        // the multi-cell claim — YAGNI-gated:
                                       // nothing today needs two output channels
```

Both membership checks pass, both channels fold, and both seal together at
s's quiescence — the honest completion for co-produced channels. The check
refuses exactly the unsound configuration (distinct workforces) and admits
exactly the sound one (a shared workforce).

**Membership is prepaid — and claim-once is the mechanism.** The seal is a
point decision — "the workforce is drained now" — sound only if the
workforce is closed-world at that point. The workforce lifecycle under
claim-once:

- *Dormant*: a scope with no members has no seal trigger either — the seal
  cascade only fires from a member's `finished()`. Zero members, zero
  triggers: a dormant scope cannot seal. Nothing to race.
- *The claim*: exactly one, CAS-guarded at the step. A second claim races
  the FIRST CLAIM, not the seal, and the CAS resolves it deterministically
  — the loser takes its other branch, independent of scheduling.
- *After the claim*: every path onto the roster is FROM WITHIN — a running
  member forking or detaching into its own scope, safe by the open-pair
  shield (the spawner's started-unit holds the counters above zero, so the
  scope cannot read as drained mid-spawn).

The outsider — a fiber billed elsewhere joining a scope that may already
have drained, the cross-production hole's sibling — has no remaining
spelling: the only outside door is the claim, and it is spent. The TOCTOU
sliver between the singleton seal's quiescence check and its CAS still
exists in the code, but the only unshielded `started()` that could exploit
it was the outsider's, and outsiders are now refused by a CAS that never
touches the counter. The law is upgraded to a mechanism, with no lock on
the engine's hottest path.

**Prior art in this codebase**: `TableEntry.tryBecomeMaster` was exactly
this CAS — one producer per entry, adopted when two masters racing
production into one cell forced it. The claim-once rule generalizes the
idiom tabling already needed; the produce CAS absorbed `tryBecomeMaster`
(§5).

**Nested workforces and the seal rule: THE LEDGER IS THE WORK.** A
workforce claimed from within another and awaited by `sealed` is a
NESTING, and nestings resolve BOTTOM-UP by singleton cascades: the inner
scope finishes, seals itself, wakes the waiter, and the outer scope seals
later at true quiescence. The mechanism is billing, not a graph rule: a
seal-waiter keeps its started/finished pair OPEN for the whole wait,
because a member that will wake with a green light — its continuation is
arbitrary further work of its home — is still its home's work. The home's
counters cannot drain past it, so no seal, singleton or group, can pass
it by; there is no blocked entry to classify and no edge-kind check in
the group walk. Value-waiters close their pair at the park precisely
because their seal-wake is the terminal EOF arm — a verdict about a
finished world, not a green light — which is what licenses the group
seal's treatment of tabling's peer rings, its only remaining
constituency. A seal-wait on a target that never seals keeps its home
unfinished forever and strands both (reported at exhaustion); its
degenerate form — awaiting the seal of your own workforce — is refused
at the await.

The temporal summary: READS ARE TIME-FREE by monotonicity — a late awaiter
of a sealed channel completes immediately with `sealed(finalValue)`, losing
nothing; an awaiter of a never-claimed channel is stranded and named loudly
at exhaustion. WRITES ARE TIME-BOUNDED by prepaid membership. Every shape
outside those two refuses loudly. A channel reference being globally
reachable is harmless: what is global is only the right to read.

## 4. Precedents

The primitive is unusual for a threading substrate because it is not a
threading primitive — it is an effect, and every effectful tradition has it:

- **Writer monad**: `tell` accumulates into an ambient monoid; `runWriter`
  binds the accumulator. `emit`/`produce` is Writer with a lattice for the
  monoid, concurrency in the tree, and seal-on-quiescence for the run's end.
- **Algebraic effect handlers** (OCaml 5, Koka): `emit` is a performed
  effect, `produce` the nearest enclosing handler. "What if no handler?"
  is the canonical question there too — a type error under effect types,
  `Effect.Unhandled` at perform-time without them. Our lexical-capability
  answer sits between the two.
- **Kotlin `flow { emit(x) }`**: emit is a method on the builder's receiver —
  syntactically impossible outside the builder. Same for generators' `yield`.
- **Unix stdout**: a process writes to a descriptor it never opened; the
  parent bound it (`produce` is redirection); writing with nobody there is
  SIGPIPE — loud.

Against `grow`, `emit` differs on exactly one axis. Power over *when and
whether* to produce is identical — necessarily, since a search cannot know
its answer count before running; the per-answer act must be dynamic, and
"decide before committing" is the completion-as-value shape that abort and
multi-emission killed. Power over *where* is removed entirely. That removed
power was precisely the unsound one.

## 5. The consumers, as built

One pattern — produce into a lattice channel, await value-or-seal — with a
lattice per construct:

- **Tabling produce**: `produce(entry.channel(), emit -> body)`. `addAnswer`
  dissolved into delta construction + emit: dedup is the inert join
  (`JoinMap.append`), residue entailment is lattice absorption
  (`TableEntry.answerDelta`), star-tabling's `capture` wraps delta
  construction before the emit. `tryBecomeMaster` dissolved too — master
  selection IS the claim CAS: tabling's second caller is a legitimate race
  whose loser silently no-ops and falls through to the uniform consume
  path (every caller consumes; the winner additionally produces).
  Consumers are unchanged (`await` on the channel, `more`/`sealed` arms).
- **findall / count / fold** (`Aggregate`): run the sub-search under
  `Exhaustion.exhausted` — claim a fresh scope, await its seal, read the
  fold. A sub-search that parks at a tabled entry keeps the scope open
  until the entry seals — findall over a tabled goal waits for the table
  instead of folding a partial set. The #68 refusals stand.
- **Conda / Condu**: the committed solution is collected inside
  `exhausted()` and delivered after — deliveries must cross the delimiter,
  or the committed branch's own deliveries would be folded into the
  head-probe's exhaustion.
- **Trace**: INLINE delivery — tracing is observation, not aggregation. A
  collect-then-deliver Trace withholds answers that a tabled entry inside
  the traced region needs before it can seal; the Fail port rides the
  control drain.
- **Pure exhaustion** (`Exhaustion.exhausted` = claim + sealed): the
  degenerate instance — a scope with no channel at all. No dumb cell.

## 6. The doors that closed

`grow` went package-private, and the collapse went further than the design
sketch (§7, decision 5): there is no `Source` interface and no
`Await.Waiter`.

- **Production** is `produce`/`emit` only. The emit step is the one gate,
  and it is typed to the runtime's `Channel` — production must be billed,
  and only the runtime's channel has a ledger.
- **Consumption** is `await` on a `Channel`. The resume handle the
  interpreter parks is `ResumeHandle` — billing is its whole job, so it is
  the interpreter's type, and the channel holds it directly.
- **Tests** in the interpreter package grow channels directly
  (package-private access, outside any drive); tests elsewhere drive
  channels through `produce` like any other client.
- **External completions** (task #64) get no half-door: when
  externally-completed work lands, it designs its own seam against the
  real use case instead of inheriting a speculative suspend contract.
  Until then, an awaiter of a channel whose workforce is never claimed is
  stranded and refused loudly at exhaustion — the honest answer.

## 7. What the review decided (the original §8, resolved)

1. **Naming**: `produceTo` → `produce`; `plant` → `claim`
   (`Scope.tryClaim`). The silent no-op of a losing claimant was judged
   easy to miss at a call site, so the loser's branch became spellable:
   `claimOrElse`/`produceOrElse` (Tom, July 2026). The original sketch had
   the loser THROW; the step-time CAS made racing claimants legitimate
   (tabling's master selection is that race), so the loser no-ops and
   `tryProduceTo` — a separate CAS-or-false form — was never needed.
2. **Conda/Condu**: collect the committed solution inside the exhaustion
   delimiter, deliver after — decided against the existing tests.
3. **Multiple channels per scope** remain expressible and sound (two cells
   sharing one workforce, sealing together); the multi-cell produce
   overload stays YAGNI-gated.
4. **Sequencing**: landed as (a) Scope public + `sealed` + the four
   consumers rewired; (b) `produce`/`emit`, tabling migrated, `grow`
   withdrawn; then the interpreter hardening (frames minted by the
   interpreter; `Frame` owns its step) and the `Source`/`Waiter` collapse
   with the `MonotoneCell` → `Channel` rename.
