package com.gameofwhales.sdk;

import android.util.Log;

public class L {

    public static boolean isDebug = true;

    public static void e(String tag, String message)
    {
        Log.e(tag, message);
    }

    public static void i(String tag, String message)
    {
        if (isDebug)
            Log.i(tag, message);
    }

    public static void w(String tag, String message)
    {
        if (isDebug)
            Log.w(tag, message);
    }

    public static void d(String tag, String message)
    {
        if (isDebug)
            Log.d(tag, message);
    }

}
