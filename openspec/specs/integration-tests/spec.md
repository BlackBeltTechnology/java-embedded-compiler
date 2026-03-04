# integration-tests Specification

## Purpose

The integration test module (`java-embedded-compiler-itest`) verifies that all compilation modes work correctly inside a real OSGi container (Apache Karaf) using Pax Exam as the test driver.

## Architecture

- `hu.blackbelt.java.embedded.compiler.itest.CompilerUtilITest` — JUnit 4 test class annotated with `@RunWith(PaxExam.class)` and `@ExamReactorStrategy(PerClass.class)`. Injects `BundleContext` and `CompilerService` from the OSGi container. Tests all combinations of compiler backend (JDT/ECJ) and output mode (file/memory/class).
- `hu.blackbelt.java.embedded.compiler.itest.KarafFeatureProvider` — Utility class that configures the Karaf container, provisions test bundles, sets up JVM options for JDK 9+ module access, and provides OSGi service lookup helpers.
- Test resources: `compile1/` (valid Northwind service sources), `compile2/` (intentionally invalid sources for error testing), `test-features.xml` (Karaf feature definitions).

## Requirements

### Requirement: File-to-output-directory compilation

The integration tests SHALL verify that Java source files can be compiled to `.class` files in a filesystem output directory.

#### Scenario: JDT file-to-directory compilation
- **GIVEN** valid Java source files in `compile1/` and the JDT compiler factory
- **WHEN** `CompilerUtil.compile()` is called with an `outputDirectory` set
- **THEN** compiled `.class` files are written to the output directory

#### Scenario: ECJ file-to-directory compilation
- **GIVEN** valid Java source files in `compile1/` and the Eclipse compiler factory (via `preferEclipseCompiler=true`)
- **WHEN** `CompilerUtil.compile()` is called with an `outputDirectory` set
- **THEN** compiled `.class` files are written to the output directory

### Requirement: File-to-memory compilation

The integration tests SHALL verify that Java source files can be compiled to in-memory `JavaFileObject` instances.

#### Scenario: JDT file-to-memory compilation
- **GIVEN** valid Java source files in `compile1/` and the JDT compiler factory
- **WHEN** `CompilerUtil.compile()` is called without an `outputDirectory`
- **THEN** compiled `JavaFileObject` instances are returned in memory

#### Scenario: ECJ file-to-memory compilation
- **GIVEN** valid Java source files in `compile1/` and the Eclipse compiler factory
- **WHEN** `CompilerUtil.compile()` is called without an `outputDirectory`
- **THEN** compiled `JavaFileObject` instances are returned in memory

### Requirement: String-to-memory compilation

The integration tests SHALL verify that Java source provided as strings (via `JavaFileObjects.forSourceString()`) can be compiled in memory.

#### Scenario: JDT string-to-memory compilation
- **GIVEN** Java source code as a string and the JDT compiler factory
- **WHEN** `CompilerUtil.compile()` is called with `compilationUnits` containing string-based `JavaFileObject` instances
- **THEN** compiled `JavaFileObject` instances are returned

#### Scenario: ECJ string-to-memory compilation
- **GIVEN** Java source code as a string and the Eclipse compiler factory
- **WHEN** `CompilerUtil.compile()` is called with string-based compilation units
- **THEN** compiled `JavaFileObject` instances are returned

### Requirement: Compile-as-class loading

The integration tests SHALL verify that compiled bytecode can be immediately loaded as `Class` objects.

#### Scenario: JDT compile and load
- **GIVEN** valid source files and the JDT compiler factory
- **WHEN** `CompilerUtil.compileAsClass()` is called
- **THEN** loaded `Class` objects are returned and can be instantiated

#### Scenario: ECJ compile and load
- **GIVEN** valid source files and the Eclipse compiler factory
- **WHEN** `CompilerUtil.compileAsClass()` is called
- **THEN** loaded `Class` objects are returned and can be instantiated

### Requirement: Compilation error handling

The integration tests SHALL verify that invalid source code produces a `CompileException` with meaningful diagnostics.

#### Scenario: Compilation with syntax errors
- **GIVEN** invalid Java source files in `compile2/`
- **WHEN** `CompilerUtil.compile()` is called
- **THEN** a `CompileException` is thrown containing diagnostics with ERROR-level entries

### Requirement: OSGi container integration

The integration tests SHALL run inside a fully provisioned Apache Karaf container with all compiler bundles deployed.

#### Scenario: CompilerService availability
- **GIVEN** the Karaf container is started with all compiler bundles provisioned
- **WHEN** the test class is injected
- **THEN** `CompilerService` is available via `@Inject` and both JDT and ECJ factories are registered
