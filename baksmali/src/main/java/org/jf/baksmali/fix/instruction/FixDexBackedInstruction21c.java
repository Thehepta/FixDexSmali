package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction21c;
import org.jf.dexlib2.dexbacked.reference.DexBackedReference;
import org.jf.dexlib2.iface.reference.Reference;

import javax.annotation.Nonnull;

public class FixDexBackedInstruction21c extends DexBackedInstruction21c {

    public DexReader reader;
    public FixDexBackedInstruction21c(@Nonnull DexBackedDexFile dexFile,
                                      @Nonnull Opcode opcode,
                                      int instructionStart, DexReader reader) {
        super(dexFile, opcode, instructionStart);
        this.reader = reader;
    }

    @Override public int getRegisterA() { return reader.readUbyte(instructionStart + 1); }

    @Nonnull
    @Override
    public Reference getReference() {
        return DexBackedReference.makeReference(
                dexFile, opcode.referenceType, reader.readUshort(instructionStart + 2));
    }

    @Override
    public int getReferenceType() {
        return opcode.referenceType;
    }
}
