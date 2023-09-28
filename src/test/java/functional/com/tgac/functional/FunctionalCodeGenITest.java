package functional.com.tgac.functional;
import com.tgac.monads.Streams;
import com.tgac.monads.continuation.Cont;
import io.vavr.Value;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static functional.com.tgac.functional.CodeGen.codeBlock;
import static functional.com.tgac.functional.CodeGen.genericsList;
import static java.lang.String.format;

public class FunctionalCodeGenITest {
	public static int DEPTH = 25;

	@Test
	public void shouldGenerateNumberClasses() {
		int n = DEPTH;
		String code = Cont.<List<String>> builder()
				.just(List.of("package com.tgac.monads.functional;")
						.append("")
						.append("import lombok.*;")
						.append("")
						.append("")
						.append("@NoArgsConstructor(access = AccessLevel.PRIVATE)")
						.append("public class Numbers"))
				.<List<String>> flatMap(l -> k -> l.appendAll(codeBlock(k.apply(List.empty()))))
				.map(l -> l.appendAll(generateNumberConstructor(n))
						.append(""))
				.map(l -> l.appendAll(IntStream.range(0, n)
								.boxed()
								.flatMap(i -> generateNumberClass(i).toJavaStream())
								.collect(List.collector()))
						.append(""))
				.apply(Function.identity())
				.collect(Collectors.joining("\n"));

		System.out.println(code);
	}

	@Test
	public void shouldGenerateFunctions() {
		int n = DEPTH;

		String code = Cont.<List<String>> builder()
				.just(List.of(""))
				.map(l -> List.of("package com.tgac.monads.functional;")
						.append("")
						.append("import lombok.*;")
						.append(""))
				.<List<String>> flatMap(l -> k -> l
						.append("")
						.append("@NoArgsConstructor(access = AccessLevel.PRIVATE)")
						.append("public class Functions")
						.appendAll(codeBlock(k.apply(List.empty()))))
				.map(l -> l.appendAll(
						IntStream.range(0, n)
								.mapToObj(FunctionalCodeGenITest::buildFunctionConstructor)
								.flatMap(List::toJavaStream)
								.collect(List.collector())))
				.map(l -> l.append(""))
				.map(l -> l.appendAll(IntStream.range(0, n)
						.mapToObj(FunctionalCodeGenITest::buildNAryFunction)
						.flatMap(Value::toJavaStream)
						.collect(List.collector())))
				.apply(Function.identity())
				.collect(Collectors.joining("\n"));

		System.out.println(code);
	}


	@Test
	public void shouldGenerateConsumers() {
		int n = DEPTH;

		String code = Cont.<List<String>> builder()
				.just(List.of(""))
				.map(l -> List.of("import lombok.*;")
						.append(""))
				.<List<String>> flatMap(l -> k -> l
						.append("")
						.append("@NoArgsConstructor(access = AccessLevel.PRIVATE)")
						.append("public class Consumers")
						.appendAll(codeBlock(k.apply(List.empty()))))
				.map(l -> l.appendAll(
						IntStream.range(0, n)
								.mapToObj(FunctionalCodeGenITest::buildConsumerConstructor)
								.flatMap(List::toJavaStream)
								.collect(List.collector())))
				.map(l -> l.append(""))
				.map(l -> l.appendAll(IntStream.range(0, n)
						.mapToObj(FunctionalCodeGenITest::buildNAryConsumer)
						.flatMap(Value::toJavaStream)
						.collect(List.collector())))
				.apply(Function.identity())
				.collect(Collectors.joining("\n"));

		System.out.println(code);
	}

	@Test
	public void shouldGenerateTuples() {
		int n = DEPTH;
		String code = Cont.<List<String>> builder()
				.just(List.of("package com.tgac.monads.functional;")
						.append("")
						.append("import lombok.*;")
						.append("import java.util.Arrays;")
						.append("import java.util.stream.Collectors;")
						.append("")
						.append("@NoArgsConstructor(access = AccessLevel.PRIVATE)")
						.append("public class Tuples"))
				.<List<String>> flatMap(l -> k -> l.appendAll(codeBlock(k.apply(List.empty()))))
				.map(l -> l.append("")
						.append("public interface Tuple")
						.appendAll(codeBlock(List.of("Object[] toArray();")))
						.append(""))
				.map(l -> l.appendAll(IntStream.range(0, n)
						.mapToObj(this::generateTupleConstructor)
						.map(f -> f.append(""))
						.flatMap(List::toJavaStream)
						.collect(List.collector())))
				.map(l -> l.appendAll(IntStream.range(0, n)
						.mapToObj(i -> generateTuple(i, n))
						.flatMap(List::toJavaStream)
						.collect(List.collector())))
				.apply(Function.identity())
				.collect(Collectors.joining("\n"));

		System.out.println(code);
	}

