package com.fourshape.a4mcqplus.app_ads.admob_ads.admob_native_ad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_ads.AdStatus;
import com.fourshape.a4mcqplus.app_ads.admob_ads.MediationAdapterStatus;
import com.fourshape.a4mcqplus.app_ads.admob_ads.PlacementIds;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Map;

public class AdmobNativeAd {

    private static final String TAG = "AdmobNativeAd";
    private int adStatus = 0;
    private NativeAd nativeAd;
    private Context context;
    private String placementId = null;

    private AdmobNativeAdListener admobNativeAdListener;
    private boolean activateMediation;
    private boolean isMediationAdapterInitialized;

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

    public AdmobNativeAd (Context context, boolean activateMediation) {
        this.context = context;
        this.activateMediation = activateMediation;

        if (!this.activateMediation) {

            if (this.context != null) {
                MobileAds.initialize(this.context);
                MobileAds.setAppMuted(true);
            }

        }

    }

    public AdmobNativeAd (Context context) {

        this.context = context;

        if (this.context != null) {
            MobileAds.initialize(this.context);
            MobileAds.setAppMuted(true);
        }

    }

    public void setPlacementId (String placementId) {
        this.placementId = placementId;
    }

    public void setAdmobNativeAdListener (AdmobNativeAdListener admobNativeAdListener) {
        this.admobNativeAdListener = admobNativeAdListener;
    }

    public boolean isAdLoading () {
        return adStatus == AdStatus.AD_LOADING;
    }

    public boolean isAdLoaded () {
        return adStatus == AdStatus.AD_LOADED;
    }

    public boolean isAdFailedToLoad () {
        return adStatus == AdStatus.AD_LOAD_FAILED;
    }

    public NativeAd getNativeAd () {
        return nativeAd;
    }

    public void destroyNativeAd () {
        if (nativeAd != null) {
            nativeAd.destroy();
            MakeLog.info(TAG, "Destroyed NativeAd");
            nativeAd = null;
        } else {
            MakeLog.error(TAG, "Can't destroy NativeAd as it's already NULL.");
        }
    }

