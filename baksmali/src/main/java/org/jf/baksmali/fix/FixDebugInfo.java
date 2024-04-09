package org.jf.baksmali.fix;

import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.DebugItemType;
import org.jf.dexlib2.dexbacked.*;
import org.jf.dexlib2.dexbacked.util.DebugInfo;
import org.jf.dexlib2.dexbacked.util.ParameterIterator;
import org.jf.dexlib2.dexbacked.util.VariableSizeIterator;
import org.jf.dexlib2.dexbacked.util.VariableSizeLookaheadIterator;
import org.jf.dexlib2.iface.MethodParameter;
import org.jf.dexlib2.iface.debug.DebugItem;
import org.jf.dexlib2.iface.debug.EndLocal;
import org.jf.dexlib2.iface.debug.LocalInfo;
import org.jf.dexlib2.immutable.debug.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;

public class FixDebugInfo extends DebugInfo {


    @Nonnull public  DexBackedDexFile dexFile;
    private  int debugInfoOffset;
    @Nonnull private  FixMethodImplementation methodImpl;
    DexBuffer dexBuffer;


    private static final LocalInfo EMPTY_LOCAL_INFO = new LocalInfo() {
        @Nullable @Override public String getName() { return null; }
        @Nullable @Override public String getType() { return null; }
        @Nullable @Override public String getSignature() { return null; }
    };



    public FixDebugInfo(DexBackedDexFile dexFile, DexBuffer dexBuffer, int debugInfoOffset, FixMethodImplementation methodImpl) {
        this.dexFile = dexFile;
        this.dexBuffer = dexBuffer;
        this.debugInfoOffset = debugInfoOffset;
        this.methodImpl = methodImpl;
    }


    @Nonnull
    @Override
    public Iterator<String> getParameterNames(@Nullable DexReader reader) {
        if (reader == null) {
            reader = dexBuffer.readerAt(debugInfoOffset);
            reader.skipUleb128();
        }
        //TODO: make sure dalvik doesn't allow more parameter names than we have parameters
        final int parameterNameCount = reader.readSmallUleb128();
        return new VariableSizeIterator<String>(reader, parameterNameCount) {
            @Override protected String readNextItem(@Nonnull DexReader reader, int index) {
                return dexFile.getStringSection().getOptional(reader.readSmallUleb128() - 1);
            }
        };
    }

    @Override
    public int getSize() {
        Iterator<DebugItem> iter = iterator();
        while(iter.hasNext()) {
            iter.next();
        }
        return ((VariableSizeLookaheadIterator) iter).getReaderOffset() - debugInfoOffset;
    }

