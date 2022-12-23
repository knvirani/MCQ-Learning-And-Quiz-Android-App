package com.fourshape.a4mcqplus.app_ads.admob_ads.interstitial_ad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.fourshape.a4mcqplus.app_ads.AdStatus;
import com.fourshape.a4mcqplus.app_ads.admob_ads.AdController;
import com.fourshape.a4mcqplus.app_ads.admob_ads.MediationAdapterStatus;
import com.fourshape.a4mcqplus.app_ads.admob_ads.PlacementIds;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public class ExitTestInterstitialAd {

    private static final String TAG = "ExitTestInterstitialAd";
    private static InterstitialAd interstitialAd;
    private static int AD_STATUS;
    private static final String placementId = PlacementIds.INTERSTITIAL_AD;

    private static boolean isAdapterInitialized = false;

    private static boolean activateMediation = false;

    private static final int MEDIATION_AD_FROM_ADMOB = 1;
    private static final int MEDIATION_AD_FROM_FACEBOOK = 2;

    private static final String[] MEDIATION_ADMOB_ADAPTERS = {"com.google.ads.mediation.admob.AdMobAdapter", "com.google.android.gms.ads.MobileAds"};
    private static final String[] MEDIATION_FACEBOOK_ADAPTERS = {"com.google.ads.mediation.facebook.FacebookAdapter", "com.google.ads.mediation.facebook.FacebookMediationAdapter"};

    private static int whoisMediator(String adapterClassName){

        if (adapterClassName == null)
            return 0;

        // 1. admob
        for ( int i=0; i< MEDIATION_ADMOB_ADAPTERS.length; i++){
            if ( MEDIATION_ADMOB_ADAPTERS[i].equals(adapterClassName) )
                return MEDIATION_AD_FROM_ADMOB;
        }

        // 2. facebook
        for ( int i=0; i< MEDIATION_FACEBOOK_ADAPTERS.length; i++){
            if ( MEDIATION_FACEBOOK_ADAPTERS[i].equals(adapterClassName) )
                return MEDIATION_AD_FROM_FACEBOOK;
        }

        return 0;
    }


    public static void initializeAdapter (Context context) {
        if (!isAdapterInitialized) {

            if (context != null) {
                MobileAds.initialize(context.getApplicationContext());
                MobileAds.setAppMuted(true);
                isAdapterInitialized = true;
            }

        }
    }

    public static boolean shouldFetchNewAd () {
        if (AdController.isExitTestInterstitialAdAllowed)
            return AD_STATUS != AdStatus.AD_LOADED && AD_STATUS != AdStatus.AD_LOADING;
        else {
            MakeLog.error(TAG, "Ads are disabled by controller");
            return false;
        }
    }

    public static boolean shouldShowAd () {
        return interstitialAd != null && AD_STATUS == AdStatus.AD_LOADED;
    }

    public static void showAd (Activity activity) {

        if (activity != null) {

            if (interstitialAd != null) {

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                        AD_STATUS = AdStatus.AD_DISMISSED_FULL_SCREEN_CONTENT;
                        MakeLog.info(TAG, "onAdDismissedFullScreenContent");
                        activity.onBackPressed();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull @NotNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);

                        AD_STATUS = AdStatus.AD_FAILED_TO_SHOW_FULL_SCREEN_CONTENT;
                        MakeLog.error(TAG, "onAdFailedToShowFullScreenContent. ERROR: " + adError.toString());

                        activity.onBackPressed();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();

                        AD_STATUS = AdStatus.AD_IMPRESSION;
                        MakeLog.info(TAG, "onAdImpression");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();

                        AD_STATUS = AdStatus.AD_SHOWED_FULL_SCREEN_CONTENT;
                        MakeLog.info(TAG, "onAdShowedFullScreenContent");
                    }
                });

                interstitialAd.show(activity);

            } else {
                MakeLog.error(TAG, "InterstitialAd found NULL");
            }

        } else {
            MakeLog.error(TAG,  "Activity found NULL");
        }

    }

    private static void loadInterstitialAd (Context context) {
        if (context == null) {
            MakeLog.error(TAG, "NULL Context onAdRequest");
            return;
        }

        if (!shouldFetchNewAd()) {
            MakeLog.info(TAG, "Either onLoaded or onLoading");
            return;
        }

        if (!activateMediation)
            initializeAdapter(context);

        interstitialAd = null;

        MakeLog.info(TAG, "onAdLoading");

        AdRequest adRequest = new AdRequest.Builder().build();
        AD_STATUS = AdStatus.AD_LOADING;

        InterstitialAd.load(context, placementId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull @NotNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);

                ExitTestInterstitialAd.interstitialAd = interstitialAd;
                AD_STATUS = AdStatus.AD_LOADED;

                if (activateMediation) {
                    printMediationAdapterClassName(interstitialAd.getResponseInfo());
                }

                MakeLog.info(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                AD_STATUS = AdStatus.AD_LOAD_FAILED;
                interstitialAd = null;

                MakeLog.error(TAG, "onAdFailedToLoad");

            }
        });
    }

    public static void requestNewAd (Context context){
        loadInterstitialAd(context);
    }

    public static void requestNewAd (Context context, boolean activateMediation) {

        ExitTestInterstitialAd.activateMediation = activateMediation;

        if (activateMediation) {

            if (MediationAdapterStatus.isInitialized) {

                loadInterstitialAd(context);

            } else {

                MobileAds.initialize(context, new OnInitializationCompleteListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onInitializationComplete(@NonNull @NotNull InitializationStatus initializationStatus) {

                        MediationAdapterStatus.isInitialized = true;
                        Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();

                        for (String adapterClass : statusMap.keySet()) {

                            AdapterStatus status = statusMap.get(adapterClass);
                            if (status != null) {
                                MakeLog.info(TAG, String.format(Locale.getDefault(),
                                        "Adapter name: %s, Description: %s, Latency: %d",
                                        adapterClass, status.getDescription(), status.getLatency()));
                            }

                        }

                    }
                });

                MobileAds.setAppMuted(true);
                loadInterstitialAd(context);

            }

        } else {
            loadInterstitialAd(context);
        }

    }

    private static void printMediationAdapterClassName (ResponseInfo responseInfo) {
        if (responseInfo != null) {

            if (responseInfo.getMediationAdapterClassName() != null) {

                if (whoisMediator(responseInfo.getMediationAdapterClassName()) == ExitTestInterstitialAd.MEDIATION_AD_FROM_FACEBOOK) {
                    MakeLog.info(TAG,  "Mediation Interstitial Ad From Facebook");
                } else if (whoisMediator(responseInfo.getMediationAdapterClassName()) == ExitTestInterstitialAd.MEDIATION_AD_FROM_ADMOB) {
                    MakeLog.info(TAG,  "Mediation Interstitial Ad From Admob");
                } else {
                    MakeLog.info(TAG,  "Mediation Interstitial Ad From Unknown.");
                }

            } else {
                MakeLog.error(TAG, "NULL MediationAdapter Class Name");
            }

        } else {
            MakeLog.error(TAG, "NULL MediationAdapter ResponseInfo");
        }
    }


}
