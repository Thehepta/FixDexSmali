package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction35mi;
import org.jf.util.NibbleUtils;

public class FixDexBackedInstruction35mi extends DexBackedInstruction35mi {
    DexReader reader;
    public FixDexBackedInstruction35mi(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile,opcode,instructionStartOffset);
        this.reader = reader;
    }
    @Override public int getRegisterCount() {
        return NibbleUtils.extractHighUnsignedNibble(reader.readUbyte(instructionStart + 1));
    }

    @Override
    public int getRegisterC() {
        return NibbleUtils.extractLowUnsignedNibble(reader.readUbyte(instructionStart + 4));
    }

    @Override
    public int getRegisterD() {
        return NibbleUtils.extractHighUnsignedNibble(reader.readUbyte(instructionStart + 4));
    }

    @Override
    public int getRegisterE() {
        return NibbleUtils.extractLowUnsignedNibble(reader.readUbyte(instructionStart + 5));
    }

    @Override
    public int getRegisterF() {
        return NibbleUtils.extractHighUnsignedNibble(reader.readUbyte(instructionStart + 5));
    }

    @Override
    public int getRegisterG() {
        return NibbleUtils.extractLowUnsignedNibble(reader.readUbyte(instructionStart + 1));
    }

    @Override
    public int getInlineIndex() {
        return reader.readUshort(instructionStart + 2);
    }
}
