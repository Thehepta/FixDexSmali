package org.jf.baksmali.fix;

import org.jf.baksmali.Adaptors.ClassDefinition;
import org.jf.baksmali.Adaptors.MethodDefinition;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;

import javax.annotation.Nonnull;

public class FixMethodDefinition extends MethodDefinition {
    public FixMethodDefinition(@Nonnull ClassDefinition classDef, @Nonnull Method method, @Nonnull MethodImplementation methodImpl) {
        super(classDef, method, methodImpl);
    }
//    public FixMethodDefinition Build(){
//
//    }
}
