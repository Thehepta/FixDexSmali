package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction11x;

import javax.annotation.Nonnull;

public class FixDexBackedInstruction11x extends DexBackedInstruction11x {
    DexReader reader;
    public FixDexBackedInstruction11x(@Nonnull DexBackedDexFile dexFile, @Nonnull Opcode opcode, int instructionStart, DexReader reader) {
        super(dexFile, opcode, instructionStart);
        this.reader = reader;
    }

    @Override public int getRegisterA() { return reader.readUbyte(instructionStart + 1); }

}
