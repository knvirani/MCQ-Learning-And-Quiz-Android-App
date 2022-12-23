package com.fourshape.a4mcqplus.utils;

import android.util.Log;

public class MakeLog {

    private static final boolean shouldLog = true;
    private static final boolean shouldLogException = true;

    public static void warning (String TAG, String logWarning) {
        if (shouldLog){
            if (logWarning != null) {
                Log.w(TAG, logWarning);
            } else {
                Log.w(TAG, "null value");
            }
        }
    }

    public static void info (String TAG, String logInfo) {
        if (shouldLog) {
            if (logInfo != null) {
                Log.i(TAG, logInfo);
            } else {
                Log.i(TAG, "null value");
            }
        }
    }

    public static void error (String TAG, String logError) {
        if (shouldLog) {
            if (logError != null) {
                Log.e(TAG, logError);
            } else {
                Log.e(TAG, "null value");
            }
        }
    }

    public static void exception (Exception e) {
        if (shouldLogException) {
            if (e != null) {
                e.printStackTrace();
            } else {
                MakeLog.error("Exception", "null value");
            }
        }
    }

}
