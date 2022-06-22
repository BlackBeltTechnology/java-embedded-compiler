package hu.blackbelt.java.embedded.compiler.osgi;

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