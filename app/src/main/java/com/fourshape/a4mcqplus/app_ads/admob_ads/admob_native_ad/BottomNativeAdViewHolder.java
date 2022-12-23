package com.fourshape.a4mcqplus.app_ads.admob_ads.admob_native_ad;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.lang.ref.WeakReference;

public class BottomNativeAdViewHolder {

    private static final String TAG = "BottomNativeAdViewHolder";

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

    public static void attachNativeBannerAd (NativeAd nativeAd, View view) {

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
        MediaContent mediaContent = nativeAd.getMediaContent();
        NativeAd.Image adIcon = nativeAd.getIcon();

        NativeAdView nativeAdView = view.findViewById(R.id.nativeAdView);
        TextView adHeadlineTextView = view.findViewById(R.id.ad_headline);
        TextView adBodyTextView = view.findViewById(R.id.ad_body);
        TextView adAdvertiserTextView = view.findViewById(R.id.ad_advertiser);
        TextView adCTATextView = view.findViewById(R.id.ad_cta);
        MediaView adMediaView = view.findViewById(R.id.ad_media);
        ImageView adIconImageView = view.findViewById(R.id.ad_icon);

        // Set Native Ad Views
        nativeAdView.setHeadlineView(adHeadlineTextView);
        nativeAdView.setBodyView(adBodyTextView);
        nativeAdView.setAdvertiserView(adAdvertiserTextView);
        nativeAdView.setCallToActionView(adCTATextView);
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

        // 2 MediaContent
        try {
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

        // 3 Click-To-Action Button
        try {
            if (adCTA != null) {
                if (nativeAdView.getCallToActionView() != null)
                {
                    ((TextView)nativeAdView.getCallToActionView()).setText(adCTA);

                    /*

                    // We're not going to add end drawable to the CTA buttons.

                    try {
                        if (adCTA.contains("install") || adCTA.contains("download")) {
                            ((TextView)nativeAdView.getCallToActionView()).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_download, 0);
                        }
                    } catch (Exception e1) {
                        MakeLog.exception(e1);
                    }

                     */

                }
            } else {
                if (nativeAdView.getCallToActionView() != null)
                {
                    nativeAdView.getCallToActionView().setVisibility(View.GONE);

                    /*
                    If CTA is not set, then body view needs to set for full-width.
                     */

                    if (nativeAdView.getBodyView() != null) {
                        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                        (nativeAdView.getBodyView()).setLayoutParams(layoutParams);
                    }

                }
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        // 4 Body
        try {
            if (adBody != null) {
                if (nativeAdView.getBodyView() != null) {
                    ((TextView)nativeAdView.getBodyView()).setText(adBody);
                }
            } else {
                if (nativeAdView.getBodyView() != null)
                    nativeAdView.getBodyView().setVisibility(View.GONE);
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        // 5 Advertiser
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

        if (nativeAdView != null)
            nativeAdView.setNativeAd(nativeAd);

    }

}
