package hu.blackbelt.java.embedded.compiler.api;

import javax.tools.JavaFileObject;

public interface FileOutputManager {
    Iterable<JavaFileObject> getOutputJavaFileObjects();
}
