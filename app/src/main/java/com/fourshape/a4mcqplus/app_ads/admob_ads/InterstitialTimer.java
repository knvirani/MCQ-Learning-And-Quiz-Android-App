package com.fourshape.a4mcqplus.app_ads.admob_ads;


import android.os.Handler;

import com.fourshape.a4mcqplus.utils.MakeLog;

public class InterstitialTimer {

    private static final String TAG = "InterstitialTimer";

    public static int TIME_INTERVAL = AdController.interstitialAdInterval;
    private static boolean shouldShowAd = false;

    public static Handler handler = new Handler();
    public static Runnable runnable = () -> {
        shouldShowAd = true;
        MakeLog.info(TAG, "Ads can show.");
    };

    public static void start () {
        handler.postDelayed(runnable, TIME_INTERVAL);
    }

    public static boolean isAdDisplayAllowed () {
        return shouldShowAd;
    }

    public static void reset () {
        shouldShowAd = false;
        handler.removeCallbacks(runnable);
        MakeLog.info(TAG, "Ads cannot show.");
    }

}
