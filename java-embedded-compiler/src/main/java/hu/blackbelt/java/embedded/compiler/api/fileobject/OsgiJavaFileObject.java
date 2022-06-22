package hu.blackbelt.java.embedded.compiler.api.fileobject;

import hu.blackbelt.java.embedded.compiler.api.FullyQualifiedName;
import lombok.SneakyThrows;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;
import java.net.URL;


public class OsgiJavaFileObject implements JavaFileObject, FullyQualifiedName {
    private final String binaryName;
    private final URL url;
    private final String fullyQualifiedName;
    private String name;
    private Kind kind;


    public OsgiJavaFileObject(String javaObjectName, String fullyQualifiedName, URL url,
                              Kind kind) throws IOException {
        this.url = url;
        this.binaryName = javaObjectName;
        this.kind = kind;
        this.fullyQualifiedName = fullyQualifiedName;
        String stripName = javaObjectName;
        if (stripName.endsWith("/")) {
            stripName = stripName.substring(0, stripName.length() - 1);
        }
        name = javaObjectName.substring(javaObjectName.lastIndexOf('/') + 1);
    }

    @SneakyThrows
    @Override
    public URI toUri() {
        return url.toURI();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return url.openStream();
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
        throw new UnsupportedOperationException();
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
        return "OsgiJavaFileObject{" + "url=" + url + '}';
    }

    @Override
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
}