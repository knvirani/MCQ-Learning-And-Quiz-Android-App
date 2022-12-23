package com.fourshape.a4mcqplus.fragments.viewmodels;

import android.os.Parcelable;

import androidx.lifecycle.ViewModel;

import com.fourshape.a4mcqplus.rv_adapter.ContentModal;

import java.util.ArrayList;

public class SubjectFragmentViewModel extends ViewModel {

    private ArrayList<ContentModal> contentModalArrayList;
    private Parcelable recyclerViewState;
    private boolean isDashboardLoaded;

    public void setDashboardLoaded(boolean dashboardLoaded) {
        isDashboardLoaded = dashboardLoaded;
    }

    public boolean isDashboardLoaded() {
        return isDashboardLoaded;
    }

    public ArrayList<ContentModal> getContentModalArrayList() {
        return contentModalArrayList;
    }

    public Parcelable getRecyclerViewState() {
        return recyclerViewState;
    }

    public void setContentModalArrayList(ArrayList<ContentModal> contentModalArrayList) {
        this.contentModalArrayList = contentModalArrayList;
    }

    public void setRecyclerViewState(Parcelable recyclerViewState) {
        this.recyclerViewState = recyclerViewState;
    }
}
