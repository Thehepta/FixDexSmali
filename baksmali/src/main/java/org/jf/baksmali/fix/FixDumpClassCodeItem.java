package org.jf.baksmali.fix;

import java.util.List;

public class FixDumpClassCodeItem {

    String clsTypeName;
    List<FixDumpMethodCodeItem> methodCodeItemList;


    public static class FixDumpMethodCodeItem {

        String methodName;
        String methodSign;

        byte[] code_item;
    }
}