	private static List<String> generateNumberConstructor(int n) {
		return IntStream.range(0, n)
				.boxed()
				.flatMap(i -> List.of(
								format("public static Numbers._%s _%s()", i, i))
						.appendAll(codeBlock(List.of(format("return Numbers._%s.INSTANCE;", i))))
						.append("")
						.toJavaStream())
				.collect(List.collector());
	}

	@Test
	public void shouldGenerateNumber() {
		int n = 10;
		String code = generateNumberClass(n)
				.collect(Collectors.joining("\n"));
		System.out.println(code);
	}

	private static List<String> generateNumberClass(int n) {
		return Cont.<List<String>> builder()
				.just(List.of("@NoArgsConstructor(access = AccessLevel.PRIVATE)",
						format("public static class _%s extends Number", n)))
				.<List<String>> flatMap(l -> k -> l.appendAll(codeBlock(k.apply(List.empty()))))
				.map(l -> l.append(format("private static final Number value = %s;", n))
						.append(format("private static final _%s INSTANCE = new _%s();", n, n))
						.append(""))
				.map(l -> l.appendAll(
						Stream.of("int", "long", "float", "double")
								.flatMap(t ->
										List.of("@Override", format("public %s %sValue()", t, t))
												.appendAll(codeBlock(List.of(format("return value.%sValue();", t))))
												.toJavaStream())
								.collect(List.collector())))
				.apply(Function.identity());
	}

	private static List<String> buildFunctionConstructor(int i) {
		String generics = createGenerics(i)
				.append("R")
				.collect(Collectors.joining(", "));
		return List.of(format("public static <%s> Functions._%s<%s> function(Functions._%s<%s> f)",
						generics, i, generics, i, generics))
				.appendAll(codeBlock(List.of("return f;")))
				.append("");
	}

	private static List<String> buildConsumerConstructor(int i) {
		String generics = genericsList(createGenerics(i));
		return List.of(format("public static %s Consumers._%s%s consumer(Consumers._%s%s f)",
						generics, i, generics, i, generics))
				.appendAll(codeBlock(List.of("return f;")))
				.append("");
	}

	private static List<String> createGenerics(int i) {
		return IntStream.range(0, i)
				.mapToObj(j -> "T" + j)
				.collect(List.collector());
	}

	@Test
	public void shouldGenerateNAryFunction() {
		int n = 5;
		String code = buildNAryFunction(n).toJavaStream().collect(Collectors.joining("\n"));
		System.out.println(code);
	}

	private static List<String> buildNAryFunction(int n) {
		String genericsList = n > 0 ? createGenerics(n).collect(Collectors.joining(", ")) + ", R" : "R";
		return Cont.<List<String>> builder()
				.just(List.of(format("public interface _%s<%s>", n,
						genericsList)))
				.<List<String>> flatMap(l -> k -> l.appendAll(codeBlock(k.apply(List.empty()))))
				.map(l -> l.appendAll(
								List.of(format("R apply(%s);", IntStream.range(0, n)
										.mapToObj(i -> "T" + i + " v" + i)
										.collect(Collectors.joining(", ")))))
						.append(""))
				.map(l -> n == 0 ? l : l.appendAll(generateFunctionPartialLeft(n)).append(""))
				.map(l -> n == 0 ? l : l.appendAll(generateFunctionPartialRight(n)).append(""))
				.map(l -> n == 0 ? l :
						l.appendAll(IntStream.range(0, n)
								.boxed()
								.flatMap(i -> generateFunctionPositionalPartial(n, i).toJavaStream())
								.collect(List.collector())))
				.apply(Function.identity());
	}

