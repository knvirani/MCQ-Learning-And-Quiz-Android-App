package com.fourshape.a4mcqplus.fragments.viewmodels;

import android.os.Parcelable;

import androidx.lifecycle.ViewModel;

import com.fourshape.a4mcqplus.rv_adapter.ContentModal;

import java.util.ArrayList;

public class SavedResultFragmentViewModel extends ViewModel {

    private ArrayList<ContentModal> contentModalArrayList;
    private Parcelable recyclerViewState;

    private boolean isContentLimitReached;
    private int paginationOffset;

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

    public boolean isContentLimitReached() {
        return isContentLimitReached;
    }

    public int getPaginationOffset() {
        return paginationOffset;
    }

    public void setContentLimitReached(boolean contentLimitReached) {
        isContentLimitReached = contentLimitReached;
    }

    public void setPaginationOffset(int paginationOffset) {
        this.paginationOffset = paginationOffset;
    }

}
