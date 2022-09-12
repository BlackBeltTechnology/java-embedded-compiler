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
import hu.blackbelt.java.embedded.compiler.api.exception.CompileException;

import javax.tools.JavaFileObject;
import java.io.IOException;

public interface CompilerService {

    CompilerFactory getCompilerFactory(CompilerContext compilerContext);

    Iterable<JavaFileObject> compile(CompilerContext compilerContext) throws IOException, CompileException;

    Iterable<Class> compileAsClass(CompilerContext compilerContext) throws IOException, CompileException;
}
