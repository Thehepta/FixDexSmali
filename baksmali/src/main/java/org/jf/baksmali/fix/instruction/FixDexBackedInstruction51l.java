package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction51l;

public class FixDexBackedInstruction51l extends DexBackedInstruction51l {
    DexReader reader;
    public FixDexBackedInstruction51l(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile,opcode,instructionStartOffset);
        this.reader = reader;
    }
    @Override public int getRegisterA() { return reader.readUbyte(instructionStart + 1); }
    @Override public long getWideLiteral() { return reader.readLong(instructionStart + 2); }
}
