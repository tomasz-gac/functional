package com.tgac.functional.fibers;

import com.tgac.functional.algebra.Semilattice;
import com.tgac.functional.category.Monad;
import com.tgac.functional.category.Nothing;
import com.tgac.functional.fibers.interpreter.MonotoneCell;
import com.tgac.functional.fibers.interpreter.Scope;
import com.tgac.functional.fibers.schedulers.BreadthFirstScheduler;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.var;

public interface Fiber<A> extends Monad<Fiber<?>, A>, Supplier<A> {
	interface Fn<T, R> extends Function<T, Fiber<R>> {
	}

	static <A> Fiber<A> done(@NonNull A v) {
		return Done.of(v);
	}

	static <A> Fiber<A> defer(Supplier<Fiber<A>> rec) {
		return Deferred.of(rec);
	}

	@Override
	default <B> Fiber<B> flatMap(Function<? super A, @NonNull ? extends Monad<Fiber<?>, B>> f) {
		return FlatMap.of(f, this);
	}

	@Override
	default <B> Fiber<B> map(Function<? super A, @NonNull B> f) {
		return flatMap(v -> done(f.apply(v)));
	}

	@Override
	default <B> Fiber<B> pure(B value) {
		return done(value);
	}

	@Override
	@SneakyThrows
	default A get() {
		try (var e = toEngine()) {
			return e.get();
		}
	}

	default boolean isDone() {
		return false;
	}

	default Scheduler<A> toEngine() {
		return new BreadthFirstScheduler<>(this);
	}

	static <A, B> Fiber<Tuple2<A, B>> zip(Fiber<A> lhs, Fiber<B> rhs) {
		return lhs.flatMap(l -> rhs.map(r -> Tuple.of(l, r)));
	}

	/**
	 * Fork the tasks as independent frames in the calling fiber's scope. A
	 * CONTROL primitive: the fork completes when control has drained out of
	 * every child — each has either finished or parked itself at a
	 * {@link Source}, fully recorded (a child yields exactly once, and done
	 * is the final yield). Completion promises NOTHING about the children's
	 * values: a parked child lives on, resumed by its source, and may keep
	 * producing after the fork has completed. Work that must observe "all
	 * results are in" awaits a source's seal instead — quiescence of the
	 * producing workforce is the only honest end-of-stream.
	 */
	static <A> Fiber<Nothing> fork(List<Fiber<A>> tasks) {
		return new Forked<A>(tasks)
				.map(_0 -> Nothing.nothing());
	}

	/**
	 * Detach a fiber to run independently without blocking the caller's completion.
	 * The detached fiber runs in the background and the caller continues immediately.
	 * The child runs UNOWNED — no scope records it; use {@link #claim} to re-parent.
	 *
	 * @param fiber The fiber to detach
	 * @return A fiber that completes immediately while the detached fiber runs independently
	 */
	static <A> Fiber<Nothing> detach(Fiber<A> fiber) {
		return new Detached<>(fiber, null);
	}

	/**
	 * CLAIM a workforce: detach {@code tree} as {@code into}'s membership,
	 * AT MOST ONCE — the claim CAS runs at the STEP, the only place
	 * claiming actually happens, so racing claimants are welcome and every
	 * loser (a re-stepped fiber value included) SILENTLY NO-OPS; use
	 * {@link #claimOrElse} when the loser has work of its own. All later
	 * membership grows from within (a running member forks or detaches
	 * into its own scope, shielded by its open started/finished pair).
	 */
	static <A> Fiber<Nothing> claim(Scope into, Fiber<A> tree) {
		return claimOrElse(into, tree, done(Nothing.nothing()));
	}

	/**
	 * {@link #claim} with a loser's branch: the first claimant to STEP wins
	 * and detaches {@code tree}; every other claimant — racing, later, or a
	 * re-stepped claim fiber — runs {@code orElse} INLINE instead (in its
	 * own frame, not detached). The claim is a CAS, not a lock: nobody
	 * waits, nothing throws, there are just the two branches.
	 */
	static <A> Fiber<Nothing> claimOrElse(Scope into, Fiber<A> tree, Fiber<Nothing> orElse) {
		return defer(() -> into.tryClaim()
				? new Detached<>(tree, into)
				: orElse);
	}

	/**
	 * The control await: completes with Nothing when {@code scope} seals —
	 * its workforce has finished and nothing it closes can ever grow again.
	 * For NON-READERS (exhaustion consumers); readers await the source,
	 * whose sealed arm is EOF on the data channel (emit.md). The waiting
	 * frame's started/finished pair stays OPEN for the whole wait: the
	 * ledger is the work, and a member that will wake with a green light is
	 * still its home's work — the home cannot drain past it.
	 */
	static Fiber<Nothing> sealed(Scope scope) {
		return new Sealed(scope);
	}

