package hu.blackbelt.java.embedded.compiler.api.classloader;

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

import com.google.common.collect.Lists;
import hu.blackbelt.java.embedded.compiler.api.FullyQualifiedName;

import javax.tools.JavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CompiledJavaFileObjectsClassLoader extends ClassLoader {

    private Map<String, Class> classCache = new HashMap<>();
    List<JavaFileObject> objects;

    public CompiledJavaFileObjectsClassLoader(ClassLoader parent, Iterable<JavaFileObject> objects) {
        super(parent);
        this.objects = Lists.newArrayList(objects);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class cc = classCache.get(name);
        if (cc == null) {
            Optional<JavaFileObject> obj = objects.stream()
                    .filter(o -> o instanceof FullyQualifiedName)
                    .filter(o -> ((FullyQualifiedName) o).getFullyQualifiedName().equals(name)).findFirst();
            if (!obj.isPresent()) {
                return super.findClass(name);
            }
            try {
                byte[] byteCode = readAllBytes(obj.get().openInputStream());
                return defineClass(name, byteCode, 0, byteCode.length);
            } catch (IOException e) {
                throw new ClassNotFoundException("IOError", e);
            }
        } else{
            return cc;
        }
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                    outputStream.write(buf, 0, readLen);

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }
}
