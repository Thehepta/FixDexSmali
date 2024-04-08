package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction21lh;

public class FixDexBackedInstruction21lh extends DexBackedInstruction21lh {

    DexReader reader;
    public FixDexBackedInstruction21lh(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile, opcode, instructionStartOffset);
        this.reader = reader;
    }

    @Override public int getRegisterA() { return reader.readUbyte(instructionStart + 1); }
    @Override public long getWideLiteral() { return ((long)getHatLiteral()) << 48; }
    @Override public short getHatLiteral() { return (short)reader.readShort(instructionStart + 2); }
}
