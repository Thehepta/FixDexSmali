package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction12x;
import org.jf.util.NibbleUtils;

import javax.annotation.Nonnull;

public class FixDexBackedInstruction12x extends DexBackedInstruction12x {

    DexReader reader;
    public FixDexBackedInstruction12x(@Nonnull DexBackedDexFile dexFile, @Nonnull Opcode opcode, int instructionStart, DexReader reader) {
        super(dexFile, opcode, instructionStart);
        this.reader = reader;
    }

    @Override
    public int getRegisterA() {
        return NibbleUtils.extractLowUnsignedNibble(reader.readByte(instructionStart + 1));
    }

    @Override
    public int getRegisterB() {
        return NibbleUtils.extractHighUnsignedNibble(reader.readByte(instructionStart + 1));
    }
}
