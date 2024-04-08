package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction22s;
import org.jf.util.NibbleUtils;

public class FixDexBackedInstruction22s extends DexBackedInstruction22s {

    DexReader reader;
    public FixDexBackedInstruction22s(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile,  opcode,  instructionStartOffset);
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

    @Override public int getNarrowLiteral() { return reader.readShort(instructionStart + 2); }
    @Override public long getWideLiteral() { return getNarrowLiteral(); }


}
