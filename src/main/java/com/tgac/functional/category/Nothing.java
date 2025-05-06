package com.tgac.functional.category;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@SuppressWarnings("InstantiationOfUtilityClass")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Nothing {
	private static final Nothing INSTANCE = new Nothing();

	public static Nothing nothing() {
		return INSTANCE;
	}
}