	private static List<String> buildNAryConsumer(int n) {
		return Cont.<List<String>> builder()
				.just(List.of(format("public interface _%s%s", n,
						CodeGen.genericsList(createGenerics(n)))))
				.<List<String>> flatMap(l -> k -> l.appendAll(codeBlock(k.apply(List.empty()))))
				.map(l -> l.appendAll(
								List.of(format("void accept(%s);", IntStream.range(0, n)
										.mapToObj(i -> "T" + i + " v" + i)
										.collect(Collectors.joining(", ")))))
						.append(""))
				.map(l -> n == 0 ? l : l.appendAll(generateConsumerPartialLeft(n).append("")))
				.map(l -> n == 0 ? l : l.appendAll(generateConsumerPartialRight(n).append("")))
				.map(l -> n == 0 ? l :
						l.appendAll(IntStream.range(0, n)
								.boxed()
								.flatMap(i -> generateConsumerPositionalPartial(n, i).toJavaStream())
								.collect(List.collector())))
				.apply(Function.identity());
	}

	private static List<String> generateFunctionPartialLeft(int n) {
		String partialGenericsList = n > 1 ? createGenerics(n).drop(1).collect(Collectors.joining(", ")) + ", R" : "R";
		return List.of(format("default Functions._%s<%s> partial(T0 v)", n - 1, partialGenericsList))
				.appendAll(codeBlock(List.of(format(
						"return (%s) -> apply(%s);",
						IntStream.range(1, n)
								.mapToObj(i -> "T" + i + " v" + i)
								.collect(Collectors.joining(", ")),
						Stream.concat(Stream.of("v"),
										IntStream.range(1, n)
												.mapToObj(i -> "v" + i))
								.collect(Collectors.joining(", "))))));
	}

	private static List<String> generateConsumerPartialLeft(int n) {
		String partialGenericsList = genericsList(createGenerics(n).drop(1));
		return List.of(format("default Consumers._%s%s partial(T0 v)", n - 1, partialGenericsList))
				.appendAll(codeBlock(List.of(format(
						"return (%s) -> accept(%s);",
						IntStream.range(1, n)
								.mapToObj(i -> "T" + i + " v" + i)
								.collect(Collectors.joining(", ")),
						Stream.concat(Stream.of("v"),
										IntStream.range(1, n)
												.mapToObj(i -> "v" + i))
								.collect(Collectors.joining(", "))))));
	}

	private static List<String> generateFunctionPartialRight(int n) {
		String partialGenericsList = n > 1 ? createGenerics(n).dropRight(1).collect(Collectors.joining(", ")) + ", R" : "R";
		return List.of(format("default Functions._%s<%s> partialRight(T%s v)", n - 1, partialGenericsList, n - 1))
				.appendAll(codeBlock(List.of(format(
						"return (%s) -> apply(%s);",
						IntStream.range(0, n - 1)
								.mapToObj(i -> "T" + i + " v" + i)
								.collect(Collectors.joining(", ")),
						Stream.concat(IntStream.range(0, n - 1)
												.mapToObj(i -> "v" + i),
										Stream.of("v"))
								.collect(Collectors.joining(", "))))));
	}

	private static List<String> generateConsumerPartialRight(int n) {
		String partialGenericsList = genericsList(createGenerics(n).dropRight(1));
		return List.of(format("default Consumers._%s%s partialRight(T%s v)", n - 1, partialGenericsList, n - 1))
				.appendAll(codeBlock(List.of(format(
						"return (%s) -> accept(%s);",
						IntStream.range(0, n - 1)
								.mapToObj(i -> "T" + i + " v" + i)
								.collect(Collectors.joining(", ")),
						Stream.concat(IntStream.range(0, n - 1)
												.mapToObj(i -> "v" + i),
										Stream.of("v"))
								.collect(Collectors.joining(", "))))));
	}

	@Test
	public void shouldGeneratePositionalPartial() {
		int n = 3;
		int i = 1;
		String code = generateConsumerPositionalPartial(n, i)
				.collect(Collectors.joining("\n"));
		System.out.println(code);
	}

	private static List<String> generateFunctionPositionalPartial(int n, int arg) {
		List<String> generics = createGenerics(n);
		String partialGenericsList = n > 1 ?
				generics.removeAt(arg).append("R").collect(Collectors.joining(", ")) : "R";
		return List.of(format("default Functions._%s<%s> partial(Numbers._%s ignored, T%s v)",
						n - 1, partialGenericsList, arg, arg))
				.appendAll(codeBlock(List.of(format("return (%s) -> apply(%s);",
						IntStream.range(0, n)
								.filter(j -> j != arg)
								.mapToObj(j -> "T" + j + " v" + j)
								.collect(Collectors.joining(", ")),
						IntStream.range(0, n)
								.mapToObj(j -> j != arg ? "v" + j : "v")
								.collect(Collectors.joining(", "))))));
	}

