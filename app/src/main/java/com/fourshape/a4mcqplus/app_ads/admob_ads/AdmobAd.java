package com.fourshape.a4mcqplus.app_ads.admob_ads;

import android.content.Context;
import android.os.Handler;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.fourshape.a4mcqplus.app_ads.AdStatus;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class AdmobAd {

    private static final String TAG = "AdmobAd";

    private Context context;
    AdView adView;
    private int adStatus;

    public AdmobAd (Context context, String placementId) {
        this.context = new WeakReference<>(context).get();

        MobileAds.initialize(this.context);

        adView = new AdView(context);

        float screenDensity = ScreenParams.getDisplayDensity(context);
        int screenWidth = ScreenParams.getDisplayWidthPixels(context);
        int adHeight = 90; // in dp.

        adView.setAdSize(new AdSize((int) (screenWidth/screenDensity), adHeight));
        adView.setAdUnitId(placementId);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adStatus = AdStatus.AD_LOAD_FAILED;
                MakeLog.error(TAG, "onAdFailedToLoad: " + loadAdError.toString());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adStatus = AdStatus.AD_LOADED;
                MakeLog.info(TAG, "onAdLoaded");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                adStatus = AdStatus.AD_IMPRESSION;
                MakeLog.info(TAG, "onAdImpression");
            }
        });
    }

    public int getAdStatus () {
        return adStatus;
    }

    public void load () {

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adStatus = AdStatus.AD_LOADING;
        MakeLog.info(TAG, "onAdLoading");

    }

    public boolean hasValidAd () {
        return adStatus == AdStatus.AD_LOADED;
    }

    public boolean shouldLoadAd () {
        return adStatus != AdStatus.AD_LOADED && adStatus != AdStatus.AD_LOADING;
    }

    public AdView getAdView () {
        return adView;
    }

}
