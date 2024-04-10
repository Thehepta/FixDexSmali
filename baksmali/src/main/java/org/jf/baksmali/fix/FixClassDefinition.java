package org.jf.baksmali.fix;

import org.jf.baksmali.Adaptors.ClassDefinition;
import org.jf.baksmali.Adaptors.MethodDefinition;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.baksmali.formatter.BaksmaliWriter;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBuffer;
import org.jf.dexlib2.iface.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FixClassDefinition extends ClassDefinition {

    FixDumpClassCodeItem fixDumpClassCodeItem;
    public FixClassDefinition(@Nonnull BaksmaliOptions options, @Nonnull ClassDef classDef, FixDumpClassCodeItem fixDumpClassCodeItem) {
        super(options, classDef);
        this.fixDumpClassCodeItem = fixDumpClassCodeItem;
    }

    @Override
    public Set<String> writeDirectMethods(BaksmaliWriter writer) throws IOException {
        boolean wroteHeader = false;
        Set<String> writtenMethods = new HashSet<String>();

        Iterable<? extends Method> directMethods;
        if (classDef instanceof DexBackedClassDef) {
            directMethods = ((DexBackedClassDef)classDef).getDirectMethods(false);
        } else {
            directMethods = classDef.getDirectMethods();
        }

        for (Method method: directMethods) {
            if (!wroteHeader) {
                writer.write("\n\n");
                writer.write("# direct methods");
                wroteHeader = true;
            }
            writer.write('\n');

            // TODO: check for method validation errors
            String methodString = getFormatter().getShortMethodDescriptor(method);

            BaksmaliWriter methodWriter = writer;
            if (!writtenMethods.add(methodString)) {
                writer.write("# duplicate method ignored\n");
                methodWriter = getCommentingWriter(writer);
            }
            MethodImplementation methodImpl = FixgetImplementation(method);
            if (methodImpl == null) {
                MethodDefinition.writeEmptyMethodTo(methodWriter, method, this);
            } else {
                MethodDefinition methodDefinition = new MethodDefinition(this, method, methodImpl);
                methodDefinition.writeTo(methodWriter);
            }
        }
        return writtenMethods;
    }

    @Override
    public void writeVirtualMethods(BaksmaliWriter writer, Set<String> directMethods) throws IOException {
        boolean wroteHeader = false;
        Set<String> writtenMethods = new HashSet<String>();

        Iterable<? extends Method> virtualMethods;
        if (classDef instanceof DexBackedClassDef) {
            virtualMethods = ((DexBackedClassDef)classDef).getVirtualMethods(false);
        } else {
            virtualMethods = classDef.getVirtualMethods();
        }

        for (Method method: virtualMethods) {
            if (!wroteHeader) {
                writer.write("\n\n");
                writer.write("# virtual methods");
                wroteHeader = true;
            }
            writer.write('\n');

            // TODO: check for method validation errors
            String methodString = getFormatter().getShortMethodDescriptor(method);

            BaksmaliWriter methodWriter = writer;
            if (!writtenMethods.add(methodString)) {
                writer.write("# duplicate method ignored\n");
                methodWriter = getCommentingWriter(writer);
            } else if (directMethods.contains(methodString)) {
                writer.write("# There is both a direct and virtual method with this signature.\n" +
                        "# You will need to rename one of these methods, including all references.\n");
                System.err.println(String.format("Duplicate direct+virtual method found: %s->%s",
                        classDef.getType(), methodString));
                System.err.println("You will need to rename one of these methods, including all references.");
            }

            MethodImplementation methodImpl = FixgetImplementation(method);
            if (methodImpl == null) {
                MethodDefinition.writeEmptyMethodTo(methodWriter, method, this);
            } else {
                MethodDefinition methodDefinition = new MethodDefinition(this, method, methodImpl);
                methodDefinition.writeTo(methodWriter);
            }
        }
    }

    MethodImplementation FixgetImplementation(Method method){

        DexBackedDexFile dexFile =  ((DexBackedClassDef)classDef).dexFile;
        if(fixDumpClassCodeItem!=null){
            String methodString = getFormatter().getShortMethodDescriptor(method);
            FixDumpClassCodeItem.FixDumpMethodCodeItem fixDumpMethodCodeItem =  fixDumpClassCodeItem.methodCodeItemList.get(methodString);
            DexBuffer dexBuffer = new DexBuffer(fixDumpMethodCodeItem.code_item);
            MethodImplementation implementation = new FixMethodImplementation(dexFile,dexBuffer,method,0);
            return implementation;
        }
        return method.getImplementation();
    }
}
