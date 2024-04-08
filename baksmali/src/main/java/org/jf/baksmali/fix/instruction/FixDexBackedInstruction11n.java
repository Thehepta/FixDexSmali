package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction11n;
import org.jf.util.NibbleUtils;

import javax.annotation.Nonnull;

public class FixDexBackedInstruction11n extends DexBackedInstruction11n {

    DexReader reader;
    public FixDexBackedInstruction11n(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile, opcode, instructionStartOffset);
        this.reader = reader;
    }



    @Override
    public int getRegisterA() {
        return NibbleUtils.extractLowUnsignedNibble(reader.readByte(instructionStart + 1));
    }

    @Override
    public int getNarrowLiteral() {
        return NibbleUtils.extractHighSignedNibble(reader.readByte(instructionStart + 1));
    }

    @Override public long getWideLiteral() { return getNarrowLiteral(); }
}
