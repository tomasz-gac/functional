package com.tgac.functional;

import com.tgac.codegen.GenerateConsumers;
import com.tgac.codegen.GenerateFunctions;
import com.tgac.codegen.GenerateTuples;

/**
 * This class serves as a trigger for annotation-based code generation.
 * By annotating this class with @GenerateTuples, we instruct the Java
 * compiler to run the TupleProcessor during the build process.
 */
@GenerateTuples
@GenerateConsumers
@GenerateFunctions
public final class CodeGenConfig {
	// This class is intentionally left empty.
}
