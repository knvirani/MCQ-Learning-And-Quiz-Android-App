package com.fourshape.a4mcqplus.utils;

import android.content.Context;

public class ScreenParams {

    public static float getDisplayDensity (Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getDisplayWidthPixels (Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDisplayHeightPixels (Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

}
