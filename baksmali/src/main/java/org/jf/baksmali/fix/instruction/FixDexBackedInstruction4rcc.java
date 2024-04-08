package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction4rcc;
import org.jf.dexlib2.dexbacked.reference.DexBackedReference;
import org.jf.dexlib2.iface.reference.Reference;

import javax.annotation.Nonnull;

public class FixDexBackedInstruction4rcc extends DexBackedInstruction4rcc {
    DexReader reader;
    public FixDexBackedInstruction4rcc(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile,opcode,instructionStartOffset);
        this.reader = reader;
    }

    @Override public int getRegisterCount() {
        return reader.readUbyte(instructionStart + 1);
    }

    @Override
    public int getStartRegister() {
        return reader.readUshort(instructionStart + 4);
    }

    @Nonnull
    @Override
    public Reference getReference() {
        return DexBackedReference.makeReference(dexFile, opcode.referenceType,
                reader.readUshort(instructionStart + 2));
    }

    @Override
    public int getReferenceType() {
        return opcode.referenceType;
    }

    @Override
    public Reference getReference2() {
        return DexBackedReference.makeReference(dexFile, opcode.referenceType2,
                reader.readUshort(instructionStart + 6));
    }

    @Override
    public int getReferenceType2() {
        return opcode.referenceType2;
    }

}
