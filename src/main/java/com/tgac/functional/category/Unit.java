package com.tgac.functional.category;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@SuppressWarnings("InstantiationOfUtilityClass")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Unit {
	private static final Unit INSTANCE = new Unit();

	public static Unit unit() {
		return INSTANCE;
	}
}
