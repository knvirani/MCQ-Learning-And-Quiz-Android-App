package com.fourshape.a4mcqplus.app_ads.admob_ads.admob_native_ad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_ads.admob_ads.PlacementIds;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.AdmobNativeAdCardView;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Map;

public class BottomNativeAd {

    private static final String TAG = "BottomNativeAd";

    private Context context;
    private FrameLayout container;
    private AdmobNativeAd admobNativeAd;
    private final int INTERVAL_MILLISECONDS = 9*1000;
    private boolean shouldStopAds = false;
    private boolean isStarted = false;
    private Handler handler;
    private Runnable runnable;

    public BottomNativeAd (Context context, FrameLayout container, boolean activateMediation) {
        this.container = container;
        this.context = context;
        admobNativeAd = new AdmobNativeAd(context, activateMediation);
        admobNativeAd.setPlacementId(PlacementIds.NATIVE_BANNER_AD);
        admobNativeAd.setAdmobNativeAdListener(new AdmobNativeAdListener() {
            @Override
            public void onSuccess() {
                attachNativeAd();
                loadAds();
                MakeLog.info(TAG, "onSuccess");
            }

            @Override
            public void onFailed() {
                MakeLog.info(TAG, "onFailed");
                loadAdsAfterPreviousFailedAttempt();
            }
        });
        handler = getHandler();
        runnable = getRunnable();

    }


    public BottomNativeAd (Context context, FrameLayout container) {
        this.container = container;
        this.context = context;
        admobNativeAd = new AdmobNativeAd(context);
        admobNativeAd.setPlacementId(PlacementIds.NATIVE_BANNER_AD);
        admobNativeAd.setAdmobNativeAdListener(new AdmobNativeAdListener() {
            @Override
            public void onSuccess() {
                attachNativeAd();
                loadAds();
                MakeLog.info(TAG, "onSuccess");
            }

            @Override
            public void onFailed() {
                MakeLog.info(TAG, "onFailed");
                loadAdsAfterPreviousFailedAttempt();
            }
        });
        handler = getHandler();
        runnable = getRunnable();
    }

    public void startAds () {

        if (!shouldStopAds) {
            if (!isStarted) {
                isStarted = true;
                loadAds();
            }
        }

    }

    private int getPostFailedTime () {
        return INTERVAL_MILLISECONDS/3;
    }

    private int getPostRegularTime () {
        return INTERVAL_MILLISECONDS;
    }

    public void attachHandler (int time) {
        if (handler != null) {
            if (runnable != null) {
                handler.postDelayed(runnable, time);
            } else {
                MakeLog.info(TAG, "Runnable is NULL.");
            }
        } else {
            MakeLog.info(TAG, "Handler is NULL.");
        }
    }

    public void detachHandler () {
        if (handler != null) {
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            } else {
                MakeLog.info(TAG, "Runnable is NULL.");
            }
        } else {
            MakeLog.info(TAG, "Handler is NULL.");
        }
    }

    private Runnable getRunnable () {
        return new Runnable() {
            @Override
            public void run() {
                if (!admobNativeAd.isAdLoading() && !admobNativeAd.isAdLoaded())
                    admobNativeAd.loadNativeAd();
            }
        };
    }

    private Handler getHandler () {
        return new Handler();
    }

    private void loadAdsAfterPreviousFailedAttempt () {
        if (!shouldStopAds) {
            attachHandler(getPostFailedTime());
        }
    }

    private void loadAds () {
        attachHandler(getPostRegularTime());
    }

    public void stopAds () {
        shouldStopAds = true;
    }

    public void resetAdsController () {
        shouldStopAds = false;
        isStarted = false;
        detachHandler();
        admobNativeAd.destroyNativeAd();
    }

    private void attachNativeAd () {
        container.removeAllViews();
        View adViewHolder = LayoutInflater.from(context).inflate(R.layout.dynamic_admob_nativead_banner_cardview, null);
        BottomNativeAdViewHolder.attachNativeBannerAd(admobNativeAd.getNativeAd(), adViewHolder);
        container.addView(adViewHolder);
    }

    public void attachNativeAdPlaceholder () {

        container.removeAllViews();
        View adViewHolder = LayoutInflater.from(context).inflate(R.layout.banner_native_ad_placeholder, null);
        container.addView(adViewHolder);

    }

}