	private static List<String> generateConsumerPositionalPartial(int n, int arg) {
		String partialGenericsList = genericsList(createGenerics(n).removeAt(arg));
		return List.of(format("default Consumers._%s%s partial(Numbers._%s ignored, T%s v)",
						n - 1, partialGenericsList, arg, arg))
				.appendAll(codeBlock(List.of(format("return (%s) -> accept(%s);",
						IntStream.range(0, n)
								.filter(j -> j != arg)
								.mapToObj(j -> "T" + j + " v" + j)
								.collect(Collectors.joining(", ")),
						IntStream.range(0, n)
								.mapToObj(j -> j != arg ? "v" + j : "v")
								.collect(Collectors.joining(", "))))));
	}

	private List<String> generateTupleConstructor(int n) {
		List<String> generics = createGenerics(n);
		String genericsString = generics.isEmpty() ? "" : "<" + String.join(", ", generics) + ">";
		return List.of(format("public static %s Tuples._%s%s tuple(%s)",
						genericsString, n, genericsString,
						Streams.zip(IntStream.range(0, n).boxed(),
										generics.toJavaStream(),
										(i, g) -> g + " v" + i)
								.collect(Collectors.joining(", "))))
				.appendAll(codeBlock(List.of(
						format("return new Tuples._%s%s(%s);",
								n, n > 0 ? "<>" : "",
								IntStream.range(0, n)
										.mapToObj(i -> "v" + i)
										.collect(Collectors.joining(", "))))));
	}

	@Test
	public void shouldGenerateNAryTuple() {
		int n = 1;
		int maxN = 3;
		String code = List.of("package com.tgac.monads.tuple;")
				.append("")
				.append("import lombok.*;")
				.append("import lombok.experimental.*;")
				.append("")
				.append("")
				.appendAll(generateTuple(n, maxN))
				.toJavaStream()
				.collect(Collectors.joining("\n"));
		System.out.println(code);

	}

	private List<String> generateTuple(int n, int maxN) {
		return Cont.<List<String>> builder()
				.just(List.of("@EqualsAndHashCode")
						.append("@RequiredArgsConstructor(staticName = \"of\", access = AccessLevel.PRIVATE)"))
				.<List<String>> flatMap(s -> k -> s.appendAll(
						genClass("public static",
								"_" + n,
								createGenerics(n),
								List.of("Tuple"),
								() -> k.apply(List.empty()))))
				.map(l -> l.appendAll(
								IntStream.range(0, n)
										.mapToObj(i -> format("public final T%s _%s;", i, i))
										.collect(List.collector()))
						.append(""))
				.map(l -> l.appendAll(
						IntStream.range(0, n)
								.mapToObj(i -> List.of(format("public T%s _%s(){", i, i))
										.append(format("return _%s;", i))
										.append("}"))
								.flatMap(Value::toJavaStream)
								.collect(List.collector())
								.append("")))
				.map(l -> l.appendAll(IntStream.range(0, maxN - n)
								.mapToObj(i -> generateTupleConcat(n, i))
								.flatMap(List::toJavaStream)
								.collect(List.collector()))
						.append(""))
				.map(l -> l.appendAll(IntStream.range(0, n)
						.boxed()
						.flatMap(i -> generateTupleMap(n, i).toJavaStream())
						.collect(List.collector())))
				.map(l -> l.appendAll(IntStream.range(0, n)
						.boxed()
						.flatMap(i -> generateTupleRemove(n, i).toJavaStream())
						.collect(List.collector())))
				.map(l -> n >= maxN - 1 ? l : l.appendAll(generateTupleAppend(n)).append(""))
				.map(l -> l.appendAll(generateTupleApply(n)).append(""))
				.map(l -> l.appendAll(override(generateTupleToArray(n))).append(""))
				.map(l -> l.appendAll(List.of("@Override",
								"public String toString()")
						.appendAll(codeBlock("return \"(\" + Arrays.stream(toArray()).map(Object::toString).collect(Collectors.joining(\", \")) + \")\";"))))
				.apply(Function.identity());
	}
	private List<String> generateTupleAppend(int n) {
		return List.of(format("public <T> Tuples._%s<%s> append(T v)",
						n + 1, createGenerics(n).append("T").collect(Collectors.joining(", "))))
				.appendAll(codeBlock(List.of(format("return tuple(%s);",
						Stream.concat(IntStream.range(0, n)
												.mapToObj(i -> "_" + i),
										Stream.of("v"))
								.collect(Collectors.joining(", "))))));
	}

