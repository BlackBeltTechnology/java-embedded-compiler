package hu.blackbelt.java.embedded.compiler.api.exception;

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

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.Locale;

public class CompileException extends Exception {
    List<Diagnostic<? extends JavaFileObject>> diagnostics;

    public CompileException(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        super(getDiagMessages(diagnostics));
        this.diagnostics = diagnostics;

    }

    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
        return diagnostics;
    }

    private static String getDiagMessages(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Diagnostic<?> diagnostic : diagnostics) {
            if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR)) {
                stringBuffer.append("\nError " + diagnostic.getMessage(new Locale("en"))  + "\nin " + diagnostic.getSource() + "\non line " + diagnostic.getLineNumber() +"\n");
            }
        }
        return stringBuffer.toString();
    }
}
