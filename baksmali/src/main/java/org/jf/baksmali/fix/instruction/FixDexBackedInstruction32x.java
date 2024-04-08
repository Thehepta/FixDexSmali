package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction32x;

public class FixDexBackedInstruction32x extends DexBackedInstruction32x {
    DexReader reader;
    public FixDexBackedInstruction32x(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile,opcode,instructionStartOffset);
        this.reader = reader;
    }

    @Override public int getRegisterA() { return reader.readUshort(instructionStart + 2); }
    @Override public int getRegisterB() { return reader.readUshort(instructionStart + 4); }
}
