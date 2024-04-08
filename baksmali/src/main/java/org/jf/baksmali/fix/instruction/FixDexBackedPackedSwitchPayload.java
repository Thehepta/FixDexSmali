package org.jf.baksmali.fix.instruction;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.instruction.DexBackedPackedSwitchPayload;
import org.jf.dexlib2.dexbacked.util.FixedSizeList;
import org.jf.dexlib2.iface.instruction.SwitchElement;

import javax.annotation.Nonnull;
import java.util.List;

public class FixDexBackedPackedSwitchPayload extends DexBackedPackedSwitchPayload {
    DexReader reader;
    public FixDexBackedPackedSwitchPayload(DexBackedDexFile dexFile,  int instructionStartOffset, DexReader reader) {
        super(dexFile,instructionStartOffset);
        this.reader = reader;
    }

    @Nonnull
    @Override
    public List<? extends SwitchElement> getSwitchElements() {
        final int firstKey = reader.readInt(instructionStart + FIRST_KEY_OFFSET);
        return new FixedSizeList<SwitchElement>() {
            @Nonnull
            @Override
            public SwitchElement readItem(final int index) {
                return new SwitchElement() {
                    @Override
                    public int getKey() {
                        return firstKey + index;
                    }

                    @Override
                    public int getOffset() {
                        return reader.readInt(instructionStart + TARGETS_OFFSET + index*4);
                    }
                };
            }

            @Override public int size() { return elementCount; }
        };
    }

    @Override public int getCodeUnits() { return 4 + elementCount*2; }

}