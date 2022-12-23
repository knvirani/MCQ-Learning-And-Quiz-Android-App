package com.fourshape.a4mcqplus.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.OutputStream;

public class ShareTestResult {

    private static final String TAG = "ShareTestResult";

    public void shareNow (Context context, Bitmap bitmap) {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);

        OutputStream outputStream;

        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            share.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(share, "Share Image"));
        } catch (Exception e) {
            MakeLog.exception(e);
            Toast.makeText(context, "Unable to Share Test Result", Toast.LENGTH_SHORT).show();
        }

    }

}
