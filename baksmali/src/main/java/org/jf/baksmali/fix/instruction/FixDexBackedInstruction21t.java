package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction21t;

public class FixDexBackedInstruction21t extends DexBackedInstruction21t {

    DexReader reader;
    public FixDexBackedInstruction21t(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile,opcode,instructionStartOffset);
        this.reader = reader;
    }

    @Override public int getRegisterA() { return reader.readUbyte(instructionStart + 1); }
    @Override public int getCodeOffset() { return reader.readShort(instructionStart + 2); }
}
