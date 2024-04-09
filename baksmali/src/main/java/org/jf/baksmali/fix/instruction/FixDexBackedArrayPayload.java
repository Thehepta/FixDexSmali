package org.jf.baksmali.fix.instruction;

import com.google.common.collect.ImmutableList;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedArrayPayload;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.util.FixedSizeList;
import org.jf.util.ExceptionWithContext;

import javax.annotation.Nonnull;
import java.util.List;

public class FixDexBackedArrayPayload extends DexBackedArrayPayload {
    DexReader reader;
    public FixDexBackedArrayPayload(DexBackedDexFile dexFile, int instructionStartOffset, DexReader reader) {
        super(dexFile,instructionStartOffset);
        this.reader = reader;
    }


    @Override public int getElementWidth() { return elementWidth; }

    @Nonnull
    @Override
    public List<Number> getArrayElements() {
        final int elementsStart = instructionStart + ELEMENTS_OFFSET;

        abstract class ReturnedList extends FixedSizeList<Number> {
            @Override public int size() { return elementCount; }
        }

        if (elementCount == 0) {
            return ImmutableList.of();
        }

        switch (elementWidth) {
            case 1:
                return new ReturnedList() {
                    @Nonnull
                    @Override
                    public Number readItem(int index) {
                        return reader.readByte(elementsStart + index);
                    }
                };
            case 2:
                return new ReturnedList() {
                    @Nonnull
                    @Override
                    public Number readItem(int index) {
                        return reader.readShort(elementsStart + index*2);
                    }
                };
            case 4:
                return new ReturnedList() {
                    @Nonnull
                    @Override
                    public Number readItem(int index) {
                        return reader.readInt(elementsStart + index*4);
                    }
                };
            case 8:
                return new ReturnedList() {
                    @Nonnull
                    @Override
                    public Number readItem(int index) {
                        return reader.readLong(elementsStart + index*8);
                    }
                };
            default:
                throw new ExceptionWithContext("Invalid element width: %d", elementWidth);
        }
    }

    @Override
    public int getCodeUnits() {
        return 4 + (elementWidth*elementCount + 1) / 2;
    }

}