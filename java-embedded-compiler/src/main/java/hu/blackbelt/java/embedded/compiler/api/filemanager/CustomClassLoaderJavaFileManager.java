package hu.blackbelt.java.embedded.compiler.api.filemanager;

import hu.blackbelt.java.embedded.compiler.api.fileobject.CustomClasLoaderJavaFileObject;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class CustomClassLoaderJavaFileManager extends ForwardingStandardJavaFileManager implements JavaFileManager {
    private final ClassLoader classLoader;
    private final CustomClassLoaderPackageInternalsFinder finder;

    public CustomClassLoaderJavaFileManager(StandardJavaFileManager standardFileManager, ClassLoader classLoader) {
        super(standardFileManager, classLoader);
        this.classLoader = classLoader;
        finder = new CustomClassLoaderPackageInternalsFinder(classLoader);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return classLoader;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof CustomClasLoaderJavaFileObject) {
            return ((CustomClasLoaderJavaFileObject) file).binaryName();
        } else { // if it's not CustomJavaFileObject, then it's coming from standard file manager - let it handle the file
            return super.inferBinaryName(location, file);
        }
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        if (location == StandardLocation.PLATFORM_CLASS_PATH) { // let standard manager hanfle
            return super.list(location, packageName, kinds, recurse);
        } else if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
            if (packageName.startsWith("java.")) { // a hack to let standard manager handle locations like "java.lang" or "java.util". Prob would make sense to join results of standard manager with those of my finder here
                return super.list(location, packageName, kinds, recurse);
            } else { // app specific classes are here
                return finder.find(packageName);
            }
        }
        return Collections.emptyList();
    }


    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(
            Iterable<? extends File> files) {
        return super.getJavaFileObjectsFromFiles(files);
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjects(File... files) {
        return super.getJavaFileObjects(files);
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjects(String... names) {
        return super.getJavaFileObjects(names);
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(Iterable<String> names) {
        return super.getJavaFileObjectsFromStrings(names);
    }

    @Override
    public void setLocation(Location location, Iterable<? extends File> path) throws IOException {
        super.setLocation(location, path);
    }

    @Override
    public Iterable<? extends File> getLocation(Location location) {
        return super.getLocation(location);
    }

}