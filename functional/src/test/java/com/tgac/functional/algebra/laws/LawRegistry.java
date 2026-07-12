package com.tgac.functional.algebra.laws;

// ABOUTME: Records which kit exercised which class, so claim verification can
// ABOUTME: detect a claimed implementor whose matching laws never ran.

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LawRegistry {

	private static final Map<Class<?>, Set<String>> EXERCISED = new ConcurrentHashMap<>();

	static void record(String kit, Class<?> exercised) {
		EXERCISED.computeIfAbsent(exercised, c -> ConcurrentHashMap.newKeySet()).add(kit);
	}

	static void recordSamples(String kit, Iterable<?> samples) {
		for (Object sample : samples) {
			record(kit, sample.getClass());
		}
	}

	static Set<String> kitsFor(Class<?> c) {
		Set<String> kits = EXERCISED.get(c);
		return kits == null ? java.util.Collections.emptySet() : kits;
	}

	static Set<Class<?>> exercisedClasses() {
		return EXERCISED.keySet();
	}
}
