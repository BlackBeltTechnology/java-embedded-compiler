# ecj-compiler Specification

## Purpose

The ECJ compiler module (`java-embedded-compiler-ecj`) provides a `CompilerFactory` implementation that wraps the Eclipse Compiler for Java (ECJ), enabling compilation without requiring a full JDK.

## Architecture

Two classes:
- `hu.blackbelt.java.embedded.compiler.ecj.EclipeCompilerFactory` — OSGi DS `@Component` implementing `CompilerFactory`, registered with property `compileType=eclipse`
- `hu.blackbelt.java.embedded.compiler.ecj.EclipseCompilerWrapper` — Static utility that wraps `org.eclipse.jdt.internal.compiler.tool.EclipseCompilerImpl` to handle two-pass compilation and module-info processing

## Requirements

### Requirement: Eclipse compiler provisioning

`EclipeCompilerFactory` SHALL provide an Eclipse-based `JavaCompiler` whose `getTask()` method delegates to `EclipseCompilerWrapper`.

#### Scenario: Obtain Eclipse compiler
- **WHEN** `getCompiler()` is called
- **THEN** a `JavaCompiler` instance is returned that uses the ECJ compiler internally

### Requirement: Factory identity

`EclipeCompilerFactory` SHALL identify itself as the "eclipse" compiler.

#### Scenario: Query compiler name
- **WHEN** `getName()` is called
- **THEN** the string `"eclipse"` is returned

### Requirement: Warning suppression by default

`EclipeCompilerFactory` SHALL suppress Eclipse-specific warnings by default.

#### Scenario: Query extra args
- **WHEN** `getExtraArgs()` is called
- **THEN** a list containing `"-warn:none"` is returned

### Requirement: Two-pass compilation

`EclipseCompilerWrapper.getTask()` SHALL perform a two-pass compilation: module-info files first, then remaining source files.

#### Scenario: Compilation with module-info
- **GIVEN** a set of compilation units including `module-info.java` and regular Java sources
- **WHEN** `EclipseCompilerWrapper.getTask()` is called
- **THEN** module-info files are compiled in a first pass, and remaining sources in a second pass

#### Scenario: Compilation without module-info
- **GIVEN** a set of compilation units with no `module-info.java`
- **WHEN** `EclipseCompilerWrapper.getTask()` is called
- **THEN** all sources are compiled in a single pass

### Requirement: OSGi service registration

`EclipeCompilerFactory` SHALL be discoverable as an OSGi service with the `compileType=eclipse` property.

#### Scenario: Service lookup in OSGi container
- **GIVEN** the ECJ bundle is active in an OSGi container
- **WHEN** a service lookup for `CompilerFactory` with filter `(compileType=eclipse)` is performed
- **THEN** the `EclipeCompilerFactory` instance is returned
