package hu.blackbelt.java.embedded.compiler.jdt;

/*-
 * #%L
 * Java Embedded compiler JDT
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
import org.osgi.service.component.annotations.Component;

import javax.tools.*;
import java.util.ArrayList;
import java.util.List;

@Component(property = {
        "compileType=system"
})
public class JdtCompilerFactory implements CompilerFactory {

    public JavaCompiler getCompiler() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Can not find compiler, please use JDK instead");
        }
        return compiler;
    }

    @Override
    public String getName() {
        return "system";
    }

    @Override
    public List<String> getExtraArgs() {
        return new ArrayList<>();
    }
}
