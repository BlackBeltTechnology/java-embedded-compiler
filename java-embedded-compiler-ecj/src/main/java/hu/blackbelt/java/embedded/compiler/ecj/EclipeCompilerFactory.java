package hu.blackbelt.java.embedded.compiler.ecj;

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
