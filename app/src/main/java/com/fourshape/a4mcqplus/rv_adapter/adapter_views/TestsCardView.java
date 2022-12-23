package com.fourshape.a4mcqplus.rv_adapter.adapter_views;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.mcq_test.LiveTestCatalog;
import com.fourshape.a4mcqplus.ui_dialogs.TestPurposeChooserDialog;
import com.fourshape.a4mcqplus.utils.FormattedData;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.google.android.material.card.MaterialCardView;

import java.lang.ref.WeakReference;

public class TestsCardView {

    public TestsCardView (FragmentActivity activity, int fragmentContainerId, View view, String testTitle, String testCode, String testAction, int testLimit, int testOffset) {

        TextView tagTitleTextView = view.findViewById(R.id.text_view_title);
        MaterialCardView materialCardView = view.findViewById(R.id.mtrl_card_view);

        if (testTitle != null)
            tagTitleTextView.setText(FormattedData.formattedContent(testTitle));

        int screenWidth = ScreenParams.getDisplayWidthPixels(new WeakReference<>(activity.getApplicationContext()).get());

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(screenWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        materialCardView.setLayoutParams(layoutParams);

        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LiveTestCatalog.TEST_NAME = testTitle;
                new TestPurposeChooserDialog(view.getContext(), activity, fragmentContainerId, testTitle, testCode, testAction, testLimit, testOffset).show();
            }
        });

    }

}
