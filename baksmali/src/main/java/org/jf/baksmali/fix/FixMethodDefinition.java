package org.jf.baksmali.fix;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.jf.baksmali.Adaptors.ClassDefinition;
import org.jf.baksmali.Adaptors.MethodDefinition;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction31t;
import org.jf.dexlib2.immutable.instruction.ImmutableInstruction31t;
import org.jf.dexlib2.util.InstructionOffsetMap;
import org.jf.util.ExceptionWithContext;
import org.jf.util.SparseIntArray;

import javax.annotation.Nonnull;

public class FixMethodDefinition extends MethodDefinition {
    public FixMethodDefinition(@Nonnull ClassDefinition classDef, @Nonnull Method method, @Nonnull MethodImplementation methodImpl) {
        super(classDef,method,methodImpl);
    }
}
