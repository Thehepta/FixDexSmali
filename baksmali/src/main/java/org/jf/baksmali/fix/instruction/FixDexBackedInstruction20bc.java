package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.ReferenceType;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction20bc;
import org.jf.dexlib2.dexbacked.reference.DexBackedReference;
import org.jf.dexlib2.iface.reference.Reference;

import javax.annotation.Nonnull;

public class FixDexBackedInstruction20bc extends DexBackedInstruction20bc {

    DexReader dexReader;
    public FixDexBackedInstruction20bc(@Nonnull DexBackedDexFile dexFile,
                                    @Nonnull Opcode opcode,
                                    int instructionStart,DexReader dexReader) {
        super(dexFile, opcode, instructionStart);
        this.dexReader = dexReader;
    }

    @Override public int getVerificationError() {
        return dexReader.readUbyte(instructionStart + 1) & 0x3f;
    }

    @Nonnull
    @Override
    public Reference getReference() {
        int referenceIndex = dexReader.readUshort(instructionStart + 2);
        try {
            int referenceType = getReferenceType();
            return DexBackedReference.makeReference(dexFile, referenceType, referenceIndex);
        } catch (ReferenceType.InvalidReferenceTypeException ex) {
            return new Reference() {
                @Override
                public void validateReference() throws InvalidReferenceException {
                    throw new InvalidReferenceException(String.format("%d@%d", ex.getReferenceType(), referenceIndex),
                            ex);
                }
            };
        }
    }

    @Override public int getReferenceType() {
        int referenceType = (dexReader.readUbyte(instructionStart + 1) >>> 6) + 1;
        ReferenceType.validateReferenceType(referenceType);
        return referenceType;
    }
}
