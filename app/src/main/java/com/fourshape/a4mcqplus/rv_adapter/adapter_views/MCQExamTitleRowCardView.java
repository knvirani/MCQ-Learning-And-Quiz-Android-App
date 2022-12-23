package com.fourshape.a4mcqplus.rv_adapter.adapter_views;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.utils.FormattedData;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.google.android.material.card.MaterialCardView;

import java.lang.ref.WeakReference;

public class MCQExamTitleRowCardView {

    private String examTitle, examCode;
    private int totalMcqs;

    public MCQExamTitleRowCardView (FragmentActivity activity, int fragmentContainerId, View view, String examTitle, String examCode, int totalMcqs) {

        this.examTitle = examTitle;
        this.examCode = examCode;
        this.totalMcqs = totalMcqs;

        ((TextView)view.findViewById(R.id.text_view_title)).setText(FormattedData.formattedContent(examTitle));

        int screenWidth = ScreenParams.getDisplayWidthPixels(new WeakReference<>(activity.getApplicationContext()).get());

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(screenWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        MaterialCardView materialCardView = view.findViewById(R.id.mtrl_card_view);
        materialCardView.setLayoutParams(layoutParams);

        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*

                if (activity != null) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {



                        }
                    }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);

                }

                 */

            }
        });

    }

}
