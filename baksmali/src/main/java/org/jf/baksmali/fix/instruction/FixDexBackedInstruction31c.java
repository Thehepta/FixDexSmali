package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction31c;
import org.jf.dexlib2.dexbacked.reference.DexBackedReference;
import org.jf.dexlib2.iface.reference.Reference;

import javax.annotation.Nonnull;

public class FixDexBackedInstruction31c extends DexBackedInstruction31c {
    DexReader reader;
    public FixDexBackedInstruction31c(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile,opcode,instructionStartOffset);
        this.reader = reader;
    }


    @Override public int getRegisterA() { return reader.readUbyte(instructionStart + 1); }

    @Nonnull
    @Override
    public Reference getReference() {
        return DexBackedReference.makeReference(dexFile, opcode.referenceType,
                reader.readSmallUint(instructionStart + 2));
    }

    @Override
    public int getReferenceType() {
        return opcode.referenceType;
    }

}
