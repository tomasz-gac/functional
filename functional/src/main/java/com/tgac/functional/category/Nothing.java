package com.tgac.functional.category;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Nothing {
	private static final Nothing INSTANCE = new Nothing();

	public static Nothing nothing() {
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "Nothing";
	}
}
