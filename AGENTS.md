# Java Embedded Compiler - Project Documentation

## Project Overview

**Repository:** BlackBeltTechnology/java-embedded-compiler
**License:** Apache License 2.0
**Java Version:** 11 (source and target)
**Build System:** Maven 3.9.4 with OSGi bundle packaging (Felix maven-bundle-plugin)

1. **Runtime Java compilation** — Provides an API for compiling Java source code at runtime, supporting both in-memory and file-based output
2. **Pluggable compiler backends** — Supports the JDK system compiler (javac via `ToolProvider`) and Eclipse Compiler for Java (ECJ) as interchangeable strategies
3. **OSGi integration** — Full support for OSGi environments with bundle-aware classloading, dynamic compiler service registration via Declarative Services, and Karaf deployment
4. **Flexible classloading** — Composite classloaders, custom classloader wrapping, and compiled-bytecode classloaders enable loading compiled classes in complex classloader hierarchies
5. **ServiceLoader discovery** — Compiler factories are discovered automatically via `java.util.ServiceLoader` in standard Java, or via OSGi DS `@Reference` in OSGi containers

## Directory Structure

```
java-embedded-compiler/
├── java-embedded-compiler/          # Core API module
├── java-embedded-compiler-jdt/      # JDK system compiler implementation
├── java-embedded-compiler-ecj/      # Eclipse ECJ compiler implementation
├── java-embedded-compiler-osgi/     # OSGi service wrapper
├── java-embedded-compiler-itest/    # Integration tests (Karaf + Pax Exam)
├── java-embedded-compiler-reports/  # JaCoCo coverage aggregation
├── .github/workflows/               # GitHub Actions CI/CD pipelines
├── .mvn/                            # Maven wrapper and JVM config
├── openspec/                        # OpenSpec configuration
└── pom.xml                          # Parent POM
```

## Core Modules

### API Layer

| Module | Type | Purpose |
|--------|------|---------|
| `java-embedded-compiler/` | Library | Core API with `CompilerContext` (builder), `CompilerFactory` (strategy interface), `CompilerUtil` (static compilation entry point), file managers, classloaders, and file objects |

### Compiler Implementations

| Module | Type | Purpose |
|--------|------|---------|
| `java-embedded-compiler-jdt/` | OSGi Bundle | `JdtCompilerFactory` — wraps `ToolProvider.getSystemJavaCompiler()`. Requires a JDK (not JRE). Registered as OSGi component with `compileType=system`. |
| `java-embedded-compiler-ecj/` | OSGi Bundle | `EclipseCompilerFactory` — wraps the Eclipse ECJ compiler with a custom `EclipseCompilerWrapper` that handles two-pass compilation (module-info first, then sources). Registered as `compileType=eclipse`. Passes `-warn:none` by default. |

### Runtime & Integration

| Module | Type | Purpose |
|--------|------|---------|
| `java-embedded-compiler-osgi/` | OSGi Bundle | `CompilerService` interface and `CompilerServiceImpl` DS component. Dynamically discovers `CompilerFactory` services, ranks them by `SERVICE_RANKING`, and selects the appropriate compiler based on `CompilerContext.preferEclipseCompiler`. |
| `java-embedded-compiler-itest/` | Test | Karaf 4.4.7 + Pax Exam 4.13.5 integration tests. Tests all compilation modes: file-to-directory, file-to-memory, string-to-memory, compile-as-class, and error handling — for both JDT and ECJ compilers. |
| `java-embedded-compiler-reports/` | Reports | JaCoCo aggregate coverage report across all modules. |

## Technology Stack

### Core Technologies
- **Java 11** — source and target compatibility
- **Google Guava 30.0-jre** — `LoadingCache` for in-memory file manager caching, `ImmutableList`, `Preconditions`
- **OSGi Core 6.0** — `BundleContext`, `BundleWiring`, `BundleListener` for bundle-aware compilation
- **OSGi Declarative Services 1.3** — `@Component`, `@Reference` annotations for service registration
- **Eclipse JDT/ECJ (Platform 4.18)** — Eclipse compiler implementation
- **Lombok 1.18.34** — `@Builder`, `@Getter`, `@Slf4j` for boilerplate reduction
- **SLF4J 2.0.16** — Logging abstraction

### Build & Quality
- **Maven 3.9.4** with Maven wrapper (`mvnw`)
- **Felix maven-bundle-plugin 5.1.8** — OSGi bundle packaging
- **flatten-maven-plugin 1.3.0** — CI-friendly `${revision}` versioning
- **JUnit 4.13.2** + **Hamcrest 2.2** + **Mockito 4.8.0** — Testing
- **Pax Exam 4.13.5** — OSGi container testing with Apache Karaf
- **JaCoCo 0.8.12** — Code coverage
- **SonarQube** — Code quality analysis
- **Logback 1.5.12** — Test logging

