package com.fourshape.a4mcqplus.firebase_analytics;

import android.content.Context;
import android.os.Bundle;

import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.firebase.analytics.FirebaseAnalytics;

public class TrackScreen {

    private static final String TAG = "TrackScreen";

    public static void now (Context context, String screenName) {

        try {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity");
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
        } catch (Exception e) {
            MakeLog.error(TAG, "Failed to Track Screen");
            MakeLog.exception(e);
        }

    }

}
