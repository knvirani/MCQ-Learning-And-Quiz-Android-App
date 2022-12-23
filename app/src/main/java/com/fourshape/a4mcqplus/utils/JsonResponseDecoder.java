package com.fourshape.a4mcqplus.utils;

import com.fourshape.a4mcqplus.app_ads.admob_ads.AdController;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonResponseDecoder {

    private static final String TAG = "JsonResponseDecoder";
    private boolean hasValidData;
    private int dataLength;
    private JSONObject resultDataObject;
    private JSONArray jsonArray;

    public JsonResponseDecoder (String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has(JsonAccessParams.RESULT_TITLE)) {
                if (jsonObject.getString(JsonAccessParams.RESULT_TITLE).equals(JsonAccessParams.RESULT_SUCCESS)) {

                    if (jsonObject.has(JsonAccessParams.RESULT_DATA)) {

                        JSONArray jsonArray;

                        try {
                            jsonArray = jsonObject.getJSONArray(JsonAccessParams.RESULT_DATA);
                            if (jsonArray.length() != 0) {
                                this.hasValidData = true;
                                this.dataLength = jsonArray.length();
                                this.jsonArray = jsonArray;
                                jsonObject = null;
                                jsonArray = null;
                            } else {
                                this.hasValidData = false;
                            }
                        } catch (Exception e) {
                            MakeLog.exception(e);
                        }

                        try {
                            if (jsonObject != null && jsonObject.has(JsonAccessParams.RESULT_DATA))
                            {
                                this.resultDataObject = jsonObject.getJSONObject(JsonAccessParams.RESULT_DATA);
                                this.hasValidData = true;
                            }

                        } catch (Exception e) {
                            MakeLog.exception(e);
                        }

                    } else {
                        this.hasValidData = false;
                    }

                } else {
                    this.hasValidData = false;
                }
            }

        } catch (Exception e) {
            MakeLog.exception(e);
            hasValidData = false;
            dataLength = 0;
        }
    }

    public boolean hasValidData() {
        return hasValidData;
    }

    public int getDataLength() {
        return dataLength;
    }

    public String[] getMcqExamsCode(int index) {
        try {

            int totalExams = 0;

            JSONArray jsonArray = new JSONObject(this.jsonArray.getJSONObject(index).getString(JsonAccessParams.MCQ_EXAM_CODES)).getJSONArray(JsonAccessParams.EXAM_CODE_ITEMS);
            totalExams = jsonArray.length();

            MakeLog.info(TAG, String.valueOf(totalExams));

            if (totalExams > 0) {
                String[] data = new String[totalExams];
                for (int i = 0; i < data.length; i++) {
                    data[i] = jsonArray.getString(i);
                }
                return data;
            } else {
                return null;
            }

        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public JSONArray getExamsDataFromMixture () {

        try {
            return this.resultDataObject.getJSONArray(JsonAccessParams.LIST_EXAMS);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }

    }

    public int getTotalSubjects () {
        try {
            return this.resultDataObject.getJSONArray(JsonAccessParams.LIST_CATEGORIES).length();
        } catch (Exception e) {
            MakeLog.exception(e);
            return 0;
        }
    }

    public String getSubjectTitleFromExamMixture (int index) {

        try {
            return this.resultDataObject.getJSONArray(JsonAccessParams.LIST_CATEGORIES).getJSONObject(index).getString(JsonAccessParams.ITEM_TITLE);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }

    }

    public String getSubjectCodeFromExamMixture (int index) {

        try {
            return this.resultDataObject.getJSONArray(JsonAccessParams.LIST_CATEGORIES).getJSONObject(index).getString(JsonAccessParams.ITEM_ID);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }

    }

    public String getItemTitle (int index) {
        try {
            return this.jsonArray.getJSONObject(index).getString(JsonAccessParams.ITEM_TITLE);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getItemCode (int index) {
        try {
            return this.jsonArray.getJSONObject(index).getString(JsonAccessParams.ITEM_ID);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getItemCount (int index) {
        try {
            return this.jsonArray.getJSONObject(index).getString(JsonAccessParams.QUESTION_COUNT);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getTotalMcqs (int index) {
        try {
            return this.jsonArray.getJSONObject(index).getString(JsonAccessParams.TOTAL_MCQS);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getItemYear (int index) {
        try {
            return this.jsonArray.getJSONObject(index).getString(JsonAccessParams.ITEM_YEAR);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getLatestAppVersionRequired () {
        try {
            return this.jsonArray.getJSONObject(0).getString(JsonAccessParams.LATEST_APP_VERSION);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public boolean getExitReadingInterstitialAdStatus () {
        try {
            return ((new JSONObject(this.jsonArray.getJSONObject(0).getString(JsonAccessParams.ADS_CONTROL))).getString(JsonAccessParams.EXIT_READING_INTERSTITIAL_AD_STATUS).trim().equals("1"));
        } catch (Exception e) {
            MakeLog.exception(e);
            return false;
        }
    }

    public boolean getExitTestInterstitialAdStatus () {
        try {
            return ((new JSONObject(this.jsonArray.getJSONObject(0).getString(JsonAccessParams.ADS_CONTROL))).getString(JsonAccessParams.EXIT_TEST_INTERSTITIAL_AD_STATUS).trim().equals("1"));
        } catch (Exception e) {
            MakeLog.exception(e);
            return false;
        }
    }

    public boolean getInterstitialAdStatus () {
        try {
            return this.jsonArray.getJSONObject(0).getString(JsonAccessParams.INTERSTITIAL_AD_STATUS).equals("1");
        } catch (Exception e) {
            MakeLog.exception(e);
            return true;
        }
    }

    public int getInterstitialAdInterval () {
        try {
            return Integer.parseInt(this.jsonArray.getJSONObject(0).getString(JsonAccessParams.INTERSTITIAL_AD_INTERVAL));
        } catch (Exception e) {
            MakeLog.exception(e);
            return AdController.interstitialAdInterval;
        }
    }

    public String getMinimumAppVersionRequired () {
        try {
            return this.jsonArray.getJSONObject(0).getString(JsonAccessParams.MIN_REQ_VERSION);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getAppUpdateRemarks () {
        try {
            return this.jsonArray.getJSONObject(0).getString(JsonAccessParams.REMARKS);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getQuestionText (int index) {

        try {
            return ((new JSONObject(this.jsonArray.getJSONObject(index).getString(JsonAccessParams.MCQ_STUFF))).getJSONObject(JsonAccessParams.MCQ_QUESTION_TITLE).getString(JsonAccessParams.MCQ_TEXT_TITLE));
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }

    }

    public String getOptionText (int stuffIndex, int optionIndex) {
        try {
            return ((new JSONObject(this.jsonArray.getJSONObject(stuffIndex).getString(JsonAccessParams.MCQ_STUFF))).getJSONArray(JsonAccessParams.MCQ_OPTIONS_TITLE).getJSONObject(optionIndex).getString(JsonAccessParams.MCQ_TEXT_TITLE));
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getOptionTrueIndex (int stuffIndex, int optionIndex) {
        try {
            return ((new JSONObject(this.jsonArray.getJSONObject(stuffIndex).getString(JsonAccessParams.MCQ_STUFF))).getJSONArray(JsonAccessParams.MCQ_OPTIONS_TITLE).getJSONObject(optionIndex).getString(JsonAccessParams.MCQ_OPTION_INDEX));
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getOptionImageUrl (int stuffIndex, int optionIndex) {

        try {
            return ((new JSONObject(this.jsonArray.getJSONObject(stuffIndex).getString(JsonAccessParams.MCQ_STUFF))).getJSONArray(JsonAccessParams.MCQ_OPTIONS_TITLE).getJSONObject(optionIndex).getString(JsonAccessParams.MCQ_IMAGE_URL_TITLE));

        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getQuestionImageUrl (int index) {
        try {
            return ((new JSONObject(this.jsonArray.getJSONObject(index).getString(JsonAccessParams.MCQ_STUFF))).getJSONObject(JsonAccessParams.MCQ_QUESTION_TITLE).getString(JsonAccessParams.MCQ_IMAGE_URL_TITLE));
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getAnswerIndex (int index) {
        try {
            return ((new JSONObject(this.jsonArray.getJSONObject(index).getString(JsonAccessParams.MCQ_STUFF))).getJSONObject(JsonAccessParams.MCQ_ANSWER_TITLE).getString(JsonAccessParams.MCQ_ANSWER_INDEX));
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getAnswerImageUrl (int index) {
        try {
            return ((new JSONObject(this.jsonArray.getJSONObject(index).getString(JsonAccessParams.MCQ_STUFF))).getJSONObject(JsonAccessParams.MCQ_ANSWER_TITLE).getString(JsonAccessParams.MCQ_IMAGE_URL_TITLE));
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getMcqId (int index) {
        try {
            return this.jsonArray.getJSONObject(index).getString(JsonAccessParams.MCQ_ID);
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

}
