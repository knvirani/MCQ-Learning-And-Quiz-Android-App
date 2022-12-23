package com.fourshape.a4mcqplus.app_ads.admob_ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_ads.AdStatus;
import com.fourshape.a4mcqplus.fragments.LiveTestFragment;
import com.fourshape.a4mcqplus.fragments.SolutionImageFragment;
import com.fourshape.a4mcqplus.fragments.TestReadFragment;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AdmobInterstitialAd {

    private static final String TAG = "AdmobInterstitialAd";
    private static InterstitialAd interstitialAd;
    private static int AD_STATUS;
    private static final String placementId = PlacementIds.INTERSTITIAL_AD;

    private static boolean isAdapterInitialized = false;
    private static boolean activateMediation = false;

    public static void initializeAdapter (Context context) {
        if (!isAdapterInitialized) {
            MobileAds.initialize(context.getApplicationContext());
            isAdapterInitialized = true;
        }
    }



    public static boolean shouldFetchNewAd () {
        if (AdController.isAdAllowed)
            return AD_STATUS != AdStatus.AD_LOADED && AD_STATUS != AdStatus.AD_LOADING;
        else {
            MakeLog.error(TAG, "Ads are disabled by controller");
            return false;
        }
    }

    public static boolean shouldShowAd () {
        if (AdController.isAdAllowed)
            return interstitialAd != null && AD_STATUS == AdStatus.AD_LOADED && InterstitialTimer.isAdDisplayAllowed();
        else
            return false;
    }

    public static void showAdBeforeNewFragmentLaunch (FragmentActivity activity, int fragmentContainerId, String mainImageUrl, String backUpImageUrl) {

        if (activity != null) {

            if (interstitialAd != null) {

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull @NotNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        AD_STATUS = AdStatus.AD_FAILED_TO_SHOW_FULL_SCREEN_CONTENT;
                        MakeLog.error(TAG, "onAdFailedToShowFullScreenContent. ERROR: " + adError.toString());

                        activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("explained_solution_fragment").replace(fragmentContainerId, SolutionImageFragment.newInstance(mainImageUrl, backUpImageUrl)).commit();

                        requestNewAd(activity.getApplicationContext());

                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        AD_STATUS = AdStatus.AD_SHOWED_FULL_SCREEN_CONTENT;
                        MakeLog.info(TAG, "onAdShowedFullScreenContent");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        AD_STATUS = AdStatus.AD_DISMISSED_FULL_SCREEN_CONTENT;
                        MakeLog.info(TAG, "onAdDismissedFullScreenContent");

                        activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("explained_solution_fragment").replace(fragmentContainerId, SolutionImageFragment.newInstance(mainImageUrl, backUpImageUrl)).commit();

                        requestNewAd(activity.getApplicationContext());
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        AD_STATUS = AdStatus.AD_IMPRESSION;
                        MakeLog.info(TAG, "onAdImpression");
                    }

                });

                interstitialAd.show(activity);

            } else {
                MakeLog.error(TAG, "Ad Instance is NULL.");
            }

        } else {
            MakeLog.error(TAG,"Activity is NULL.");
        }

    }

    public static void showAdBeforeNewFragmentLaunch (FragmentActivity activity, int fragmentContainerId, boolean shouldGoForLiveTest, String testTitle, String testCode, String testAction, int testLimit, int testOffset) {

        if (activity != null) {

            if (interstitialAd != null) {

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull @NotNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        AD_STATUS = AdStatus.AD_FAILED_TO_SHOW_FULL_SCREEN_CONTENT;
                        MakeLog.error(TAG, "onAdFailedToShowFullScreenContent. ERROR: " + adError.toString());

                        if (shouldGoForLiveTest) {
                            activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("live_test_fragment").replace(fragmentContainerId, LiveTestFragment.newInstance(testTitle, testCode, testAction, testLimit, testOffset)).commit();
                        } else {
                            activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("test_read_fragment").replace(fragmentContainerId, TestReadFragment.newInstance(testTitle, testCode, testAction, testLimit, testOffset)).commit();
                        }

                        requestNewAd(activity.getApplicationContext());

                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        AD_STATUS = AdStatus.AD_SHOWED_FULL_SCREEN_CONTENT;
                        MakeLog.info(TAG, "onAdShowedFullScreenContent");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        AD_STATUS = AdStatus.AD_DISMISSED_FULL_SCREEN_CONTENT;
                        MakeLog.info(TAG, "onAdDismissedFullScreenContent");

                        if (shouldGoForLiveTest) {
                            activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("live_test_fragment").replace(fragmentContainerId, LiveTestFragment.newInstance(testTitle, testCode, testAction, testLimit, testOffset)).commit();
                        } else {
                            activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("test_read_fragment").replace(fragmentContainerId, TestReadFragment.newInstance(testTitle, testCode, testAction, testLimit, testOffset)).commit();
                        }

                        requestNewAd(activity.getApplicationContext());
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        AD_STATUS = AdStatus.AD_IMPRESSION;
                        MakeLog.info(TAG, "onAdImpression");
                    }
                });

                interstitialAd.show(activity);

            } else {
                MakeLog.error(TAG, "Ad Instance is NULL");
            }

        } else {
            MakeLog.error(TAG,"Activity is NULL");
        }

    }

    public static void showAdAfterBeforeCurrentFragmentFinish (Activity activity) {

        if (activity != null) {

            if (interstitialAd != null) {

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull @NotNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        AD_STATUS = AdStatus.AD_FAILED_TO_SHOW_FULL_SCREEN_CONTENT;
                        MakeLog.error(TAG, "onAdFailedToShowFullScreenContent. ERROR: " + adError.toString());

                        activity.onBackPressed();
                        requestNewAd(activity.getApplicationContext());

                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        AD_STATUS = AdStatus.AD_SHOWED_FULL_SCREEN_CONTENT;
                        MakeLog.info(TAG, "onAdShowedFullScreenContent");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        AD_STATUS = AdStatus.AD_DISMISSED_FULL_SCREEN_CONTENT;
                        MakeLog.info(TAG, "onAdDismissedFullScreenContent");
                        activity.onBackPressed();
                        requestNewAd(activity.getApplicationContext());
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        AD_STATUS = AdStatus.AD_IMPRESSION;
                        MakeLog.info(TAG, "onAdImpression");
                    }
                });

                interstitialAd.show(activity);

            } else {
                MakeLog.error(TAG, "Ad Instance is NULL");
            }

        } else {
            MakeLog.error(TAG, "Activity is NULL");
        }

    }

    public static void requestNewAd (Context context) {

        if (context == null) {
            MakeLog.error(TAG, "NULL Context onAdRequest");
            return;
        }

        if (!shouldFetchNewAd()) {
            MakeLog.info(TAG, "Either onLoaded or onLoading");
            return;
        }

        InterstitialTimer.reset();
        InterstitialTimer.start();

        initializeAdapter(context);

        interstitialAd = null;

        MakeLog.info(TAG, "onAdLoading");

        AdRequest adRequest = new AdRequest.Builder().build();
        AD_STATUS = AdStatus.AD_LOADING;

        InterstitialAd.load(context, placementId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull @NotNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                AdmobInterstitialAd.interstitialAd = interstitialAd;
                AD_STATUS = AdStatus.AD_LOADED;
                MakeLog.info(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                AD_STATUS = AdStatus.AD_LOAD_FAILED;
                MakeLog.error(TAG, "onAdFailedToLoad");
                interstitialAd = null;
            }
        });

    }

}
