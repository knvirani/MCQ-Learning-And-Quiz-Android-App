package com.fourshape.a4mcqplus.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppRatings {

    private Context context;
    private static final String TAG = "AppRatingsChecker";
    private static final String DATE_FORMAT = "dd-MMM-yyyy";
    private static final int RATINGS_INTERVAL_THRESHOLD = 3;

    public AppRatings (Context context) {
        this.context = context;
    }

    public boolean isRightTimeForAppRatings () {

        if (context == null) {
            MakeLog.error(TAG, "Context is NULL. Hence false");
            return false;
        }

        SharedData sharedData = new SharedData(context);

        if (sharedData == null) {
            MakeLog.error(TAG, "SharedData is NULL. Hence false");
            return false;
        }

        String firstTimeUseDate = sharedData.getFirstTimeUseDate();
        String lastTimeReviewDate = sharedData.getLastAppReviewPostDate();
        String todayDate = getDateToday();
        String dialogLastShownDate = sharedData.getAppRatingsDialogLastShown();

        if (firstTimeUseDate == null) {
            MakeLog.error(TAG, "User is fresh. Hence false");
            return false;
        } else {
            MakeLog.info(TAG, "First Landed on: " + firstTimeUseDate);
        }

        if (todayDate == null) {
            MakeLog.error(TAG, "Today's Date is NULL. Hence false");
            return false;
        } else {
            MakeLog.info(TAG, "Today's fucking date: " + todayDate);
        }

        if (dialogLastShownDate != null) {
            if (dialogLastShownDate.equals(todayDate)) {
                MakeLog.info(TAG,  "Dialog has been shown today. Hence false.");
                return false;
            } else {
                MakeLog.info(TAG, "Dialog Last Shown on: " + dialogLastShownDate);
            }
        }

        if (lastTimeReviewDate == null) {

            int dayDiff = getDateDifference(todayDate, firstTimeUseDate);

            if (dayDiff > RATINGS_INTERVAL_THRESHOLD) {
                MakeLog.info(TAG, "Yaah! Good Time to Ask For Review. Diff: " + dayDiff);
                return true;
            } else {
                MakeLog.info(TAG, "Not a good time. Hence false. Diff: " + dayDiff);
                return false;
            }

        } else {
            MakeLog.info(TAG, "Last Review Date is Present. Date is: " + lastTimeReviewDate);
            return false;
        }

    }

    private static String getDateToday () {

        try {

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

            return df.format(c);

        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }

    }

    private int getDateDifference (String date1, String date2) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

        long difference = 0;

        if (date1 == null) {
            MakeLog.error(TAG, "Date1 is NULL.");
            return 0;
        }

        if (date2 == null) {
            MakeLog.error(TAG, "Date2 is NULL.");
            return 0;
        }

        try {

            difference = simpleDateFormat.parse(date2).getTime() - simpleDateFormat.parse(date1).getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;
            long elapsedDays = difference / daysInMilli;

            return (int) (elapsedDays < 0 ? -1*elapsedDays : elapsedDays);

        } catch (Exception e) {
            MakeLog.exception(e);
            return 0;
        }


    }


}
