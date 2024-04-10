package org.jf.baksmali.fix;

import java.util.List;
import java.util.Map;

public class FixDumpClassCodeItem {

    public Map<String,FixDumpMethodCodeItem> methodCodeItemList;

    public static class FixDumpMethodCodeItem {

        byte[] code_item;
    }
}
