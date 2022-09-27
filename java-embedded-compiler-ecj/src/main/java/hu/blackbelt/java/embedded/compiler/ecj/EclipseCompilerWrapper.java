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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.util.*;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompilerImpl;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;

import javax.annotation.processing.Processor;
import javax.tools.*;

public class EclipseCompilerWrapper {
    public static JavaCompiler.CompilationTask getTask(Writer out, JavaFileManager fileManager, DiagnosticListener<? super JavaFileObject> someDiagnosticListener, Iterable<String> options, Iterable<String> classes, Iterable<? extends JavaFileObject> compilationUnits) {
        PrintWriter writerOut = null;
        PrintWriter writerErr = null;
        if (out == null) {
            writerOut = new PrintWriter(System.err);
            writerErr = new PrintWriter(System.err);
        } else {
            writerOut = new PrintWriter(out);
            writerErr = new PrintWriter(out);
        }
        final EclipseCompilerImpl eclipseCompiler2 = new EclipseCompilerImpl(writerOut, writerErr, false) {
            @Override
            public CompilationUnit[] getCompilationUnits() {
                HashtableOfObject knownFileNames = new HashtableOfObject();
                ArrayList<CompilationUnit> units = new ArrayList<>();
                for (int round = 0; round < 2; round++) {
                    int i = 0;
                    for (final JavaFileObject javaFileObject : compilationUnits) {
                        String name = javaFileObject.getName();
                        char[] charName = name.toCharArray();
                        boolean isModuleInfo = CharOperation.endsWith(charName, TypeConstants.MODULE_INFO_FILE_NAME);
                        if (isModuleInfo == (round==0)) { // 1st round: modules, 2nd round others (to ensure populating pathToModCU well in time)
                            if (knownFileNames.get(charName) != null)
                                throw new IllegalArgumentException(this.bind("unit.more", name)); //$NON-NLS-1$
                            knownFileNames.put(charName, charName);
                            File file = new File(name);
//                            if (!file.exists())
//                                throw new IllegalArgumentException(this.bind("unit.missing", name)); //$NON-NLS-1$
                            CompilationUnit cu = new CompilationUnit(null,
                                    name,
                                    null,
                                    this.destinationPaths[i],
                                    shouldIgnoreOptionalProblems(this.ignoreOptionalProblemsFromFolders, name.toCharArray()), this.modNames[i]) {

                                @Override
                                public char[] getContents() {
                                    try {
                                        return javaFileObject.getCharContent(true).toString().toCharArray();
                                    } catch(IOException e) {
                                        e.printStackTrace();
                                        throw new AbortCompilationUnit(null, e, null);
                                    }
                                }
                            };
                            units.add(cu);
                            //this.javaFileObjectMap.put(cu, javaFileObject);
                        }
                        i++;
                    }
                }
                CompilationUnit[] result = new CompilationUnit[units.size()];
                units.toArray(result);
                return result;            }
        };
        eclipseCompiler2.diagnosticListener = someDiagnosticListener;
        eclipseCompiler2.fileManager = fileManager;

        eclipseCompiler2.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_8);
        eclipseCompiler2.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_8);
        eclipseCompiler2.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_8);

        ArrayList<String> allOptions = new ArrayList<>();
        if (options != null) {
            for (Iterator<String> iterator = options.iterator(); iterator.hasNext(); ) {
                eclipseCompiler2.fileManager.handleOption(iterator.next(), iterator);
            }
            for (String option : options) {
                allOptions.add(option);
            }
        }

        if (compilationUnits != null) {
            for (JavaFileObject javaFileObject : compilationUnits) {
                // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6419926
                // compells us to check that the returned URIs are absolute,
                // which they happen not to be for the default compiler on some
                // unices
                URI uri = javaFileObject.toUri();
                if (!uri.isAbsolute()) {
                    uri = URI.create("file://" + uri.toString()); //$NON-NLS-1$
                }
                if (uri.getScheme().equals("file")) { //$NON-NLS-1$
                    allOptions.add(new File(uri).getAbsolutePath());
                } else {
                    allOptions.add(uri.toString());
                }
            }
        }

        if (classes != null) {
            allOptions.add("-classNames"); //$NON-NLS-1$
            StringBuilder builder = new StringBuilder();
            int i = 0;
            for (String className : classes) {
                if (i != 0) {
                    builder.append(',');
                }
                builder.append(className);
                i++;
            }
            allOptions.add(String.valueOf(builder));
        }

        final String[] optionsToProcess = new String[allOptions.size()];
        allOptions.toArray(optionsToProcess);
        try {
            eclipseCompiler2.configure(optionsToProcess);
        } catch (IllegalArgumentException e) {
            throw e;
        }

        if (eclipseCompiler2.fileManager instanceof StandardJavaFileManager) {
            StandardJavaFileManager javaFileManager = (StandardJavaFileManager) eclipseCompiler2.fileManager;

            Iterable<? extends File> location = javaFileManager.getLocation(StandardLocation.CLASS_OUTPUT);
            if (location != null) {
                eclipseCompiler2.setDestinationPath(location.iterator().next().getAbsolutePath());
            }
        }

        return new JavaCompiler.CompilationTask() {
            private boolean hasRun = false;
            @Override
            public Boolean call() {
                // set up compiler with passed options
                if (this.hasRun) {
                    throw new IllegalStateException("This task has already been run"); //$NON-NLS-1$
                }
                Boolean value = eclipseCompiler2.call() ? Boolean.TRUE : Boolean.FALSE;
                this.hasRun = true;
                return value;
            }
            @Override
            public void setLocale(Locale locale) {
                eclipseCompiler2.setLocale(locale);
            }
            @Override
            public void setProcessors(Iterable<? extends Processor> processors) {
                ArrayList<Processor> temp = new ArrayList<>();
                for (Processor processor : processors) {
                    temp.add(processor);
                }
                Processor[] processors2 = new Processor[temp.size()];
                temp.toArray(processors2);
                //eclipseCompiler2.processors = processors2;
            }

            public void addModules(Iterable<String> moduleName) {
            }
        };
    }
}