	private List<String> generateTupleToArray(int n) {
		return List.of("public Object[] toArray()")
				.appendAll(codeBlock(List.of("return new Object[]")
						.append("{" + IntStream.range(0, n)
								.mapToObj(i -> "_" + i)
								.collect(Collectors.joining(",")) + "};")));
	}

	private static List<String> generateTupleApply(int n) {
		List<String> generics = createGenerics(n);
		return List.of(format("public <R> R apply(Functions._%s<%s> f)",
						n, generics.append("R").collect(Collectors.joining(","))))
				.appendAll(codeBlock(List.of(format("return f.apply(%s);",
						IntStream.range(0, n)
								.mapToObj(i -> "_" + i)
								.collect(Collectors.joining(", "))))));
	}

	@Test
	public void shouldGenerateTupleConcat() {
		int lhs = 3;
		int rhs = 2;
		System.out.println(generateTupleConcat(lhs, rhs)
				.collect(Collectors.joining("\n")));
	}

	private static List<String> generateTupleConcat(int lhsN, int rhsN) {
		int n = lhsN + rhsN;
		List<String> allGenericsList = createGenerics(n);
		String allGenerics = allGenericsList.isEmpty() ? "" : "<" + allGenericsList.collect(Collectors.joining(", ")) + ">";
		List<String> rhsGenericsList = allGenericsList.drop(lhsN);
		String rhsGenerics = rhsGenericsList.isEmpty() ? "" : "<" + rhsGenericsList.collect(Collectors.joining(", ")) + ">";
		return List.of(format("public %s _%s%s concat(_%s%s rhs)",
						rhsGenerics, n, allGenerics,
						rhsN, rhsGenerics))
				.appendAll(codeBlock(List.of(
						format("return new Tuples._%s%s(%s);", n,
								n > 0 ? "<>" : "",
								Stream.concat(
												IntStream.range(0, lhsN).mapToObj(i -> "_" + i),
												IntStream.range(0, rhsN).mapToObj(i -> "rhs._" + i))
										.collect(Collectors.joining(", "))))));
	}

	@Test
	public void shouldGenerateMap() {
		int n = 5;
		int i = 3;
		System.out.println(generateTupleMap(n, i)
				.collect(Collectors.joining("\n")));
	}

	private static List<String> generateTupleMap(int n, int i) {
		return List.of(format("public <R> Tuples._%s<%s> map(Numbers._%s ignored, Functions._1<%s> mapper)",
						n, IntStream.range(0, n)
								.mapToObj(j -> j == i ? "R" : "T" + j)
								.collect(Collectors.joining(", ")),
						i, "T" + i + ", R"))
				.appendAll(codeBlock(
						List.of(format("return tuple(%s);",
								IntStream.range(0, n)
										.mapToObj(j -> j == i ? "mapper.apply(_" + j + ")" : "_" + j)
										.collect(Collectors.joining(", "))))));
	}

	@Test
	public void shouldGenerateTupleDrop() {
		int n = 5;
		int i = 3;
		System.out.println(generateTupleRemove(n, i)
				.collect(Collectors.joining("\n")));
	}

	private static List<String> generateTupleRemove(int n, int i) {
		String generics = IntStream.range(0, n)
				.filter(j -> j != i)
				.mapToObj(j -> "T" + j)
				.collect(Collectors.joining(", "));
		return List.of(format("public Tuples._%s%s remove(Numbers._%s ignored)",
						n - 1, generics.isEmpty() ? "" : "<" + generics + ">", i))
				.appendAll(codeBlock(
						List.of(format("return tuple(%s);",
								IntStream.range(0, n)
										.filter(j -> j != i)
										.mapToObj(j -> "_" + j)
										.collect(Collectors.joining(", "))))));
	}

	public static List<String> genClass(
			String prefix,
			String name,
			List<String> generics,
			List<String> interfaces,
			Supplier<List<String>> contents) {
		String implementedList = String.join(", ", interfaces);
		prefix = prefix.isEmpty() ? "" : prefix + " ";
		return List.of(
						format(prefix + "class %s%s%s", name,
								generics.isEmpty() ? "" :
										"<" + String.join(", ", generics) + ">",
								implementedList.isEmpty() ? "" : " implements " + implementedList))
				.appendAll(codeBlock(contents.get()));
	}

	private static List<String> override(List<String> code) {
		return List.of("@Override").appendAll(code);
	}
}
