package com.fourshape.a4mcqplus.utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

public class FormattedData {

    public static String getBackupImageName (String mcqId, String index) {
        if (mcqId == null || index == null)
            return null;
        else {
            return "mcq_"+mcqId+"_"+index+".jpg";
        }
    }

    public static String getExamConductingBody (String bodyCode) {
        if (bodyCode == null)
            return "None";
        else {
            if (bodyCode.equals("1"))
                return "GSSSB";
            else if (bodyCode.equals("2"))
                return "GPSSB";
            else
                return "None";
        }
    }

    public static Spanned formattedContent (String text) {
        if (text != null) {
            if (Build.VERSION.SDK_INT >= 24)
                return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
            else
                return Html.fromHtml(text);
        } else {
            if (Build.VERSION.SDK_INT >= 24)
                return Html.fromHtml("", Html.FROM_HTML_MODE_LEGACY);
            else
                return Html.fromHtml("");
        }
    }

    public static String getFormattedTime (int time) {

        String formattedTime = "";

        if (time < 60){
            if (time < 10) {
                formattedTime = "00" + ":" + "0" + time;
            } else {
                formattedTime = "00" + ":" + time;
            }
        }
        else if (time < 3600) {

            int minute = time/60;
            int second = time%60;

            if (minute < 10)
                formattedTime = "0" + minute;
            else
                formattedTime = String.valueOf(minute);

            if (second < 10)
                formattedTime += ":" + "0" + second;
            else
                formattedTime += ":" + second;

        } else if (time < 36000) {
            int hour = time/3600;

            formattedTime = "0" + hour;

            int data_for_m_s = time%3600;
            int minute = data_for_m_s/60;
            int second = data_for_m_s%60;

            if (minute < 10)
                formattedTime += ":" + "0" + minute;
            else
                formattedTime += ":" + minute;

            if (second < 10)
                formattedTime += ":" + "0" + second;
            else
                formattedTime += ":" + second;
        } else {
            return String.valueOf(time);
        }

        return formattedTime;
    }

}
