package com.fourshape.a4mcqplus.rv_adapter.adapter_views;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.google.android.material.card.MaterialCardView;

public class BlankOrNoMoreResultCardView {

    public BlankOrNoMoreResultCardView (View view, boolean isEmptyResult) {

        MaterialCardView materialCardView = view.findViewById(R.id.mtrl_card_view);
        TextView resultTextView = view.findViewById(R.id.result_text);

        if (isEmptyResult) {
            resultTextView.setText("No results found.");
        } else {
            resultTextView.setText("That's all for you.");
        }

        int screenWidth = ScreenParams.getDisplayWidthPixels(view.getContext());
        float screenDensity = ScreenParams.getDisplayDensity(view.getContext());

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(screenWidth, (int) (180 * screenDensity));
        materialCardView.setLayoutParams(layoutParams);

    }

}
