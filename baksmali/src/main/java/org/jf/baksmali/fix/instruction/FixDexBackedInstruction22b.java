package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction22b;

public class FixDexBackedInstruction22b extends DexBackedInstruction22b {

    DexReader reader;
    public FixDexBackedInstruction22b(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile, opcode, instructionStartOffset);
        this.reader = reader;
    }

    @Override public int getRegisterA() { return reader.readUbyte(instructionStart + 1); }
    @Override public int getRegisterB() { return reader.readUbyte(instructionStart + 2); }
    @Override public int getNarrowLiteral() { return reader.readByte(instructionStart + 3); }
    @Override public long getWideLiteral() { return getNarrowLiteral(); }
}
