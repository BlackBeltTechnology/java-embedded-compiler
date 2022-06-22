package hu.blackbelt.java.embedded.compiler.api.exception;

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