## Build Commands

The project includes a Maven wrapper. Always use `./mvnw` (or `mvnw.cmd` on Windows).

```bash
# Full build
./mvnw clean install

# Tests only
./mvnw clean test

# Skip tests
./mvnw clean install -DskipTests

# Build parent POM only (no sub-modules)
./mvnw clean install -DskipModules=true

# Run a specific test
./mvnw test -Dtest=CompilerUtilITest -f java-embedded-compiler-itest/pom.xml

# Deploy to Judong Nexus
./mvnw clean deploy -Prelease-judong
```

### Maven Profiles

| Profile | Purpose |
|---------|---------|
| `modules` | Active by default. Includes all sub-modules. Disable with `-DskipModules=true`. |
| `sign-artifacts` | GPG-sign artifacts for Maven Central publication. |
| `release-dummy` | Deploy to local `/tmp/` directory for testing the release process. |
| `release-judong` | Deploy to the Judong Nexus snapshot repository. |
| `release-central` | Deploy to Maven Central via Sonatype OSSRH with auto-release. |
| `generate-github-asciidoc-diagrams` | Render PlantUML diagrams from AsciiDoc sources. |
| `update-source-code-license` | Update Apache 2.0 license headers across all source files. |

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Parent POM: version management (`${revision}` = 1.1.0), dependency management, plugin configuration |
| `.mvn/jvm.config` | JVM flags for Maven: heap sizing (-Xms1024m -Xmx2048m), `--add-opens` for JDK 9+ module system |
| `.mvn/wrapper/maven-wrapper.properties` | Maven wrapper version configuration |
| `logback-test.xml` | Logback configuration for test execution |
| `.github/workflows/build.yml` | Primary CI pipeline: build, test, deploy, tag, release |
| `java-embedded-compiler-itest/src/test/resources/test-features.xml` | Karaf feature definitions for integration tests |

## Development Environment

**Required:**
- Java 11 JDK (Azul Zulu recommended)
- Maven 3.9.4+ (or use the included `mvnw` wrapper)

**JVM flags** are automatically applied via `.mvn/jvm.config`:
- `-Xms1024m -Xmx2048m`
- `--add-opens java.base/java.lang=ALL-UNNAMED`
- `--add-opens java.base/java.util=ALL-UNNAMED`
- `--add-opens java.base/java.time=ALL-UNNAMED`

## Git Workflow

- **Main Branch:** `develop`
- **Stable Releases:** `master`
- **Versioning:** CI-friendly `${revision}` property (currently `1.1.0`). Non-release builds get a qualified version: `major.minor.qualifier.date_commitId_branchName`.
- **Branch naming:** `feature/JNG-xxx_summary`, `bugfix/JNG-xxx_summary`, `hotfix/JNG-xxx_summary`, `release/X.Y.Z`
- **Every commit must reference a JIRA ticket** in the format `JNG-xxx`
- **CI/CD:** GitHub Actions on `judong` runner with JDK 21. Builds deploy to Judong Nexus; release branches also deploy to Maven Central.

## Important Notes

1. **Two compiler modes exist** — JDT (system javac) requires a full JDK to be present at runtime; ECJ works with just a JRE since it bundles its own compiler
2. **In-memory is the default** — When no `outputDirectory` is set on `CompilerContext`, compilation happens entirely in memory using `InMemoryJavaFileManager` with Guava caching
3. **OSGi service ranking** — `CompilerServiceImpl` selects the highest-ranked `CompilerFactory` unless `preferEclipseCompiler` is set, in which case it filters for the Eclipse implementation
4. **Java 1.8 compilation target in CompilerUtil** — The actual compilation target inside `CompilerUtil` is hardcoded to Java 1.8 (`-source 1.8 -target 1.8`), regardless of the JDK version used to run the build
5. **ECJ two-pass compilation** — `EclipseCompilerWrapper` performs a two-pass compilation: module-info files first, then remaining sources
6. **Test resources** — Integration test source files are in `java-embedded-compiler-itest/src/test/resources/compile1/` (valid) and `compile2/` (intentionally invalid for error testing)
7. **All modules are OSGi bundles** — Every module is packaged as an OSGi bundle via Felix maven-bundle-plugin, exporting `hu.blackbelt.java.embedded.compiler.*`
8. **Class name typo** — `EclipeCompilerFactory` (missing 's' in Eclipse) is the actual class name in the ECJ module; do not "fix" this without a coordinated rename

## Related Documentation

- [README.md](README.md) — Project overview with architecture diagrams
- [CONTRIBUTING.md](CONTRIBUTING.md) — Development setup and submission guidelines
- [.github/CIFLOW.md](.github/CIFLOW.md) — CI/CD pipeline and GitFlow branching details
