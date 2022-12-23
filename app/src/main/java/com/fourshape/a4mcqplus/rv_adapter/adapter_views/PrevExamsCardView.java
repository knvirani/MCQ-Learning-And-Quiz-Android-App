package com.fourshape.a4mcqplus.rv_adapter.adapter_views;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.fragments.TestListFragment;
import com.fourshape.a4mcqplus.mcq_test.LiveTestCatalog;
import com.fourshape.a4mcqplus.utils.ContentAccessParams;
import com.fourshape.a4mcqplus.utils.FormattedData;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.google.android.material.card.MaterialCardView;

import java.lang.ref.WeakReference;

public class PrevExamsCardView {

    public PrevExamsCardView (FragmentActivity activity, int fragmentContainerId, View view, String examTitle, String examCode, int itemCount) {

        TextView examTitleTextView = view.findViewById(R.id.text_view_title);
        examTitleTextView.setGravity(Gravity.START);
        MaterialCardView materialCardView = view.findViewById(R.id.mtrl_card_view);

        if (examTitle != null)
            examTitleTextView.setText(FormattedData.formattedContent(examTitle));

        int screenWidth = ScreenParams.getDisplayWidthPixels(new WeakReference<>(activity.getApplicationContext()).get());

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(screenWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        materialCardView.setLayoutParams(layoutParams);

        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    LiveTestCatalog.TEST_TITLE = examTitle;
                    LiveTestCatalog.TEST_SUB_TITLE = "";
                    activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("test_list_fragment").replace(fragmentContainerId, TestListFragment.newInstance(ContentAccessParams.ACCESS_MCQ_EXAM_WISE, examTitle, examCode, itemCount)).commit();
                }
            }
        });

    }

}
