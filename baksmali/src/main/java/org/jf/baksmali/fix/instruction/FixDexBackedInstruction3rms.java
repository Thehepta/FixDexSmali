package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction3rms;

public class FixDexBackedInstruction3rms extends DexBackedInstruction3rms {
    DexReader reader;
    public FixDexBackedInstruction3rms(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile,opcode,instructionStartOffset);
        this.reader = reader;
    }

    @Override public int getRegisterCount() {
        return reader.readUbyte(instructionStart + 1);
    }

    @Override
    public int getStartRegister() {
        return reader.readUshort(instructionStart + 4);
    }

    @Override
    public int getVtableIndex() {
        return reader.readUshort(instructionStart + 2);
    }

}
