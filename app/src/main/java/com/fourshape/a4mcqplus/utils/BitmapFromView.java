package com.fourshape.a4mcqplus.utils;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.fourshape.a4mcqplus.R;

public class BitmapFromView {

    public static Bitmap getBitmapFromView(View view) {

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        final Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ScreenParams.getDisplayWidthPixels(view.getContext()), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        return bitmap;
    }

}


