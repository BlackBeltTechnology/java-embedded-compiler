package hu.blackbelt.java.embedded.compiler.api;

/*-
 * #%L
 * Java Embedded compiler
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.ImmutableList;
import hu.blackbelt.java.embedded.compiler.api.classloader.CompiledJavaFileObjectsClassLoader;
import hu.blackbelt.java.embedded.compiler.api.exception.CompileException;
import hu.blackbelt.java.embedded.compiler.api.filemanager.CustomClassLoaderJavaFileManager;
import hu.blackbelt.java.embedded.compiler.api.filemanager.InMemoryJavaFileManager;
import hu.blackbelt.java.embedded.compiler.api.filemanager.OsgiJavaFileManager;
import hu.blackbelt.java.embedded.compiler.api.filemanager.StaticOutputDirectoryJavaFileManager;
import lombok.extern.slf4j.Slf4j;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

@Slf4j
public final class CompilerUtil {

    private static final List<String> COMMON_ARGS = Arrays.asList("-source 1.8 -target 1.8".split(" "));

    private CompilerUtil() {
    }

    public static Iterable<JavaFileObject> compile(CompilerContext compilerContext) throws IOException, CompileException {

        //JavaCompiler compiler = CompilerFactory.getCompiler(compilerContext);
        JavaCompiler compiler = null;
        CompilerFactory compilerFactory = null;

        if (compilerContext.getCompilerFactory() != null) {
            compilerFactory = compilerContext.getCompilerFactory();
        } else {
            ServiceLoader<CompilerFactory> loader = ServiceLoader.load(CompilerFactory.class);
            compilerFactory = loader.findFirst().orElseThrow(() -> new IllegalStateException("Could not found compiler"));
        }
        compiler = compilerFactory.getCompiler();

        try (StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(null, null, null)) {

            StandardJavaFileManager customClassLoaderJavaFileManager =
                    new CustomClassLoaderJavaFileManager(standardJavaFileManager, compilerContext.getClassLoader());

            Iterable<? extends JavaFileObject> compilationUnits =
                    compilerContext.getCompilationUnits()
                            .orElse(customClassLoaderJavaFileManager.getJavaFileObjectsFromFiles(
                                    compilerContext.getCompilationFiles().orElse(ImmutableList.of())));

            JavaFileManager delegatedFileManager;
            FileOutputManager fileOutputManager;
            if (compilerContext.getOutputDirectory().isPresent()) {
                delegatedFileManager = new StaticOutputDirectoryJavaFileManager(customClassLoaderJavaFileManager,
                        compilerContext.getOutputDirectory().get(),
                        compilerContext.getClassLoader());
            } else {
                delegatedFileManager = new InMemoryJavaFileManager(customClassLoaderJavaFileManager, compilerContext.getClassLoader());
            }
            fileOutputManager = (FileOutputManager) delegatedFileManager;

            if (compilerContext.getBundleContext().isPresent()) {
                delegatedFileManager = new OsgiJavaFileManager(compilerContext.getBundleContext(),
                        compilerContext.getClassLoader(),
                        delegatedFileManager);
            }
            //javaFileObjectResolverDelegate.delegate = delegatedFileManager;

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

            List<String> options = new ArrayList<>();
            options.add(compilerContext.isIncludeDebugInfo() ? "-g" : "-g:none");
            if (compilerContext.isDisablePreprocessors()) {
                options.add("-proc:none");
            }
            options.addAll(COMMON_ARGS);
            options.addAll(compilerFactory.getExtraArgs());

            Boolean result;
            CompilationTask task = compiler.getTask(new StringWriter(), delegatedFileManager, diagnostics, options,
                    null, compilationUnits);
            result = task.call();
            delegatedFileManager.close();
            if (!result) {
                throw new CompileException(diagnostics.getDiagnostics());
            }

            if (Boolean.TRUE.equals(result)) {
                return fileOutputManager.getOutputJavaFileObjects();
            }

            return Collections.emptyList();
        }
    }

    public static Iterable<Class> compileAsClass(CompilerContext compilerContext) throws IOException, CompileException {
        Iterable<JavaFileObject> objects = compile(compilerContext);

        CompiledJavaFileObjectsClassLoader compiledJavaFileObjectsClassLoader =
                new CompiledJavaFileObjectsClassLoader(Optional.ofNullable(compilerContext.getClassLoader())
                        .orElse(CompilerUtil.class.getClass().getClassLoader()), objects);

        return stream(objects.spliterator(), false)
                .map(o -> {
                    try {
                        if (!(o instanceof FullyQualifiedName)) {
                            throw new RuntimeException("Could not determinate fully qualified name for: " + o.getName());
                        }
                        return compiledJavaFileObjectsClassLoader.loadClass(((FullyQualifiedName) o).getFullyQualifiedName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }
}
