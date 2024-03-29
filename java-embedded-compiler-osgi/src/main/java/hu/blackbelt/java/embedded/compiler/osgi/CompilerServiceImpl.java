package hu.blackbelt.java.embedded.compiler.osgi;

/*-
 * #%L
 * Java Embedded compiler OSGi
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

import hu.blackbelt.java.embedded.compiler.api.CompilerContext;
import hu.blackbelt.java.embedded.compiler.api.CompilerFactory;
import hu.blackbelt.java.embedded.compiler.api.CompilerUtil;
import hu.blackbelt.java.embedded.compiler.api.exception.CompileException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CompilerServiceImpl implements CompilerService {

    @AllArgsConstructor
    @Getter
    static class ServiceKey implements Comparable {
        String compilerType;
        Integer rank;

        @Override
        public int compareTo(Object o) {
            ServiceKey b = null;
            if (o instanceof ServiceKey) {
                b = (ServiceKey) o;
            }
            return Comparator
                    .comparing(ServiceKey::getRank, Comparator.reverseOrder()) // Reverse
                    .thenComparing(ServiceKey::getCompilerType, Comparator.naturalOrder())
                    .compare(this, b);
        }
    }

    Map<ServiceKey, CompilerFactory> compilerFactories = new ConcurrentHashMap<>();

    @Reference(
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC)
    void addCompilerFactory(CompilerFactory compilerFactory, Map<String, Object> properties) {
        String type = compilerFactory.getName();
        Integer rank = (Integer) properties.getOrDefault(Constants.SERVICE_RANKING, 0);
        synchronized (compilerFactories) {
            compilerFactories.put(new ServiceKey(type, rank), compilerFactory);
        }
    }

    void removeCompilerFactory(CompilerFactory compilerFactory, Map<String, Object> properties) {
        String type = compilerFactory.getName();
        Integer rank = (Integer) properties.getOrDefault(Constants.SERVICE_RANKING, 0);
        synchronized (compilerFactories) {
            compilerFactories.remove(new ServiceKey(type, rank));
        }
    }


    public CompilerFactory getCompilerFactory(CompilerContext compilerContext) {
        synchronized (compilerFactories) {
            Optional<ServiceKey> key = Optional.empty();
            if (compilerContext != null && compilerContext.isPreferEclipseCompiler()) {
                key = compilerFactories.keySet().stream().filter(k -> k.getCompilerType().equals("eclipse")).findFirst();
            }
            if (key.isEmpty()) {
                List<ServiceKey> keys = new ArrayList(compilerFactories.keySet());
                Collections.sort(keys);
                if (!keys.isEmpty()) {
                    key = Optional.of(keys.get(0));
                }
            }
            if (key.isPresent()) {
                return compilerFactories.get(key.get());
            }
            return null;
        }
    }

    public Iterable<JavaFileObject> compile(CompilerContext compilerContext) throws IOException, CompileException {
        return CompilerUtil.compile(compilerContext.toBuilder().compilerFactory(getCompilerFactory(compilerContext)).build());
    }

    public Iterable<Class> compileAsClass(CompilerContext compilerContext) throws IOException, CompileException {
        return CompilerUtil.compileAsClass(compilerContext.toBuilder().compilerFactory(getCompilerFactory(compilerContext)).build());
    }
}
