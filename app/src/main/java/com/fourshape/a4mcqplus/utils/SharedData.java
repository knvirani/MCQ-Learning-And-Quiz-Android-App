package com.fourshape.a4mcqplus.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fourshape.a4mcqplus.ui_dialogs.AppThemeChooserDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SharedData {

    private final String PREFERENCE_NAME = "EvergreenOldVideosPreference";
    private SharedPreferences sharedPreference;
    private static final String DATE_FORMAT = "dd-MMM-yyyy";

    public SharedData(Context context){

        if (context != null)
            this.sharedPreference = context.getSharedPreferences(this.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void registerFirstTimeUseDate(){
        if (this.sharedPreference == null)
            return;

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        this.sharedPreference.edit().putString("app_first_time_date", formattedDate).apply();
    }

    public String getFirstTimeUseDate(){
        if (this.sharedPreference == null)
            return null;
        return this.sharedPreference.getString("app_first_time_date", null);
    }


    public void setDoNotShowAgainForExternalAppOpenDialog () {

        if (this.sharedPreference == null)
            return;

        this.sharedPreference.edit().putBoolean("do_not_show_ext_app_open_dialog", false).apply();

    }

    public boolean getDoNotShowAgainForExternalAppOpenDialog () {

        if (this.sharedPreference == null)
            return true;

        return this.sharedPreference.getBoolean("do_not_show_ext_app_open_dialog", true);

    }

    public void registerAppReviewPostDate(){
        if (this.sharedPreference == null)
            return;

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        String formattedDate = df.format(c);

        this.sharedPreference.edit().putString("app_review_date", formattedDate).apply();
    }

    public String getLastAppReviewPostDate(){
        if (this.sharedPreference == null)
            return null;
        return this.sharedPreference.getString("app_review_date", null);
    }

    public boolean isPolicyAccepted(){

        if (this.sharedPreference == null)
            return false;

        return this.sharedPreference.getBoolean("policy_accepted",false);
    }

    public String getAppRatingsDialogLastShown () {
        if (this.sharedPreference == null)
            return null;
        return this.sharedPreference.getString("app_review_dialog_last_shown_date", null);
    }

    public void registerAppRatingsDialogShownToday () {
        if (this.sharedPreference == null)
            return;

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        String formattedDate = df.format(c);

        this.sharedPreference.edit().putString("app_review_dialog_last_shown_date", formattedDate).apply();
    }

    public void setTheme(String theme) {

        if (this.sharedPreference == null)
            return;
        this.sharedPreference.edit().putString("app_theme", theme).apply();

    }

    public String getTheme () {

        if (this.sharedPreference == null)
            return AppThemeChooserDialog.LIGHT_THEME;
        else
            return this.sharedPreference.getString("app_theme", AppThemeChooserDialog.LIGHT_THEME);

    }

    public void acceptPolicy(){

        if (this.sharedPreference == null)
            return;

        this.sharedPreference.edit().putBoolean("policy_accepted", true).apply();
    }
    public void rejectPolicy(){

        if (this.sharedPreference == null)
            return;

        this.sharedPreference.edit().putBoolean("policy_accepted", false).apply();
    }


}
