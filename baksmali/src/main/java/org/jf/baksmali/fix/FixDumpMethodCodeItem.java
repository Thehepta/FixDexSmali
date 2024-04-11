package org.jf.baksmali.fix;


import java.io.Serializable;

public  class FixDumpMethodCodeItem implements Serializable {

    byte[] code_item;
    public FixDumpMethodCodeItem(byte[] code_item){
        this.code_item = code_item;
    }

}
