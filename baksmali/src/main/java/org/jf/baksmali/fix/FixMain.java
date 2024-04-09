package org.jf.baksmali.fix;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.jf.baksmali.Adaptors.ClassDefinition;
import org.jf.baksmali.AnalysisArguments;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.baksmali.formatter.BaksmaliWriter;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.MultiDexContainer;
import org.jf.dexlib2.util.SyntheticAccessorResolver;
import org.jf.util.ClassFileNameHandler;
import org.jf.util.ConsoleUtil;
import org.jf.util.StringWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class FixMain {

    protected AnalysisArguments analysisArguments = new AnalysisArguments();

    protected MultiDexContainer.DexEntry<? extends DexBackedDexFile> dexEntry;

    protected DexBackedDexFile dexFile;
    public int apiLevel = -1;

    protected String inputEntry;
    protected File inputFile;

    public static void main(String[] args) {

        FixMain fixMain = new FixMain();
        String input = "examples/fix/classes4.dex";
        String outDir = "out1";
        int jobs = 16;
        fixMain.Main1(input,outDir,jobs,null,null);
    }
    public void Main1(String input, String outputDir, int jobs, List<String> classes,List<FixDumpClassCodeItem> dumpClassCodeItemList){

        loadDexFile(input);

        if (showDeodexWarning() && dexFile.supportsOptimizedOpcodes()) {
            StringWrapper.printWrappedString(System.err,
                    "Warning: You are disassembling an odex/oat file without deodexing it. You won't be able to " +
                            "re-assemble the results unless you deodex it. See \"baksmali help deodex\"",
                    ConsoleUtil.getConsoleWidth());
        }
        File outputDirectoryFile = new File(outputDir);
        if (!outputDirectoryFile.exists()) {
            if (!outputDirectoryFile.mkdirs()) {
                System.err.println("Can't create the output directory " + outputDir);
                System.exit(-1);
            }
        }

        if (analysisArguments.classPathDirectories == null || analysisArguments.classPathDirectories.isEmpty()) {
            analysisArguments.classPathDirectories = Lists.newArrayList(inputFile.getAbsoluteFile().getParent());
        }

        if (!disassembleDexFile(dexFile, outputDirectoryFile, jobs, getOptions(), classes,dumpClassCodeItemList)) {
            System.exit(-1);
        }

    }


    protected boolean showDeodexWarning() {
        return true;
    }

    protected void loadDexFile(@Nonnull String input) {
        File file = new File(input);

        while (file != null && !file.exists()) {
            file = file.getParentFile();
        }

        if (file == null || !file.exists() || file.isDirectory()) {
            System.err.println("Can't find file: " + input);
            System.exit(1);
        }

        inputFile = file;

        String dexEntryName = null;
        if (file.getPath().length() < input.length()) {
            dexEntryName = input.substring(file.getPath().length() + 1);
        }

        Opcodes opcodes = null;
        if (apiLevel != -1) {
            opcodes = Opcodes.forApi(apiLevel);
        }

        if (!Strings.isNullOrEmpty(dexEntryName)) {
            boolean exactMatch = false;
            if (dexEntryName.length() > 2 && dexEntryName.charAt(0) == '"' && dexEntryName.charAt(dexEntryName.length() - 1) == '"') {
                dexEntryName = dexEntryName.substring(1, dexEntryName.length() - 1);
                exactMatch = true;
            }

            inputEntry = dexEntryName;

            try {
                dexEntry = DexFileFactory.loadDexEntry(file, dexEntryName, exactMatch, opcodes);
                dexFile = dexEntry.getDexFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            try {
                MultiDexContainer<? extends DexBackedDexFile> container =
                        DexFileFactory.loadDexContainer(file, opcodes);

                if (container.getDexEntryNames().size() == 1) {
                    dexEntry = container.getEntry(container.getDexEntryNames().get(0));
                    assert dexEntry != null;
                    dexFile = dexEntry.getDexFile();
                } else if (container.getDexEntryNames().size() > 1) {
                    dexEntry = container.getEntry("classes.dex");
                    if (dexEntry == null) {
                        dexEntry = container.getEntry(container.getDexEntryNames().get(0));
                    }
                    assert dexEntry != null;
                    dexFile = dexEntry.getDexFile();
                } else {
                    throw new RuntimeException(String.format("\"%s\" has no dex files", input));
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    protected BaksmaliOptions getOptions() {
        if (dexFile == null) {
            throw new IllegalStateException("You must call loadDexFile first");
        }

        final BaksmaliOptions options = new BaksmaliOptions();

        options.parameterRegisters = true;
        options.localsDirective = false;
        options.sequentialLabels = false;
        options.debugInfo = true;
        options.codeOffsets = false;
        options.accessorComments = true;
        options.implicitReferences = false;
        options.normalizeVirtualMethods = false;
        options.registerInfo = 0;
        options.syntheticAccessorResolver = new SyntheticAccessorResolver(dexFile.getOpcodes(),
                dexFile.getClasses());
        options.allowOdex = false;
        return options;
    }


    public static boolean disassembleDexFile(DexFile dexFile, File outputDir, int jobs, final BaksmaliOptions options,
                                             @Nullable List<String> classes, List<FixDumpClassCodeItem> dumpClassCodeItemList) {

        //sort the classes, so that if we're on a case-insensitive file system and need to handle classes with file
        //name collisions, then we'll use the same name for each class, if the dex file goes through multiple
        //baksmali/smali cycles for some reason. If a class with a colliding name is added or removed, the filenames
        //may still change of course
        List<? extends ClassDef> classDefs = Ordering.natural().sortedCopy(dexFile.getClasses());

        final ClassFileNameHandler fileNameHandler = new ClassFileNameHandler(outputDir, ".smali");

        ExecutorService executor = Executors.newFixedThreadPool(jobs);
        List<Future<Boolean>> tasks = Lists.newArrayList();

        Set<String> classSet = null;
        if (classes != null) {
            classSet = new HashSet<String>(classes);
        }

        for (final ClassDef classDef: classDefs) {
            if (classSet != null && !classSet.contains(classDef.getType())) {
                continue;
            }
            tasks.add(executor.submit(new Callable<Boolean>() {
                @Override public Boolean call() throws Exception {
                    return disassembleClass(classDef, fileNameHandler, options,dumpClassCodeItemList);
                }
            }));
        }

        boolean errorOccurred = false;
        try {
            for (Future<Boolean> task: tasks) {
                while(true) {
                    try {
                        if (!task.get()) {
                            errorOccurred = true;
                        }
                    } catch (InterruptedException ex) {
                        continue;
                    } catch (ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
        } finally {
            executor.shutdown();
        }
        return !errorOccurred;
    }

    private static boolean disassembleClass(ClassDef classDef, ClassFileNameHandler fileNameHandler,
                                            BaksmaliOptions options, List<FixDumpClassCodeItem> dumpClassCodeItemList) {
        /**
         * The path for the disassembly file is based on the package name
         * The class descriptor will look something like:
         * Ljava/lang/Object;
         * Where the there is leading 'L' and a trailing ';', and the parts of the
         * package name are separated by '/'
         */
        String classDescriptor = classDef.getType();
        FixDumpClassCodeItem FixDumpClassCodeItem = null;
//        for(FixDumpClassCodeItem DumpClassCodeItem:dumpClassCodeItemList){
//            if(DumpClassCodeItem.clsTypeName.equals(classDescriptor)){
//                FixDumpClassCodeItem = DumpClassCodeItem;
//            }
//        }
        //validate that the descriptor is formatted like we expect
        if (classDescriptor.charAt(0) != 'L' ||
                classDescriptor.charAt(classDescriptor.length()-1) != ';') {
            System.err.println("Unrecognized class descriptor - " + classDescriptor + " - skipping class");
            return false;
        }

        File smaliFile = null;
        try {
            smaliFile = fileNameHandler.getUniqueFilenameForClass(classDescriptor);
        } catch (IOException ex) {
            System.err.println("\n\nError occurred while creating file for class " + classDescriptor);
            ex.printStackTrace();
            return false;
        }

        //create and initialize the top level string template
        ClassDefinition classDefinition = new FixClassDefinition(options, classDef,FixDumpClassCodeItem);

        //write the disassembly
        BaksmaliWriter writer = null;
        try
        {
            File smaliParent = smaliFile.getParentFile();
            if (!smaliParent.exists()) {
                if (!smaliParent.mkdirs()) {
                    // check again, it's likely it was created in a different thread
                    if (!smaliParent.exists()) {
                        System.err.println("Unable to create directory " + smaliParent.toString() + " - skipping class");
                        return false;
                    }
                }
            }

            if (!smaliFile.exists()){
                if (!smaliFile.createNewFile()) {
                    System.err.println("Unable to create file " + smaliFile.toString() + " - skipping class");
                    return false;
                }
            }

            BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(smaliFile), "UTF8"));

            writer = new BaksmaliWriter(
                    bufWriter,
                    options.implicitReferences ? classDef.getType() : null);
            classDefinition.writeTo(writer);
        } catch (Exception ex) {
            System.err.println("\n\nError occurred while disassembling class " + classDescriptor.replace('/', '.') + " - skipping class");
            ex.printStackTrace();
            // noinspection ResultOfMethodCallIgnored
            smaliFile.delete();
            return false;
        }
        finally
        {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Throwable ex) {
                    System.err.println("\n\nError occurred while closing file " + smaliFile.toString());
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }



}
