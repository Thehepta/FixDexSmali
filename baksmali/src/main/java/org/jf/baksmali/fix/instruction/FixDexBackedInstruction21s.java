package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction21s;

public class FixDexBackedInstruction21s extends DexBackedInstruction21s {

    DexReader reader;
    public FixDexBackedInstruction21s(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile, opcode, instructionStartOffset);
        this.reader = reader;
    }

    @Override public int getRegisterA() { return reader.readUbyte(instructionStart + 1); }
    @Override public int getNarrowLiteral() { return reader.readShort(instructionStart + 2); }
    @Override public long getWideLiteral() { return getNarrowLiteral(); }
}
