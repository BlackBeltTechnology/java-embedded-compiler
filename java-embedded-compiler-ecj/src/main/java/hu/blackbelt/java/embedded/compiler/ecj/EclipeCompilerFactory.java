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

import hu.blackbelt.java.embedded.compiler.api.CompilerFactory;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.osgi.service.component.annotations.Component;

import javax.tools.*;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@Component(property = {
        "compileType=eclipse"
})
public class EclipeCompilerFactory implements CompilerFactory {
    public JavaCompiler getCompiler() {
        JavaCompiler compiler = new EclipseCompiler() {
                public JavaCompiler.CompilationTask getTask(Writer out, JavaFileManager fileManager, DiagnosticListener<? super JavaFileObject> someDiagnosticListener, Iterable<String> options, Iterable<String> classes, Iterable<? extends JavaFileObject> compilationUnits) {
                    return EclipseCompilerWrapper.getTask(out, fileManager, someDiagnosticListener, options, classes, compilationUnits);
                }
            };
        return compiler;
    }

    @Override
    public String getName() {
        return "eclipse";
    }

    @Override
    public List<String> getExtraArgs() {
        List options = new ArrayList();
        options.add("-warn:none");
        return options;
    }
}
