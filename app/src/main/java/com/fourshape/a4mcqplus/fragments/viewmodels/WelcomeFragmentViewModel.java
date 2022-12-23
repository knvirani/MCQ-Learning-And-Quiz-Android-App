package com.fourshape.a4mcqplus.fragments.viewmodels;

import androidx.lifecycle.ViewModel;

public class WelcomeFragmentViewModel extends ViewModel {

    private boolean isAppUtilChecked = false;

    private boolean isAppUpdateRequired = false;
    private String appUpdateMessage;

    public String getAppUpdateMessage() {
        return appUpdateMessage;
    }

    public void setAppUpdateMessage(String appUpdateMessage) {
        this.appUpdateMessage = appUpdateMessage;
    }

    public boolean isAppUpdateRequired() {
        return isAppUpdateRequired;
    }

    public void setAppUpdateRequired(boolean appUpdateRequired) {
        isAppUpdateRequired = appUpdateRequired;
    }

    public boolean isAppUtilChecked() {
        return isAppUtilChecked;
    }

    public void setAppUtilChecked(boolean appUtilChecked) {
        isAppUtilChecked = appUtilChecked;
    }

}
