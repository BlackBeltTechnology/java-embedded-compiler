package hu.blackbelt.java.embedded.compiler.api.fileobject;

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

import hu.blackbelt.java.embedded.compiler.api.FullyQualifiedName;

import javax.tools.SimpleJavaFileObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ClassFileObject extends SimpleJavaFileObject implements FullyQualifiedName {
    private final File file;
    private final String fullyQualifiedName;

    public ClassFileObject(File file, String fullyQualifiedName, Kind kind) {
        super(file.toURI(), kind);
        this.file = file;
        this.fullyQualifiedName = fullyQualifiedName;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        file.getParentFile().mkdirs();
        return new FileOutputStream(file);
    }

    @Override
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
}
