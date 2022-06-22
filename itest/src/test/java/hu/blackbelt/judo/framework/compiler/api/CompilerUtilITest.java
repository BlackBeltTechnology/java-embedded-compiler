package hu.blackbelt.judo.framework.compiler.api;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.java.embedded.compiler.api.FullyQualifiedName;
import hu.blackbelt.java.embedded.compiler.api.exception.CompileException;
import hu.blackbelt.java.embedded.compiler.api.fileobject.JavaFileObjects;
import hu.blackbelt.java.embedded.compiler.api.fileobject.MemoryJavaFileObject;
import hu.blackbelt.java.embedded.compiler.osgi.CompilerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.*;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hu.blackbelt.java.embedded.compiler.api.CompilerContext.compilerContextBuilder;
import static hu.blackbelt.java.embedded.compiler.api.CompilerUtil.compile;
import static hu.blackbelt.java.embedded.compiler.api.CompilerUtil.compileAsClass;
import static hu.blackbelt.judo.meta.esm.osgi.itest.KarafFeatureProvider.karafConfig;
import static java.util.stream.StreamSupport.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CompilerUtilITest {

    @Inject
    BundleContext bundleContext;

    @Inject
    CompilerService compilerService;

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(org.osgi.framework.Constants.IMPORT_PACKAGE, "*, org.osgi.service.log");
        return probe;
    }

    @Configuration
    public Option[] config() throws FileNotFoundException, MalformedURLException {
        return OptionUtils.combine(
                karafConfig(this.getClass()),
                new Option[] {
                        mavenBundle().groupId("hu.blackbelt.bundles.eclipse-jdt-ecj").artifactId("org.eclipse.jdt.ecj").version("3.21.0_1"),
                        mavenBundle().groupId("hu.blackbelt").artifactId("java-embedded-compiler").versionAsInProject(),
                        mavenBundle().groupId("hu.blackbelt").artifactId("java-embedded-compiler-ecj").versionAsInProject(),
                        mavenBundle().groupId("hu.blackbelt").artifactId("java-embedded-compiler-jdt").versionAsInProject(),
                        mavenBundle().groupId("hu.blackbelt").artifactId("java-embedded-compiler-osgi").versionAsInProject()
                });
    }

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


    private List<String> toJavaFileObjectFullyQualifiedNameList(Iterable<JavaFileObject> iterable) {
        return stream(iterable.spliterator(), false)
                .map(e -> (FullyQualifiedName) e)
                .map(e -> e.getFullyQualifiedName()).collect(Collectors.toList());
    }

    private List<String> toClassfulyQualifiedNameList(Iterable<Class> iterable) {
        return stream(iterable.spliterator(), false)
                .map(e -> e.getName()).collect(Collectors.toList());
    }

    @Test(expected = CompileException.class)
    public void testErrorWithStandardCompiler() throws IOException, CompileException {
        compilerService.compile(compilerContextBuilder()
                .bundleContext(bundleContext)
                .outputDirectory(new File(targetDir().getAbsolutePath(), "compile2-classes-jdk"))
                .compilationFiles(listjavaFileTree(new File(targetDir().getAbsolutePath(), SOURCES_FAIL)))
                .build());
    }

    @Test(expected = CompileException.class)
    public void testErrorWithEclipseCompiler() throws IOException, CompileException {
        compilerService.compile(compilerContextBuilder()
                .preferEclipseCompiler(true)
                .outputDirectory(new File(targetDir().getAbsolutePath(), "compile2-classes-jdk"))
                .compilationFiles(listjavaFileTree(new File(targetDir().getAbsolutePath(), SOURCES_FAIL)))
                .build());
    }

    @Test
    public void testOkFromFileListToOutputDirectoryWithStandardCompiler() throws IOException, CompileException {
        Iterable<JavaFileObject> compiled = compilerService.compile(compilerContextBuilder()
                .bundleContext(bundleContext)
                .outputDirectory(new File(targetDir().getAbsolutePath(), "compile1-classes-jdk"))
                .compilationFiles(listjavaFileTree(new File(targetDir().getAbsolutePath(), SOURCES_OK)))
                .build());

        assertThat(stream(compiled.spliterator(), false)
                        .filter(e -> !(e instanceof FullyQualifiedName))
                        .count(),
                equalTo(0L));

        assertThat(this.toJavaFileObjectFullyQualifiedNameList(compiled), containsInAnyOrder(OK_CLASSES));

    }

    @Test
    public void testOkFromFileListToOutputDirectoryWithEclipseCompiler() throws IOException, CompileException {
        Iterable<JavaFileObject> compiled = compilerService.compile(compilerContextBuilder()
                .bundleContext(bundleContext)
                .preferEclipseCompiler(true)
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
    public void testOkFromFileListToMemoryWithStandardCompiler() throws IOException, CompileException {
        Iterable<JavaFileObject> compiled = compilerService.compile(compilerContextBuilder()
                .bundleContext(bundleContext)
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
        Iterable<JavaFileObject> compiled = compilerService.compile(compilerContextBuilder()
                .bundleContext(bundleContext)
                .preferEclipseCompiler(true)
                .compilationFiles(listjavaFileTree(new File(targetDir().getAbsolutePath(), SOURCES_OK)))
                .build());

        assertThat(stream(compiled.spliterator(), false)
                        .filter(e -> !(e instanceof FullyQualifiedName))
                        .count(),
                equalTo(0L));

        assertThat(this.toJavaFileObjectFullyQualifiedNameList(compiled), containsInAnyOrder(OK_CLASSES));
    }

    @Test
    public void testOkCodeFromStringToMemoryWithStandardCompiler() throws IOException, CompileException {
        Iterable<JavaFileObject> compiled = compilerService.compile(compilerContextBuilder()
                .bundleContext(bundleContext)
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
    public void testOkCodeFromStringToMemoryWithEclipseCompiler() throws IOException, CompileException {
        Iterable<JavaFileObject> compiled = compilerService.compile(compilerContextBuilder()
                .bundleContext(bundleContext)
                .preferEclipseCompiler(true)
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
    public void testOkCodeFromFileToMemoryWithStandardCompiler() throws IOException, CompileException {
        Iterable<JavaFileObject> compiled = compilerService.compile(compilerContextBuilder()
                .bundleContext(bundleContext)
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
        Iterable<JavaFileObject> compiled = compilerService.compile(compilerContextBuilder()
                .bundleContext(bundleContext)
                .preferEclipseCompiler(true)
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
    public void testOkFromFileListToMemoryAsClassListWithStandardCompiler() throws IOException, CompileException {
        Iterable<Class> compiledClasses = compilerService.compileAsClass(compilerContextBuilder()
                .bundleContext(bundleContext)
                .compilationUnits(readjavaFileTree(new File(targetDir().getAbsolutePath(), SOURCES_OK)))
                .build());

        assertThat(toClassfulyQualifiedNameList(compiledClasses), containsInAnyOrder(OK_CLASSES));
    }

    @Test
    public void testOkFromFileListToMemoryAsClassListWithEclipseCompiler() throws IOException, CompileException {
        Iterable<Class> compiledClasses = compilerService.compileAsClass(compilerContextBuilder()
                .bundleContext(bundleContext)
                .preferEclipseCompiler(true)
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

    private File targetDir() {
        return new File(PathUtils.getBaseDir(), "../../test-classes");
    }

}