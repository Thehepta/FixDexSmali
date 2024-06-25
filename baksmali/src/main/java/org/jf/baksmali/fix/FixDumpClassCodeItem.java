package org.jf.baksmali.fix;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FixDumpClassCodeItem implements Serializable {

    public Map<String,FixDumpMethodCodeItem> methodCodeItemList;
    public FixMethodCall fixMethodCall;

    public FixDumpClassCodeItem(Map<String,FixDumpMethodCodeItem> methodCodeItemList,FixMethodCall fixMethodCall ){
        this.methodCodeItemList = methodCodeItemList;
        this.fixMethodCall = fixMethodCall;
    }

    public  FixDumpMethodCodeItem getMethodCodeItemList(String methodString) {
        if (fixMethodCall !=null){
            return fixMethodCall.MethodFixCall(methodString);
        }
        return methodCodeItemList.get(methodString);
    }
}
