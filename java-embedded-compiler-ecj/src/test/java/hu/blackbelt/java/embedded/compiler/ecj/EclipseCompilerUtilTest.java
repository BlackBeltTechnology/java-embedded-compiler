package hu.blackbelt.java.embedded.compiler.ecj;

/*-
 * #%L
 * Java Embedded compiler ECJ
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
import hu.blackbelt.java.embedded.compiler.api.CompilerFactory;
import hu.blackbelt.java.embedded.compiler.api.FullyQualifiedName;
import hu.blackbelt.java.embedded.compiler.api.exception.CompileException;
import hu.blackbelt.java.embedded.compiler.api.fileobject.JavaFileObjects;
import hu.blackbelt.java.embedded.compiler.api.fileobject.MemoryJavaFileObject;
import org.junit.Before;
import org.junit.Test;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hu.blackbelt.java.embedded.compiler.api.CompilerContext.compilerContextBuilder;
import static hu.blackbelt.java.embedded.compiler.api.CompilerUtil.compile;
import static hu.blackbelt.java.embedded.compiler.api.CompilerUtil.compileAsClass;
import static java.util.stream.StreamSupport.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class EclipseCompilerUtilTest {

    public static final String SOURCES_OK = "compile1";
    public static final String SOURCES_FAIL = "compile2";
    public static final String[] OK_CLASSES = new String[] {
            "rest.northwind.services.OrderItem",
            "rest.northwind.services.ProductInfo",
            "rest.northwind.services.CategoryInfo",
            "rest.northwind.services.ProductLink",
            "rest.northwind.services.CategoryLink",
            "rest.northwind.services.Comment",
            "rest.northwind.services.OrderInfo",
            "rest.northwind.services.OrderItemProduct",
            "rest.northwind.services.OrderLink" };

    CompilerFactory compilerFactory;

    private List<String> toJavaFileObjectFullyQualifiedNameList(Iterable<JavaFileObject> iterable) {
        return stream(iterable.spliterator(), false)
                .map(e -> (FullyQualifiedName) e)
                .map(e -> e.getFullyQualifiedName()).collect(Collectors.toList());
    }

    private List<String> toClassfulyQualifiedNameList(Iterable<Class> iterable) {
        return stream(iterable.spliterator(), false)
                .map(e -> e.getName()).collect(Collectors.toList());
    }

    @Before
    public void setup() throws MalformedURLException, ClassNotFoundException {

        List<File> classPath = ImmutableList.of(
                new File("target/test-classes", "javax.ws.rs-api-2.1.1.jar"),
                new File("target/test-classes", "snakeyaml-1.24.jar")
        );
        URL[] urlsForClassLoader = classPath.stream().map(f -> {
            try {
                return f.toURI().toURL();
            } catch (MalformedURLException e) {
                return null;
            }
        }).collect(Collectors.toList()).toArray(new URL[classPath.size()]);

        // need to define parent classloader which knows all dependencies of the plugin
        ClassLoader classLoader = new URLClassLoader(urlsForClassLoader, EclipseCompilerUtilTest.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
        classLoader.loadClass("javax.ws.rs.Path");
        compilerFactory = new EclipeCompilerFactory();
    }

    @Test
    public void testFactoryServiceLoader() {
        ServiceLoader<CompilerFactory> loader = ServiceLoader.load(CompilerFactory.class);
        CompilerFactory compilerFactory = loader.findFirst().orElseThrow(() -> new IllegalStateException("Could not found compiler"));
        assertThat(compilerFactory.getClass(), equalTo(EclipeCompilerFactory.class));
    }

    @Test(expected = CompileException.class)
    public void testErrorWithEclipseCompiler() throws IOException, CompileException {
        compile(compilerContextBuilder()
                .compilerFactory(compilerFactory)
                .outputDirectory(new File(targetDir().getAbsolutePath(), "compile2-classes-jdk"))
                .compilationFiles(listjavaFileTree(new File(targetDir().getAbsolutePath(), SOURCES_FAIL)))
                .build());
    }

    @Test
    public void testOkFromFileListToOutputDirectoryWithEclipseCompiler() throws IOException, CompileException {
        Iterable<JavaFileObject> compiled = compile(compilerContextBuilder()
                .compilerFactory(compilerFactory)
                .outputDirectory(new File(targetDir().getAbsolutePath(), "compile1-classes-eclipse"))
                .compilationFiles(listjavaFileTree(new File(targetDir().getAbsolutePath(), SOURCES_OK)))
                .build());

        assertThat(stream(compiled.spliterator(), false)
                        .filter(e -> !(e instanceof FullyQualifiedName))
                        .count(),
                equalTo(0L));

        assertThat(this.toJavaFileObjectFullyQualifiedNameList(compiled), containsInAnyOrder(OK_CLASSES));
    }

    @Test
    public void testOkFromFileListToMemoryWithEclipseCompiler() throws IOException, CompileException {
        Iterable<JavaFileObject> compiled = compile(compilerContextBuilder()
                .compilerFactory(compilerFactory)
                .compilationFiles(listjavaFileTree(new File(targetDir().getAbsolutePath(), SOURCES_OK)))
                .build());

        assertThat(stream(compiled.spliterator(), false)
                        .filter(e -> !(e instanceof FullyQualifiedName))
                        .count(),
                equalTo(0L));

        assertThat(this.toJavaFileObjectFullyQualifiedNameList(compiled), containsInAnyOrder(OK_CLASSES));
    }

    @Test
    public void testOkCodeFromStringToMemoryWithEclipseCompiler() throws IOException, CompileException {
        Iterable<JavaFileObject> compiled = compile(compilerContextBuilder()
                .compilerFactory(compilerFactory)
                .compilationUnits(ImmutableList.of(
                        JavaFileObjects.forSourceLines("example.HelloWorld",
                                "package example;",
                                "",
                                "final class HelloWorld {",
                                "  void sayHello() {",
                                "    System.out.println(\"hello!\");",
                                "  }",
                                "}")
                ))
                .build());

        assertThat(stream(compiled.spliterator(), false)
                        .filter(e -> !(e instanceof FullyQualifiedName))
                        .count(),
                equalTo(0L));

        assertThat(this.toJavaFileObjectFullyQualifiedNameList(compiled), containsInAnyOrder("example.HelloWorld"));
    }

    @Test
    public void testOkCodeFromFileToMemoryWithEclipseCompiler() throws IOException, CompileException {
        Iterable<JavaFileObject> compiled = compile(compilerContextBuilder()
                .compilerFactory(compilerFactory)
                .compilationUnits(ImmutableList.of(
                        JavaFileObjects.forSourceLines("example.HelloWorld",
                                "package example;",
                                "",
                                "final class HelloWorld {",
                                "  void sayHello() {",
                                "    System.out.println(\"hello!\");",
                                "  }",
                                "}")
                ))
                .build());

        assertThat(stream(compiled.spliterator(), false)
                        .filter(e -> !(e instanceof FullyQualifiedName))
                        .count(),
                equalTo(0L));

        assertThat(this.toJavaFileObjectFullyQualifiedNameList(compiled), containsInAnyOrder("example.HelloWorld"));
    }

    @Test
    public void testOkFromFileListToMemoryAsClassListWithEclipseCompiler() throws IOException, CompileException {
        Iterable<Class> compiledClasses = compileAsClass(compilerContextBuilder()
                .compilerFactory(compilerFactory)
                .compilationUnits(readjavaFileTree(new File(targetDir().getAbsolutePath(), SOURCES_OK)))
                .build());

        assertThat(toClassfulyQualifiedNameList(compiledClasses), containsInAnyOrder(OK_CLASSES));
    }

    private static Collection<File> listjavaFileTree(File dir) {
        Set<File> fileTree = new HashSet<File>();
        if(dir == null || dir.listFiles() == null) {
            return fileTree;
        }
        for (File entry : dir.listFiles()) {
            if (entry.isFile() && entry.getAbsolutePath().endsWith(".java")) {
                fileTree.add(entry);
            }
            else if (entry.isDirectory()) {
                fileTree.addAll(listjavaFileTree(entry));
            }
        }
        return fileTree;
    }

    private static Collection<MemoryJavaFileObject> readjavaFileTree(File dir) {
        return listjavaFileTree(dir).stream().map(f -> {
            try {
                return new MemoryJavaFileObject(getFullyQualifiedNameOfFile(dir, f), readLines(f), JavaFileObject.Kind.SOURCE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    private static String getFullyQualifiedNameOfFile(File dir, File file) {
        String relative = dir.toURI().relativize(file.toURI()).getPath();
        relative =  relative.substring(0, relative.lastIndexOf(".java")).replaceAll("/", ".");
        return relative;
    }

    private static String readLines(File file) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        return contentBuilder.toString();
    }

    private File targetDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath);
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

}
