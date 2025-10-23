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

@SupportedAnnotationTypes("com.tgac.codegen.GenerateConsumers")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ConsumerProcessor extends AbstractProcessor {

	private static final int MAX_ARITY = 10;
	private static final String PACKAGE_NAME = "com.tgac.functional";

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement annotation : annotations) {
			Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
			if (!annotatedElements.isEmpty()) {
				try {
					generateConsumerClasses();
				} catch (IOException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate consumer classes: " + e.getMessage());
					return false;
				}
				return true;
			}
		}
		return false;
	}

	private void generateConsumerClasses() throws IOException {
		TypeSpec.Builder consumersClassBuilder = TypeSpec.classBuilder("Consumers")
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addSuperinterface(Serializable.class)
				.addJavadoc("A container for generated Consumer interfaces and their factory methods.")
				.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

		consumersClassBuilder.addField(FieldSpec.builder(long.class, "serialVersionUID", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("1L")
				.build());

		for (int i = 0; i < MAX_ARITY; i++) {
			consumersClassBuilder.addType(generateConsumerN_Interface(i));
			consumersClassBuilder.addMethod(generateConsumerFactory(i));
		}

		JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, consumersClassBuilder.build())
				.build();

		javaFile.writeTo(processingEnv.getFiler());
	}

	private TypeSpec generateConsumerN_Interface(int n) {
		String interfaceName = "_" + n;
		List<TypeVariableName> typeVariables = getTypeVariables(n);

		TypeSpec.Builder interfaceBuilder = TypeSpec.interfaceBuilder(interfaceName)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addSuperinterface(Serializable.class);

		if (!typeVariables.isEmpty()) {
			interfaceBuilder.addTypeVariables(typeVariables);
		}

		MethodSpec.Builder acceptBuilder = MethodSpec.methodBuilder("accept")
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

		for (int i = 0; i < n; i++) {
			acceptBuilder.addParameter(typeVariables.get(i), "v" + i);
		}
		interfaceBuilder.addMethod(acceptBuilder.build());

		addPartialLeftMethod(interfaceBuilder, n, typeVariables);
		addPartialRightMethod(interfaceBuilder, n, typeVariables);
		addPositionalPartialMethods(interfaceBuilder, n, typeVariables);
		addAsFunctionMethod(interfaceBuilder, n, typeVariables);

		return interfaceBuilder.build();
	}

	private void addPartialLeftMethod(TypeSpec.Builder interfaceBuilder, int n, List<TypeVariableName> typeVariables) {
		if (n <= 0)
			return;

		List<TypeName> partialTypeVars = new ArrayList<>(typeVariables.subList(1, n));
		ClassName consumerType = ClassName.get(PACKAGE_NAME, "Consumers", "_" + (n - 1));
		TypeName returnType = partialTypeVars.isEmpty() ? consumerType : ParameterizedTypeName.get(consumerType, partialTypeVars.toArray(new TypeName[0]));

		String params = IntStream.range(1, n).mapToObj(i -> "v" + i).collect(Collectors.joining(", "));
		String args = Stream.concat(Stream.of("v"), IntStream.range(1, n).mapToObj(i -> "v" + i)).collect(Collectors.joining(", "));

		interfaceBuilder.addMethod(MethodSpec.methodBuilder("partial")
				.addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
				.returns(returnType)
				.addParameter(typeVariables.get(0), "v")
				.addStatement("return ($L) -> accept($L)", params, args)
				.build());
	}

	private void addPartialRightMethod(TypeSpec.Builder interfaceBuilder, int n, List<TypeVariableName> typeVariables) {
		if (n <= 0)
			return;

		List<TypeName> partialTypeVars = new ArrayList<>(typeVariables.subList(0, n - 1));
		ClassName consumerType = ClassName.get(PACKAGE_NAME, "Consumers", "_" + (n - 1));
		TypeName returnType = partialTypeVars.isEmpty() ? consumerType : ParameterizedTypeName.get(consumerType, partialTypeVars.toArray(new TypeName[0]));

		String params = IntStream.range(0, n - 1).mapToObj(i -> "v" + i).collect(Collectors.joining(", "));
		String args = Stream.concat(IntStream.range(0, n - 1).mapToObj(i -> "v" + i), Stream.of("v")).collect(Collectors.joining(", "));

		interfaceBuilder.addMethod(MethodSpec.methodBuilder("partialRight")
				.addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
				.returns(returnType)
				.addParameter(typeVariables.get(n - 1), "v")
				.addStatement("return ($L) -> accept($L)", params, args)
				.build());
	}

	private void addPositionalPartialMethods(TypeSpec.Builder interfaceBuilder, int n, List<TypeVariableName> typeVariables) {
		if (n <= 0)
			return;
		for (int i = 0; i < n; i++) {
			addPositionalPartialMethod(interfaceBuilder, n, typeVariables, i);
		}
	}

	private void addPositionalPartialMethod(TypeSpec.Builder interfaceBuilder, int n, List<TypeVariableName> typeVariables, int argIndex) {
		List<TypeName> partialTypeVars = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			if (i != argIndex) {
				partialTypeVars.add(typeVariables.get(i));
			}
		}
		ClassName consumerType = ClassName.get(PACKAGE_NAME, "Consumers", "_" + (n - 1));
		TypeName returnType = partialTypeVars.isEmpty() ? consumerType : ParameterizedTypeName.get(consumerType, partialTypeVars.toArray(new TypeName[0]));

		String params = IntStream.range(0, n).filter(i -> i != argIndex).mapToObj(i -> "v" + i).collect(Collectors.joining(", "));
		String args = IntStream.range(0, n).mapToObj(i -> i == argIndex ? "v" : "v" + i).collect(Collectors.joining(", "));

		interfaceBuilder.addMethod(MethodSpec.methodBuilder("partial" + argIndex)
				.addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
				.returns(returnType)
				.addParameter(typeVariables.get(argIndex), "v")
				.addStatement("return ($L) -> accept($L)", params, args)
				.build());
	}

	private void addAsFunctionMethod(TypeSpec.Builder interfaceBuilder, int n, List<TypeVariableName> typeVariables) {
		ClassName functionType = ClassName.get(PACKAGE_NAME, "Functions", "_" + n);
		TypeName voidType = TypeName.VOID.box();

		List<TypeName> functionTypeParams = new ArrayList<>(typeVariables);
		functionTypeParams.add(voidType);

		TypeName returnType = ParameterizedTypeName.get(functionType, functionTypeParams.toArray(new TypeName[0]));

		String params = IntStream.range(0, n).mapToObj(i -> "v" + i).collect(Collectors.joining(", "));

		interfaceBuilder.addMethod(MethodSpec.methodBuilder("asFunction")
				.addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
				.returns(returnType)
				.addCode("return ($L) -> {\n", params)
				.addCode("    accept($L);\n", params)
				.addCode("    return null;\n")
				.addCode("};\n")
				.build());
	}

	private MethodSpec generateConsumerFactory(int n) {
		String methodName = "consumer";
		List<TypeVariableName> typeVariables = getTypeVariables(n);
		ClassName consumerType = ClassName.get(PACKAGE_NAME, "Consumers", "_" + n);

		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC);

		TypeName consumerTypeName = consumerType;
		if (!typeVariables.isEmpty()) {
			methodBuilder.addTypeVariables(typeVariables);
			consumerTypeName = ParameterizedTypeName.get(consumerType, typeVariables.toArray(new TypeName[0]));
		}

		methodBuilder.returns(consumerTypeName);
		methodBuilder.addParameter(consumerTypeName, "f");
		methodBuilder.addStatement("return f");

		return methodBuilder.build();
	}

	private List<TypeVariableName> getTypeVariables(int n) {
		return IntStream.range(0, n)
				.mapToObj(i -> TypeVariableName.get("T" + i))
				.collect(Collectors.toList());
	}
}
