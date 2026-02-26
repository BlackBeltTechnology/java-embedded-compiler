# osgi-service Specification

## Purpose

The OSGi service module (`java-embedded-compiler-osgi`) wraps the core compilation API as an OSGi Declarative Services component, dynamically discovering and ranking available `CompilerFactory` services.

## Architecture

- `hu.blackbelt.java.embedded.compiler.osgi.CompilerService` — Service interface exposing `getCompilerFactory()`, `compile()`, and `compileAsClass()` methods
- `hu.blackbelt.java.embedded.compiler.osgi.CompilerServiceImpl` — DS `@Component` implementation that manages `CompilerFactory` registrations via `@Reference(cardinality=MULTIPLE, policy=DYNAMIC)`, stores them in a `ConcurrentHashMap` keyed by a `ServiceKey` (comparable by rank and name), and delegates compilation to `CompilerUtil`

## Requirements

### Requirement: Dynamic compiler factory registration

`CompilerServiceImpl` SHALL dynamically register and unregister `CompilerFactory` services as they appear and disappear in the OSGi service registry.

#### Scenario: New CompilerFactory service appears
- **GIVEN** the `CompilerServiceImpl` component is active
- **WHEN** a new `CompilerFactory` service is registered with `SERVICE_RANKING` property
- **THEN** the factory is stored in the internal map keyed by its rank and name

#### Scenario: CompilerFactory service removed
- **GIVEN** a registered `CompilerFactory` service
- **WHEN** the service is unregistered from the OSGi registry
- **THEN** the factory is removed from the internal map

### Requirement: Compiler selection by preference

`CompilerServiceImpl.getCompilerFactory()` SHALL select the appropriate compiler based on the `CompilerContext.preferEclipseCompiler` flag.

#### Scenario: Default selection (no preference)
- **GIVEN** multiple registered `CompilerFactory` services with different rankings
- **WHEN** `getCompilerFactory(context)` is called with `preferEclipseCompiler=false`
- **THEN** the factory with the highest `SERVICE_RANKING` is returned

#### Scenario: Eclipse compiler preferred
- **GIVEN** both system and eclipse `CompilerFactory` services are registered
- **WHEN** `getCompilerFactory(context)` is called with `preferEclipseCompiler=true`
- **THEN** the factory with `getName().equals("eclipse")` is returned

### Requirement: Compilation delegation

`CompilerServiceImpl` SHALL delegate actual compilation to `CompilerUtil` with the selected factory.

#### Scenario: Compile via service
- **GIVEN** an active `CompilerServiceImpl` with at least one `CompilerFactory`
- **WHEN** `compile(compilerContext)` is called
- **THEN** `CompilerUtil.compile()` is invoked with the context's `compilerFactory` set to the selected factory

#### Scenario: Compile as class via service
- **GIVEN** an active `CompilerServiceImpl` with at least one `CompilerFactory`
- **WHEN** `compileAsClass(compilerContext)` is called
- **THEN** `CompilerUtil.compileAsClass()` is invoked with the selected factory
