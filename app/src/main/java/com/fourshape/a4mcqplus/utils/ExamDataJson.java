package com.fourshape.a4mcqplus.utils;

import org.json.JSONArray;

public class ExamDataJson {

    private static final String TAG = "ExamDataJson";
    public static JSONArray DATA = null;
    private static int dataLength = 0;

    public static String[] getExamStuff (String examCode) {

        String[] data = new String[2];

        if (DATA == null) {
            MakeLog.error(TAG, "DATA is NULL");
            return null;
        }

        if (examCode == null) {
            MakeLog.error(TAG, "ExamCode is NULL");
            return null;
        }

        try {

            if (dataLength == 0) {
                dataLength = DATA.length();
            }

            for (int index = 0; index < dataLength; index++) {
                if (DATA.getJSONObject(index).getString(JsonAccessParams.ITEM_ID).trim().equals(examCode)) {
                    data[0] = DATA.getJSONObject(index).getString(JsonAccessParams.ITEM_TITLE).trim();
                    data[1] = DATA.getJSONObject(index).getString(JsonAccessParams.TOTAL_MCQS).trim();
                    return data;
                }
            }

            return null;

        } catch (Exception e) {
            MakeLog.error(TAG, e.toString());
            return null;
        }

    }

}
