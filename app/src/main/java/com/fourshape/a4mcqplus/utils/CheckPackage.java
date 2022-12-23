package com.fourshape.a4mcqplus.utils;

import android.content.Context;

public class CheckPackage {

    public static boolean isFound (Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (Exception e) {
            MakeLog.exception(e);
            return false;
        }
    }

}
