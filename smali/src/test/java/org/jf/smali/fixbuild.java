package org.jf.smali;

public class fixbuild {

    public static void main(String[] args) {

        String[] smali_args = new String[2];
        smali_args[0] = "assemble";
        smali_args[1] = "D:\\Project\\Android\\androidGRPC\\grpc_client\\ab536f0";
        org.jf.smali.Main.main(smali_args);
//        smali_args[3] = "ab536f0.dex";
    }

}
