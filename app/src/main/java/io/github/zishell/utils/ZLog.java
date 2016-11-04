package io.github.zishell.utils;

import android.util.Log;

/**
 * Created by zishell on 3/1/16.
 */
public class ZLog {
    public static void d(String TAG, String logString, boolean debug) {
        if (debug)
            Log.d(TAG, "==>" + logString);
    }

    public static void d(String TAG, String logString) {
        Log.d(TAG, "==>" + logString);
    }

    public static void e(String TAG, String logString, boolean debug) {
        if (debug)
            Log.e(TAG, "==>" + logString);
    }

    public static void e(String TAG, String logString) {
        Log.e(TAG, "==>" + logString);
    }
}
