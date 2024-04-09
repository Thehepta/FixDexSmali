package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.*;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.util.ExceptionWithContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FixDexBackedInstruction extends DexBackedInstruction {
    public FixDexBackedInstruction(@Nonnull DexBackedDexFile dexFile, @Nonnull Opcode opcode, int instructionStart) {
        super(dexFile, opcode, instructionStart);
    }

    @Nonnull
    public static Instruction readFrom(DexBackedDexFile dexFile, @Nonnull DexReader reader) {
        int opcodeValue = reader.peekUbyte();

        if (opcodeValue == 0) {
            opcodeValue = reader.peekUshort();
        }

        Opcode opcode = dexFile.getOpcodes().getOpcodeByValue(opcodeValue);

        Instruction instruction = buildInstruction(dexFile, opcode,
                reader.getOffset() + reader.dexBuf.getBaseOffset() -
                        dexFile.getBuffer().getBaseOffset() - dexFile.getBaseDataOffset(),reader);
        reader.moveRelative(instruction.getCodeUnits()*2);
        return instruction;
    }

    private static DexBackedInstruction buildInstruction(@Nonnull DexBackedDexFile dexFile, @Nullable Opcode opcode,
                                                         int instructionStartOffset, DexReader reader) {
        if (opcode == null) {
            return new DexBackedUnknownInstruction(dexFile, instructionStartOffset);
        }
        switch (opcode.format) {
            case Format10t:
                return new FixDexBackedInstruction10t(dexFile, opcode, instructionStartOffset,reader);
            case Format10x:
                return new DexBackedInstruction10x(dexFile, opcode, instructionStartOffset);  //
            case Format11n:
                return new FixDexBackedInstruction11n(dexFile, opcode, instructionStartOffset,reader);
            case Format11x:
                return new FixDexBackedInstruction11x(dexFile, opcode, instructionStartOffset,reader);
            case Format12x:
                return new FixDexBackedInstruction12x(dexFile, opcode, instructionStartOffset,reader);
            case Format20bc:
                return new FixDexBackedInstruction20bc(dexFile, opcode, instructionStartOffset,reader);
            case Format20t:
                return new FixDexBackedInstruction20t(dexFile, opcode, instructionStartOffset,reader);
            case Format21c:
                return new FixDexBackedInstruction21c(dexFile, opcode, instructionStartOffset,reader);
            case Format21ih:
                return new FixDexBackedInstruction21ih(dexFile, opcode, instructionStartOffset,reader);
            case Format21lh:
                return new FixDexBackedInstruction21lh(dexFile, opcode, instructionStartOffset,reader);
            case Format21s:
                return new FixDexBackedInstruction21s(dexFile, opcode, instructionStartOffset,reader);
            case Format21t:
                return new FixDexBackedInstruction21t(dexFile, opcode, instructionStartOffset,reader);
            case Format22b:
                return new FixDexBackedInstruction22b(dexFile, opcode, instructionStartOffset,reader);
            case Format22c:
                return new FixDexBackedInstruction22c(dexFile, opcode, instructionStartOffset,reader);
            case Format22cs:
                return new FixDexBackedInstruction22cs(dexFile, opcode, instructionStartOffset,reader);
            case Format22s:
                return new FixDexBackedInstruction22s(dexFile, opcode, instructionStartOffset,reader);
            case Format22t:
                return new FixDexBackedInstruction22t(dexFile, opcode, instructionStartOffset,reader);
            case Format22x:
                return new FixDexBackedInstruction22x(dexFile, opcode, instructionStartOffset,reader);
            case Format23x:
                return new FixDexBackedInstruction23x(dexFile, opcode, instructionStartOffset,reader);
            case Format30t:
                return new FixDexBackedInstruction30t(dexFile, opcode, instructionStartOffset,reader);
            case Format31c:
                return new FixDexBackedInstruction31c(dexFile, opcode, instructionStartOffset,reader);
            case Format31i:
                return new FixDexBackedInstruction31i(dexFile, opcode, instructionStartOffset,reader);
            case Format31t:
                return new FixDexBackedInstruction31t(dexFile, opcode, instructionStartOffset,reader);
            case Format32x:
                return new FixDexBackedInstruction32x(dexFile, opcode, instructionStartOffset,reader);
            case Format35c:
                return new FixDexBackedInstruction35c(dexFile, opcode, instructionStartOffset,reader);
            case Format35ms:
                return new FixDexBackedInstruction35ms(dexFile, opcode, instructionStartOffset,reader);
            case Format35mi:
                return new FixDexBackedInstruction35mi(dexFile, opcode, instructionStartOffset,reader);
            case Format3rc:
                return new FixDexBackedInstruction3rc(dexFile, opcode, instructionStartOffset,reader);
            case Format3rmi:
                return new FixDexBackedInstruction3rmi(dexFile, opcode, instructionStartOffset,reader);
            case Format3rms:
                return new FixDexBackedInstruction3rms(dexFile, opcode, instructionStartOffset,reader);
            case Format45cc:
                return new FixDexBackedInstruction45cc(dexFile, opcode, instructionStartOffset,reader);
            case Format4rcc:
                return new FixDexBackedInstruction4rcc(dexFile, opcode, instructionStartOffset,reader);
            case Format51l:
                return new FixDexBackedInstruction51l(dexFile, opcode, instructionStartOffset,reader);
            case PackedSwitchPayload:
                return new FixDexBackedPackedSwitchPayload(dexFile, instructionStartOffset,reader);
            case SparseSwitchPayload:
                return new FixDexBackedSparseSwitchPayload(dexFile, instructionStartOffset,reader);
            case ArrayPayload:
                return new FixDexBackedArrayPayload(dexFile, instructionStartOffset,reader);
            default:
                throw new ExceptionWithContext("Unexpected opcode format: %s", opcode.format.toString());
        }
    }

}
