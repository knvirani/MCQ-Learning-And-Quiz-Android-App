package com.fourshape.a4mcqplus.rv_adapter.adapter_views;

import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.fragments.PrevExamsFragment;
import com.fourshape.a4mcqplus.fragments.TagsFragment;
import com.fourshape.a4mcqplus.mcq_test.LiveTestCatalog;
import com.fourshape.a4mcqplus.utils.ContentAccessParams;
import com.fourshape.a4mcqplus.utils.FormattedData;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.google.android.material.card.MaterialCardView;

import java.lang.ref.WeakReference;

public class ExamSectionCardView {

    public ExamSectionCardView (FragmentActivity activity, int fragmentContainerId, View view, String itemTitle, String itemCode) {

        ((TextView)view.findViewById(R.id.text_view_title)).setText(FormattedData.formattedContent(itemTitle));
        ((TextView)view.findViewById(R.id.text_view_title)).setGravity(Gravity.START);
        MaterialCardView materialCardView = view.findViewById(R.id.mtrl_card_view);

        int screenWidth = ScreenParams.getDisplayWidthPixels(new WeakReference<>(activity.getApplicationContext()).get());

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(screenWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        materialCardView.setLayoutParams(layoutParams);

        ((MaterialCardView)view.findViewById(R.id.mtrl_card_view)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("prev_exam_fragment").replace(fragmentContainerId, PrevExamsFragment.newInstance(itemTitle, itemCode, ContentAccessParams.ACCESS_EXAM_LIST)).commit();
                        }
                    }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);
                }
            }
        });

    }

}
