package com.fourshape.a4mcqplus.server;

import android.content.Context;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fourshape.a4mcqplus.app_ads.admob_ads.PlacementIds;
import com.fourshape.a4mcqplus.app_ads.admob_ads.admob_native_ad.AdmobNativeAd;
import com.fourshape.a4mcqplus.app_ads.admob_ads.admob_native_ad.AdmobNativeAdListener;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.VariableControls;

import java.util.Map;

public class LoadData {

    private static final String TAG = "LoadData";

    private DataLoadListener dataLoadListener;

    private Context context;
    private String url;
    private Map<String, String> params;
    private AdmobNativeAd admobNativeAd;
    private String dataResponse;

    private boolean shouldLoadNativeAd = true;
    private boolean isListenerCalled = false;
    private boolean isAdResponseFetched = false;

    public LoadData (Context context, String url, Map<String, String> params) {
        this.context = context;
        this.url = url;
        this.params = params;

        try {
            admobNativeAd = new AdmobNativeAd(context, VariableControls.shouldUseMediationForBigNativeAd);
            admobNativeAd.setPlacementId(PlacementIds.NATIVE_AD);
        } catch (Exception e) {
            MakeLog.exception(e);
        }

    }

    public void setShouldLoadNativeAd (boolean shouldLoadNativeAd) {
        this.shouldLoadNativeAd = shouldLoadNativeAd;
    }

    public void setLoadDataListener (DataLoadListener dataLoadListener) {
        this.dataLoadListener = dataLoadListener;
    }

    private void loadWithAds () {

        if (admobNativeAd == null) {
            return;
        }

        admobNativeAd.setAdmobNativeAdListener(new AdmobNativeAdListener() {
            @Override
            public void onSuccess() {

                isAdResponseFetched = true;

                if (dataLoadListener != null && admobNativeAd != null && dataResponse != null && !isListenerCalled) {
                    isListenerCalled = true;
                    dataLoadListener.onSuccess(dataResponse, admobNativeAd.getNativeAd());
                }

            }

            @Override
            public void onFailed() {

                isAdResponseFetched = true;

                if (dataLoadListener != null && admobNativeAd != null && dataResponse != null && !isListenerCalled) {
                    isListenerCalled = true;
                    dataLoadListener.onSuccess(dataResponse, null);
                }

            }
        });

        admobNativeAd.loadNativeAd();

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dataResponse = response;

                        if (!isListenerCalled && isAdResponseFetched) {
                            isListenerCalled = true;
                            if (dataLoadListener != null) {
                                dataLoadListener.onSuccess(dataResponse, admobNativeAd.getNativeAd());
                            }
                        }

                    }
                },
                error -> {

                    dataResponse = error.toString();
                    if (dataLoadListener != null && !isListenerCalled) {
                        isListenerCalled = true;
                        dataLoadListener.onError(dataResponse);
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

    private void loadWithoutAds () {

        if (context == null) {
            if (dataLoadListener != null) {
                dataLoadListener.onError("Error");
            }
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (dataLoadListener != null)
                            dataLoadListener.onSuccess(response, null);

                    }
                },
                error -> {

                    dataResponse = error.toString();

                    if (dataLoadListener != null)
                        dataLoadListener.onError(dataResponse);

                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

    public void load () {

        if (shouldLoadNativeAd) {
            if (admobNativeAd != null) {
                loadWithAds();
            } else {
                loadWithoutAds();
            }
        } else {
            loadWithoutAds();
        }

    }

}
