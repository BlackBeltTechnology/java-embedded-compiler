package hu.blackbelt.java.embedded.compiler.api.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newCopyOnWriteArrayList;
import static java.util.Arrays.asList;


public class CompositeClassLoader extends ClassLoader {
    private static final String MANDATORY_CLASS_LOADER_MESSAGE = "The ClassLoader argument must be non-null.";
    // Class is used instead of interface to access the putIfAbsent() method.
    private CopyOnWriteArrayList<ClassLoader> classLoaders;

    public CompositeClassLoader(ClassLoader... classLoaders) {
        super(CompositeClassLoader.class.getClassLoader());
        Arrays.stream(classLoaders)
                .forEach(classLoader -> checkNotNull(classLoader, MANDATORY_CLASS_LOADER_MESSAGE));
        this.classLoaders = newCopyOnWriteArrayList(asList(classLoaders));
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Objects.requireNonNull(name);
        @SuppressWarnings("unchecked")
        Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[classLoaders.size()];
        int idx = 0;
        for (ClassLoader cl : classLoaders) {
            tmp[idx] = cl.getResources(name);
            idx++;
        }
        return new CompoundEnumeration<>(tmp);
    }

    public void insert(ClassLoader classLoader) {
        checkNotNull(classLoader, MANDATORY_CLASS_LOADER_MESSAGE);
        throw new UnsupportedOperationException();
    }

    public void append(ClassLoader classLoader) {
        checkNotNull(classLoader, MANDATORY_CLASS_LOADER_MESSAGE);
        classLoaders.addIfAbsent(classLoader);
    }

    public void remove(ClassLoader classLoader) {
        checkNotNull(classLoader, MANDATORY_CLASS_LOADER_MESSAGE);
        classLoaders.remove(classLoader);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (ClassLoader classLoader : classLoaders) {
            try {
                Class<?> cl = classLoader.loadClass(name);
                return cl;
            } catch (ClassNotFoundException cnfe) {
            }
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    protected URL findResource(String name) {
        URL result = null;
        for (ClassLoader classLoader : classLoaders) {
            result = classLoader.getResource(name);
            if (result != null) {
                break;
            }
        }
        return result;
    }


    /*
     * A utility class that will enumerate over an array of enumerations.
     */
    final class CompoundEnumeration<E> implements Enumeration<E> {
        private final Enumeration<E>[] enums;
        private int index;

        public CompoundEnumeration(Enumeration<E>[] enums) {
            this.enums = enums;
        }

        private boolean next() {
            while (index < enums.length) {
                if (enums[index] != null && enums[index].hasMoreElements()) {
                    return true;
                }
                index++;
            }
            return false;
        }

        public boolean hasMoreElements() {
            return next();
        }

        public E nextElement() {
            if (!next()) {
                throw new NoSuchElementException();
            }
            return enums[index].nextElement();
        }
    }
}
