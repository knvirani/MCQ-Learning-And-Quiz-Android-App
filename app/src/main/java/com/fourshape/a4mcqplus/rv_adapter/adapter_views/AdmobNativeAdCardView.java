package com.fourshape.a4mcqplus.rv_adapter.adapter_views;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_ads.AdStatus;
import com.fourshape.a4mcqplus.app_ads.admob_ads.NativeAdTimer;
import com.fourshape.a4mcqplus.app_ads.admob_ads.PlacementIds;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class AdmobNativeAdCardView {

    private static final String TAG = "AdmobNativeAdCardView";
    private static int AD_STATUS = 0;
    private static NativeAd nativeAd;

    private static boolean shouldLoadAd () {
        return AD_STATUS == AdStatus.AD_IMPRESSION || AD_STATUS == AdStatus.AD_LOAD_FAILED || AD_STATUS == 0;
    }

    public static boolean shouldAttachAd () {
        return AD_STATUS == AdStatus.AD_LOADED;
    }

    public static NativeAd getNativeAdInstance () {
        return nativeAd;
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

    public static void loadAd (Context context) {
        requestNewAd(new WeakReference<>(context).get(), PlacementIds.NATIVE_AD);
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

    private static void requestNewAd (Context context, String placementId) {

        if (AD_STATUS == AdStatus.AD_LOADED) {
            MakeLog.info(TAG, "Ad Already Loaded");
            return;
        }

        if (AD_STATUS == AdStatus.AD_LOADING) {
            MakeLog.info(TAG, "Ad Loading...");
            return;
        }

        if (context == null) {
            MakeLog.error(TAG, "NULL Context");
            return;
        }

        if (placementId == null) {
            MakeLog.error(TAG, "NULL Placement");
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();

        AdLoader adLoader = new AdLoader.Builder(context, placementId).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull @NotNull NativeAd nativeAd) {
                MakeLog.info(TAG, "onNativeAdLoaded");
                AD_STATUS = AdStatus.AD_LOADED;
                AdmobNativeAdCardView.nativeAd = nativeAd;
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                AD_STATUS = AdStatus.AD_LOAD_FAILED;
                MakeLog.error(TAG, "Ad Failed to Load. Error : " + loadAdError.toString());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                MakeLog.info(TAG, "onAdLoaded");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();

                AD_STATUS = AdStatus.AD_IMPRESSION;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestNewAd(context, placementId);
                    }
                }, NativeAdTimer.TIME_INTERVAL);

            }
        }).withNativeAdOptions(new NativeAdOptions.Builder().setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build()).build();

        adLoader.loadAd(adRequest);
        AD_STATUS = AdStatus.AD_LOADING;
        MakeLog.info(TAG, "onAdLoading");

    }

}
