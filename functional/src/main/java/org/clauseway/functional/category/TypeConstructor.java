package org.clauseway.functional.category;

public interface TypeConstructor<C extends TypeConstructor<C, ?>, A> {
	@SuppressWarnings("unchecked")
	default <N extends TypeConstructor<C, A>> N cast() {
		return (N) this;
	}
}
