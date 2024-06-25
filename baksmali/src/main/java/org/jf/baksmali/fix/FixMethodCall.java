package org.jf.baksmali.fix;

import java.util.Map;

public class FixMethodCall {

    public Map<String,FixDumpMethodCodeItem> methodCodeItemList;
    public FixMethodCall(Map<String,FixDumpMethodCodeItem> methodCodeItemList){
        this.methodCodeItemList = methodCodeItemList;
    }
    public FixDumpMethodCodeItem MethodFixCall(String classDescriptor){
        return methodCodeItemList.get(classDescriptor);
    }
}
