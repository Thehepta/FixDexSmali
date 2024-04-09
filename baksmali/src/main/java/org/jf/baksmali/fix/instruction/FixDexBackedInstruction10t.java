package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction10t;

import javax.annotation.Nonnull;

public class FixDexBackedInstruction10t extends DexBackedInstruction10t {

    DexReader reader;
    public FixDexBackedInstruction10t(@Nonnull DexBackedDexFile dexFile, @Nonnull Opcode opcode, int instructionStart, DexReader reader) {
        super(dexFile, opcode, instructionStart);
        this.reader = reader;
    }

    @Override public int getCodeOffset() { return reader.readByte(instructionStart + 1); }

}
