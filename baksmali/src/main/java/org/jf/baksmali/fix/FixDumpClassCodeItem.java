package org.jf.baksmali.fix;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FixDumpClassCodeItem implements Serializable {

    public Map<String,FixDumpMethodCodeItem> methodCodeItemList;

    public FixDumpClassCodeItem(Map<String,FixDumpMethodCodeItem> methodCodeItemList ){
        this.methodCodeItemList = methodCodeItemList;
    }


}
