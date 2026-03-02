# core-api Specification

## Purpose

The core API module (`java-embedded-compiler`) defines the compilation framework's public contracts, utility classes, classloaders, file managers, and file objects that enable runtime Java source compilation and class loading.

## Architecture

The module is organized into five packages under `hu.blackbelt.java.embedded.compiler.api`:

- **Root package** — `CompilerFactory` (strategy interface), `CompilerContext` (builder configuration), `CompilerUtil` (static entry point), `FileOutputManager` (output abstraction), `FullyQualifiedName` (metadata interface)
- **classloader** — `CompositeClassLoader` (chains multiple classloaders), `CompiledJavaFileObjectsClassLoader` (loads compiled bytecode from JavaFileObjects)
- **filemanager** — `ForwardingStandardJavaFileManager`, `CustomClassLoaderJavaFileManager`, `InMemoryJavaFileManager`, `StaticOutputDirectoryJavaFileManager`, `OsgiJavaFileManager`, `CustomClassLoaderPackageInternalsFinder`
- **fileobject** — `JavaFileObjects` (factory), `MemoryJavaFileObject`, `ClassFileObject`, `CustomClasLoaderJavaFileObject`, `OsgiJavaFileObject`, `OsgiJavaFileFolder`
- **exception** — `CompileException` (wraps compiler diagnostics)

## Requirements

### Requirement: CompilerFactory strategy contract

The `CompilerFactory` interface SHALL provide a pluggable strategy for obtaining a `JavaCompiler` instance, a human-readable name, and optional extra compiler arguments.

#### Scenario: Obtain a compiler instance
- **GIVEN** a `CompilerFactory` implementation is available
- **WHEN** `getCompiler()` is called
- **THEN** a valid `javax.tools.JavaCompiler` instance is returned

#### Scenario: Retrieve compiler-specific extra arguments
- **GIVEN** a `CompilerFactory` implementation
- **WHEN** `getExtraArgs()` is called
- **THEN** a list of additional compiler arguments is returned (may be empty)

### Requirement: CompilerContext configuration builder

`CompilerContext` SHALL use the builder pattern to configure all aspects of a compilation: source input, classloader, output destination, compiler preference, and compilation options.

#### Scenario: Build a minimal in-memory compilation context
- **GIVEN** a set of `JavaFileObject` compilation units
- **WHEN** `CompilerContext.builder().compilationUnits(units).build()` is called
- **THEN** a `CompilerContext` is created with default values: `includeDebugInfo=true`, `preferEclipseCompiler=false`, `disablePreprocessors=true`, no output directory (in-memory mode)

#### Scenario: Build a file-to-directory compilation context
- **GIVEN** a set of source `File` objects and an output `File` directory
- **WHEN** `CompilerContext.builder().compilationFiles(files).outputDirectory(dir).build()` is called
- **THEN** a `CompilerContext` is created targeting filesystem output

#### Scenario: Custom classloader initialization
- **GIVEN** a `CompilerContext.Builder` with `sameClassLoaderAs(SomeClass.class)` set
- **WHEN** the `CompilerContext` is constructed
- **THEN** the classloader of `SomeClass` is added to the internal `CompositeClassLoader`

### Requirement: CompilerUtil compilation orchestration

`CompilerUtil.compile()` SHALL orchestrate the full compilation pipeline: factory discovery, file manager creation, compiler task execution, and result collection.

#### Scenario: Compile with ServiceLoader discovery
- **GIVEN** a `CompilerContext` with no explicit `compilerFactory` set
- **WHEN** `CompilerUtil.compile(context)` is called
- **THEN** `ServiceLoader.load(CompilerFactory.class)` is used to find the first available factory

#### Scenario: Compile with explicit factory
- **GIVEN** a `CompilerContext` with `compilerFactory` explicitly set
- **WHEN** `CompilerUtil.compile(context)` is called
- **THEN** the provided factory is used directly

