# Java Embedded Compiler

[![Build](https://github.com/BlackBeltTechnology/java-embedded-compiler/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/BlackBeltTechnology/java-embedded-compiler/actions/workflows/build.yml)

## Introduction

Java Embedded Compiler is a library that enables on-the-fly Java source compilation and class loading in both standard Java and OSGi environments. It provides a unified API over two pluggable compiler backends — the JDK system compiler (javac via `ToolProvider`) and the Eclipse Compiler for Java (ECJ) — so that application code can compile Java sources at runtime without being coupled to a specific compiler implementation.

Typical use cases include dynamic code generation, runtime model transformations, and hot-deploying compiled classes inside an OSGi container such as Apache Karaf.

## Module Overview

The project is a multi-module Maven build consisting of six modules that layer on top of each other:

```mermaid
graph TD
    Core["java-embedded-compiler<br/>(Core API)"]
    JDT["java-embedded-compiler-jdt<br/>(JDK System Compiler)"]
    ECJ["java-embedded-compiler-ecj<br/>(Eclipse Compiler)"]
    OSGi["java-embedded-compiler-osgi<br/>(OSGi Service Wrapper)"]
    ITest["java-embedded-compiler-itest<br/>(Integration Tests)"]
    Reports["java-embedded-compiler-reports<br/>(Coverage Reports)"]

    JDT --> Core
    ECJ --> Core
    OSGi --> Core
    ITest -.->|test| Core
    ITest -.->|test| JDT
    ITest -.->|test| ECJ
    ITest -.->|test| OSGi
    Reports -.->|aggregate| Core
```

| Module | Purpose |
|--------|---------|
| `java-embedded-compiler` | Core API: `CompilerContext`, `CompilerUtil`, file managers, classloaders, and file objects |
| `java-embedded-compiler-jdt` | `JdtCompilerFactory` — wraps the JDK system compiler (`ToolProvider.getSystemJavaCompiler()`) |
| `java-embedded-compiler-ecj` | `EclipseCompilerFactory` — wraps the Eclipse ECJ compiler with a custom compilation task wrapper |
| `java-embedded-compiler-osgi` | `CompilerService` — an OSGi Declarative Services component that dynamically discovers and ranks compiler factories |
| `java-embedded-compiler-itest` | Karaf + Pax Exam integration tests that exercise all compilation modes in a real OSGi container |
| `java-embedded-compiler-reports` | JaCoCo coverage aggregation across modules |

## Architecture

### Compilation Flow

The main entry point is `CompilerUtil.compile()`, which orchestrates the full compilation pipeline:

```mermaid
sequenceDiagram
    participant Client
    participant CompilerUtil
    participant CompilerFactory
    participant FileManager
    participant JavaCompiler

    Client->>CompilerUtil: compile(CompilerContext)
    CompilerUtil->>CompilerFactory: getCompiler()
    CompilerFactory-->>CompilerUtil: JavaCompiler
    CompilerUtil->>FileManager: create (InMemory / OutputDir / OSGi)
    CompilerUtil->>JavaCompiler: getTask(fileManager, sources, options)
    JavaCompiler-->>CompilerUtil: CompilationTask
    CompilerUtil->>JavaCompiler: task.call()
    JavaCompiler-->>CompilerUtil: success/failure
    CompilerUtil-->>Client: Iterable<JavaFileObject>
```

### Class Diagram — Key Public APIs

```mermaid
classDiagram
    class CompilerFactory {
        <<interface>>
        +getCompiler() JavaCompiler
        +getName() String
        +getExtraArgs() List~String~
    }
    class CompilerContext {
        +bundleContext: BundleContext
        +classLoader: ClassLoader
        +compilationFiles: Iterable~File~
        +compilationUnits: Iterable~JavaFileObject~
        +outputDirectory: File
        +compilerFactory: CompilerFactory
        +preferEclipseCompiler: boolean
        +includeDebugInfo: boolean
        +disablePreprocessors: boolean
    }
    class CompilerUtil {
        +compile(CompilerContext)$ Iterable~JavaFileObject~
        +compileAsClass(CompilerContext)$ Iterable~Class~
    }
    class FileOutputManager {
        <<interface>>
        +getOutputJavaFileObjects() Iterable~JavaFileObject~
    }
    class JdtCompilerFactory {
        +getCompiler() JavaCompiler
    }
    class EclipseCompilerFactory {
        +getCompiler() JavaCompiler
    }

    CompilerFactory <|.. JdtCompilerFactory
    CompilerFactory <|.. EclipseCompilerFactory
    CompilerUtil --> CompilerContext
    CompilerUtil --> CompilerFactory
    FileOutputManager <|.. InMemoryJavaFileManager
    FileOutputManager <|.. StaticOutputDirectoryJavaFileManager
```

### Dependency Graph

```mermaid
graph LR
    subgraph External
        Guava["Google Guava 30.0"]
        OSGiCore["OSGi Core 6.0"]
        OSGiDS["OSGi DS Annotations"]
        SLF4J["SLF4J 2.0"]
        Lombok["Lombok 1.18"]
        EclipsePlatform["Eclipse Platform 4.18<br/>(JDT / ECJ)"]
        Karaf["Apache Karaf 4.4"]
        PaxExam["Pax Exam 4.13"]
    end
    subgraph Project
        Core["Core API"] --> Guava
        Core --> OSGiCore
        Core --> SLF4J
        Core --> Lombok
        JDT["JDT Module"] --> EclipsePlatform
        ECJ["ECJ Module"] --> EclipsePlatform
        OSGiMod["OSGi Module"] --> OSGiDS
        ITest["Integration Tests"] --> Karaf
        ITest --> PaxExam
    end
```

## Quick Start

```java
// In-memory compilation from source string
CompilerContext context = CompilerContext.builder()
    .compilationUnits(ImmutableList.of(
        JavaFileObjects.forSourceString("com.example.Hello",
            "package com.example; public class Hello { }")
    ))
    .build();

Iterable<JavaFileObject> compiled = CompilerUtil.compile(context);

// Compile and load classes directly
Iterable<Class> classes = CompilerUtil.compileAsClass(context);
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development setup, submission guidelines, and CI workflow details.

## License

This project is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
