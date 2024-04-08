package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction35c;
import org.jf.dexlib2.dexbacked.reference.DexBackedReference;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.util.NibbleUtils;

import javax.annotation.Nonnull;

public class FixDexBackedInstruction35c extends DexBackedInstruction35c {
    DexReader reader;
    public FixDexBackedInstruction35c(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
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

    @Nonnull
    @Override
    public Reference getReference() {
        return DexBackedReference.makeReference(dexFile, opcode.referenceType,
                reader.readUshort(instructionStart + 2));
    }

    @Override
    public int getReferenceType() {
        return opcode.referenceType;
    }
}
