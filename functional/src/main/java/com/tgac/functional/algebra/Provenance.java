package com.tgac.functional.algebra;

// ABOUTME: A provenance value — a regular expression over base symbols recording
// ABOUTME: WHICH derivations produced an answer; the free closed-semiring element.

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A regular expression over base symbols, denoting the SET of derivations (words)
 * that produced an answer: {@code ⊕} is alternation, {@code ⊗} concatenation,
 * {@code star} "loop any number of times". The {@link Semirings#PROVENANCE}
 * semiring is idempotent (union) and closed (a real {@code star}) but NOT bounded
 * — {@code a*} is {@code {ε, a, aa, …}}, not {@code {ε}} — so cyclic STREAMING
 * diverges and only the star closes it. It is the canonical {@code solveClosed}
 * case, and (concatenation being order-sensitive) the first NON-commutative ⊗.
 *
 * <p>The tree's {@code equals} is structural, but the semiring LAWS hold only up
 * to LANGUAGE equivalence (regexes denoting the same words), so law kits compare
 * via {@link #sameLanguage} over a bounded word length, not {@code equals}.
 */
public final class Provenance {

	private enum Kind {EMPTY, EPS, SYM, ALT, CAT, STAR}

	private final Kind kind;
	private final String sym;
	private final Provenance a;
	private final Provenance b;

	private Provenance(Kind kind, String sym, Provenance a, Provenance b) {
		this.kind = kind;
		this.sym = sym;
		this.a = a;
		this.b = b;
	}

	private static final Provenance EMPTY = new Provenance(Kind.EMPTY, null, null, null);
	private static final Provenance EPS = new Provenance(Kind.EPS, null, null, null);

	/** ∅ — no derivation (the ⊕ identity / ⊗ annihilator). */
	public static Provenance empty() {
		return EMPTY;
	}

	/** ε — the trivial (empty) derivation (the ⊗ identity). */
	public static Provenance epsilon() {
		return EPS;
	}

	/** A base fact. */
	public static Provenance sym(String s) {
		return new Provenance(Kind.SYM, s, null, null);
	}

	/** ⊕: alternation, simplifying identities and idempotence. */
	public static Provenance alt(Provenance a, Provenance b) {
		if (a.kind == Kind.EMPTY) {
			return b;
		}
		if (b.kind == Kind.EMPTY) {
			return a;
		}
		if (a.equals(b)) {
			return a;
		}
		return new Provenance(Kind.ALT, null, a, b);
	}

	/** ⊗: concatenation, simplifying identities and annihilation. */
	public static Provenance cat(Provenance a, Provenance b) {
		if (a.kind == Kind.EMPTY || b.kind == Kind.EMPTY) {
			return EMPTY;
		}
		if (a.kind == Kind.EPS) {
			return b;
		}
		if (b.kind == Kind.EPS) {
			return a;
		}
		return new Provenance(Kind.CAT, null, a, b);
	}

	/** Kleene star: loop zero or more times. */
	public static Provenance star(Provenance a) {
		if (a.kind == Kind.EMPTY || a.kind == Kind.EPS) {
			return EPS;
		}
		if (a.kind == Kind.STAR) {
			return a;
		}
		return new Provenance(Kind.STAR, null, a, null);
	}

	/** The words (symbol sequences) this denotes, restricted to length ≤ {@code maxLen}. */
	public Set<List<String>> words(int maxLen) {
		Set<List<String>> out = new HashSet<>();
		switch (kind) {
			case EMPTY:
				break;
			case EPS:
				out.add(new ArrayList<>());
				break;
			case SYM:
				if (maxLen >= 1) {
					List<String> w = new ArrayList<>();
					w.add(sym);
					out.add(w);
				}
				break;
			case ALT:
				out.addAll(a.words(maxLen));
				out.addAll(b.words(maxLen));
				break;
			case CAT:
				concatInto(out, a.words(maxLen), b.words(maxLen), maxLen);
				break;
			case STAR:
				Set<List<String>> aWords = a.words(maxLen);
				out.add(new ArrayList<>());
				while (true) {
					Set<List<String>> next = new HashSet<>(out);
					concatInto(next, aWords, out, maxLen);
					if (next.equals(out)) {
						break;
					}
					out = next;
				}
				break;
			default:
				break;
		}
		return out;
	}

	private static void concatInto(Set<List<String>> out,
			Set<List<String>> us, Set<List<String>> vs, int maxLen) {
		for (List<String> u : us) {
			for (List<String> v : vs) {
				if (u.size() + v.size() <= maxLen) {
					List<String> w = new ArrayList<>(u);
					w.addAll(v);
					out.add(w);
				}
			}
		}
	}

	/** Language equivalence up to {@code maxLen}: the quotient the semiring laws hold in. */
	public boolean sameLanguage(Provenance other, int maxLen) {
		return words(maxLen).equals(other.words(maxLen));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Provenance)) {
			return false;
		}
		Provenance p = (Provenance) o;
		return kind == p.kind && Objects.equals(sym, p.sym)
				&& Objects.equals(a, p.a) && Objects.equals(b, p.b);
	}

	@Override
	public int hashCode() {
		return Objects.hash(kind, sym, a, b);
	}

	@Override
	public String toString() {
		switch (kind) {
			case EMPTY:
				return "∅";
			case EPS:
				return "ε";
			case SYM:
				return sym;
			case ALT:
				return "(" + a + "+" + b + ")";
			case CAT:
				return a + "·" + b;
			case STAR:
				return a + "*";
			default:
				return "?";
		}
	}
}
