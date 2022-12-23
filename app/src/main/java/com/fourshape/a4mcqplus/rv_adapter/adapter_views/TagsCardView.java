package com.fourshape.a4mcqplus.rv_adapter.adapter_views;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.fragments.TestListFragment;
import com.fourshape.a4mcqplus.mcq_test.LiveTestCatalog;
import com.fourshape.a4mcqplus.utils.ContentAccessParams;
import com.fourshape.a4mcqplus.utils.FormattedData;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.google.android.material.card.MaterialCardView;

import java.lang.ref.WeakReference;

public class TagsCardView {

    public TagsCardView (FragmentActivity activity, int fragmentContainerId, View view, String tagTitle, String tagCode, int itemCount) {

        TextView tagTitleTextView = view.findViewById(R.id.text_view_title);
        MaterialCardView materialCardView = view.findViewById(R.id.mtrl_card_view);

        if (tagTitle != null)
            tagTitleTextView.setText(FormattedData.formattedContent(tagTitle));

        int screenWidth = ScreenParams.getDisplayWidthPixels(new WeakReference<>(activity.getApplicationContext()).get());

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(screenWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        materialCardView.setLayoutParams(layoutParams);

        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    LiveTestCatalog.TEST_SUB_TITLE = tagTitle;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("test_list_fragment").replace(fragmentContainerId, TestListFragment.newInstance(ContentAccessParams.ACCESS_MCQ_NORMALLY, tagTitle, tagCode, itemCount)).commit();
                        }
                    }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);

                }
            }
        });

    }

}
