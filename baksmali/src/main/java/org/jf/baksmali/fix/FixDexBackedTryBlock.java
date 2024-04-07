package org.jf.baksmali.fix;

import org.jf.dexlib2.base.BaseTryBlock;
import org.jf.dexlib2.dexbacked.*;
import org.jf.dexlib2.dexbacked.raw.CodeItem;
import org.jf.dexlib2.dexbacked.util.VariableSizeList;

import javax.annotation.Nonnull;
import java.util.List;

public class FixDexBackedTryBlock extends BaseTryBlock<DexBackedExceptionHandler> {

    DexBackedDexFile dexFile;
    DexBuffer dexBuffer;
    private final int tryItemOffset;
    private final int handlersStartOffset;

    public FixDexBackedTryBlock(DexBackedDexFile dexFile,DexBuffer dexBuffer, int tryItemOffset, int handlersStartOffset) {
        this.dexFile = dexFile;
        this.dexBuffer = dexBuffer;
        this.tryItemOffset = tryItemOffset;
        this.handlersStartOffset = handlersStartOffset;
    }

    @Override public int getCodeUnitCount() {
        return dexBuffer.readUshort(tryItemOffset + CodeItem.TryItem.CODE_UNIT_COUNT_OFFSET);
    }
    @Override public int getStartCodeAddress() {
        return dexBuffer.readSmallUint(tryItemOffset + CodeItem.TryItem.START_ADDRESS_OFFSET);

    }

    @Nonnull
    @Override
    public List<? extends DexBackedExceptionHandler> getExceptionHandlers() {
        DexReader reader = dexBuffer.readerAt(
                handlersStartOffset + dexBuffer.readUshort(tryItemOffset + CodeItem.TryItem.HANDLER_OFFSET));
        final int encodedSize = reader.readSleb128();

        if (encodedSize > 0) {
            //no catch-all
            return new VariableSizeList<DexBackedTypedExceptionHandler>(
                    dexBuffer, reader.getOffset(), encodedSize) {
                @Nonnull
                @Override
                protected DexBackedTypedExceptionHandler readNextItem(@Nonnull DexReader reader, int index) {
                    return new DexBackedTypedExceptionHandler(dexFile, reader);
                }
            };
        } else {
            //with catch-all
            final int sizeWithCatchAll = (-1 * encodedSize) + 1;
            return new VariableSizeList<DexBackedExceptionHandler>(
                    dexBuffer, reader.getOffset(), sizeWithCatchAll) {
                @Nonnull
                @Override
                protected DexBackedExceptionHandler readNextItem(@Nonnull DexReader dexReader, int index) {
                    if (index == sizeWithCatchAll-1) {
                        return new DexBackedCatchAllExceptionHandler(dexReader);
                    } else {
                        return new DexBackedTypedExceptionHandler(dexFile, dexReader);
                    }
                }
            };
        }
    }
}
