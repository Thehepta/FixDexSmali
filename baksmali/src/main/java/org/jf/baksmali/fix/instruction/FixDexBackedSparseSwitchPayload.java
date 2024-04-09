package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedSparseSwitchPayload;
import org.jf.dexlib2.dexbacked.util.FixedSizeList;
import org.jf.dexlib2.iface.instruction.SwitchElement;

import javax.annotation.Nonnull;
import java.util.List;

public class FixDexBackedSparseSwitchPayload extends DexBackedSparseSwitchPayload {
    DexReader reader;
    public FixDexBackedSparseSwitchPayload(DexBackedDexFile dexFile, int instructionStartOffset, DexReader reader) {
        super(dexFile,instructionStartOffset);
        this.reader = reader;
    }



    @Nonnull
    @Override
    public List<? extends SwitchElement> getSwitchElements() {
        return new FixedSizeList<SwitchElement>() {
            @Nonnull
            @Override
            public SwitchElement readItem(final int index) {
                return new SwitchElement() {
                    @Override
                    public int getKey() {
                        return reader.readInt(instructionStart + KEYS_OFFSET + index*4);
                    }

                    @Override
                    public int getOffset() {
                        return reader.readInt(instructionStart + KEYS_OFFSET + elementCount*4 + index*4);
                    }
                };
            }

            @Override public int size() { return elementCount; }
        };
    }

    @Override public int getCodeUnits() { return 2 + elementCount*4; }
}