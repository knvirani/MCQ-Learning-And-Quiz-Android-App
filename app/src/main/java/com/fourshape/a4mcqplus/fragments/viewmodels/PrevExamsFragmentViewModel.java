package com.fourshape.a4mcqplus.fragments.viewmodels;

import android.os.Parcelable;

import androidx.lifecycle.ViewModel;

import com.fourshape.a4mcqplus.rv_adapter.ContentModal;

import java.util.ArrayList;

public class PrevExamsFragmentViewModel extends ViewModel {

    private ArrayList<ContentModal> contentModalArrayList;
    private Parcelable recyclerViewState;

    private boolean isContentLimitReached;
    private int paginationOffset;

    private String examBodyTitle, examBodyCode;

    public void setExamBodyCode(String examBodyCode) {
        this.examBodyCode = examBodyCode;
    }

    public void setExamBodyTitle(String examBodyTitle) {
        this.examBodyTitle = examBodyTitle;
    }

    public String getExamBodyCode() {
        return examBodyCode;
    }

    public String getExamBodyTitle() {
        return examBodyTitle;
    }

    public boolean isCurrentFragmentIsSameAsPreviousOne (String examBodyTitle, String examBodyCode) {
        if (examBodyCode == null && examBodyTitle == null && this.examBodyTitle == null && this.examBodyCode == null)
            return true;
        else {
            return examBodyCode != null && examBodyCode.equals(this.examBodyCode);
        }
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
