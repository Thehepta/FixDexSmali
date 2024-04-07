package org.jf.baksmali.fix;

import com.google.common.collect.ImmutableList;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedMethod;
import org.jf.dexlib2.dexbacked.DexBuffer;
import org.jf.dexlib2.dexbacked.DexReader;
import org.jf.dexlib2.dexbacked.instruction.DexBackedInstruction;
import org.jf.dexlib2.dexbacked.raw.CodeItem;
import org.jf.dexlib2.dexbacked.util.DebugInfo;
import org.jf.dexlib2.dexbacked.util.FixedSizeList;
import org.jf.dexlib2.dexbacked.util.VariableSizeLookaheadIterator;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.iface.debug.DebugItem;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.util.AlignmentUtils;
import org.jf.util.ExceptionWithContext;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class FixMethodImplementation implements MethodImplementation {

    DexBuffer dexBuffer ;
    DexBackedDexFile dexFile;
    Method method;
    int codeOffset;
    public FixMethodImplementation(DexBackedDexFile dexFile, DexBuffer dexBuffer, Method method, int codeOffset) {
        this.dexFile = dexFile;
        this.dexBuffer = dexBuffer;
        this.method = method;
        this.codeOffset = codeOffset;
    }

    @Override
    public int getRegisterCount() {
        return dexBuffer.readShort(0);
    }
    protected int getTriesSize() {
        return dexBuffer.readUshort( CodeItem.TRIES_SIZE_OFFSET);
    }

    protected int getInstructionsSize() {
        return dexBuffer.readSmallUint( CodeItem.INSTRUCTION_COUNT_OFFSET);
    }
    protected int getInstructionsStartOffset() {
        return  CodeItem.INSTRUCTION_START_OFFSET;
    }

    @Nonnull
    @Override
    public Iterable<? extends Instruction> getInstructions() {
        // instructionsSize is the number of 16-bit code units in the instruction list, not the number of instructions
        int instructionsSize = getInstructionsSize();

        final int instructionsStartOffset = getInstructionsStartOffset();
        final int endOffset = instructionsStartOffset + (instructionsSize*2);
        return new Iterable<Instruction>() {
            @Override
            public Iterator<Instruction> iterator() {
                return new VariableSizeLookaheadIterator<Instruction>(
                        dexBuffer, instructionsStartOffset) {
                    @Override
                    protected Instruction readNextItem(@Nonnull DexReader reader) {
                        if (reader.getOffset() >= endOffset) {
                            return endOfData();
                        }

                        Instruction instruction = DexBackedInstruction.readFrom(dexFile, reader);

                        // Does the instruction extend past the end of the method?
                        int offset = reader.getOffset();
                        if (offset > endOffset || offset < 0) {
                            throw new ExceptionWithContext("The last instruction in method %s is truncated", method);
                        }
                        return instruction;
                    }
                };
            }
        };

    }

    @Nonnull
    @Override
    public List<? extends TryBlock<? extends ExceptionHandler>> getTryBlocks() {
        final int triesSize = getTriesSize();
        if (triesSize > 0) {
            int instructionsSize = getInstructionsSize();
            final int triesStartOffset = AlignmentUtils.alignOffset(
                    getInstructionsStartOffset() + (instructionsSize*2), 4);
            final int handlersStartOffset = triesStartOffset + triesSize* CodeItem.TryItem.ITEM_SIZE;

            return new FixedSizeList<FixDexBackedTryBlock>() {
                @Nonnull
                @Override
                public FixDexBackedTryBlock readItem(int index) {
                    return new FixDexBackedTryBlock(dexFile, dexBuffer,triesStartOffset + index*CodeItem.TryItem.ITEM_SIZE,
                            handlersStartOffset);
                }

                @Override
                public int size() {
                    return triesSize;
                }
            };
        }
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public Iterable<? extends DebugItem> getDebugItems() {
        return getDebugInfo();

    }

    @Nonnull
    private DebugInfo getDebugInfo() {
        int debugOffset = getDebugOffset();

        if (debugOffset == -1 || debugOffset == 0) {
            return fixDebugInfo.newOrEmpty(dexFile,dexBuffer, 0, this);
        }
        if (debugOffset < 0) {
            System.err.println(String.format("%s: Invalid debug offset1", method));
            return fixDebugInfo.newOrEmpty(dexFile, dexBuffer,0, this);
        }
        if ((debugOffset + dexFile.getBaseDataOffset()) >= dexFile.getBuffer().getBuf().length) {
            System.err.println(String.format("%s: Invalid debug offset2", method));
            return fixDebugInfo.newOrEmpty(dexFile, dexBuffer,0, this);
        }
        return fixDebugInfo.newOrEmpty(dexFile,dexBuffer, debugOffset, this);
    }

    protected int getDebugOffset() {
        return dexBuffer.readInt(codeOffset + CodeItem.DEBUG_INFO_OFFSET);
    }
}
