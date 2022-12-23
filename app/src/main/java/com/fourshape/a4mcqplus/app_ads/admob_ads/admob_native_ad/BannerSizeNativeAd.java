package com.fourshape.a4mcqplus.app_ads.admob_ads.admob_native_ad;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_ads.admob_ads.PlacementIds;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

public class BannerSizeNativeAd {

    private static final String TAG = "AppNativeAd";

    private Context context;
    private final View view;
    private String adPlacementId = null;

    private NativeAdView nativeAdView;
    private MediaView mediaView;
    private ImageView adIconIV;
    private TextView advertiserTV, adHeadlineTV, adDescriptionTV;
    private MaterialButton adCtaMB;
    private MaterialCardView adMediaCV, adIconCV;

    public BannerSizeNativeAd (Context context, int nativeAdType, View view) {

        this.context = context;
        adPlacementId = PlacementIds.NATIVE_BANNER_AD;

        this.view = view;

    }

    public void fetchViewWidgets () {

        MakeLog.info(TAG, "fetchViewWidgets");

        nativeAdView = view.findViewById(R.id.native_ad_view);
        adMediaCV = view.findViewById(R.id.ad_media_cv);
        mediaView = view.findViewById(R.id.ad_media);
        advertiserTV = view.findViewById(R.id.ad_advertiser);
        adHeadlineTV = view.findViewById(R.id.ad_headline);
        adDescriptionTV = view.findViewById(R.id.ad_body);
        adCtaMB = view.findViewById(R.id.ad_cta);
        adIconIV = view.findViewById(R.id.ad_icon);
        adIconCV = view.findViewById(R.id.ad_icon_cv);

        designViewInDefault();

    }