    private void loadNativeAdWithMediationAdapter () {

        if (MediationAdapterStatus.isInitialized) {
            loadNativeAdWithoutMediationAdapter();
        } else {

            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {

                    MediationAdapterStatus.isInitialized = true;

                    if (initializationStatus != null) {
                        Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                        for (String adapterClass : statusMap.keySet()) {
                            AdapterStatus status = statusMap.get(adapterClass);
                            MakeLog.info(TAG, String.format(
                                    "Adapter name: %s, Description: %s, Latency: %d",
                                    adapterClass, status.getDescription(), status.getLatency()));
                        }
                    }

                    MobileAds.setAppMuted(true);

                    requestNativeAd();

                }
            });

        }

    }

    private void loadNativeAdWithoutMediationAdapter () {
        requestNativeAd();
    }

    public void loadNativeAd () {

        if (activateMediation) {
            loadNativeAdWithMediationAdapter();
        } else {
            loadNativeAdWithoutMediationAdapter();
        }

    }

    private void requestNativeAd ()     {

        if (adStatus == AdStatus.AD_LOADED) {
            MakeLog.info(TAG, "Ad Already Loaded");
            return;
        }

        if (adStatus == AdStatus.AD_LOADING) {
            MakeLog.info(TAG, "Ad Loading...");
            return;
        }

        if (context == null) {
            MakeLog.error(TAG, "NULL Context");
            return;
        }

        if (placementId == null) {
            MakeLog.error(TAG, "NULL Placement Id");
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();

        AdLoader adLoader = new AdLoader.Builder(context, placementId).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull @NotNull NativeAd nativeAd) {
                MakeLog.info(TAG, "onNativeAdLoaded");
                adStatus = AdStatus.AD_LOADED;
                AdmobNativeAd.this.nativeAd = nativeAd;

                if (activateMediation) {
                    printMediationAdapterClassName(nativeAd.getResponseInfo());
                }

                if (admobNativeAdListener != null)
                    admobNativeAdListener.onSuccess();

            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adStatus = AdStatus.AD_LOAD_FAILED;
                MakeLog.error(TAG, "Ad Failed to Load. Error : " + loadAdError.toString());
                if (admobNativeAdListener != null)
                    admobNativeAdListener.onFailed();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                MakeLog.info(TAG, "onAdLoaded");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                adStatus = AdStatus.AD_IMPRESSION;
                MakeLog.info(TAG, "onAdImpression");
            }
        }).withNativeAdOptions(new NativeAdOptions.Builder().setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build()).build();

        adLoader.loadAd(adRequest);
        adStatus = AdStatus.AD_LOADING;
        MakeLog.info(TAG, "onAdLoading");

    }

    private void printMediationAdapterClassName (ResponseInfo responseInfo) {
        if (responseInfo != null) {

            if (responseInfo.getMediationAdapterClassName() != null) {

                if (whoisMediator(responseInfo.getMediationAdapterClassName()) == AdmobNativeAd.MEDIATION_AD_FROM_FACEBOOK) {
                    MakeLog.info(TAG,  "Mediation Native Ad From Facebook");
                } else if (whoisMediator(responseInfo.getMediationAdapterClassName()) == AdmobNativeAd.MEDIATION_AD_FROM_ADMOB) {
                    MakeLog.info(TAG,  "Mediation Native Ad From Admob");
                } else {
                    MakeLog.info(TAG,  "Mediation Native Ad From Unknown.");
                }

            } else {
                MakeLog.error(TAG, "NULL MediationAdapter Class Name");
            }

        } else {
            MakeLog.error(TAG, "NULL MediationAdapter ResponseInfo");
        }
    }

    public static void attachNativeAd (NativeAd nativeAd, View view) {
        attachAdmobNativeAd(nativeAd, view);
    }

    private static void attachAdmobNativeAd (NativeAd nativeAd, View view) {

        if (view == null) {
            MakeLog.error(TAG, "NULL View for Admob Native Ad");
            return;
        }

        if (nativeAd == null) {
            MakeLog.error(TAG, "NULL NativeAd for Admob Native Ad");
            return;
        }

        String adHeadline = nativeAd.getHeadline();
        String adBody = nativeAd.getBody();
        String adAdvertiser = nativeAd.getAdvertiser();
        String adCTA = nativeAd.getCallToAction();
        String adPrice = nativeAd.getPrice();
        String adStore = nativeAd.getStore();
        MediaContent mediaContent = nativeAd.getMediaContent();
        NativeAd.Image adIcon = nativeAd.getIcon();

        float adStarRatings = -1.0f;

        if (nativeAd.getStarRating() != null)
            adStarRatings = nativeAd.getStarRating().floatValue();

        NativeAdView nativeAdView = view.findViewById(R.id.nativeAdView);

        TextView adHeadlineTextView = view.findViewById(R.id.ad_headline);
        TextView adBodyTextView = view.findViewById(R.id.ad_body);
        TextView adAdvertiserTextView = view.findViewById(R.id.ad_advertiser);
        TextView adCTATextView = view.findViewById(R.id.ad_cta);
        TextView adStoreTextView = view.findViewById(R.id.ad_store);
        TextView adPriceTextView = view.findViewById(R.id.ad_price);
        RatingBar adRatingBarView = view.findViewById(R.id.ad_stars);
        MediaView adMediaView = view.findViewById(R.id.ad_media);
        ImageView adIconImageView = view.findViewById(R.id.ad_icon);
        TextView bulletPoint1TextView = view.findViewById(R.id.bullet_point);
        TextView bulletPoint2TextView = view.findViewById(R.id.bullet_point_2);

        // Set Native Ad Views
        nativeAdView.setHeadlineView(adHeadlineTextView);
        nativeAdView.setBodyView(adBodyTextView);
        nativeAdView.setAdvertiserView(adAdvertiserTextView);
        nativeAdView.setCallToActionView(adCTATextView);
        nativeAdView.setStoreView(adStoreTextView);
        nativeAdView.setPriceView(adPriceTextView);
        nativeAdView.setStarRatingView(adRatingBarView);
        nativeAdView.setIconView(adIconImageView);
        nativeAdView.setMediaView(adMediaView);

        // 1. Headline and Icon.

        try {
            if (nativeAdView.getHeadlineView() != null) {
                ((TextView)nativeAdView.getHeadlineView()).setText(adHeadline == null ? "" : adHeadline);
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        try {
            if (adIcon != null) {
                if (nativeAdView.getIconView() != null)
                    ((ImageView)nativeAdView.getIconView()).setImageDrawable(adIcon.getDrawable());
            } else {
                if (nativeAdView.getIconView() != null)
                    nativeAdView.getIconView().setVisibility(View.GONE);
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        try {
            // 2 MediaContent and CTA
            if (mediaContent != null) {
                if (nativeAdView.getMediaView() != null)
                {
                    if (!mediaContent.hasVideoContent())
                        ((MediaView)nativeAdView.getMediaView()).setImageScaleType(ImageView.ScaleType.FIT_XY);

                    ((MediaView)nativeAdView.getMediaView()).setMediaContent(mediaContent);
                }
            } else {
                if (nativeAdView.getMediaView() != null)
                    nativeAdView.getMediaView().setVisibility(View.GONE);
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        try {
            if (adCTA != null) {
                if (nativeAdView.getCallToActionView() != null)
                {
                    ((TextView)nativeAdView.getCallToActionView()).setText(adCTA);

                    try {
                        if (adCTA.contains("install") || adCTA.contains("download")) {
                            ((TextView)nativeAdView.getCallToActionView()).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_download, 0);
                        }
                    } catch (Exception e1) {
                        MakeLog.exception(e1);
                    }

                }
            } else {
                if (nativeAdView.getCallToActionView() != null)
                    nativeAdView.getCallToActionView().setVisibility(View.GONE);
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        try {
            // 3 Body and Advertiser
            if (adBody != null) {
                if (nativeAdView.getBodyView() != null)
                    ((TextView)nativeAdView.getBodyView()).setText(adBody);
            } else {
                if (nativeAdView.getBodyView() != null)
                    nativeAdView.getBodyView().setVisibility(View.GONE);
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        try {
            if (adAdvertiser != null) {
                if (nativeAdView.getAdvertiserView() != null)
                    ((TextView)nativeAdView.getAdvertiserView()).setText(adAdvertiser);
            } else {
                if (nativeAdView.getAdvertiserView() != null)
                    nativeAdView.getAdvertiserView().setVisibility(View.GONE);
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        try {
            // 4 Store, Price, and Ratings
            if (adStore != null) {
                if (nativeAdView.getStoreView() != null)
                    ((TextView)nativeAdView.getStoreView()).setText(adStore);
            } else {
                if (nativeAdView.getStoreView() != null)
                    nativeAdView.getStoreView().setVisibility(View.GONE);
                if (bulletPoint1TextView != null)
                    bulletPoint1TextView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        try {
            if (adPrice != null) {
                if (nativeAdView.getPriceView() != null) {

                    if (adPrice.trim().length() > 0) {
                        ((TextView)nativeAdView.getPriceView()).setText(adPrice);
                    } else {
                        if (nativeAdView.getPriceView() != null)
                            nativeAdView.getPriceView().setVisibility(View.GONE);
                        if (bulletPoint2TextView != null)
                            bulletPoint2TextView.setVisibility(View.GONE);
                    }

                }

            } else {
                if (nativeAdView.getPriceView() != null)
                    nativeAdView.getPriceView().setVisibility(View.GONE);
                if (bulletPoint2TextView != null)
                    bulletPoint2TextView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        try {
            if (adStarRatings != -1.0) {
                if (nativeAdView.getStarRatingView() != null)
                    ((RatingBar)nativeAdView.getStarRatingView()).setRating((float) adStarRatings);
            } else {
                if (nativeAdView.getStarRatingView() != null)
                    nativeAdView.getStarRatingView().setVisibility(View.GONE);
                if (bulletPoint2TextView != null)
                    bulletPoint2TextView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        try {
            if (nativeAdView != null)
                nativeAdView.setNativeAd(nativeAd);
        } catch (Exception e) {
            MakeLog.exception(e);
        }

    }

    public static void clear (View adContainer) {

        WeakReference<View> adContainerWeakReference = new WeakReference<View>(adContainer);

        if (adContainerWeakReference != null) {

            MediaView mediaView = adContainerWeakReference.get().findViewById(R.id.ad_media);
            try {
                mediaView.removeAllViews();
                MakeLog.info(TAG, "Cleaned Up the NativeAd Media View");
            } catch (Exception e) {
                MakeLog.exception(e);
            }

        } else {
            MakeLog.error(TAG, "Ad Container found NULL while cleaning up");
        }

    }


}
