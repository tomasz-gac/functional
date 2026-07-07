# functional

Functional programming substrate for Java 8: stack-safe suspendable computations
(fibers), a continuation monad, and a family of schedulers that all drive the same
step interpreter — plus a small monad/transformer library. This is the engine room
under [`logic`](../logic) (the miniKanren), but nothing in it knows about logic
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
| `BreadthFirstScheduler` | fair and complete — a result at depth n is reached even if a sibling branch diverges |
| `DepthFirstScheduler` | run a branch to completion (Prolog order) |
| `RoundRobin` | rotate between branches |
| `UnfairBreadthFirstScheduler` | breadth-shaped, cheaper, sacrifices the fairness guarantee |
| `ForkJoinScheduler` | steps frames in parallel on a work-stealing pool |

Swapping schedulers never changes *what* is computed, only the order (and
wall-clock) — `SchedulerEquivalenceTest` pins exactly that. Every
interpreter-driven scheduler accepts a `StepListener`
(`scheduler.withListener(...)`), the observability seam: per-step callbacks for
tracing, counting, or snapshotting a live search (`SearchInspectable` /
`SearchSnapshot`).

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
```

Consumers: [`logic`](../logic) uses `Cont`, fibers and the scheduler family as
its search engine; the fiber substrate is deliberately isolated (it depends only
on `category/`) so it could graduate to its own artifact if it ever needs to.

## Status

Unreleased (`-SNAPSHOT`); APIs may still move. If you touch this library while
working on `logic`, remember the sibling rule: `mvn install` here before `logic`
can see the change.
