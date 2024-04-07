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
    public FixClassDefinition(@Nonnull BaksmaliOptions options, @Nonnull ClassDef classDef) {
        super(options, classDef);
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
                MethodDefinition methodDefinition = new FixMethodDefinition(this, method, methodImpl);
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
        if(method.getDefiningClass().contains("Lcom/hepta/androidgrpc/dump")){
            if(method.getName().contains("PreLoadNativeSO")){
                System.out.println(method.getDefiningClass()+"->"+method.getName());

                byte[] dumpdexMethod_codeitem={
                        0x05,0x00,0x02,0x00,0x02,0x00,0x01,0x00,0x38,0x1C,0x00,0x00,0x35,0x00,0x00,0x00,
                        0x1A,0x00, (byte) 0x8F,0x00,0x71,0x00,0x01,0x00,0x00,0x00,0x0A,0x01,0x39,0x01,0x05,0x00,
                        0x1A,0x01, (byte) 0x90,0x00,0x07,0x10,0x22,0x01,0x36,0x00,0x70,0x10,0x57,0x00,0x01,0x00,
                        0x6E,0x20,0x59,0x00,0x41,0x00,0x0C,0x01,0x1A,0x02,0x01,0x00,0x6E,0x20,0x59,0x00,
                        0x21,0x00,0x0C,0x01,0x6E,0x20,0x59,0x00,0x01,0x00,0x0C,0x01,0x1A,0x02,0x08,0x00,
                        0x6E,0x20,0x59,0x00,0x21,0x00,0x0C,0x01,0x6E,0x10,0x5A,0x00,0x01,0x00,0x0C,0x01,
                        0x71,0x10,0x5B,0x00,0x01,0x00,0x28,0x09,0x0D,0x00,0x1A,0x01,0x6F,0x00,0x1A,0x02,
                        0x72,0x00,0x71,0x20,0x02,0x00,0x21,0x00,0x0E,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                        0x2B,0x00,0x01,0x00,0x01,0x01,0x30,0x2C
                };
                DexBuffer dexBuffer = new DexBuffer(dumpdexMethod_codeitem);
                MethodImplementation implementation = new FixMethodImplementation(dexFile,dexBuffer,method,0);
                return implementation;
            }
        }
        return method.getImplementation();
    }
}
