package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction21ih;

public class FixDexBackedInstruction21ih extends DexBackedInstruction21ih {

    DexReader reader;

    public FixDexBackedInstruction21ih(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile,opcode,instructionStartOffset);
        this.reader = reader;
    }


    @Override public int getRegisterA() { return reader.readUbyte(instructionStart + 1); }
    @Override public int getNarrowLiteral() { return getHatLiteral() << 16; }
    @Override public long getWideLiteral() { return getNarrowLiteral(); }
    @Override public short getHatLiteral() { return (short)reader.readShort(instructionStart + 2); }

}
