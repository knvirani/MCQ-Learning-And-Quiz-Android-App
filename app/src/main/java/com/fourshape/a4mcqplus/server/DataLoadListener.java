package com.fourshape.a4mcqplus.server;

import com.google.android.gms.ads.nativead.NativeAd;

public interface DataLoadListener {
    void onSuccess (String response, NativeAd nativeAd);
    void onError (String error);
}
