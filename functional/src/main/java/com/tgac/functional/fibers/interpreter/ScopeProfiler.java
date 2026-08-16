package com.tgac.functional.fibers.interpreter;

// ABOUTME: A profiling StepListener: steps bucketed by the stepped frame's
// ABOUTME: owning scope, labeled by the scope's name or its construction site.

import com.tgac.functional.fibers.Fiber;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * The engine-semantic profiler: every reduction bills to the stepped frame's
 * owning workforce. A named scope bills under its name; an anonymous one
 * derives its label from its {@link Scope#origin()} — the first construction
 * frame outside the substrate and the caller-supplied skip prefixes — so
 * every workforce self-labels with the client code that minted it. Root
 * frames (no scope) bill under {@code root}. Labels are derived once per
 * scope; the per-step cost is two map operations.
 */
public final class ScopeProfiler implements StepListener {

	private static final String ROOT = "root";

	private final String[] skipPrefixes;
	private final ConcurrentHashMap<Scope, String> labels = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, LongAdder> steps = new ConcurrentHashMap<>();

	/**
	 * @param skipPrefixes class-name prefixes to skip when deriving a label
	 * 		from an anonymous scope's origin — the layers that mint scopes on
	 * 		behalf of others (e.g. {@code "com.tgac.functional."} and an
	 * 		exhaustion helper), so the label lands on their caller.
	 */
	public ScopeProfiler(String... skipPrefixes) {
		this.skipPrefixes = skipPrefixes.clone();
	}

	@Override
	public void onStep(Fiber<?> node, Scope scope, String name) {
		String label = name != null ? name
				: scope == null ? ROOT
						: labels.computeIfAbsent(scope, this::label);
		steps.computeIfAbsent(label, key -> new LongAdder()).increment();
	}

	/** Steps per label, a snapshot. */
	public Map<String, Long> counts() {
		Map<String, Long> snapshot = new LinkedHashMap<>();
		steps.forEach((label, count) -> snapshot.put(label, count.sum()));
		return snapshot;
	}

	/** The counts as printable lines, heaviest first. */
	public List<String> report() {
		List<Map.Entry<String, Long>> entries = new ArrayList<>(counts().entrySet());
		entries.sort(Comparator.<Map.Entry<String, Long>> comparingLong(Map.Entry::getValue).reversed());
		List<String> lines = new ArrayList<>();
		for (Map.Entry<String, Long> entry : entries) {
			lines.add(String.format("%-70s %12d", entry.getKey(), entry.getValue()));
		}
		return lines;
	}

	private String label(Scope scope) {
		if (scope.name() != null) {
			return scope.name();
		}
		if (scope.origin() == null) {
			return scope.toString();
		}
		for (StackTraceElement frame : scope.origin().getStackTrace()) {
			if (!skipped(frame.getClassName())) {
				return frame.toString();
			}
		}
		return scope.toString();
	}

	private boolean skipped(String className) {
		if (className.startsWith("java.")
				|| className.startsWith("sun.")
				|| className.equals(Scope.class.getName())) {
			return true;
		}
		for (String prefix : skipPrefixes) {
			if (className.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
}
