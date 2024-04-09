package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction10x;

import javax.annotation.Nonnull;

public class FixDexBackedInstruction10x extends DexBackedInstruction10x {
    DexReader reader;
    public FixDexBackedInstruction10x(@Nonnull DexBackedDexFile dexFile, @Nonnull Opcode opcode, int instructionStart, DexReader reader) {
        super(dexFile, opcode, instructionStart);
        this.reader = reader;
    }

}
