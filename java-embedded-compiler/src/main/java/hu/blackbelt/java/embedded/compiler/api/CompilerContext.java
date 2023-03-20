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

import hu.blackbelt.java.embedded.compiler.api.classloader.CompositeClassLoader;
import lombok.Builder;
import lombok.Getter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

import javax.tools.JavaFileObject;
import java.io.File;
import java.util.Optional;

@Builder(builderMethodName = "compilerContextBuilder", toBuilder=true)
@Getter
public class CompilerContext {

    @Builder.Default
    BundleContext bundleContext = null;

    @Builder.Default
    boolean includeDebugInfo = true;

    @Builder.Default
    boolean preferEclipseCompiler = false;

    @Builder.Default
    boolean disablePreprocessors = true;

    @Builder.Default
    ClassLoader classLoader = null;

    @Builder.Default
    Class sameClassLoaderAs = null;

    @Builder.Default
    File outputDirectory = null;

    @Builder.Default
    Iterable<File> compilationFiles = null;

    @Builder.Default
    Iterable<? extends JavaFileObject> compilationUnits = null;

    @Builder.Default
    Iterable<? extends String> classPath = null;

    @Builder.Default
    CompilerFactory compilerFactory = null;


    private CompositeClassLoader internalClassLoader = null;

    public Optional<Iterable<File>> getCompilationFiles() {
        return Optional.ofNullable(compilationFiles);
    }

    public Optional<Iterable<? extends JavaFileObject>> getCompilationUnits() {
        return Optional.ofNullable(compilationUnits);
    }

    public Optional<BundleContext> getBundleContext() {
        if (getSameClassLoaderAs().isPresent()) {
            Bundle bundle = FrameworkUtil.getBundle(getSameClassLoaderAs().get());
            if (bundle != null) {
                return Optional.ofNullable(bundle.getBundleContext());
            }
        }
        return Optional.ofNullable(bundleContext);
    }

    public Optional<Class> getSameClassLoaderAs() {
        return Optional.ofNullable(sameClassLoaderAs);
    }

    private Optional<ClassLoader> getSameClassCLassloader() {
        if (sameClassLoaderAs != null) {
            return Optional.of(sameClassLoaderAs.getClassLoader());
        }
        return Optional.empty();
    }

    public ClassLoader getClassLoader() {
        if (internalClassLoader != null) {
            return internalClassLoader;
        }
        internalClassLoader = new CompositeClassLoader();

        if (classLoader != null) {
            internalClassLoader.append(classLoader);
        }

        if (getBundleContext().isPresent()) {
            internalClassLoader.append(getBundleContext().get().getBundle().adapt(BundleWiring.class).getClassLoader());
        }

        if (getSameClassCLassloader().isPresent()) {
            internalClassLoader.append(getSameClassCLassloader().get());
        }

        if (Thread.currentThread().getContextClassLoader() != null) {
            internalClassLoader.append(Thread.currentThread().getContextClassLoader());
        }
        return internalClassLoader;
    }

    public Optional<File> getOutputDirectory() {
        return Optional.ofNullable(outputDirectory);
    }
}