    @Override
    public Iterator<DebugItem> iterator() {
        DexReader reader = dexFile.getDataBuffer().readerAt(debugInfoOffset);
        final int lineNumberStart = reader.readBigUleb128();
        int registerCount = methodImpl.getRegisterCount();

        //TODO: does dalvik allow references to invalid registers?
        final LocalInfo[] locals = new LocalInfo[registerCount];
        Arrays.fill(locals, EMPTY_LOCAL_INFO);

        DexBackedMethod method = (DexBackedMethod) methodImpl.method;

        // Create a MethodParameter iterator that uses our DexReader instance to read the parameter names.
        // After we have finished iterating over the parameters, reader will "point to" the beginning of the
        // debug instructions
        final Iterator<? extends MethodParameter> parameterIterator =
                new ParameterIterator(method.getParameterTypes(),
                        method.getParameterAnnotations(),
                        getParameterNames(reader));

        // first, we grab all the parameters and temporarily store them at the beginning of locals,
        // disregarding any wide types
        int parameterIndex = 0;
        if (!AccessFlags.STATIC.isSet(methodImpl.method.getAccessFlags())) {
            // add the local info for the "this" parameter
            locals[parameterIndex++] = new LocalInfo() {
                @Override public String getName() { return "this"; }
                @Override public String getType() { return methodImpl.method.getDefiningClass(); }
                @Override public String getSignature() { return null; }
            };
        }
        while (parameterIterator.hasNext()) {
            locals[parameterIndex++] = parameterIterator.next();
        }

        if (parameterIndex < registerCount) {
            // now, we push the parameter locals back to their appropriate register, starting from the end
            int localIndex = registerCount-1;
            while(--parameterIndex > -1) {
                LocalInfo currentLocal = locals[parameterIndex];
                String type = currentLocal.getType();
                if (type != null && (type.equals("J") || type.equals("D"))) {
                    localIndex--;
                    if (localIndex == parameterIndex) {
                        // there's no more room to push, the remaining registers are already in the correct place
                        break;
                    }
                }
                locals[localIndex] = currentLocal;
                locals[parameterIndex] = EMPTY_LOCAL_INFO;
                localIndex--;
            }
        }

        return new VariableSizeLookaheadIterator<DebugItem>(dexFile.getDataBuffer(), reader.getOffset()) {
            private int codeAddress = 0;
            private int lineNumber = lineNumberStart;

            @Nullable
            protected DebugItem readNextItem(@Nonnull DexReader reader) {
                while (true) {
                    int next = reader.readUbyte();
                    switch (next) {
                        case DebugItemType.END_SEQUENCE: {
                            return endOfData();
                        }
                        case DebugItemType.ADVANCE_PC: {
                            int addressDiff = reader.readSmallUleb128();
                            codeAddress += addressDiff;
                            continue;
                        }
                        case DebugItemType.ADVANCE_LINE: {
                            int lineDiff = reader.readSleb128();
                            lineNumber += lineDiff;
                            continue;
                        }
                        case DebugItemType.START_LOCAL: {
                            int register = reader.readSmallUleb128();
                            String name = dexFile.getStringSection().getOptional(reader.readSmallUleb128() - 1);
                            String type = dexFile.getTypeSection().getOptional(reader.readSmallUleb128() - 1);
                            ImmutableStartLocal startLocal =
                                    new ImmutableStartLocal(codeAddress, register, name, type, null);
                            if (register >= 0 && register < locals.length) {
                                locals[register] = startLocal;
                            }
                            return startLocal;
                        }
                        case DebugItemType.START_LOCAL_EXTENDED: {
                            int register = reader.readSmallUleb128();
                            String name = dexFile.getStringSection().getOptional(reader.readSmallUleb128() - 1);
                            String type = dexFile.getTypeSection().getOptional(reader.readSmallUleb128() - 1);
                            String signature = dexFile.getStringSection().getOptional(
                                    reader.readSmallUleb128() - 1);
                            ImmutableStartLocal startLocal =
                                    new ImmutableStartLocal(codeAddress, register, name, type, signature);
                            if (register >= 0 && register < locals.length) {
                                locals[register] = startLocal;
                            }
                            return startLocal;
                        }
                        case DebugItemType.END_LOCAL: {
                            int register = reader.readSmallUleb128();

                            boolean replaceLocalInTable = true;
                            LocalInfo localInfo;
                            if (register >= 0 && register < locals.length) {
                                localInfo = locals[register];
                            } else {
                                localInfo = EMPTY_LOCAL_INFO;
                                replaceLocalInTable = false;
                            }

                            if (localInfo instanceof EndLocal) {
                                localInfo = EMPTY_LOCAL_INFO;
                                // don't replace the local info in locals. The new EndLocal won't have any info at all,
                                // and we dont want to wipe out what's there, so that it is available for a subsequent
                                // RestartLocal
                                replaceLocalInTable = false;
                            }
                            ImmutableEndLocal endLocal =
                                    new ImmutableEndLocal(codeAddress, register, localInfo.getName(),
                                            localInfo.getType(), localInfo.getSignature());
                            if (replaceLocalInTable) {
                                locals[register] = endLocal;
                            }
                            return endLocal;
                        }
                        case DebugItemType.RESTART_LOCAL: {
                            int register = reader.readSmallUleb128();
                            LocalInfo localInfo;
                            if (register >= 0 && register < locals.length) {
                                localInfo = locals[register];
                            } else {
                                localInfo = EMPTY_LOCAL_INFO;
                            }
                            ImmutableRestartLocal restartLocal =
                                    new ImmutableRestartLocal(codeAddress, register, localInfo.getName(),
                                            localInfo.getType(), localInfo.getSignature());
                            if (register >= 0 && register < locals.length) {
                                locals[register] = restartLocal;
                            }
                            return restartLocal;
                        }
                        case DebugItemType.PROLOGUE_END: {
                            return new ImmutablePrologueEnd(codeAddress);
                        }
                        case DebugItemType.EPILOGUE_BEGIN: {
                            return new ImmutableEpilogueBegin(codeAddress);
                        }
                        case DebugItemType.SET_SOURCE_FILE: {
                            String sourceFile = dexFile.getStringSection().getOptional(
                                    reader.readSmallUleb128() - 1);
                            return new ImmutableSetSourceFile(codeAddress, sourceFile);
                        }
                        default: {
                            int adjusted = next - 0x0A;
                            codeAddress += adjusted / 15;
                            lineNumber += (adjusted % 15) - 4;
                            return new ImmutableLineNumber(codeAddress, lineNumber);
                        }
                    }
                }
            }
        };
    }



    public static DebugInfo newOrEmpty(@Nonnull DexBackedDexFile dexFile,DexBuffer dexBuffer, int debugInfoOffset,
                                       @Nonnull FixMethodImplementation methodImpl) {

        return new FixDebugInfo(dexFile, dexBuffer,debugInfoOffset, methodImpl);
    }

}