#### Scenario: Compile to in-memory output
- **GIVEN** a `CompilerContext` with no `outputDirectory` and no `bundleContext`
- **WHEN** `CompilerUtil.compile(context)` is called
- **THEN** an `InMemoryJavaFileManager` is used and compiled `JavaFileObject` instances are returned

#### Scenario: Compile to output directory
- **GIVEN** a `CompilerContext` with an `outputDirectory` set
- **WHEN** `CompilerUtil.compile(context)` is called
- **THEN** a `StaticOutputDirectoryJavaFileManager` writes `.class` files to the specified directory

#### Scenario: Compile in OSGi environment
- **GIVEN** a `CompilerContext` with a `bundleContext` and no `outputDirectory`
- **WHEN** `CompilerUtil.compile(context)` is called
- **THEN** an `OsgiJavaFileManager` wrapping an `InMemoryJavaFileManager` is used for bundle-aware class resolution

#### Scenario: Compilation failure
- **GIVEN** source code with syntax errors
- **WHEN** `CompilerUtil.compile(context)` is called
- **THEN** a `CompileException` is thrown containing the `List<Diagnostic>` of ERROR-level issues

### Requirement: CompilerUtil class loading

`CompilerUtil.compileAsClass()` SHALL compile sources and immediately load them as `Class` objects.

#### Scenario: Compile and load classes
- **GIVEN** valid source code
- **WHEN** `CompilerUtil.compileAsClass(context)` is called
- **THEN** compiled classes are loaded via `CompiledJavaFileObjectsClassLoader` and returned as `Iterable<Class>`

### Requirement: CompositeClassLoader delegation chain

`CompositeClassLoader` SHALL delegate class loading across an ordered list of child classloaders, trying each in sequence.

#### Scenario: Class found in second loader
- **GIVEN** a `CompositeClassLoader` with two child classloaders, where only the second can load class `com.example.Foo`
- **WHEN** `findClass("com.example.Foo")` is called
- **THEN** the class is loaded from the second child classloader

#### Scenario: Append a new classloader at runtime
- **GIVEN** an existing `CompositeClassLoader`
- **WHEN** `append(newClassLoader)` is called
- **THEN** the new classloader is added to the end of the delegation chain (thread-safe via CopyOnWriteArrayList)

### Requirement: InMemoryJavaFileManager caching

`InMemoryJavaFileManager` SHALL store compiled bytecode in memory using Guava's `LoadingCache`.

#### Scenario: Retrieve compiled output
- **GIVEN** a successful compilation through `InMemoryJavaFileManager`
- **WHEN** `getOutputJavaFileObjects()` is called
- **THEN** an `ImmutableList<JavaFileObject>` containing all compiled class bytecode is returned

### Requirement: JavaFileObjects factory methods

`JavaFileObjects` SHALL provide static factory methods for creating `JavaFileObject` instances from various sources.

#### Scenario: Create from source string
- **WHEN** `JavaFileObjects.forSourceString("com.example.Hello", sourceCode)` is called
- **THEN** a `JavaFileObject` of kind `SOURCE` is returned with the given content

#### Scenario: Create from resource URL
- **WHEN** `JavaFileObjects.forResource(url)` is called
- **THEN** a `JavaFileObject` wrapping the URL resource is returned

### Requirement: OsgiJavaFileManager bundle awareness

`OsgiJavaFileManager` SHALL discover and list Java classes from OSGi bundles and respond to bundle lifecycle events.

#### Scenario: List classes from active bundles
- **GIVEN** an OSGi environment with active bundles
- **WHEN** `list(PLATFORM_CLASS_PATH, "com.example", kinds, recurse)` is called
- **THEN** classes from active bundles matching the package are returned

#### Scenario: Cache invalidation on bundle change
- **GIVEN** cached bundle class information
- **WHEN** a `BundleEvent` is received
- **THEN** the cached OSGi file folders are cleared and re-discovered on next access
