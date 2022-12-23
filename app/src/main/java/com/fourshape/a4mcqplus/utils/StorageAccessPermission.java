package com.fourshape.a4mcqplus.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import java.lang.ref.WeakReference;

public class StorageAccessPermission {

    public static final int REQUEST_CODE = 110;
    private final FragmentActivity activity;

    public StorageAccessPermission () {
        activity = null;
    }

    public StorageAccessPermission (FragmentActivity activity) {
        this.activity = new WeakReference<>(activity).get();
    }

    public boolean isPermissionGranted () {
        if (activity != null)
            return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        else
            return false;
    }

    public String getStorageWriteString () {
        return Manifest.permission.WRITE_EXTERNAL_STORAGE;
    }


}
