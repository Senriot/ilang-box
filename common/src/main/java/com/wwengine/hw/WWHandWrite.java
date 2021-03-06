package com.wwengine.hw;

import android.content.Context;

public class WWHandWrite
{

    static
    {
        System.loadLibrary("dwEngineHw");
    }

    public static native int apkBinding(Context param);

    public static native int hwInit(byte[] data, int param);

    public static native int hwRecognize(short[] tracks, char[] result, int candNum, int option);

    public static native int hwRecognizeMulti(short[] tracks, char[] result);
}

