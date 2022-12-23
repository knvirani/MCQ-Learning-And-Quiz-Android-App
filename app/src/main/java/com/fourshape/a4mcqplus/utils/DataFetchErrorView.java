package com.fourshape.a4mcqplus.utils;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.google.android.material.card.MaterialCardView;

public class DataFetchErrorView {

    private MaterialCardView errView;
    private ViewGroup parent;
    private DataFetchErrorViewOnClickListener dataFetchErrorViewOnClickListener;

    public DataFetchErrorView (MaterialCardView errView, View parent) {
        this.errView = errView;
        this.parent = (ViewGroup) parent;
    }

    public void setDataFetchErrorViewOnClickListener(DataFetchErrorViewOnClickListener dataFetchErrorViewOnClickListener) {
        this.dataFetchErrorViewOnClickListener = dataFetchErrorViewOnClickListener;
        this.errView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleErrorBar();
            }
        });
    }

    public void toggleErrorBar () {

        if (errView != null) {

            if (errView.getVisibility() == View.GONE) {
                addTransitionAnimation();
                showErrBar();
            } else {
                addTransitionAnimation();
                hideErrBar();
            }

            dataFetchErrorViewOnClickListener.onClick(errView.getVisibility() == View.GONE);
        }

    }

    private void addTransitionAnimation () {
        if (errView != null && parent != null) {
            Transition transition = new Slide(Gravity.TOP);
            transition.setDuration(300);
            transition.addTarget(errView);
            TransitionManager.beginDelayedTransition((ViewGroup) parent, transition);
        }
    }

    private void showErrBar () {
        if (errView != null) {
            if (errView.getVisibility() != View.VISIBLE) {
                errView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideErrBar () {
        if (errView != null) {
            if (errView.getVisibility() != View.GONE) {
                errView.setVisibility(View.GONE);
            }
        }
    }

}
