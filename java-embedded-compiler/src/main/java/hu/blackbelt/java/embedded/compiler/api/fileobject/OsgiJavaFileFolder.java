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


import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OsgiJavaFileFolder {
    private final List<JavaFileObject> elements = new ArrayList<JavaFileObject>();
    private final BundleContext context;

    public OsgiJavaFileFolder(BundleContext context, String packageName) throws IOException, URISyntaxException {
        this.context = context;
        elements.addAll(findAll(packageName));
    }

    public List<JavaFileObject> getEntries() {
        return Collections.unmodifiableList(elements);
    }

    private List<JavaFileObject> findAll(String packageName) throws IOException, URISyntaxException {

        List<JavaFileObject> result;
        String packagePath = packageName.replaceAll("\\.", "/");

        result = new ArrayList<JavaFileObject>();
        Bundle[] b = context.getBundles();
        for (Bundle bundle : b) {
            enumerateWiring(packagePath, result, bundle);
        }
        return result;
    }

    private void enumerateWiring(String packagePath, List<JavaFileObject> result, Bundle b) throws URISyntaxException, IOException {
        BundleWiring bw =  b.adapt(BundleWiring.class);
        if (bw != null) {
            Collection<String> cc = bw.listResources(packagePath, null, BundleWiring.LISTRESOURCES_RECURSE);
            for (String resource : cc) {
                URL u = b.getResource(resource);
                if (u != null && resource.endsWith(".class")) {
                    String fullyQualifiedName = resource.replace('/', '.');
                    if (fullyQualifiedName.contains(".")) {
                        fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf('.'));
                    }
                    InputStream openStream = null;
                    try {
                        openStream = u.openStream();
                        final OsgiJavaFileObject osgiJavaFileObject = new OsgiJavaFileObject(resource, fullyQualifiedName, u, Kind.CLASS);
                        result.add(osgiJavaFileObject);
                    } catch (FileNotFoundException e) {
                        final OsgiJavaFileObject osgiJavaFileObject = new OsgiJavaFileObject(resource, fullyQualifiedName, u, Kind.CLASS);
                        result.add(osgiJavaFileObject);
                    }
                }
            }
        }
    }
}
