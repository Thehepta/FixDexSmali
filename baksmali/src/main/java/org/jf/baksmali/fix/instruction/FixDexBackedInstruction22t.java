package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction22t;
import org.jf.util.NibbleUtils;

public class FixDexBackedInstruction22t extends DexBackedInstruction22t {

    DexReader reader;
    public FixDexBackedInstruction22t(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
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

    @Override public int getCodeOffset() { return reader.readShort(instructionStart + 2); }

}
