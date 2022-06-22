package hu.blackbelt.java.embedded.compiler.jdt;

import hu.blackbelt.java.embedded.compiler.api.CompilerFactory;
import org.osgi.service.component.annotations.Component;

import javax.tools.*;

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
}
