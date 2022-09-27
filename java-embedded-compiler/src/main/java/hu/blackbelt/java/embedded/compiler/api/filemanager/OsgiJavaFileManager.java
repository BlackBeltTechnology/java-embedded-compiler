package hu.blackbelt.java.embedded.compiler.api.filemanager;

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

import hu.blackbelt.java.embedded.compiler.api.fileobject.OsgiJavaFileFolder;
import hu.blackbelt.java.embedded.compiler.api.fileobject.OsgiJavaFileObject;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;

import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Slf4j
public class OsgiJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> implements JavaFileManager, BundleListener {
    private final Optional<ClassLoader> classLoader;
    private final Optional<BundleContext> bundleContext;

    private final Map<String, OsgiJavaFileFolder> folderMap = new HashMap<String, OsgiJavaFileFolder>();

    public OsgiJavaFileManager(Optional<BundleContext> bundleContext,
                               ClassLoader classLoader, JavaFileManager parentFileManager) {
        super(parentFileManager);
        this.classLoader = Optional.ofNullable(classLoader);
        this.bundleContext = bundleContext;
        if (this.bundleContext.isPresent()) {
            Bundle[] b = bundleContext.get().getBundles();
            for (Bundle bundle : b) {

            }
            this.bundleContext.get().addBundleListener(this);
        }
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return classLoader.orElse(super.getClassLoader(location));
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof OsgiJavaFileObject) {
            String binaryName = ((OsgiJavaFileObject) file).binaryName();
            if (binaryName.indexOf('/') >= 0) {
                binaryName = binaryName
                        .substring(binaryName.lastIndexOf("/") + 1);
            }
            if (binaryName.indexOf('.') >= 0) {
                binaryName = binaryName.substring(0, binaryName.indexOf('.'));
            }
            return binaryName;
        } else {
            return super.inferBinaryName(location, file);
        }
    }

    /*
    @Override
    public boolean hasLocation(Location location) {
        return true;
    } */
    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        if (!bundleContext.isPresent()) {
            return super.getJavaFileForInput(location, className, kind);
        } else {
            if (location == StandardLocation.CLASS_PATH && kind == JavaFileObject.Kind.CLASS && className.lastIndexOf("/") > 0) {
                String packageName = className.substring(0, className.lastIndexOf('/'));
                OsgiJavaFileFolder folder = folderMap.get(packageName);
                if (folder != null) {
                    Optional<OsgiJavaFileObject> o =  folder.getEntries()
                            .stream()
                            .filter(OsgiJavaFileObject.class::isInstance)
                            .map(OsgiJavaFileObject.class::cast)
                            .filter(f -> f.binaryName().equals(className + ".class")).findFirst();
                    if (o.isPresent()) {
                        return o.get();
                    }
                }
            }
        }
        return super.getJavaFileForInput(location, className, kind);
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        if (!bundleContext.isPresent()) {
            return super.list(location, packageName, kinds, recurse);
        } else {
            if (location == StandardLocation.PLATFORM_CLASS_PATH) {
                return super.list(location, packageName, kinds, recurse);
            } else if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
                try {
                    OsgiJavaFileFolder folder = folderMap.get(packageName);
                    if (folder == null) {
                        folder = new OsgiJavaFileFolder(bundleContext.get(), packageName);
                        folderMap.put(packageName, folder);
                    }
                    return folder.getEntries();
                } catch (URISyntaxException e) {
                    log.error("Illegal URI while listing entries for package: " + packageName, e);
                }
                // }
            }
            return Collections.emptyList();
        }

    }

    /*
    @Override
    public int isSupportedOption(String option) {
        return -1;
    } */

    @Override
    public void bundleChanged(BundleEvent be) {
        final Bundle bundle = be.getBundle();
        switch (be.getType()) {
            case BundleEvent.UNRESOLVED:
            case BundleEvent.RESOLVED:
                BundleWiring bw = bundle.adapt(BundleWiring.class);
                Iterable<String> pkgs = getAffectedPackages(bw);
                flush(pkgs);
                break;
        }

    }

    private void flush(Iterable<String> pkgs) {
        for (String pkg : pkgs) {
            if(folderMap.containsKey(pkg)) {
                log.info("Flushed package: "+pkg);
            }
            folderMap.remove(pkg);
        }

    }

    private Iterable<String> getAffectedPackages(BundleWiring bw) {
        List<String> result = new ArrayList<String>();
        if(bw==null) {
            return result;
        }
        List<BundleCapability> l = bw.getCapabilities("osgi.wiring.package");
        for (BundleCapability bundleCapability : l) {
            String pkg = (String) bundleCapability.getAttributes().get("osgi.wiring.package");
            log.debug("Affected package: " + pkg);
            result.add(pkg);
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (bundleContext.isPresent()) {
            bundleContext.get().removeBundleListener(this);
        }
    }

}
