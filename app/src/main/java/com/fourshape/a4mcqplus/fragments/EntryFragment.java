package com.fourshape.a4mcqplus.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.popups.AskForRatingsPopup;
import com.fourshape.a4mcqplus.utils.ActionBarTitle;
import com.fourshape.a4mcqplus.utils.AppRatings;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;


public class EntryFragment extends Fragment {

    public EntryFragment() {
        // Required empty public constructor
    }

    public static EntryFragment newInstance(String param1, String param2) {
        EntryFragment fragment = new EntryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        TrackScreen.now(getContext(), "EntryFragment");

        if (getContext() != null) {
            ActionBarTitle.TITLE = getContext().getString(R.string.app_name);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_entry, container, false);

        MaterialCardView subjectWiseCV, examsWithMixedBodyCV, examsBodyWiseCV;

        subjectWiseCV = mainView.findViewById(R.id.subject_wise_proceed);
        examsWithMixedBodyCV = mainView.findViewById(R.id.exams_with_all_bodies_mtrl_card_view);
        examsBodyWiseCV = mainView.findViewById(R.id.exams_body_wise);

        if (getActivity() != null && getContext() != null) {

            if (new AppRatings(getContext()).isRightTimeForAppRatings()) {
                new AskForRatingsPopup(getContext()).show();
            }

        }

        subjectWiseCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("subject_fragment").replace(container.getId(), new SubjectFragment()).commit();
                        }
                    }
                }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);
            }
        });

        examsBodyWiseCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("select_exam_body_fragment").replace(container.getId(), new SelectExamBodyFragment()).commit();
                        }
                    }
                }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);

            }
        });

        examsWithMixedBodyCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("select_exam_section_fragment").replace(container.getId(), new ExamSectionFragment()).commit();
                        }
                    }
                }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);
            }
        });

        return mainView;

    }
}