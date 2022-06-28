package hu.blackbelt.java.embedded.compiler.jdt;

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
