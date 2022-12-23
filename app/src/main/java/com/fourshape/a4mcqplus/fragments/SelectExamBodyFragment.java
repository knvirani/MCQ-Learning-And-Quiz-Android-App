package com.fourshape.a4mcqplus.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.utils.ActionBarTitle;
import com.fourshape.a4mcqplus.utils.ContentAccessParams;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.material.card.MaterialCardView;

public class SelectExamBodyFragment extends Fragment {

    private static final String TAG = "SelectExamBodyFragment";

    public SelectExamBodyFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        TrackScreen.now(getContext(), "SelectExamBodyFragment");

        if (getContext() != null)
            ActionBarTitle.TITLE = getContext().getString(R.string.list_of_exam_bodies);
        else {
            ActionBarTitle.TITLE = "List of Exam Bodies";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_select_exam_body, container, false);

        ViewGroup examBodyContainer = mainView.findViewById(R.id.exam_body_container);

        addExamBody(examBodyContainer, container.getId(), "GSSSB", "1");
        addExamBody(examBodyContainer, container.getId(), "GPSSB", "2");

        return mainView;
    }

    private void addExamBody (ViewGroup parentView, int fragmentContainerId, String bodyTitle, String bodyCode) {

        if (getContext() == null) {
            MakeLog.error(TAG, "NULL Context is detected.");
        }

        if (parentView == null) {
            MakeLog.error(TAG, "NULL ParentView.");
            return;
        }

        if (bodyTitle == null) {
            MakeLog.error(TAG, "Body Title is NULL");
            return;
        }

        if (bodyCode == null) {
            MakeLog.error(TAG, "Body Code is NULL");
            return;
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_text_view, null);

        View divider = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_divider_view, null);
        parentView.addView(divider);

        MaterialCardView materialCardView = view.findViewById(R.id.mtrl_card_view);
        TextView textView = view.findViewById(R.id.text_view_title);

        textView.setText(bodyTitle);

        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (getActivity() != null) {
                            PrevExamsFragment prevExamsFragment = PrevExamsFragment.newInstance(bodyCode, ContentAccessParams.ACCESS_EXAM_LIST);
                            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("prev_exam_fragment").replace(fragmentContainerId, prevExamsFragment).commit();
                        }

                    }
                }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);

            }
        });

        parentView.addView(view);

    }
}