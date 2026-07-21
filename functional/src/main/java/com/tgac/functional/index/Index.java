package com.tgac.functional.index;

// ABOUTME: A path-addressed index: look up a node by a key sequence and read
// ABOUTME: its value. ImmutableIndex is the persistent trie implementation.

import io.vavr.collection.Array;
import io.vavr.collection.IndexedSeq;
import io.vavr.control.Option;

public interface Index<K, V> {
	default Option<Index<K, V>> get(K key) {
		return get(Array.of(key));
	}

	Option<Index<K, V>> get(IndexedSeq<K> seq);

	V getValue();
}
