package hu.blackbelt.java.embedded.compiler.api.fileobject;

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
