# jdt-compiler Specification

## Purpose

The JDT compiler module (`java-embedded-compiler-jdt`) provides a `CompilerFactory` implementation that wraps the JDK system Java compiler accessed via `javax.tools.ToolProvider`.

## Architecture

Single class: `hu.blackbelt.java.embedded.compiler.jdt.JdtCompilerFactory`
- Implements `CompilerFactory` interface from the core API
- Registered as an OSGi DS `@Component` with property `compileType=system`
- Delegates to `ToolProvider.getSystemJavaCompiler()` for the actual compiler instance

## Requirements

### Requirement: System compiler provisioning

`JdtCompilerFactory` SHALL provide the JDK system Java compiler via `ToolProvider.getSystemJavaCompiler()`.

#### Scenario: JDK is available
- **GIVEN** the runtime is a full JDK (not a JRE)
- **WHEN** `getCompiler()` is called
- **THEN** the system `JavaCompiler` instance is returned

#### Scenario: Only JRE is available
- **GIVEN** the runtime is a JRE without compiler tools
- **WHEN** `getCompiler()` is called
- **THEN** an `IllegalStateException` is thrown indicating JDK is required

### Requirement: Factory identity

`JdtCompilerFactory` SHALL identify itself as the "system" compiler.

#### Scenario: Query compiler name
- **WHEN** `getName()` is called
- **THEN** the string `"system"` is returned

### Requirement: No extra compiler arguments

`JdtCompilerFactory` SHALL not inject additional compiler arguments.

#### Scenario: Query extra args
- **WHEN** `getExtraArgs()` is called
- **THEN** an empty list is returned

### Requirement: OSGi service registration

`JdtCompilerFactory` SHALL be discoverable as an OSGi service with the `compileType=system` property.

#### Scenario: Service lookup in OSGi container
- **GIVEN** the JDT bundle is active in an OSGi container
- **WHEN** a service lookup for `CompilerFactory` with filter `(compileType=system)` is performed
- **THEN** the `JdtCompilerFactory` instance is returned
