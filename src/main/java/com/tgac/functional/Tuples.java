package com.tgac.functional;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Tuples {

	public interface Tuple {
		Object[] toArray();
	}

	public static Tuples._0 tuple() {
		return new Tuples._0();
	}

	public static <T0> Tuples._1<T0> tuple(T0 v0) {
		return new Tuples._1<>(v0);
	}

	public static <T0, T1> Tuples._2<T0, T1> tuple(T0 v0, T1 v1) {
		return new Tuples._2<>(v0, v1);
	}

	public static <T0, T1, T2> Tuples._3<T0, T1, T2> tuple(T0 v0, T1 v1, T2 v2) {
		return new Tuples._3<>(v0, v1, v2);
	}

	public static <T0, T1, T2, T3> Tuples._4<T0, T1, T2, T3> tuple(T0 v0, T1 v1, T2 v2, T3 v3) {
		return new Tuples._4<>(v0, v1, v2, v3);
	}

	public static <T0, T1, T2, T3, T4> Tuples._5<T0, T1, T2, T3, T4> tuple(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4) {
		return new Tuples._5<>(v0, v1, v2, v3, v4);
	}

	public static <T0, T1, T2, T3, T4, T5> Tuples._6<T0, T1, T2, T3, T4, T5> tuple(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5) {
		return new Tuples._6<>(v0, v1, v2, v3, v4, v5);
	}

	public static <T0, T1, T2, T3, T4, T5, T6> Tuples._7<T0, T1, T2, T3, T4, T5, T6> tuple(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6) {
		return new Tuples._7<>(v0, v1, v2, v3, v4, v5, v6);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7> Tuples._8<T0, T1, T2, T3, T4, T5, T6, T7> tuple(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7) {
		return new Tuples._8<>(v0, v1, v2, v3, v4, v5, v6, v7);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8> Tuples._9<T0, T1, T2, T3, T4, T5, T6, T7, T8> tuple(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8) {
		return new Tuples._9<>(v0, v1, v2, v3, v4, v5, v6, v7, v8);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuples._10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9) {
		return new Tuples._10<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Tuples._11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> tuple(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9,
			T10 v10) {
		return new Tuples._11<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Tuples._12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> tuple(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8,
			T9 v9, T10 v10, T11 v11) {
		return new Tuples._12<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> tuple(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6,
			T7 v7,
			T8 v8, T9 v9, T10 v10, T11 v11, T12 v12) {
		return new Tuples._13<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> tuple(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5,
			T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11, T12 v12, T13 v13) {
		return new Tuples._14<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> tuple(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4,
			T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11, T12 v12, T13 v13, T14 v14) {
		return new Tuples._15<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> tuple(T0 v0, T1 v1, T2 v2,
			T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11, T12 v12, T13 v13, T14 v14, T15 v15) {
		return new Tuples._16<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> tuple(T0 v0, T1 v1,
			T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11, T12 v12, T13 v13, T14 v14, T15 v15, T16 v16) {
		return new Tuples._17<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> tuple(
			T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11, T12 v12, T13 v13, T14 v14, T15 v15, T16 v16, T17 v17) {
		return new Tuples._18<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> tuple(
			T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11, T12 v12, T13 v13, T14 v14, T15 v15, T16 v16, T17 v17, T18 v18) {
		return new Tuples._19<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> tuple(
			T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11, T12 v12, T13 v13, T14 v14, T15 v15, T16 v16, T17 v17, T18 v18, T19 v19) {
		return new Tuples._20<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> tuple(
			T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11, T12 v12, T13 v13, T14 v14, T15 v15, T16 v16, T17 v17, T18 v18, T19 v19, T20 v20) {
		return new Tuples._21<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> tuple(
			T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11, T12 v12, T13 v13, T14 v14, T15 v15, T16 v16, T17 v17, T18 v18, T19 v19, T20 v20, T21 v21) {
		return new Tuples._22<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> tuple(
			T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11, T12 v12, T13 v13, T14 v14, T15 v15, T16 v16, T17 v17, T18 v18, T19 v19, T20 v20, T21 v21, T22 v22) {
		return new Tuples._23<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22);
	}

	public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> tuple(
			T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11, T12 v12, T13 v13, T14 v14, T15 v15, T16 v16, T17 v17, T18 v18, T19 v19, T20 v20, T21 v21, T22 v22,
			T23 v23) {
		return new Tuples._24<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23);
	}

	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _0 implements Tuple {

		public _0 concat(_0 rhs) {
			return new Tuples._0();
		}

		public <T0> _1<T0> concat(_1<T0> rhs) {
			return new Tuples._1<>(rhs._0);
		}

		public <T0, T1> _2<T0, T1> concat(_2<T0, T1> rhs) {
			return new Tuples._2<>(rhs._0, rhs._1);
		}

		public <T0, T1, T2> _3<T0, T1, T2> concat(_3<T0, T1, T2> rhs) {
			return new Tuples._3<>(rhs._0, rhs._1, rhs._2);
		}

		public <T0, T1, T2, T3> _4<T0, T1, T2, T3> concat(_4<T0, T1, T2, T3> rhs) {
			return new Tuples._4<>(rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T0, T1, T2, T3, T4> _5<T0, T1, T2, T3, T4> concat(_5<T0, T1, T2, T3, T4> rhs) {
			return new Tuples._5<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T0, T1, T2, T3, T4, T5> _6<T0, T1, T2, T3, T4, T5> concat(_6<T0, T1, T2, T3, T4, T5> rhs) {
			return new Tuples._6<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T0, T1, T2, T3, T4, T5, T6> _7<T0, T1, T2, T3, T4, T5, T6> concat(_7<T0, T1, T2, T3, T4, T5, T6> rhs) {
			return new Tuples._7<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7> _8<T0, T1, T2, T3, T4, T5, T6, T7> concat(_8<T0, T1, T2, T3, T4, T5, T6, T7> rhs) {
			return new Tuples._8<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8> _9<T0, T1, T2, T3, T4, T5, T6, T7, T8> concat(_9<T0, T1, T2, T3, T4, T5, T6, T7, T8> rhs) {
			return new Tuples._9<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(_10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> rhs) {
			return new Tuples._10<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> rhs) {
			return new Tuples._11<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> rhs) {
			return new Tuples._12<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(
				_13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> rhs) {
			return new Tuples._13<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(
				_14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> rhs) {
			return new Tuples._14<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(
				_15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(
				_16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(
				_17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(
				_18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17, rhs._18);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17, rhs._18,
					rhs._19);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17, rhs._18,
					rhs._19, rhs._20);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17, rhs._18,
					rhs._19, rhs._20, rhs._21);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17, rhs._18,
					rhs._19, rhs._20, rhs._21, rhs._22);
		}

		public <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17, rhs._18,
					rhs._19, rhs._20, rhs._21, rhs._22, rhs._23);
		}

		public <T> Tuples._1<T> append(T v) {
			return tuple(v);
		}

		public <R> R apply(Functions._0<R> f) {
			return f.apply();
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _1<T0> implements Tuple {
		public final T0 _0;

		public T0 _0() {
			return _0;
		}

		public _1<T0> concat(_0 rhs) {
			return new Tuples._1<>(_0);
		}

		public <T1> _2<T0, T1> concat(_1<T1> rhs) {
			return new Tuples._2<>(_0, rhs._0);
		}

		public <T1, T2> _3<T0, T1, T2> concat(_2<T1, T2> rhs) {
			return new Tuples._3<>(_0, rhs._0, rhs._1);
		}

		public <T1, T2, T3> _4<T0, T1, T2, T3> concat(_3<T1, T2, T3> rhs) {
			return new Tuples._4<>(_0, rhs._0, rhs._1, rhs._2);
		}

		public <T1, T2, T3, T4> _5<T0, T1, T2, T3, T4> concat(_4<T1, T2, T3, T4> rhs) {
			return new Tuples._5<>(_0, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T1, T2, T3, T4, T5> _6<T0, T1, T2, T3, T4, T5> concat(_5<T1, T2, T3, T4, T5> rhs) {
			return new Tuples._6<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T1, T2, T3, T4, T5, T6> _7<T0, T1, T2, T3, T4, T5, T6> concat(_6<T1, T2, T3, T4, T5, T6> rhs) {
			return new Tuples._7<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T1, T2, T3, T4, T5, T6, T7> _8<T0, T1, T2, T3, T4, T5, T6, T7> concat(_7<T1, T2, T3, T4, T5, T6, T7> rhs) {
			return new Tuples._8<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8> _9<T0, T1, T2, T3, T4, T5, T6, T7, T8> concat(_8<T1, T2, T3, T4, T5, T6, T7, T8> rhs) {
			return new Tuples._9<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9> _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(_9<T1, T2, T3, T4, T5, T6, T7, T8, T9> rhs) {
			return new Tuples._10<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> rhs) {
			return new Tuples._11<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> rhs) {
			return new Tuples._12<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> rhs) {
			return new Tuples._13<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(
				_13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> rhs) {
			return new Tuples._14<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(
				_14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(
				_15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(
				_16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(
				_17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17,
					rhs._18);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17, rhs._18,
					rhs._19);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17, rhs._18,
					rhs._19, rhs._20);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17, rhs._18,
					rhs._19, rhs._20, rhs._21);
		}

		public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_23<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17, rhs._18,
					rhs._19, rhs._20, rhs._21, rhs._22);
		}

		public <R> Tuples._1<R> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0));
		}

		public Tuples._0 remove(Numbers._0 ignored) {
			return tuple();
		}

		public <T> Tuples._2<T0, T> append(T v) {
			return tuple(_0, v);
		}

		public <R> R apply(Functions._1<T0, R> f) {
			return f.apply(_0);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _2<T0, T1> implements Tuple {
		public final T0 _0;
		public final T1 _1;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public _2<T0, T1> concat(_0 rhs) {
			return new Tuples._2<>(_0, _1);
		}

		public <T2> _3<T0, T1, T2> concat(_1<T2> rhs) {
			return new Tuples._3<>(_0, _1, rhs._0);
		}

		public <T2, T3> _4<T0, T1, T2, T3> concat(_2<T2, T3> rhs) {
			return new Tuples._4<>(_0, _1, rhs._0, rhs._1);
		}

		public <T2, T3, T4> _5<T0, T1, T2, T3, T4> concat(_3<T2, T3, T4> rhs) {
			return new Tuples._5<>(_0, _1, rhs._0, rhs._1, rhs._2);
		}

		public <T2, T3, T4, T5> _6<T0, T1, T2, T3, T4, T5> concat(_4<T2, T3, T4, T5> rhs) {
			return new Tuples._6<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T2, T3, T4, T5, T6> _7<T0, T1, T2, T3, T4, T5, T6> concat(_5<T2, T3, T4, T5, T6> rhs) {
			return new Tuples._7<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T2, T3, T4, T5, T6, T7> _8<T0, T1, T2, T3, T4, T5, T6, T7> concat(_6<T2, T3, T4, T5, T6, T7> rhs) {
			return new Tuples._8<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T2, T3, T4, T5, T6, T7, T8> _9<T0, T1, T2, T3, T4, T5, T6, T7, T8> concat(_7<T2, T3, T4, T5, T6, T7, T8> rhs) {
			return new Tuples._9<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9> _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(_8<T2, T3, T4, T5, T6, T7, T8, T9> rhs) {
			return new Tuples._10<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10> _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_9<T2, T3, T4, T5, T6, T7, T8, T9, T10> rhs) {
			return new Tuples._11<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_10<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> rhs) {
			return new Tuples._12<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_11<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> rhs) {
			return new Tuples._13<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(
				_12<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> rhs) {
			return new Tuples._14<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(
				_13<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(
				_14<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(
				_15<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(
				_16<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_17<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_18<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_19<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17,
					rhs._18);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_20<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17,
					rhs._18, rhs._19);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_21<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17,
					rhs._18, rhs._19, rhs._20);
		}

		public <T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_22<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17,
					rhs._18, rhs._19, rhs._20, rhs._21);
		}

		public <R> Tuples._2<R, T1> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1);
		}

		public <R> Tuples._2<T0, R> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1));
		}

		public Tuples._1<T1> remove(Numbers._0 ignored) {
			return tuple(_1);
		}

		public Tuples._1<T0> remove(Numbers._1 ignored) {
			return tuple(_0);
		}

		public <T> Tuples._3<T0, T1, T> append(T v) {
			return tuple(_0, _1, v);
		}

		public <R> R apply(Functions._2<T0, T1, R> f) {
			return f.apply(_0, _1);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _3<T0, T1, T2> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public _3<T0, T1, T2> concat(_0 rhs) {
			return new Tuples._3<>(_0, _1, _2);
		}

		public <T3> _4<T0, T1, T2, T3> concat(_1<T3> rhs) {
			return new Tuples._4<>(_0, _1, _2, rhs._0);
		}

		public <T3, T4> _5<T0, T1, T2, T3, T4> concat(_2<T3, T4> rhs) {
			return new Tuples._5<>(_0, _1, _2, rhs._0, rhs._1);
		}

		public <T3, T4, T5> _6<T0, T1, T2, T3, T4, T5> concat(_3<T3, T4, T5> rhs) {
			return new Tuples._6<>(_0, _1, _2, rhs._0, rhs._1, rhs._2);
		}

		public <T3, T4, T5, T6> _7<T0, T1, T2, T3, T4, T5, T6> concat(_4<T3, T4, T5, T6> rhs) {
			return new Tuples._7<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T3, T4, T5, T6, T7> _8<T0, T1, T2, T3, T4, T5, T6, T7> concat(_5<T3, T4, T5, T6, T7> rhs) {
			return new Tuples._8<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T3, T4, T5, T6, T7, T8> _9<T0, T1, T2, T3, T4, T5, T6, T7, T8> concat(_6<T3, T4, T5, T6, T7, T8> rhs) {
			return new Tuples._9<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T3, T4, T5, T6, T7, T8, T9> _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(_7<T3, T4, T5, T6, T7, T8, T9> rhs) {
			return new Tuples._10<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10> _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_8<T3, T4, T5, T6, T7, T8, T9, T10> rhs) {
			return new Tuples._11<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_9<T3, T4, T5, T6, T7, T8, T9, T10, T11> rhs) {
			return new Tuples._12<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_10<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> rhs) {
			return new Tuples._13<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_11<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> rhs) {
			return new Tuples._14<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(
				_12<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(
				_13<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(
				_14<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(
				_15<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_16<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_17<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_18<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_19<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17,
					rhs._18);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_20<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17,
					rhs._18, rhs._19);
		}

		public <T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_21<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16, rhs._17,
					rhs._18, rhs._19, rhs._20);
		}

		public <R> Tuples._3<R, T1, T2> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2);
		}

		public <R> Tuples._3<T0, R, T2> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2);
		}

		public <R> Tuples._3<T0, T1, R> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2));
		}

		public Tuples._2<T1, T2> remove(Numbers._0 ignored) {
			return tuple(_1, _2);
		}

		public Tuples._2<T0, T2> remove(Numbers._1 ignored) {
			return tuple(_0, _2);
		}

		public Tuples._2<T0, T1> remove(Numbers._2 ignored) {
			return tuple(_0, _1);
		}

		public <T> Tuples._4<T0, T1, T2, T> append(T v) {
			return tuple(_0, _1, _2, v);
		}

		public <R> R apply(Functions._3<T0, T1, T2, R> f) {
			return f.apply(_0, _1, _2);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _4<T0, T1, T2, T3> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public _4<T0, T1, T2, T3> concat(_0 rhs) {
			return new Tuples._4<>(_0, _1, _2, _3);
		}

		public <T4> _5<T0, T1, T2, T3, T4> concat(_1<T4> rhs) {
			return new Tuples._5<>(_0, _1, _2, _3, rhs._0);
		}

		public <T4, T5> _6<T0, T1, T2, T3, T4, T5> concat(_2<T4, T5> rhs) {
			return new Tuples._6<>(_0, _1, _2, _3, rhs._0, rhs._1);
		}

		public <T4, T5, T6> _7<T0, T1, T2, T3, T4, T5, T6> concat(_3<T4, T5, T6> rhs) {
			return new Tuples._7<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2);
		}

		public <T4, T5, T6, T7> _8<T0, T1, T2, T3, T4, T5, T6, T7> concat(_4<T4, T5, T6, T7> rhs) {
			return new Tuples._8<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T4, T5, T6, T7, T8> _9<T0, T1, T2, T3, T4, T5, T6, T7, T8> concat(_5<T4, T5, T6, T7, T8> rhs) {
			return new Tuples._9<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T4, T5, T6, T7, T8, T9> _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(_6<T4, T5, T6, T7, T8, T9> rhs) {
			return new Tuples._10<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T4, T5, T6, T7, T8, T9, T10> _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_7<T4, T5, T6, T7, T8, T9, T10> rhs) {
			return new Tuples._11<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_8<T4, T5, T6, T7, T8, T9, T10, T11> rhs) {
			return new Tuples._12<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_9<T4, T5, T6, T7, T8, T9, T10, T11, T12> rhs) {
			return new Tuples._13<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_10<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> rhs) {
			return new Tuples._14<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_11<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(
				_12<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(
				_13<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(
				_14<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_15<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_16<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_17<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_18<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16,
					rhs._17);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_19<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16,
					rhs._17,
					rhs._18);
		}

		public <T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_20<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16,
					rhs._17,
					rhs._18, rhs._19);
		}

		public <R> Tuples._4<R, T1, T2, T3> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3);
		}

		public <R> Tuples._4<T0, R, T2, T3> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3);
		}

		public <R> Tuples._4<T0, T1, R, T3> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3);
		}

		public <R> Tuples._4<T0, T1, T2, R> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3));
		}

		public Tuples._3<T1, T2, T3> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3);
		}

		public Tuples._3<T0, T2, T3> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3);
		}

		public Tuples._3<T0, T1, T3> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3);
		}

		public Tuples._3<T0, T1, T2> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2);
		}

		public <T> Tuples._5<T0, T1, T2, T3, T> append(T v) {
			return tuple(_0, _1, _2, _3, v);
		}

		public <R> R apply(Functions._4<T0, T1, T2, T3, R> f) {
			return f.apply(_0, _1, _2, _3);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _5<T0, T1, T2, T3, T4> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public _5<T0, T1, T2, T3, T4> concat(_0 rhs) {
			return new Tuples._5<>(_0, _1, _2, _3, _4);
		}

		public <T5> _6<T0, T1, T2, T3, T4, T5> concat(_1<T5> rhs) {
			return new Tuples._6<>(_0, _1, _2, _3, _4, rhs._0);
		}

		public <T5, T6> _7<T0, T1, T2, T3, T4, T5, T6> concat(_2<T5, T6> rhs) {
			return new Tuples._7<>(_0, _1, _2, _3, _4, rhs._0, rhs._1);
		}

		public <T5, T6, T7> _8<T0, T1, T2, T3, T4, T5, T6, T7> concat(_3<T5, T6, T7> rhs) {
			return new Tuples._8<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2);
		}

		public <T5, T6, T7, T8> _9<T0, T1, T2, T3, T4, T5, T6, T7, T8> concat(_4<T5, T6, T7, T8> rhs) {
			return new Tuples._9<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T5, T6, T7, T8, T9> _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(_5<T5, T6, T7, T8, T9> rhs) {
			return new Tuples._10<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T5, T6, T7, T8, T9, T10> _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_6<T5, T6, T7, T8, T9, T10> rhs) {
			return new Tuples._11<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T5, T6, T7, T8, T9, T10, T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_7<T5, T6, T7, T8, T9, T10, T11> rhs) {
			return new Tuples._12<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_8<T5, T6, T7, T8, T9, T10, T11, T12> rhs) {
			return new Tuples._13<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_9<T5, T6, T7, T8, T9, T10, T11, T12, T13> rhs) {
			return new Tuples._14<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_10<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(
				_11<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(
				_12<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(
				_13<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_14<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_15<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_16<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_17<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_18<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16,
					rhs._17);
		}

		public <T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_19<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15, rhs._16,
					rhs._17, rhs._18);
		}

		public <R> Tuples._5<R, T1, T2, T3, T4> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4);
		}

		public <R> Tuples._5<T0, R, T2, T3, T4> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4);
		}

		public <R> Tuples._5<T0, T1, R, T3, T4> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4);
		}

		public <R> Tuples._5<T0, T1, T2, R, T4> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4);
		}

		public <R> Tuples._5<T0, T1, T2, T3, R> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4));
		}

		public Tuples._4<T1, T2, T3, T4> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4);
		}

		public Tuples._4<T0, T2, T3, T4> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4);
		}

		public Tuples._4<T0, T1, T3, T4> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4);
		}

		public Tuples._4<T0, T1, T2, T4> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4);
		}

		public Tuples._4<T0, T1, T2, T3> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3);
		}

		public <T> Tuples._6<T0, T1, T2, T3, T4, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, v);
		}

		public <R> R apply(Functions._5<T0, T1, T2, T3, T4, R> f) {
			return f.apply(_0, _1, _2, _3, _4);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _6<T0, T1, T2, T3, T4, T5> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public _6<T0, T1, T2, T3, T4, T5> concat(_0 rhs) {
			return new Tuples._6<>(_0, _1, _2, _3, _4, _5);
		}

		public <T6> _7<T0, T1, T2, T3, T4, T5, T6> concat(_1<T6> rhs) {
			return new Tuples._7<>(_0, _1, _2, _3, _4, _5, rhs._0);
		}

		public <T6, T7> _8<T0, T1, T2, T3, T4, T5, T6, T7> concat(_2<T6, T7> rhs) {
			return new Tuples._8<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1);
		}

		public <T6, T7, T8> _9<T0, T1, T2, T3, T4, T5, T6, T7, T8> concat(_3<T6, T7, T8> rhs) {
			return new Tuples._9<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2);
		}

		public <T6, T7, T8, T9> _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(_4<T6, T7, T8, T9> rhs) {
			return new Tuples._10<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T6, T7, T8, T9, T10> _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_5<T6, T7, T8, T9, T10> rhs) {
			return new Tuples._11<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T6, T7, T8, T9, T10, T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_6<T6, T7, T8, T9, T10, T11> rhs) {
			return new Tuples._12<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T6, T7, T8, T9, T10, T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_7<T6, T7, T8, T9, T10, T11, T12> rhs) {
			return new Tuples._13<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T6, T7, T8, T9, T10, T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_8<T6, T7, T8, T9, T10, T11, T12, T13> rhs) {
			return new Tuples._14<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T6, T7, T8, T9, T10, T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_9<T6, T7, T8, T9, T10, T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(_10<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(
				_11<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(
				_12<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_13<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_14<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13);
		}

		public <T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_15<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14);
		}

		public <T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_16<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15);
		}

		public <T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_17<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15,
					rhs._16);
		}

		public <T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_18<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15,
					rhs._16,
					rhs._17);
		}

		public <R> Tuples._6<R, T1, T2, T3, T4, T5> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5);
		}

		public <R> Tuples._6<T0, R, T2, T3, T4, T5> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5);
		}

		public <R> Tuples._6<T0, T1, R, T3, T4, T5> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5);
		}

		public <R> Tuples._6<T0, T1, T2, R, T4, T5> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5);
		}

		public <R> Tuples._6<T0, T1, T2, T3, R, T5> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5);
		}

		public <R> Tuples._6<T0, T1, T2, T3, T4, R> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5));
		}

		public Tuples._5<T1, T2, T3, T4, T5> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5);
		}

		public Tuples._5<T0, T2, T3, T4, T5> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5);
		}

		public Tuples._5<T0, T1, T3, T4, T5> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5);
		}

		public Tuples._5<T0, T1, T2, T4, T5> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5);
		}

		public Tuples._5<T0, T1, T2, T3, T5> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5);
		}

		public Tuples._5<T0, T1, T2, T3, T4> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4);
		}

		public <T> Tuples._7<T0, T1, T2, T3, T4, T5, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, v);
		}

		public <R> R apply(Functions._6<T0, T1, T2, T3, T4, T5, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _7<T0, T1, T2, T3, T4, T5, T6> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public _7<T0, T1, T2, T3, T4, T5, T6> concat(_0 rhs) {
			return new Tuples._7<>(_0, _1, _2, _3, _4, _5, _6);
		}

		public <T7> _8<T0, T1, T2, T3, T4, T5, T6, T7> concat(_1<T7> rhs) {
			return new Tuples._8<>(_0, _1, _2, _3, _4, _5, _6, rhs._0);
		}

		public <T7, T8> _9<T0, T1, T2, T3, T4, T5, T6, T7, T8> concat(_2<T7, T8> rhs) {
			return new Tuples._9<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1);
		}

		public <T7, T8, T9> _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(_3<T7, T8, T9> rhs) {
			return new Tuples._10<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2);
		}

		public <T7, T8, T9, T10> _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_4<T7, T8, T9, T10> rhs) {
			return new Tuples._11<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T7, T8, T9, T10, T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_5<T7, T8, T9, T10, T11> rhs) {
			return new Tuples._12<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T7, T8, T9, T10, T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_6<T7, T8, T9, T10, T11, T12> rhs) {
			return new Tuples._13<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T7, T8, T9, T10, T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_7<T7, T8, T9, T10, T11, T12, T13> rhs) {
			return new Tuples._14<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T7, T8, T9, T10, T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_8<T7, T8, T9, T10, T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T7, T8, T9, T10, T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(_9<T7, T8, T9, T10, T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(
				_10<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(
				_11<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_12<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_13<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_14<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13);
		}

		public <T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_15<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14);
		}

		public <T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_16<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15);
		}

		public <T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_17<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14, rhs._15,
					rhs._16);
		}

		public <R> Tuples._7<R, T1, T2, T3, T4, T5, T6> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6);
		}

		public <R> Tuples._7<T0, R, T2, T3, T4, T5, T6> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6);
		}

		public <R> Tuples._7<T0, T1, R, T3, T4, T5, T6> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6);
		}

		public <R> Tuples._7<T0, T1, T2, R, T4, T5, T6> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6);
		}

		public <R> Tuples._7<T0, T1, T2, T3, R, T5, T6> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6);
		}

		public <R> Tuples._7<T0, T1, T2, T3, T4, R, T6> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6);
		}

		public <R> Tuples._7<T0, T1, T2, T3, T4, T5, R> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6));
		}

		public Tuples._6<T1, T2, T3, T4, T5, T6> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6);
		}

		public Tuples._6<T0, T2, T3, T4, T5, T6> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6);
		}

		public Tuples._6<T0, T1, T3, T4, T5, T6> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6);
		}

		public Tuples._6<T0, T1, T2, T4, T5, T6> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6);
		}

		public Tuples._6<T0, T1, T2, T3, T5, T6> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6);
		}

		public Tuples._6<T0, T1, T2, T3, T4, T6> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6);
		}

		public Tuples._6<T0, T1, T2, T3, T4, T5> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5);
		}

		public <T> Tuples._8<T0, T1, T2, T3, T4, T5, T6, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, v);
		}

		public <R> R apply(Functions._7<T0, T1, T2, T3, T4, T5, T6, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _8<T0, T1, T2, T3, T4, T5, T6, T7> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public _8<T0, T1, T2, T3, T4, T5, T6, T7> concat(_0 rhs) {
			return new Tuples._8<>(_0, _1, _2, _3, _4, _5, _6, _7);
		}

		public <T8> _9<T0, T1, T2, T3, T4, T5, T6, T7, T8> concat(_1<T8> rhs) {
			return new Tuples._9<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0);
		}

		public <T8, T9> _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(_2<T8, T9> rhs) {
			return new Tuples._10<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1);
		}

		public <T8, T9, T10> _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_3<T8, T9, T10> rhs) {
			return new Tuples._11<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2);
		}

		public <T8, T9, T10, T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_4<T8, T9, T10, T11> rhs) {
			return new Tuples._12<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T8, T9, T10, T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_5<T8, T9, T10, T11, T12> rhs) {
			return new Tuples._13<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T8, T9, T10, T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_6<T8, T9, T10, T11, T12, T13> rhs) {
			return new Tuples._14<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T8, T9, T10, T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_7<T8, T9, T10, T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T8, T9, T10, T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(_8<T8, T9, T10, T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T8, T9, T10, T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(_9<T8, T9, T10, T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(
				_10<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_11<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_12<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_13<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_14<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13);
		}

		public <T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_15<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14);
		}

		public <T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_16<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14,
					rhs._15);
		}

		public <R> Tuples._8<R, T1, T2, T3, T4, T5, T6, T7> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7);
		}

		public <R> Tuples._8<T0, R, T2, T3, T4, T5, T6, T7> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7);
		}

		public <R> Tuples._8<T0, T1, R, T3, T4, T5, T6, T7> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7);
		}

		public <R> Tuples._8<T0, T1, T2, R, T4, T5, T6, T7> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7);
		}

		public <R> Tuples._8<T0, T1, T2, T3, R, T5, T6, T7> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7);
		}

		public <R> Tuples._8<T0, T1, T2, T3, T4, R, T6, T7> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7);
		}

		public <R> Tuples._8<T0, T1, T2, T3, T4, T5, R, T7> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7);
		}

		public <R> Tuples._8<T0, T1, T2, T3, T4, T5, T6, R> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7));
		}

		public Tuples._7<T1, T2, T3, T4, T5, T6, T7> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7);
		}

		public Tuples._7<T0, T2, T3, T4, T5, T6, T7> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7);
		}

		public Tuples._7<T0, T1, T3, T4, T5, T6, T7> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7);
		}

		public Tuples._7<T0, T1, T2, T4, T5, T6, T7> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7);
		}

		public Tuples._7<T0, T1, T2, T3, T5, T6, T7> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7);
		}

		public Tuples._7<T0, T1, T2, T3, T4, T6, T7> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7);
		}

		public Tuples._7<T0, T1, T2, T3, T4, T5, T7> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7);
		}

		public Tuples._7<T0, T1, T2, T3, T4, T5, T6> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6);
		}

		public <T> Tuples._9<T0, T1, T2, T3, T4, T5, T6, T7, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, v);
		}

		public <R> R apply(Functions._8<T0, T1, T2, T3, T4, T5, T6, T7, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _9<T0, T1, T2, T3, T4, T5, T6, T7, T8> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public _9<T0, T1, T2, T3, T4, T5, T6, T7, T8> concat(_0 rhs) {
			return new Tuples._9<>(_0, _1, _2, _3, _4, _5, _6, _7, _8);
		}

		public <T9> _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(_1<T9> rhs) {
			return new Tuples._10<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0);
		}

		public <T9, T10> _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_2<T9, T10> rhs) {
			return new Tuples._11<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1);
		}

		public <T9, T10, T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_3<T9, T10, T11> rhs) {
			return new Tuples._12<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2);
		}

		public <T9, T10, T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_4<T9, T10, T11, T12> rhs) {
			return new Tuples._13<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T9, T10, T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_5<T9, T10, T11, T12, T13> rhs) {
			return new Tuples._14<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T9, T10, T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_6<T9, T10, T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T9, T10, T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(_7<T9, T10, T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T9, T10, T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(_8<T9, T10, T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T9, T10, T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(
				_9<T9, T10, T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_10<T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_11<T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_12<T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_13<T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_14<T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13);
		}

		public <T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_15<T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13, rhs._14);
		}

		public <R> Tuples._9<R, T1, T2, T3, T4, T5, T6, T7, T8> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8);
		}

		public <R> Tuples._9<T0, R, T2, T3, T4, T5, T6, T7, T8> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8);
		}

		public <R> Tuples._9<T0, T1, R, T3, T4, T5, T6, T7, T8> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8);
		}

		public <R> Tuples._9<T0, T1, T2, R, T4, T5, T6, T7, T8> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8);
		}

		public <R> Tuples._9<T0, T1, T2, T3, R, T5, T6, T7, T8> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8);
		}

		public <R> Tuples._9<T0, T1, T2, T3, T4, R, T6, T7, T8> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8);
		}

		public <R> Tuples._9<T0, T1, T2, T3, T4, T5, R, T7, T8> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8);
		}

		public <R> Tuples._9<T0, T1, T2, T3, T4, T5, T6, R, T8> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8);
		}

		public <R> Tuples._9<T0, T1, T2, T3, T4, T5, T6, T7, R> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8));
		}

		public Tuples._8<T1, T2, T3, T4, T5, T6, T7, T8> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8);
		}

		public Tuples._8<T0, T2, T3, T4, T5, T6, T7, T8> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8);
		}

		public Tuples._8<T0, T1, T3, T4, T5, T6, T7, T8> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8);
		}

		public Tuples._8<T0, T1, T2, T4, T5, T6, T7, T8> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8);
		}

		public Tuples._8<T0, T1, T2, T3, T5, T6, T7, T8> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8);
		}

		public Tuples._8<T0, T1, T2, T3, T4, T6, T7, T8> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8);
		}

		public Tuples._8<T0, T1, T2, T3, T4, T5, T7, T8> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8);
		}

		public Tuples._8<T0, T1, T2, T3, T4, T5, T6, T8> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8);
		}

		public Tuples._8<T0, T1, T2, T3, T4, T5, T6, T7> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7);
		}

		public <T> Tuples._10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, v);
		}

		public <R> R apply(Functions._9<T0, T1, T2, T3, T4, T5, T6, T7, T8, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public _10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(_0 rhs) {
			return new Tuples._10<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9);
		}

		public <T10> _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_1<T10> rhs) {
			return new Tuples._11<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0);
		}

		public <T10, T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_2<T10, T11> rhs) {
			return new Tuples._12<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1);
		}

		public <T10, T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_3<T10, T11, T12> rhs) {
			return new Tuples._13<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2);
		}

		public <T10, T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_4<T10, T11, T12, T13> rhs) {
			return new Tuples._14<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T10, T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_5<T10, T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T10, T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(_6<T10, T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T10, T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(_7<T10, T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T10, T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(_8<T10, T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T10, T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_9<T10, T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_10<T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_11<T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_12<T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_13<T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_14<T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12, rhs._13);
		}

		public <R> Tuples._10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9);
		}

		public <R> Tuples._10<T0, R, T2, T3, T4, T5, T6, T7, T8, T9> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9);
		}

		public <R> Tuples._10<T0, T1, R, T3, T4, T5, T6, T7, T8, T9> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9);
		}

		public <R> Tuples._10<T0, T1, T2, R, T4, T5, T6, T7, T8, T9> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9);
		}

		public <R> Tuples._10<T0, T1, T2, T3, R, T5, T6, T7, T8, T9> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9);
		}

		public <R> Tuples._10<T0, T1, T2, T3, T4, R, T6, T7, T8, T9> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9);
		}

		public <R> Tuples._10<T0, T1, T2, T3, T4, T5, R, T7, T8, T9> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9);
		}

		public <R> Tuples._10<T0, T1, T2, T3, T4, T5, T6, R, T8, T9> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9);
		}

		public <R> Tuples._10<T0, T1, T2, T3, T4, T5, T6, T7, R, T9> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9);
		}

		public <R> Tuples._10<T0, T1, T2, T3, T4, T5, T6, T7, T8, R> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9));
		}

		public Tuples._9<T1, T2, T3, T4, T5, T6, T7, T8, T9> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9);
		}

		public Tuples._9<T0, T2, T3, T4, T5, T6, T7, T8, T9> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9);
		}

		public Tuples._9<T0, T1, T3, T4, T5, T6, T7, T8, T9> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9);
		}

		public Tuples._9<T0, T1, T2, T4, T5, T6, T7, T8, T9> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9);
		}

		public Tuples._9<T0, T1, T2, T3, T5, T6, T7, T8, T9> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9);
		}

		public Tuples._9<T0, T1, T2, T3, T4, T6, T7, T8, T9> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9);
		}

		public Tuples._9<T0, T1, T2, T3, T4, T5, T7, T8, T9> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9);
		}

		public Tuples._9<T0, T1, T2, T3, T4, T5, T6, T8, T9> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9);
		}

		public Tuples._9<T0, T1, T2, T3, T4, T5, T6, T7, T9> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9);
		}

		public Tuples._9<T0, T1, T2, T3, T4, T5, T6, T7, T8> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8);
		}

		public <T> Tuples._11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, v);
		}

		public <R> R apply(Functions._10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public _11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> concat(_0 rhs) {
			return new Tuples._11<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10);
		}

		public <T11> _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_1<T11> rhs) {
			return new Tuples._12<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0);
		}

		public <T11, T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_2<T11, T12> rhs) {
			return new Tuples._13<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1);
		}

		public <T11, T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_3<T11, T12, T13> rhs) {
			return new Tuples._14<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1, rhs._2);
		}

		public <T11, T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_4<T11, T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T11, T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(_5<T11, T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T11, T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(_6<T11, T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T11, T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(_7<T11, T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T11, T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(
				_8<T11, T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T11, T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_9<T11, T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_10<T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_11<T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_12<T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_13<T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11, rhs._12);
		}

		public <R> Tuples._11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10);
		}

		public <R> Tuples._11<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10);
		}

		public <R> Tuples._11<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10);
		}

		public <R> Tuples._11<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10);
		}

		public <R> Tuples._11<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10);
		}

		public <R> Tuples._11<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10);
		}

		public <R> Tuples._11<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10);
		}

		public <R> Tuples._11<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10);
		}

		public <R> Tuples._11<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10);
		}

		public <R> Tuples._11<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10);
		}

		public <R> Tuples._11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10));
		}

		public Tuples._10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10);
		}

		public Tuples._10<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10);
		}

		public Tuples._10<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10);
		}

		public Tuples._10<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10);
		}

		public Tuples._10<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10);
		}

		public Tuples._10<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10);
		}

		public Tuples._10<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10);
		}

		public Tuples._10<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10);
		}

		public Tuples._10<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10);
		}

		public Tuples._10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10);
		}

		public Tuples._10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9);
		}

		public <T> Tuples._12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, v);
		}

		public <R> R apply(Functions._11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public _12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> concat(_0 rhs) {
			return new Tuples._12<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11);
		}

		public <T12> _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_1<T12> rhs) {
			return new Tuples._13<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0);
		}

		public <T12, T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_2<T12, T13> rhs) {
			return new Tuples._14<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0, rhs._1);
		}

		public <T12, T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_3<T12, T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0, rhs._1, rhs._2);
		}

		public <T12, T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(_4<T12, T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T12, T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(_5<T12, T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T12, T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(_6<T12, T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T12, T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(_7<T12, T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T12, T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(
				_8<T12, T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T12, T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_9<T12, T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_10<T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_11<T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_12<T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10, rhs._11);
		}

		public <R> Tuples._12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11);
		}

		public <R> Tuples._12<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11);
		}

		public <R> Tuples._12<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11);
		}

		public <R> Tuples._12<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11);
		}

		public <R> Tuples._12<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11);
		}

		public <R> Tuples._12<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11);
		}

		public <R> Tuples._12<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11);
		}

		public <R> Tuples._12<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11);
		}

		public <R> Tuples._12<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11);
		}

		public <R> Tuples._12<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11);
		}

		public <R> Tuples._12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11);
		}

		public <R> Tuples._12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11));
		}

		public Tuples._11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11);
		}

		public Tuples._11<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11);
		}

		public Tuples._11<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11);
		}

		public Tuples._11<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11);
		}

		public Tuples._11<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11);
		}

		public Tuples._11<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11);
		}

		public Tuples._11<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11);
		}

		public Tuples._11<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11);
		}

		public Tuples._11<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11);
		}

		public Tuples._11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11);
		}

		public Tuples._11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11);
		}

		public Tuples._11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10);
		}

		public <T> Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, v);
		}

		public <R> R apply(Functions._12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public _13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> concat(_0 rhs) {
			return new Tuples._13<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public <T13> _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_1<T13> rhs) {
			return new Tuples._14<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, rhs._0);
		}

		public <T13, T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_2<T13, T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, rhs._0, rhs._1);
		}

		public <T13, T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(_3<T13, T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, rhs._0, rhs._1, rhs._2);
		}

		public <T13, T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(_4<T13, T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T13, T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(_5<T13, T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T13, T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(_6<T13, T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T13, T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(_7<T13, T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T13, T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_8<T13, T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T13, T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_9<T13, T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_10<T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_11<T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9, rhs._10);
		}

		public <R> Tuples._13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public <R> Tuples._13<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public <R> Tuples._13<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public <R> Tuples._13<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public <R> Tuples._13<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public <R> Tuples._13<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12);
		}

		public <R> Tuples._13<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12);
		}

		public <R> Tuples._13<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12);
		}

		public <R> Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12);
		}

		public <R> Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12);
		}

		public <R> Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12);
		}

		public <R> Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12);
		}

		public <R> Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12));
		}

		public Tuples._12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public Tuples._12<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public Tuples._12<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public Tuples._12<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public Tuples._12<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public Tuples._12<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12);
		}

		public Tuples._12<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12);
		}

		public Tuples._12<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12);
		}

		public Tuples._12<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12);
		}

		public Tuples._12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12);
		}

		public Tuples._12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12);
		}

		public Tuples._12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12);
		}

		public Tuples._12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11);
		}

		public <T> Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, v);
		}

		public <R> R apply(Functions._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;
		public final T13 _13;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public T13 _13() {
			return _13;
		}

		public _14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> concat(_0 rhs) {
			return new Tuples._14<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public <T14> _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_1<T14> rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, rhs._0);
		}

		public <T14, T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(_2<T14, T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, rhs._0, rhs._1);
		}

		public <T14, T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(_3<T14, T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, rhs._0, rhs._1, rhs._2);
		}

		public <T14, T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(_4<T14, T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T14, T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(_5<T14, T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T14, T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(_6<T14, T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T14, T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(
				_7<T14, T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T14, T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_8<T14, T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T14, T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_9<T14, T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_10<T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8, rhs._9);
		}

		public <R> Tuples._14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public <R> Tuples._14<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public <R> Tuples._14<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public <R> Tuples._14<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public <R> Tuples._14<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12, T13> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public <R> Tuples._14<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12, T13> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public <R> Tuples._14<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12, T13> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12, _13);
		}

		public <R> Tuples._14<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12, T13> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12, _13);
		}

		public <R> Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12, T13> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12, _13);
		}

		public <R> Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12, T13> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12, _13);
		}

		public <R> Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12, T13> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12, _13);
		}

		public <R> Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12, T13> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12, _13);
		}

		public <R> Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R, T13> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12), _13);
		}

		public <R> Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> map(Numbers._13 ignored, Functions._1<T13, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, mapper.apply(_13));
		}

		public Tuples._13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public Tuples._13<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public Tuples._13<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public Tuples._13<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public Tuples._13<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12, T13> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public Tuples._13<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12, T13> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public Tuples._13<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12, T13> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12, _13);
		}

		public Tuples._13<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12, T13> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12, _13);
		}

		public Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12, T13> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12, _13);
		}

		public Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12, T13> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12, _13);
		}

		public Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12, T13> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12, _13);
		}

		public Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12, T13> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12, _13);
		}

		public Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T13> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _13);
		}

		public Tuples._13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> remove(Numbers._13 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12);
		}

		public <T> Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, v);
		}

		public <R> R apply(Functions._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;
		public final T13 _13;
		public final T14 _14;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public T13 _13() {
			return _13;
		}

		public T14 _14() {
			return _14;
		}

		public _15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> concat(_0 rhs) {
			return new Tuples._15<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public <T15> _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(_1<T15> rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, rhs._0);
		}

		public <T15, T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(_2<T15, T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, rhs._0, rhs._1);
		}

		public <T15, T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(_3<T15, T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, rhs._0, rhs._1, rhs._2);
		}

		public <T15, T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(_4<T15, T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T15, T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(_5<T15, T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T15, T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(_6<T15, T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T15, T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(
				_7<T15, T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T15, T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_8<T15, T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <T15, T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_9<T15, T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7, rhs._8);
		}

		public <R> Tuples._15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public <R> Tuples._15<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public <R> Tuples._15<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public <R> Tuples._15<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public <R> Tuples._15<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public <R> Tuples._15<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12, T13, T14> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public <R> Tuples._15<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12, T13, T14> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public <R> Tuples._15<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12, T13, T14> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12, _13, _14);
		}

		public <R> Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12, T13, T14> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12, _13, _14);
		}

		public <R> Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12, T13, T14> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12, _13, _14);
		}

		public <R> Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12, T13, T14> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12, _13, _14);
		}

		public <R> Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12, T13, T14> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12, _13, _14);
		}

		public <R> Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R, T13, T14> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12), _13, _14);
		}

		public <R> Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R, T14> map(Numbers._13 ignored, Functions._1<T13, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, mapper.apply(_13), _14);
		}

		public <R> Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> map(Numbers._14 ignored, Functions._1<T14, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, mapper.apply(_14));
		}

		public Tuples._14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public Tuples._14<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public Tuples._14<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public Tuples._14<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public Tuples._14<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public Tuples._14<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12, T13, T14> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public Tuples._14<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12, T13, T14> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public Tuples._14<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12, T13, T14> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12, _13, _14);
		}

		public Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12, T13, T14> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12, _13, _14);
		}

		public Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12, T13, T14> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12, _13, _14);
		}

		public Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12, T13, T14> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12, _13, _14);
		}

		public Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12, T13, T14> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12, _13, _14);
		}

		public Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T13, T14> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _13, _14);
		}

		public Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T14> remove(Numbers._13 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _14);
		}

		public Tuples._14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> remove(Numbers._14 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
		}

		public <T> Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, v);
		}

		public <R> R apply(Functions._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;
		public final T13 _13;
		public final T14 _14;
		public final T15 _15;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public T13 _13() {
			return _13;
		}

		public T14 _14() {
			return _14;
		}

		public T15 _15() {
			return _15;
		}

		public _16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> concat(_0 rhs) {
			return new Tuples._16<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public <T16> _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(_1<T16> rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, rhs._0);
		}

		public <T16, T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(_2<T16, T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, rhs._0, rhs._1);
		}

		public <T16, T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(_3<T16, T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, rhs._0, rhs._1, rhs._2);
		}

		public <T16, T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(_4<T16, T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T16, T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(_5<T16, T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T16, T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(_6<T16, T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T16, T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_7<T16, T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <T16, T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_8<T16, T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6, rhs._7);
		}

		public <R> Tuples._16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12, T13, T14, T15> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12, T13, T14, T15> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12, T13, T14, T15> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12, T13, T14, T15> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12, T13, T14, T15> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12, T13, T14, T15> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12, _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R, T13, T14, T15> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12), _13, _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R, T14, T15> map(Numbers._13 ignored, Functions._1<T13, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, mapper.apply(_13), _14, _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R, T15> map(Numbers._14 ignored, Functions._1<T14, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, mapper.apply(_14), _15);
		}

		public <R> Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> map(Numbers._15 ignored, Functions._1<T15, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, mapper.apply(_15));
		}

		public Tuples._15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12, T13, T14, T15> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12, T13, T14, T15> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12, T13, T14, T15> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12, T13, T14, T15> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12, T13, T14, T15> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12, T13, T14, T15> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T13, T14, T15> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _13, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T14, T15> remove(Numbers._13 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _14, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T15> remove(Numbers._14 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _15);
		}

		public Tuples._15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> remove(Numbers._15 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
		}

		public <T> Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, v);
		}

		public <R> R apply(Functions._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;
		public final T13 _13;
		public final T14 _14;
		public final T15 _15;
		public final T16 _16;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public T13 _13() {
			return _13;
		}

		public T14 _14() {
			return _14;
		}

		public T15 _15() {
			return _15;
		}

		public T16 _16() {
			return _16;
		}

		public _17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> concat(_0 rhs) {
			return new Tuples._17<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public <T17> _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(_1<T17> rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, rhs._0);
		}

		public <T17, T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(_2<T17, T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, rhs._0, rhs._1);
		}

		public <T17, T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(_3<T17, T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, rhs._0, rhs._1, rhs._2);
		}

		public <T17, T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(_4<T17, T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T17, T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(_5<T17, T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T17, T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(
				_6<T17, T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <T17, T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_7<T17, T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5, rhs._6);
		}

		public <R> Tuples._17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12, T13, T14, T15, T16> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12, T13, T14, T15, T16> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12, T13, T14, T15, T16> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12, T13, T14, T15, T16> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12, T13, T14, T15, T16> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12, _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R, T13, T14, T15, T16> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12), _13, _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R, T14, T15, T16> map(Numbers._13 ignored, Functions._1<T13, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, mapper.apply(_13), _14, _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R, T15, T16> map(Numbers._14 ignored, Functions._1<T14, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, mapper.apply(_14), _15, _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R, T16> map(Numbers._15 ignored, Functions._1<T15, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, mapper.apply(_15), _16);
		}

		public <R> Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> map(Numbers._16 ignored, Functions._1<T16, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, mapper.apply(_16));
		}

		public Tuples._16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12, T13, T14, T15, T16> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12, T13, T14, T15, T16> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12, T13, T14, T15, T16> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12, T13, T14, T15, T16> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12, T13, T14, T15, T16> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T13, T14, T15, T16> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _13, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T14, T15, T16> remove(Numbers._13 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _14, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T15, T16> remove(Numbers._14 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _15, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T16> remove(Numbers._15 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _16);
		}

		public Tuples._16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> remove(Numbers._16 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
		}

		public <T> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, v);
		}

		public <R> R apply(Functions._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;
		public final T13 _13;
		public final T14 _14;
		public final T15 _15;
		public final T16 _16;
		public final T17 _17;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public T13 _13() {
			return _13;
		}

		public T14 _14() {
			return _14;
		}

		public T15 _15() {
			return _15;
		}

		public T16 _16() {
			return _16;
		}

		public T17 _17() {
			return _17;
		}

		public _18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> concat(_0 rhs) {
			return new Tuples._18<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <T18> _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(_1<T18> rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, rhs._0);
		}

		public <T18, T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(_2<T18, T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, rhs._0, rhs._1);
		}

		public <T18, T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(_3<T18, T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, rhs._0, rhs._1, rhs._2);
		}

		public <T18, T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(_4<T18, T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T18, T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(_5<T18, T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <T18, T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(
				_6<T18, T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4, rhs._5);
		}

		public <R> Tuples._18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12, T13, T14, T15, T16, T17> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12, T13, T14, T15, T16, T17> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12, T13, T14, T15, T16, T17> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12, T13, T14, T15, T16, T17> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12, _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R, T13, T14, T15, T16, T17> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12), _13, _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R, T14, T15, T16, T17> map(Numbers._13 ignored, Functions._1<T13, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, mapper.apply(_13), _14, _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R, T15, T16, T17> map(Numbers._14 ignored, Functions._1<T14, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, mapper.apply(_14), _15, _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R, T16, T17> map(Numbers._15 ignored, Functions._1<T15, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, mapper.apply(_15), _16, _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R, T17> map(Numbers._16 ignored, Functions._1<T16, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, mapper.apply(_16), _17);
		}

		public <R> Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> map(Numbers._17 ignored, Functions._1<T17, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, mapper.apply(_17));
		}

		public Tuples._17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12, T13, T14, T15, T16, T17> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T13, T14, T15, T16, T17> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _13, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T14, T15, T16, T17> remove(Numbers._13 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _14, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T15, T16, T17> remove(Numbers._14 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _15, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T16, T17> remove(Numbers._15 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _16, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T17> remove(Numbers._16 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _17);
		}

		public Tuples._17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> remove(Numbers._17 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
		}

		public <T> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, v);
		}

		public <R> R apply(Functions._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;
		public final T13 _13;
		public final T14 _14;
		public final T15 _15;
		public final T16 _16;
		public final T17 _17;
		public final T18 _18;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public T13 _13() {
			return _13;
		}

		public T14 _14() {
			return _14;
		}

		public T15 _15() {
			return _15;
		}

		public T16 _16() {
			return _16;
		}

		public T17 _17() {
			return _17;
		}

		public T18 _18() {
			return _18;
		}

		public _19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> concat(_0 rhs) {
			return new Tuples._19<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <T19> _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(_1<T19> rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, rhs._0);
		}

		public <T19, T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(_2<T19, T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, rhs._0, rhs._1);
		}

		public <T19, T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(_3<T19, T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, rhs._0, rhs._1, rhs._2);
		}

		public <T19, T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(_4<T19, T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <T19, T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(_5<T19, T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, rhs._0, rhs._1, rhs._2, rhs._3, rhs._4);
		}

		public <R> Tuples._19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12, T13, T14, T15, T16, T17, T18> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12, T13, T14, T15, T16, T17, T18> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12, T13, T14, T15, T16, T17, T18> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12, _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R, T13, T14, T15, T16, T17, T18> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12), _13, _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R, T14, T15, T16, T17, T18> map(Numbers._13 ignored, Functions._1<T13, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, mapper.apply(_13), _14, _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R, T15, T16, T17, T18> map(Numbers._14 ignored, Functions._1<T14, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, mapper.apply(_14), _15, _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R, T16, T17, T18> map(Numbers._15 ignored, Functions._1<T15, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, mapper.apply(_15), _16, _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R, T17, T18> map(Numbers._16 ignored, Functions._1<T16, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, mapper.apply(_16), _17, _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R, T18> map(Numbers._17 ignored, Functions._1<T17, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, mapper.apply(_17), _18);
		}

		public <R> Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> map(Numbers._18 ignored, Functions._1<T18, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, mapper.apply(_18));
		}

		public Tuples._18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T13, T14, T15, T16, T17, T18> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _13, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T14, T15, T16, T17, T18> remove(Numbers._13 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _14, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T15, T16, T17, T18> remove(Numbers._14 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _15, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T16, T17, T18> remove(Numbers._15 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _16, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T17, T18> remove(Numbers._16 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _17, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T18> remove(Numbers._17 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _18);
		}

		public Tuples._18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> remove(Numbers._18 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17);
		}

		public <T> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, v);
		}

		public <R> R apply(Functions._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;
		public final T13 _13;
		public final T14 _14;
		public final T15 _15;
		public final T16 _16;
		public final T17 _17;
		public final T18 _18;
		public final T19 _19;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public T13 _13() {
			return _13;
		}

		public T14 _14() {
			return _14;
		}

		public T15 _15() {
			return _15;
		}

		public T16 _16() {
			return _16;
		}

		public T17 _17() {
			return _17;
		}

		public T18 _18() {
			return _18;
		}

		public T19 _19() {
			return _19;
		}

		public _20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> concat(_0 rhs) {
			return new Tuples._20<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <T20> _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(_1<T20> rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, rhs._0);
		}

		public <T20, T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(_2<T20, T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, rhs._0, rhs._1);
		}

		public <T20, T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(_3<T20, T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, rhs._0, rhs._1, rhs._2);
		}

		public <T20, T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(_4<T20, T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, rhs._0, rhs._1, rhs._2, rhs._3);
		}

		public <R> Tuples._20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12, T13, T14, T15, T16, T17, T18, T19> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R, T13, T14, T15, T16, T17, T18, T19> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12), _13, _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R, T14, T15, T16, T17, T18, T19> map(Numbers._13 ignored, Functions._1<T13, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, mapper.apply(_13), _14, _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R, T15, T16, T17, T18, T19> map(Numbers._14 ignored, Functions._1<T14, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, mapper.apply(_14), _15, _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R, T16, T17, T18, T19> map(Numbers._15 ignored, Functions._1<T15, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, mapper.apply(_15), _16, _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R, T17, T18, T19> map(Numbers._16 ignored, Functions._1<T16, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, mapper.apply(_16), _17, _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R, T18, T19> map(Numbers._17 ignored, Functions._1<T17, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, mapper.apply(_17), _18, _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R, T19> map(Numbers._18 ignored, Functions._1<T18, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, mapper.apply(_18), _19);
		}

		public <R> Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> map(Numbers._19 ignored, Functions._1<T19, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, mapper.apply(_19));
		}

		public Tuples._19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _13, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T14, T15, T16, T17, T18, T19> remove(Numbers._13 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _14, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T15, T16, T17, T18, T19> remove(Numbers._14 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _15, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T16, T17, T18, T19> remove(Numbers._15 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _16, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T17, T18, T19> remove(Numbers._16 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _17, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T18, T19> remove(Numbers._17 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _18, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T19> remove(Numbers._18 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _19);
		}

		public Tuples._19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> remove(Numbers._19 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18);
		}

		public <T> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, v);
		}

		public <R> R apply(Functions._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;
		public final T13 _13;
		public final T14 _14;
		public final T15 _15;
		public final T16 _16;
		public final T17 _17;
		public final T18 _18;
		public final T19 _19;
		public final T20 _20;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public T13 _13() {
			return _13;
		}

		public T14 _14() {
			return _14;
		}

		public T15 _15() {
			return _15;
		}

		public T16 _16() {
			return _16;
		}

		public T17 _17() {
			return _17;
		}

		public T18 _18() {
			return _18;
		}

		public T19 _19() {
			return _19;
		}

		public T20 _20() {
			return _20;
		}

		public _21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> concat(_0 rhs) {
			return new Tuples._21<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <T21> _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(_1<T21> rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, rhs._0);
		}

		public <T21, T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(_2<T21, T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, rhs._0, rhs._1);
		}

		public <T21, T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(_3<T21, T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, rhs._0, rhs._1, rhs._2);
		}

		public <R> Tuples._21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R, T13, T14, T15, T16, T17, T18, T19, T20> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12), _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R, T14, T15, T16, T17, T18, T19, T20> map(Numbers._13 ignored, Functions._1<T13, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, mapper.apply(_13), _14, _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R, T15, T16, T17, T18, T19, T20> map(Numbers._14 ignored, Functions._1<T14, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, mapper.apply(_14), _15, _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R, T16, T17, T18, T19, T20> map(Numbers._15 ignored, Functions._1<T15, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, mapper.apply(_15), _16, _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R, T17, T18, T19, T20> map(Numbers._16 ignored, Functions._1<T16, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, mapper.apply(_16), _17, _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R, T18, T19, T20> map(Numbers._17 ignored, Functions._1<T17, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, mapper.apply(_17), _18, _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R, T19, T20> map(Numbers._18 ignored, Functions._1<T18, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, mapper.apply(_18), _19, _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R, T20> map(Numbers._19 ignored, Functions._1<T19, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, mapper.apply(_19), _20);
		}

		public <R> Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> map(Numbers._20 ignored, Functions._1<T20, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, mapper.apply(_20));
		}

		public Tuples._20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._13 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _14, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T15, T16, T17, T18, T19, T20> remove(Numbers._14 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _15, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T16, T17, T18, T19, T20> remove(Numbers._15 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _16, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T17, T18, T19, T20> remove(Numbers._16 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _17, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T18, T19, T20> remove(Numbers._17 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _18, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T19, T20> remove(Numbers._18 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _19, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T20> remove(Numbers._19 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _20);
		}

		public Tuples._20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> remove(Numbers._20 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19);
		}

		public <T> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, v);
		}

		public <R> R apply(Functions._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;
		public final T13 _13;
		public final T14 _14;
		public final T15 _15;
		public final T16 _16;
		public final T17 _17;
		public final T18 _18;
		public final T19 _19;
		public final T20 _20;
		public final T21 _21;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public T13 _13() {
			return _13;
		}

		public T14 _14() {
			return _14;
		}

		public T15 _15() {
			return _15;
		}

		public T16 _16() {
			return _16;
		}

		public T17 _17() {
			return _17;
		}

		public T18 _18() {
			return _18;
		}

		public T19 _19() {
			return _19;
		}

		public T20 _20() {
			return _20;
		}

		public T21 _21() {
			return _21;
		}

		public _22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> concat(_0 rhs) {
			return new Tuples._22<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <T22> _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(_1<T22> rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, rhs._0);
		}

		public <T22, T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(_2<T22, T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, rhs._0, rhs._1);
		}

		public <R> Tuples._22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R, T13, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12), _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R, T14, T15, T16, T17, T18, T19, T20, T21> map(Numbers._13 ignored, Functions._1<T13, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, mapper.apply(_13), _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R, T15, T16, T17, T18, T19, T20, T21> map(Numbers._14 ignored, Functions._1<T14, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, mapper.apply(_14), _15, _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R, T16, T17, T18, T19, T20, T21> map(Numbers._15 ignored, Functions._1<T15, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, mapper.apply(_15), _16, _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R, T17, T18, T19, T20, T21> map(Numbers._16 ignored, Functions._1<T16, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, mapper.apply(_16), _17, _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R, T18, T19, T20, T21> map(Numbers._17 ignored, Functions._1<T17, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, mapper.apply(_17), _18, _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R, T19, T20, T21> map(Numbers._18 ignored, Functions._1<T18, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, mapper.apply(_18), _19, _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R, T20, T21> map(Numbers._19 ignored, Functions._1<T19, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, mapper.apply(_19), _20, _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R, T21> map(Numbers._20 ignored, Functions._1<T20, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, mapper.apply(_20), _21);
		}

		public <R> Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> map(Numbers._21 ignored, Functions._1<T21, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, mapper.apply(_21));
		}

		public Tuples._21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._13 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._14 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _15, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T16, T17, T18, T19, T20, T21> remove(Numbers._15 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _16, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T17, T18, T19, T20, T21> remove(Numbers._16 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _17, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T18, T19, T20, T21> remove(Numbers._17 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _18, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T19, T20, T21> remove(Numbers._18 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _19, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T20, T21> remove(Numbers._19 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _20, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T21> remove(Numbers._20 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _21);
		}

		public Tuples._21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> remove(Numbers._21 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20);
		}

		public <T> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, v);
		}

		public <R> R apply(Functions._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;
		public final T13 _13;
		public final T14 _14;
		public final T15 _15;
		public final T16 _16;
		public final T17 _17;
		public final T18 _18;
		public final T19 _19;
		public final T20 _20;
		public final T21 _21;
		public final T22 _22;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public T13 _13() {
			return _13;
		}

		public T14 _14() {
			return _14;
		}

		public T15 _15() {
			return _15;
		}

		public T16 _16() {
			return _16;
		}

		public T17 _17() {
			return _17;
		}

		public T18 _18() {
			return _18;
		}

		public T19 _19() {
			return _19;
		}

		public T20 _20() {
			return _20;
		}

		public T21 _21() {
			return _21;
		}

		public T22 _22() {
			return _22;
		}

		public _23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> concat(_0 rhs) {
			return new Tuples._23<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <T23> _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(_1<T23> rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, rhs._0);
		}

		public <R> Tuples._23<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12), _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R, T14, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._13 ignored, Functions._1<T13, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, mapper.apply(_13), _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R, T15, T16, T17, T18, T19, T20, T21, T22> map(Numbers._14 ignored, Functions._1<T14, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, mapper.apply(_14), _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R, T16, T17, T18, T19, T20, T21, T22> map(Numbers._15 ignored, Functions._1<T15, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, mapper.apply(_15), _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R, T17, T18, T19, T20, T21, T22> map(Numbers._16 ignored, Functions._1<T16, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, mapper.apply(_16), _17, _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R, T18, T19, T20, T21, T22> map(Numbers._17 ignored, Functions._1<T17, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, mapper.apply(_17), _18, _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R, T19, T20, T21, T22> map(Numbers._18 ignored, Functions._1<T18, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, mapper.apply(_18), _19, _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R, T20, T21, T22> map(Numbers._19 ignored, Functions._1<T19, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, mapper.apply(_19), _20, _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R, T21, T22> map(Numbers._20 ignored, Functions._1<T20, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, mapper.apply(_20), _21, _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R, T22> map(Numbers._21 ignored, Functions._1<T21, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, mapper.apply(_21), _22);
		}

		public <R> Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> map(Numbers._22 ignored, Functions._1<T22, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, mapper.apply(_22));
		}

		public Tuples._22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._13 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._14 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._15 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _16, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T17, T18, T19, T20, T21, T22> remove(Numbers._16 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _17, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T18, T19, T20, T21, T22> remove(Numbers._17 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _18, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T19, T20, T21, T22> remove(Numbers._18 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _19, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T20, T21, T22> remove(Numbers._19 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _20, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T21, T22> remove(Numbers._20 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _21, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T22> remove(Numbers._21 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _22);
		}

		public Tuples._22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> remove(Numbers._22 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21);
		}

		public <T> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T> append(T v) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, v);
		}

		public <R> R apply(Functions._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
	@EqualsAndHashCode
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> implements Tuple {
		public final T0 _0;
		public final T1 _1;
		public final T2 _2;
		public final T3 _3;
		public final T4 _4;
		public final T5 _5;
		public final T6 _6;
		public final T7 _7;
		public final T8 _8;
		public final T9 _9;
		public final T10 _10;
		public final T11 _11;
		public final T12 _12;
		public final T13 _13;
		public final T14 _14;
		public final T15 _15;
		public final T16 _16;
		public final T17 _17;
		public final T18 _18;
		public final T19 _19;
		public final T20 _20;
		public final T21 _21;
		public final T22 _22;
		public final T23 _23;

		public T0 _0() {
			return _0;
		}

		public T1 _1() {
			return _1;
		}

		public T2 _2() {
			return _2;
		}

		public T3 _3() {
			return _3;
		}

		public T4 _4() {
			return _4;
		}

		public T5 _5() {
			return _5;
		}

		public T6 _6() {
			return _6;
		}

		public T7 _7() {
			return _7;
		}

		public T8 _8() {
			return _8;
		}

		public T9 _9() {
			return _9;
		}

		public T10 _10() {
			return _10;
		}

		public T11 _11() {
			return _11;
		}

		public T12 _12() {
			return _12;
		}

		public T13 _13() {
			return _13;
		}

		public T14 _14() {
			return _14;
		}

		public T15 _15() {
			return _15;
		}

		public T16 _16() {
			return _16;
		}

		public T17 _17() {
			return _17;
		}

		public T18 _18() {
			return _18;
		}

		public T19 _19() {
			return _19;
		}

		public T20 _20() {
			return _20;
		}

		public T21 _21() {
			return _21;
		}

		public T22 _22() {
			return _22;
		}

		public T23 _23() {
			return _23;
		}

		public _24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> concat(_0 rhs) {
			return new Tuples._24<>(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._0 ignored, Functions._1<T0, R> mapper) {
			return tuple(mapper.apply(_0), _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, R, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._1 ignored, Functions._1<T1, R> mapper) {
			return tuple(_0, mapper.apply(_1), _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, R, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._2 ignored, Functions._1<T2, R> mapper) {
			return tuple(_0, _1, mapper.apply(_2), _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, R, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._3 ignored, Functions._1<T3, R> mapper) {
			return tuple(_0, _1, _2, mapper.apply(_3), _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, R, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._4 ignored, Functions._1<T4, R> mapper) {
			return tuple(_0, _1, _2, _3, mapper.apply(_4), _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, R, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._5 ignored, Functions._1<T5, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, mapper.apply(_5), _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, R, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._6 ignored, Functions._1<T6, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, mapper.apply(_6), _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, R, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._7 ignored, Functions._1<T7, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, mapper.apply(_7), _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, R, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._8 ignored, Functions._1<T8, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, mapper.apply(_8), _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, R, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._9 ignored, Functions._1<T9, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, mapper.apply(_9), _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._10 ignored, Functions._1<T10, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, mapper.apply(_10), _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._11 ignored, Functions._1<T11, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, mapper.apply(_11), _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._12 ignored, Functions._1<T12, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, mapper.apply(_12), _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._13 ignored, Functions._1<T13, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, mapper.apply(_13), _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R, T15, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._14 ignored, Functions._1<T14, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, mapper.apply(_14), _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R, T16, T17, T18, T19, T20, T21, T22, T23> map(Numbers._15 ignored, Functions._1<T15, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, mapper.apply(_15), _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R, T17, T18, T19, T20, T21, T22, T23> map(Numbers._16 ignored, Functions._1<T16, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, mapper.apply(_16), _17, _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R, T18, T19, T20, T21, T22, T23> map(Numbers._17 ignored, Functions._1<T17, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, mapper.apply(_17), _18, _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R, T19, T20, T21, T22, T23> map(Numbers._18 ignored, Functions._1<T18, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, mapper.apply(_18), _19, _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R, T20, T21, T22, T23> map(Numbers._19 ignored, Functions._1<T19, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, mapper.apply(_19), _20, _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R, T21, T22, T23> map(Numbers._20 ignored, Functions._1<T20, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, mapper.apply(_20), _21, _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R, T22, T23> map(Numbers._21 ignored, Functions._1<T21, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, mapper.apply(_21), _22, _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R, T23> map(Numbers._22 ignored, Functions._1<T22, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, mapper.apply(_22), _23);
		}

		public <R> Tuples._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> map(Numbers._23 ignored, Functions._1<T23, R> mapper) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, mapper.apply(_23));
		}

		public Tuples._23<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._0 ignored) {
			return tuple(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._1 ignored) {
			return tuple(_0, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._2 ignored) {
			return tuple(_0, _1, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._3 ignored) {
			return tuple(_0, _1, _2, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._4 ignored) {
			return tuple(_0, _1, _2, _3, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._5 ignored) {
			return tuple(_0, _1, _2, _3, _4, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._6 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._7 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._8 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._9 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._10 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._11 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._12 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._13 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T15, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._14 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T16, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._15 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T17, T18, T19, T20, T21, T22, T23> remove(Numbers._16 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _17, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T18, T19, T20, T21, T22, T23> remove(Numbers._17 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _18, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T19, T20, T21, T22, T23> remove(Numbers._18 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _19, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T20, T21, T22, T23> remove(Numbers._19 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _20, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T21, T22, T23> remove(Numbers._20 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _21, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T22, T23> remove(Numbers._21 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _22, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T23> remove(Numbers._22 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _23);
		}

		public Tuples._23<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> remove(Numbers._23 ignored) {
			return tuple(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22);
		}

		public <R> R apply(Functions._24<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, R> f) {
			return f.apply(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23);
		}

		@Override
		public Object[] toArray() {
			return new Object[]
					{_0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23};
		}

		@Override
		public String toString() {
			return "(" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(", ")) + ")";
		}
	}
}
