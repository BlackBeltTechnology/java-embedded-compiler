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

import com.google.common.base.CharMatcher;
import hu.blackbelt.java.embedded.compiler.api.FullyQualifiedName;
import lombok.SneakyThrows;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;


public class MemoryJavaFileObject implements JavaFileObject, FullyQualifiedName {
    private final String binaryName;
    private final String fullyQualifiedName;
    private String name;
    private Kind kind;
    private String content;


    public MemoryJavaFileObject(String fullyQualifiedName, String content,
                                Kind kind) throws IOException {
        this.content = content;
        this.binaryName = fullyQualifiedName.replace(".", "/") + ".class";
        this.kind = kind;
        this.fullyQualifiedName = fullyQualifiedName;
        this.name = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf('.') + 1) + ".java";
    }

    @SneakyThrows
    @Override
    public URI toUri() {
        return URI.create("file:///" + CharMatcher.is('.').replaceFrom(fullyQualifiedName, '/') + ".java" );
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(content.getBytes("UTF-8"));
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return new InputStreamReader(openInputStream());
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors)
            throws IOException {
        return content;
    }

    @Override
    public Writer openWriter() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind k) {
        this.kind = k;
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        String baseName = simpleName + kind.extension;
        return kind.equals(getKind())
                && (baseName.equals(getName()) || getName().endsWith(
                "/" + baseName));
    }

    @Override
    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Modifier getAccessLevel() {
        throw new UnsupportedOperationException();
    }

    public String binaryName() {
        return binaryName;
    }

    @Override
    public String toString() {
        return "MemoryJavaFileObject{" + "url=" + toUri() + " name: " + name + "}";
    }

    @Override
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
}
