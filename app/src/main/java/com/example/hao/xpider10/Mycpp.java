package com.example.hao.xpider10;

public class Mycpp {
    static {
        System.loadLibrary("native-lib");
    }
    public static native int getlines(long addsrc);
    public static native double[] getcircle(long addsrc,int lowh,int lows,int lowv,int highh,boolean testflag);
    public static native int[] getsquare(long addsrc);
}
