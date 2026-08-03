# functional

Functional programming substrate for Java 8: stack-safe suspendable computations
(fibers) with channels, scopes and termination detection; a continuation monad; a
family of schedulers that all drive the same step interpreter; and a law-checked
algebra library (semilattices to semirings) whose property kits ship as their own
artifact — plus a small monad/transformer library. This is the engine room under
[`logic`](../logic) (the miniKanren), but nothing in it knows about logic
programming; it stands alone.

## Fibers: recursion as data

A `Fiber<A>` is a computation that describes its own control flow instead of
consuming the JVM stack. `done` finishes, `defer` suspends — recursion of any
depth becomes heap-allocated structure:

```java
Fiber<BigDecimal> fib(int n, BigDecimal current, BigDecimal last) {
    return n == 0 ?
            done(current) :
            defer(() -> fib(n - 1, last.add(current), current));
}

fib(100_000, ONE, ZERO).get();   // fine — the plain-recursion version blows the stack
```

Because control flow is data, it can be *paused*: `toEngine()` turns a fiber into
a `Scheduler` you can run a few steps at a time —

```java
Scheduler<Long> engine = collatz(27).toEngine();
engine.run(5);          // Optional.empty() — not done yet
engine.run(5);          // ...
```

— and it can *branch*: `Fiber.fork(tasks, sink)` runs a list of fibers as
siblings, `Fiber.detach` fires-and-forgets one. Forking is what turns a fiber
from a trampoline into a search tree, which is where the schedulers come in.

## Schedulers: one interpreter, many disciplines

Every scheduler is a driver over the same single-step interpreter (`FiberStep`);
they differ only in *which suspended frame steps next*:

| Scheduler | Discipline |
|---|---|
| `BreadthFirstScheduler` | honest BFS: one bucket per depth, round-robin within a level; a dead level crashes through after prolonged no-progress |
| `DepthFirstScheduler` | run a branch to completion (Prolog order) |
| `RoundRobin` | rotate between branches |
| `UnfairBreadthFirstScheduler` | the priced search shape: fragmented sibling buckets, long-running buckets poured down a level — unfair within and across levels, and measurably faster for propagation-heavy search |
| `ForkJoinScheduler` | steps frames in parallel on a work-stealing pool |
| `RandomizedScheduler` | seeded random frame choice — the chaos driver: run the same program under 24 seeds and order-independence becomes a testable property instead of a hope |

Swapping schedulers never changes *what* is computed, only the order (and
wall-clock) — `SchedulerEquivalenceTest` pins exactly that, FOR PURE
PROGRAMS: committed choice observes arrival order by design, side-effecting
consumers observe interleaving, and a bounded step budget observes pace. The
invariant is the answer SET of an order-tolerant program, nothing wider. Every
interpreter-driven scheduler accepts a `StepListener`
(`scheduler.withListener(...)`), the observability seam: per-step callbacks for
tracing, counting, or snapshotting a live search (`SearchInspectable` /
`SearchSnapshot`).

## Channels, scopes, and knowing when you're done

Concurrency here is organized around a MONOTONE value, not message-passing.
A `Channel<V extends Semilattice<V>>` holds a value that only grows — growth
is the semilattice join, a delta that adds nothing refuses, and every parked
consumer (`Fiber.await(channel, ready)`) is woken exactly by the growth it
waits for. Production is claimed, not assumed: `Fiber.produce(channel, work)`
is a compare-and-swap — the first claimant's body runs as the channel's
workforce, the loser no-ops (or runs an alternative: `produceOrElse`,
`claimOrElse`), and emission goes through the one typed door (`Emitter`).

The workforce is a `Scope`: two monotone counters (started/finished,
Dijkstra–Scholten style) plus the parked-sleeper records. When the counters
meet and every sleeper is parked home, no new value can ever arrive — that
is the SEAL, and it completes every waiting consumer with the final value.
Groups of mutually-feeding channels seal together (the group walk); a drive
that runs out of work with a consumer still parked refuses loudly and NAMES
the channel it starved at, instead of hanging. `Worklist` is the same
discipline for drain-to-quiescence loops.

This is what `logic` builds tabling's answer cells and completion detection
from — but the primitives are domain-free: a cell is any growing value, a
seal is any "this monotone process is finished".

## Cont: continuations

`Cont<T, R>` is a stack-safe continuation-passing monad built on fibers —
`suspend`, `defer`, `just`, `complete`. It is the type `logic` builds goals from
(a goal is `Package -> Cont<Package, Nothing>`), and the reason backtracking
search, tabling, and constraint propagation all trampoline through one engine
without touching the JVM stack.

## The monad shelf

`monad/`: `Option`, `Either`, `Try`, `Lazy`, `State`, `Reader`, `Identity`,
`Free`, `Future`, `Stream` — with `transformer/` stacking (`OptionT`, `EitherT`,
`TryT`, `FutureT`, `StreamT`) over the `category/` abstractions
(`Functor`/`Monad`/`Comonad`, `TypeConstructor`, and `Nothing` — the type with
exactly one value, for effects that only ever signal completion).

`step/` holds the incremental stream shape (`Step`: cons / empty / single /
*incomplete*) that lets a stream say "no element yet, ask again" — the primitive
under fair interleaving. Utilities: `Exceptions` (throwing lambdas without
ceremony), `Streams` (zip and friends for `java.util.stream`), `Reference`,
`reflection/Types` (cast helpers), `graph/` (small graph builder).

## The algebra, with its laws as a shipping artifact

`algebra/` is the value-discipline layer: `Semilattice` (one idempotent
commutative associative op, direction deliberately unnamed — meet and join
are the domain's reading of it), `PartialOrder` (entailment alone), and the
semiring capability ladder — `Semiring`, `IdempotentSemiring` (dedup is
lawful), `ClosedSemiring` (Kleene star), `BoundedSemiring` (`a ⊕ 1 = 1`, so
cyclic streaming terminates), `SuperiorSemiring` (best-first commitment is
legal). Capabilities are TYPES: a call site that needs a property demands
the interface, so an illegal plug is a compile error.

Claims are audited, not aspirational: every algebraic interface is
`@CheckedBy` its law kit, and the `laws` module — a separate, test-scoped
artifact — ships the property kits (`SemilatticeLaws`, `SemiringLaws`,
`StarLaws`, …) plus `LawCoverage`, a gate that fails the build if any
implementor lacks a claiming law test. Consumers declaring their own
instances get the same audit by depending on `functional-laws` in test
scope.

## Building

Java 8, vavr is the only runtime dependency. Maven, currently `-SNAPSHOT`:

```bash
mvn install          # parent; builds the functional module
```

```xml
<dependency>
    <groupId>com.tgac</groupId>
    <artifactId>functional</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
<dependency>                          <!-- law kits for your own instances -->
    <groupId>com.tgac</groupId>
    <artifactId>functional-laws</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

Consumers: [`logic`](../logic) uses `Cont`, fibers, the scheduler family and
the algebra as its search engine. The fiber substrate depends on `category/`
and — deliberately — on `algebra/`: a channel's value is CONTRACTUALLY a
lawful semilattice, which is what makes growth, dedup and sealing theorems
rather than conventions. It could still graduate to its own artifact
(together with `algebra/` and `category/`) if an external consumer ever
wants the substrate without the monad shelf — a shelved decision with that
trigger, not a plan.

## Status

Unreleased (`-SNAPSHOT`); APIs may still move. If you touch this library while
working on `logic`, remember the sibling rule: `mvn install` here before `logic`
can see the change.
