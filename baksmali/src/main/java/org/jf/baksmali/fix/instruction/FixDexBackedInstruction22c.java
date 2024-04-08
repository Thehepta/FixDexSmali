package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction22c;
import org.jf.dexlib2.dexbacked.reference.DexBackedReference;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.util.NibbleUtils;

import javax.annotation.Nonnull;

public class FixDexBackedInstruction22c extends DexBackedInstruction22c {

    DexReader reader;
    public FixDexBackedInstruction22c(DexBackedDexFile dexFile, Opcode opcode, int instructionStartOffset, DexReader reader) {
        super(dexFile, opcode, instructionStartOffset);
        this.reader = reader;
    }

    @Override
    public int getRegisterA() {
        return NibbleUtils.extractLowUnsignedNibble(reader.readByte(instructionStart + 1));
    }

    @Override
    public int getRegisterB() {
        return NibbleUtils.extractHighUnsignedNibble(reader.readByte(instructionStart + 1));
    }

    @Nonnull
    @Override
    public Reference getReference() {
        return DexBackedReference.makeReference(
                dexFile, opcode.referenceType, reader.readUshort(instructionStart + 2));
    }

    @Override
    public int getReferenceType() {
        return opcode.referenceType;
    }
}
