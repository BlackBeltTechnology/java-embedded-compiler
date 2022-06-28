package hu.blackbelt.java.embedded.compiler.api;

import javax.tools.*;
import java.util.List;

public interface CompilerFactory {
    JavaCompiler getCompiler();
    String getName();

    List<String> getExtraArgs();
}
