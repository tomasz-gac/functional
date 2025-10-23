package com.tgac.functional.category;

public interface TypeConstructor<C extends TypeConstructor<C, ?>, A> {
	default <N extends TypeConstructor<C, A>> N cast() {
		return (N) this;
	}
}