    public void load () {

        AdRequest adRequest = new AdRequest.Builder().build();

        AdLoader adLoader = new AdLoader.Builder(context, adPlacementId).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull @NotNull NativeAd nativeAd) {

                MakeLog.info(TAG, "onNativeAdLoaded");

                implementNativeIntoView(nativeAd);

            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                MakeLog.info(TAG, "onAdClicked");
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                MakeLog.info(TAG, "onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                MakeLog.info(TAG, "onAdFailedToLoad: Error: " + loadAdError.toString());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                MakeLog.info(TAG, "onAdImpression");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                MakeLog.info(TAG, "onAdLoaded");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                MakeLog.info(TAG, "onAdOpened");
            }
        }).build();

        adLoader.loadAd(adRequest);


    }

    private void implementNativeIntoView (NativeAd nativeAd) {

        if (nativeAd == null) {
            MakeLog.error(TAG, "NULL NativeAd. So, can't attach it to the view");
            return;
        }

        if (nativeAdView == null) {
            MakeLog.error(TAG, "NativeAdView is NULL.");
            return;
        }

        if (advertiserTV == null) {
            MakeLog.error(TAG, "AdvertiserTV is NULL");
            return;
        }

        if (adHeadlineTV == null) {
            MakeLog.error(TAG, "adHeadlineTV is NULL");
            return;
        }

        if (adDescriptionTV == null) {
            MakeLog.error(TAG, "adDescriptionTV is NULL");
            return;
        }

        if (adIconIV == null) {
            MakeLog.error(TAG, "adIconIV is NULL");
            return;
        }

        if (mediaView == null) {
            MakeLog.error(TAG, "MediaView is NULL");
            return;
        }

        if (adCtaMB == null) {
            MakeLog.error(TAG, "AdCTA MB is NULL");
            return;
        }

        removeDefaultDesign();

        String adHeadline = nativeAd.getHeadline();
        String adBody = nativeAd.getBody();
        String adAdvertiser = nativeAd.getAdvertiser();
        String adCTA = nativeAd.getCallToAction();
        MediaContent mediaContent = nativeAd.getMediaContent();
        NativeAd.Image adIcon = nativeAd.getIcon();

        nativeAdView.setHeadlineView(adHeadlineTV);
        nativeAdView.setMediaView(mediaView);
        nativeAdView.setIconView(adIconIV);
        nativeAdView.setBodyView(adDescriptionTV);
        nativeAdView.setCallToActionView(adCtaMB);
        nativeAdView.setAdvertiserView(advertiserTV);

        if (adHeadline != null) {
            ((TextView)nativeAdView.getHeadlineView()).setText(adHeadline);
        } else {
            nativeAdView.getHeadlineView().setVisibility(View.GONE);
        }

        if (adBody != null) {
            ((TextView)nativeAdView.getBodyView()).setText(adBody);
        } else {
            nativeAdView.getBodyView().setVisibility(View.GONE);
        }

        if (adAdvertiser != null) {
            ((TextView)nativeAdView.getAdvertiserView()).setText(adAdvertiser);
        } else {
            nativeAdView.getAdvertiserView().setVisibility(View.GONE);
        }

        if (mediaContent != null) {

            ((MediaView)nativeAdView.getMediaView()).setMediaContent(mediaContent);

            if (!mediaContent.hasVideoContent())
                ((MediaView)nativeAdView.getMediaView()).setImageScaleType(ImageView.ScaleType.FIT_XY);

        } else {
            adMediaCV.setVisibility(View.GONE);
            nativeAdView.getMediaView().setVisibility(View.GONE);
        }

        if (adIcon != null) {
            ((ImageView)nativeAdView.getIconView()).setImageDrawable(adIcon.getDrawable().getCurrent());
            nativeAdView.getIconView().invalidate();
            MakeLog.info(TAG, "Ad Icon set");
        } else {
            nativeAdView.getIconView().setVisibility(View.GONE);
            adIconCV.setVisibility(View.GONE);
        }

        if (adCTA != null) {

            ((MaterialButton)nativeAdView.getCallToActionView()).setText(adCTA);

            if (adCTA.contains("install") || adCTA.contains("download")) {
                ((MaterialButton)nativeAdView.getCallToActionView()).setIcon(context.getDrawable(R.drawable.ic_download));
            } else {
                ((MaterialButton)nativeAdView.getCallToActionView()).setIcon(context.getDrawable(R.drawable.ic_external_link));
            }

            ((MaterialButton)nativeAdView.getCallToActionView()).setIconTint(ColorStateList.valueOf(context.getColor(R.color.white)));

        } else {

            ((MaterialButton)nativeAdView.getCallToActionView()).setVisibility(View.GONE);

        }

        nativeAdView.setNativeAd(nativeAd);

    }

    private void removeDefaultDesign () {

        if (mediaView != null) {
            if (mediaView.getVisibility() == View.VISIBLE) {
                mediaView.setBackgroundColor(context.getColor(R.color.transparent));
            }
        }

        if (adMediaCV != null) {
            if (adMediaCV.getVisibility() == View.VISIBLE) {
                adMediaCV.setCardBackgroundColor(context.getColor(R.color.transparent));
            }
        }

        if (advertiserTV != null) {
            if (advertiserTV.getVisibility() == View.VISIBLE) {
                advertiserTV.setBackgroundColor(context.getColor(R.color.transparent));
            }
        }

        if (adHeadlineTV != null) {
            if (adHeadlineTV.getVisibility() == View.VISIBLE) {
                adHeadlineTV.setBackgroundColor(context.getColor(R.color.transparent));
            }
        }

        if (adDescriptionTV != null) {
            if (adDescriptionTV.getVisibility() == View.VISIBLE) {
                adDescriptionTV.setBackgroundColor(context.getColor(R.color.transparent));
            }
        }

        if (adCtaMB != null) {
            if (adCtaMB.getVisibility() == View.VISIBLE) {
                adCtaMB.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.app_main_color)));
            }
            if (!adCtaMB.isEnabled()) {
                adCtaMB.setEnabled(true);
            }
        }

        if (adIconCV != null) {
            if (adIconCV.getVisibility() == View.VISIBLE) {
                adIconCV.setCardBackgroundColor(context.getColor(R.color.transparent));
            }
        }

        if (adIconIV != null) {
            if (adIconIV.getVisibility() == View.VISIBLE) {
                adIconIV.setBackground(context.getDrawable(R.drawable.native_ad_icon_src_transparent));
            }
        }

    }

    private void designViewInDefault () {

        if (mediaView != null) {
            if (mediaView.getVisibility() != View.VISIBLE) {
                mediaView.setVisibility(View.VISIBLE);
            }
            mediaView.setBackgroundColor(context.getColor(R.color.native_ad_placeholder_bg));
        }

        if (adMediaCV != null) {
            if (adMediaCV.getVisibility() != View.VISIBLE) {
                adMediaCV.setVisibility(View.VISIBLE);
            }
            adMediaCV.setCardBackgroundColor(context.getColor(R.color.native_ad_placeholder_bg));
        }

        if (advertiserTV != null) {
            if (advertiserTV.getVisibility() != View.VISIBLE) {
                advertiserTV.setVisibility(View.VISIBLE);
            }
            advertiserTV.setText("");
            advertiserTV.setBackgroundColor(context.getColor(R.color.native_ad_placeholder_bg));
        }

        if (adHeadlineTV != null) {
            if (adHeadlineTV.getVisibility() != View.VISIBLE) {
                adHeadlineTV.setVisibility(View.VISIBLE);
            }
            adHeadlineTV.setText("");
            adHeadlineTV.setBackgroundColor(context.getColor(R.color.native_ad_placeholder_bg));
        }

        if (adDescriptionTV != null) {
            if (adDescriptionTV.getVisibility() != View.VISIBLE) {
                adDescriptionTV.setVisibility(View.VISIBLE);
            }
            adDescriptionTV.setText("");
            adDescriptionTV.setBackgroundColor(context.getColor(R.color.native_ad_placeholder_bg));
        }

        if (adCtaMB != null) {
            if (adCtaMB.getVisibility() != View.VISIBLE) {
                adCtaMB.setVisibility(View.VISIBLE);
            }
            adCtaMB.setText("");
            adCtaMB.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.native_ad_placeholder_bg)));
            if (adCtaMB.isEnabled()) {
                adCtaMB.setEnabled(false);
            }
            adCtaMB.setIcon(context.getDrawable(R.drawable.native_ad_icon_src_transparent));
        }

        if (adIconIV != null) {
            if (adIconIV.getVisibility() != View.VISIBLE) {
                adIconIV.setVisibility(View.VISIBLE);
            }
            adIconIV.setImageDrawable(context.getDrawable(R.drawable.native_ad_icon_src));
        }

        if (adIconCV != null) {
            if (adIconCV.getVisibility() != View.VISIBLE) {
                adIconCV.setVisibility(View.VISIBLE);
            }
            adIconCV.setCardBackgroundColor(context.getColor(R.color.native_ad_placeholder_bg));
        }

    }

    public void releaseResources () {

        if (mediaView != null) {
            if (mediaView.getVisibility() == View.VISIBLE) {
                mediaView.removeAllViews();
                mediaView.clearFocus();
                MakeLog.info(TAG, "Resources Released");
            }
        }

        if (adIconCV != null) {
            adIconIV.setImageDrawable(context.getDrawable(R.drawable.native_ad_icon_src_transparent));
        }

    }


}
