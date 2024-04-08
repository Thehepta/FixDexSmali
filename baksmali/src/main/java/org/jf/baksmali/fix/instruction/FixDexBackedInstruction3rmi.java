package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction3rmi;

public class FixDexBackedInstruction3rmi extends DexBackedInstruction3rmi {
    DexReader reader;
    public FixDexBackedInstruction3rmi(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
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
    public int getInlineIndex() {
        return reader.readUshort(instructionStart + 2);
    }

}