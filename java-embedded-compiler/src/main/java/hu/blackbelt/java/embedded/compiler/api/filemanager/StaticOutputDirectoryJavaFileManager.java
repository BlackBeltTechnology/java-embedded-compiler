package hu.blackbelt.java.embedded.compiler.api.filemanager;

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

import hu.blackbelt.java.embedded.compiler.api.FileOutputManager;
import hu.blackbelt.java.embedded.compiler.api.fileobject.ClassFileObject;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaticOutputDirectoryJavaFileManager extends CustomClassLoaderJavaFileManager implements FileOutputManager
{
    private final List<JavaFileObject> files = new ArrayList<>();
    private final File outDir;
    private final Optional<ClassLoader> classLoader;

    public StaticOutputDirectoryJavaFileManager(StandardJavaFileManager fileManager, File outputDirectory, ClassLoader classLoader) {
        super(fileManager, classLoader);
        this.outDir = outputDirectory;
        this.classLoader = Optional.ofNullable(classLoader);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return classLoader.orElse(super.getClassLoader(location));
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        return super.getFileForInput(location, packageName, relativeName);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
        if (kind == JavaFileObject.Kind.CLASS) {
            File file = new File(outDir, className.replace('.', '/') + ".class");
            ClassFileObject classFileObject = new ClassFileObject(file, className.replace('/', '.'), kind);
            files.add(classFileObject);
            return classFileObject;
        }
        throw new UnsupportedOperationException("Can't save location with kind: " + kind);
    }

    public Iterable<JavaFileObject> getOutputJavaFileObjects() {
        return files;
    }

}
