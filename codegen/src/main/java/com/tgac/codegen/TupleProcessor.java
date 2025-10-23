package com.tgac.codegen;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.tgac.codegen.GenerateTuples")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class TupleProcessor extends AbstractProcessor {

	private static final int MAX_ARITY = 10;
	private static final String PACKAGE_NAME = "com.tgac.functional";

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement annotation : annotations) {
			Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
			if (!annotatedElements.isEmpty()) {
				try {
					generateTupleClasses();
				} catch (IOException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate tuple classes: " + e.getMessage());
					return false;
				}
				return true;
			}
		}
		return false;
	}

	private void generateTupleClasses() throws IOException {
		TypeSpec.Builder tuplesClassBuilder = TypeSpec.classBuilder("Tuples")
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addSuperinterface(Serializable.class)
				.addJavadoc("A container for generated Tuple classes and their factory methods.")
				.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

		tuplesClassBuilder.addField(FieldSpec.builder(long.class, "serialVersionUID", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("1L")
				.build());

		tuplesClassBuilder.addType(generateTupleInterface());

		for (int i = 0; i < MAX_ARITY; i++) {
			tuplesClassBuilder.addType(generateTupleN_Class(i));
			tuplesClassBuilder.addMethod(generateTupleFactory(i));
		}

		JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, tuplesClassBuilder.build())
				.build();

		javaFile.writeTo(processingEnv.getFiler());
	}

	private TypeSpec generateTupleInterface() {
		return TypeSpec.interfaceBuilder("Tuple")
				.addModifiers(Modifier.PUBLIC)
				.addSuperinterface(Serializable.class)
				.addMethod(MethodSpec.methodBuilder("toArray")
						.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
						.returns(Object[].class)
						.build())
				.build();
	}

	private TypeSpec generateTupleN_Class(int n) {
		String className = "_" + n;
		List<TypeVariableName> typeVariables = getTypeVariables(n);

		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
				.addSuperinterface(ClassName.get(PACKAGE_NAME, "Tuples", "Tuple"))
				.addAnnotation(ClassName.get("lombok", "EqualsAndHashCode"));

		classBuilder.addField(FieldSpec.builder(long.class, "serialVersionUID", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("1L")
				.build());

		if (!typeVariables.isEmpty()) {
			classBuilder.addTypeVariables(typeVariables);
		}

		// Manually generate fields
		for (int i = 0; i < n; i++) {
			TypeVariableName t = typeVariables.get(i);
			String fieldName = "_" + i;
			classBuilder.addField(FieldSpec.builder(t, fieldName, Modifier.PUBLIC, Modifier.FINAL).build());
		}

		// Manually generate public constructor instead of using Lombok
		MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC);
		for (int i = 0; i < n; i++) {
			constructorBuilder.addParameter(typeVariables.get(i), "_" + i);
			constructorBuilder.addStatement("this._$L = _$L", i, i);
		}
		classBuilder.addMethod(constructorBuilder.build());

		// Add accessor methods
		for (int i = 0; i < n; i++) {
			TypeVariableName t = typeVariables.get(i);
			String fieldName = "_" + i;
			classBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
					.addModifiers(Modifier.PUBLIC)
					.returns(t)
					.addStatement("return this.$N", fieldName)
					.build());
		}

		String arrayInitializer = IntStream.range(0, n).mapToObj(i -> "_" + i).collect(Collectors.joining(", "));
		classBuilder.addMethod(MethodSpec.methodBuilder("toArray")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC)
				.returns(Object[].class)
				.addStatement("return new Object[]{$L}", arrayInitializer)
				.build());

		classBuilder.addMethod(MethodSpec.methodBuilder("toString")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC)
				.returns(String.class)
				.addStatement("return $T.stream(toArray()).map(String::valueOf).collect($T.joining(\", \", \"(\", \")\"))",
						Arrays.class, Collectors.class)
				.build());

		addTupleMethods(classBuilder, n, typeVariables);

		return classBuilder.build();
	}

	private void addTupleMethods(TypeSpec.Builder classBuilder, int n, List<TypeVariableName> typeVariables) {
		addAppendMethod(classBuilder, n, typeVariables);
		addApplyMethod(classBuilder, n, typeVariables);
		addConcatMethods(classBuilder, n, typeVariables);
		addMapMethods(classBuilder, n, typeVariables);
		addRemoveMethods(classBuilder, n, typeVariables);
	}

	private void addAppendMethod(TypeSpec.Builder classBuilder, int n, List<TypeVariableName> typeVariables) {
		if (n + 1 >= MAX_ARITY) {
			return;
		}
		TypeVariableName newType = TypeVariableName.get("T");
		List<TypeName> newTypeVars = new ArrayList<>(typeVariables);
		newTypeVars.add(newType);

		ClassName tupleNPlus1 = ClassName.get(PACKAGE_NAME, "Tuples", "_" + (n + 1));
		TypeName returnType = ParameterizedTypeName.get(tupleNPlus1, newTypeVars.toArray(new TypeName[0]));

		String args = IntStream.range(0, n)
				.mapToObj(i -> "_" + i)
				.collect(Collectors.joining(", "));
		if (n > 0) {
			args += ", v";
		} else {
			args = "v";
		}

		classBuilder.addMethod(MethodSpec.methodBuilder("append")
				.addModifiers(Modifier.PUBLIC)
				.addTypeVariable(newType)
				.returns(returnType)
				.addParameter(newType, "v")
				.addStatement("return tuple($L)", args)
				.build());
	}

	private void addApplyMethod(TypeSpec.Builder classBuilder, int n, List<TypeVariableName> typeVariables) {
		if (n == 0)
			return;
		TypeVariableName r = TypeVariableName.get("R");
		ClassName functionType = ClassName.get(PACKAGE_NAME, "Functions", "_" + n);
		List<TypeName> functionTypeParams = new ArrayList<>(typeVariables);
		functionTypeParams.add(r);
		TypeName functionParam = ParameterizedTypeName.get(functionType, functionTypeParams.toArray(new TypeName[0]));

		String args = IntStream.range(0, n)
				.mapToObj(i -> "_" + i)
				.collect(Collectors.joining(", "));

		classBuilder.addMethod(MethodSpec.methodBuilder("apply")
				.addModifiers(Modifier.PUBLIC)
				.addTypeVariable(r)
				.returns(r)
				.addParameter(functionParam, "f")
				.addStatement("return f.apply($L)", args)
				.build());
	}

	private void addConcatMethods(TypeSpec.Builder classBuilder, int n, List<TypeVariableName> typeVariables) {
		for (int i = 1; n + i < MAX_ARITY; i++) {
			addConcatMethod(classBuilder, n, typeVariables, i);
		}
	}

	private void addConcatMethod(TypeSpec.Builder classBuilder, int lhsN, List<TypeVariableName> lhsTypeVars, int rhsN) {
		int n = lhsN + rhsN;
		List<TypeVariableName> rhsMethodTypeVars = IntStream.range(lhsN, n)
				.mapToObj(i -> TypeVariableName.get("T" + i))
				.collect(Collectors.toList());

		List<TypeName> allTypeVars = new ArrayList<>(lhsTypeVars);
		allTypeVars.addAll(rhsMethodTypeVars);

		ClassName rhsTupleType = ClassName.get(PACKAGE_NAME, "Tuples", "_" + rhsN);
		TypeName rhsTupleParam = rhsN > 0 ? ParameterizedTypeName.get(rhsTupleType, rhsMethodTypeVars.toArray(new TypeName[0])) : rhsTupleType;

		ClassName resultTupleType = ClassName.get(PACKAGE_NAME, "Tuples", "_" + n);
		TypeName returnType = n > 0 ? ParameterizedTypeName.get(resultTupleType, allTypeVars.toArray(new TypeName[0])) : resultTupleType;

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("concat")
				.addModifiers(Modifier.PUBLIC)
				.returns(returnType)
				.addParameter(rhsTupleParam, "rhs");

		if (!rhsMethodTypeVars.isEmpty()) {
			methodBuilder.addTypeVariables(rhsMethodTypeVars);
		}

		String lhsArgs = IntStream.range(0, lhsN).mapToObj(i -> "_" + i).collect(Collectors.joining(", "));
		String rhsArgs = IntStream.range(0, rhsN).mapToObj(i -> "rhs._" + i).collect(Collectors.joining(", "));

		String allArgs = Stream.of(lhsArgs, rhsArgs).filter(s -> !s.isEmpty()).collect(Collectors.joining(", "));

		methodBuilder.addStatement("return tuple($L)", allArgs);

		classBuilder.addMethod(methodBuilder.build());
	}

	private void addMapMethods(TypeSpec.Builder classBuilder, int n, List<TypeVariableName> typeVariables) {
		if (n == 0)
			return;
		for (int i = 0; i < n; i++) {
			addMapMethod(classBuilder, n, typeVariables, i);
		}
	}

	private void addMapMethod(TypeSpec.Builder classBuilder, int n, List<TypeVariableName> typeVariables, int mapIndex) {
		TypeVariableName r = TypeVariableName.get("R");

		List<TypeName> mappedTypes = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			if (i == mapIndex) {
				mappedTypes.add(r);
			} else {
				mappedTypes.add(typeVariables.get(i));
			}
		}
		ClassName tupleType = ClassName.get(PACKAGE_NAME, "Tuples", "_" + n);
		TypeName mappedTuple = ParameterizedTypeName.get(tupleType, mappedTypes.toArray(new TypeName[0]));

		String args = IntStream.range(0, n)
				.mapToObj(i -> (i == mapIndex) ? "mapper.apply(_" + i + ")" : "_" + i)
				.collect(Collectors.joining(", "));

		ClassName function1Type = ClassName.get(PACKAGE_NAME, "Functions", "_1");
		TypeName mapperType = ParameterizedTypeName.get(function1Type, typeVariables.get(mapIndex), r);

		classBuilder.addMethod(MethodSpec.methodBuilder("map" + mapIndex)
				.addModifiers(Modifier.PUBLIC)
				.addTypeVariable(r)
				.returns(mappedTuple)
				.addParameter(mapperType, "mapper")
				.addStatement("return tuple($L)", args)
				.build());
	}

	private void addRemoveMethods(TypeSpec.Builder classBuilder, int n, List<TypeVariableName> typeVariables) {
		if (n == 0)
			return;
		for (int i = 0; i < n; i++) {
			addRemoveMethod(classBuilder, n, typeVariables, i);
		}
	}

	private void addRemoveMethod(TypeSpec.Builder classBuilder, int n, List<TypeVariableName> typeVariables, int removeIndex) {
		if (n - 1 < 0)
			return;

		List<TypeName> newTypeVars = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			if (i != removeIndex) {
				newTypeVars.add(typeVariables.get(i));
			}
		}

		ClassName tupleType = ClassName.get(PACKAGE_NAME, "Tuples", "_" + (n - 1));
		TypeName returnType = (n - 1 > 0) ? ParameterizedTypeName.get(tupleType, newTypeVars.toArray(new TypeName[0])) : tupleType;

		String args = IntStream.range(0, n)
				.filter(i -> i != removeIndex)
				.mapToObj(i -> "_" + i)
				.collect(Collectors.joining(", "));

		classBuilder.addMethod(MethodSpec.methodBuilder("remove" + removeIndex)
				.addModifiers(Modifier.PUBLIC)
				.returns(returnType)
				.addStatement("return tuple($L)", args)
				.build());
	}

	private MethodSpec generateTupleFactory(int n) {
		String methodName = "tuple";
		List<TypeVariableName> typeVariables = getTypeVariables(n);
		ClassName tupleType = ClassName.get(PACKAGE_NAME, "Tuples", "_" + n);

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC);

		if (!typeVariables.isEmpty()) {
			methodBuilder.addTypeVariables(typeVariables);
			methodBuilder.returns(ParameterizedTypeName.get(tupleType, typeVariables.toArray(new TypeName[0])));
		} else {
			methodBuilder.returns(tupleType);
		}

		List<String> params = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			String paramName = "v" + i;
			methodBuilder.addParameter(typeVariables.get(i), paramName);
			params.add(paramName);
		}

		String args = String.join(", ", params);
		if (n > 0) {
			methodBuilder.addStatement("return new _$L<>($L)", n, args);
		} else {
			methodBuilder.addStatement("return new _$L()", n);
		}

		return methodBuilder.build();
	}

	private List<TypeVariableName> getTypeVariables(int n) {
		return IntStream.range(0, n)
				.mapToObj(i -> TypeVariableName.get("T" + i))
				.collect(Collectors.toList());
	}
}