	/**
	 * CLAIM {@code cell}'s workforce and hand it the cell's typed emitter —
	 * the handler shape (emit.md): emits inside the tree fold into the
	 * cell; the tree stays {@code Fiber<Nothing>} and values travel only
	 * through emits. AT MOST ONCE per scope: the claim CAS runs at the
	 * STEP — the only place claiming happens — so racing callers are
	 * welcome (tabling's master selection IS this race) and every loser
	 * SILENTLY NO-OPS and reads the cell as a consumer; the loser's body
	 * is never built. Use {@link #produceOrElse} when the loser has work
	 * of its own.
	 */
	static <V extends Semilattice<V>> Fiber<Nothing> produce(MonotoneCell<V> cell,
			Function<Emitter<V>, Fiber<Nothing>> body) {
		return produceOrElse(cell, body, done(Nothing.nothing()));
	}

	/**
	 * {@link #produce} with a loser's branch: the first claimant to STEP
	 * wins the cell's workforce and runs its body; every other claimant
	 * runs {@code orElse} INLINE instead (in its own frame, not detached) —
	 * the loser's {@code body} is never built.
	 */
	static <V extends Semilattice<V>> Fiber<Nothing> produceOrElse(MonotoneCell<V> cell,
			Function<Emitter<V>, Fiber<Nothing>> body, Fiber<Nothing> orElse) {
		return defer(() -> cell.scope().tryClaim()
				? new Detached<>(body.apply(delta -> new Emit<>(cell, delta)), cell.scope())
				: orElse);
	}

	/**
	 * Suspend until {@code ready} holds of {@code source}'s value or the
	 * source's scope seals — the condition variable over a monotone source
	 * (docs/design/await.md). The fiber does not end while blocked: its
	 * scope's started/finished pair converts into a blocked record, so every
	 * quiescence question stays answerable. Run-once: one await completes at
	 * most once ({@code more} or {@code sealed}); re-arm with flatMap.
	 */
	static <V extends Semilattice<V>> Fiber<Await.Result<V>> await(Source<V> source, Predicate<V> ready) {
		return new Awaiting<>(source, ready);
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	class Done<A> implements Fiber<A> {
		A value;

		@Override
		public A get() {
			return value;
		}

		@Override
		public <B> Fiber<B> flatMap(Function<? super A, ? extends Monad<Fiber<?>, B>> f) {
			return (Fiber<B>) f.apply(value);
		}

		@Override
		public <B> Fiber<B> map(Function<? super A, B> f) {
			return Done.of(f.apply(value));
		}

		@Override
		public boolean isDone() {
			return true;
		}
	}

	@Value
	@RequiredArgsConstructor(staticName = "of")
	class Deferred<A> implements Fiber<A> {
		Supplier<Fiber<A>> rec;
	}

	@Getter
	@RequiredArgsConstructor(staticName = "of")
	class Forked<A> implements Fiber<A> {
		private final List<Fiber<A>> options;
	}

	/**
	 * ABOUTME: Represents a detached fiber that runs independently in the background.
	 * ABOUTME: The parent fiber completes immediately while the detached fiber continues execution.
	 */
	@Value
	@RequiredArgsConstructor
	class Detached<A> implements Fiber<Nothing> {
		Fiber<A> fiber;
		Scope into;
	}

	/** A fiber suspended on a {@link Source} until ready or sealed. */
	@Value
	@RequiredArgsConstructor
	class Awaiting<V extends Semilattice<V>> implements Fiber<Await.Result<V>> {
		Source<V> source;
		Predicate<V> ready;
	}

	/** A fiber suspended until a {@link Scope} seals — billed through the wait. */
	@Value
	@RequiredArgsConstructor
	class Sealed implements Fiber<Nothing> {
		Scope scope;
	}

	/**
	 * Production as an instruction: fold {@code delta} into {@code cell},
	 * lawful only from the workforce that closes it — the interpreter
	 * verifies the emitting frame's scope at the step (emit.md).
	 */
	@Value
	@RequiredArgsConstructor
	class Emit<V extends Semilattice<V>> implements Fiber<Nothing> {
		MonotoneCell<V> cell;
		V delta;
	}

	@Getter
	@ToString
	@EqualsAndHashCode
	@AllArgsConstructor
	@FieldDefaults(makeFinal = true, level = AccessLevel.MODULE)
	class FlatMap<A, B> implements Fiber<B>, Function<A, Fiber<B>> {
		Function<A, Fiber<B>> f;
		Fiber<A> arg;

		@Override
		public Fiber<B> apply(A v) {
			return f.apply(v);
		}

		@SuppressWarnings("unchecked")
		public static <C, D> FlatMap<Object, D> of(Function<? super C, ? extends Monad<Fiber<?>, D>> f, Fiber<C> r) {
			return new FlatMap<>(o -> (Fiber<D>) f.apply((C) o), (Fiber<Object>) r);
		}
	}
}