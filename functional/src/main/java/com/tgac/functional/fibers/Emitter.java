package com.tgac.functional.fibers;

// ABOUTME: The typed production capability a produceTo hands its workforce:
// ABOUTME: emit(delta) folds into the cell, checked against the ambient scope.

import com.tgac.functional.category.Nothing;

/**
 * The ONE way to produce (emit.md): minted only by
 * {@link Fiber#produceTo}, typed against the cell, executed by the
 * interpreter in the emitting frame's own workforce — an emit from a
 * foreign workforce refuses loudly. Abort by not emitting; produce many
 * by emitting repeatedly.
 */
public interface Emitter<V> {

	Fiber<Nothing> emit(V delta);
}
