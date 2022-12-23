package com.fourshape.a4mcqplus.mcq_test;

import com.fourshape.a4mcqplus.utils.MakeLog;

public class LiveTestCatalog {

    public static String TEST_TITLE = null;
    public static String TEST_SUB_TITLE = null;
    public static String TEST_NAME = null;

    public static LiveTestCatalog[] UNIVERSAL_LIVE_TEST_CATALOG = null;

    private String mcqId;
    private String mcqIndex;
    private McqRow question, options [];
    private String answerIndex;
    private int mcqStatus;
    private String userAnswerIndex;
    private int timeTaken;

    public LiveTestCatalog () {}

    public void setMcqQuestion (String questionText, String questionImageUrl) {
        question = new McqRow(questionText, questionImageUrl);
    }

    public void setMcqOptions (String[] optionText, String[] optionImageUrl, String[] optionIndexes) {
        options = new McqRow[optionIndexes.length];
        for (int i = 0; i < optionIndexes.length; i++) {
            options[i] = new McqRow(optionText[i], optionImageUrl[i], optionIndexes[i]);
        }
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void setUserAnswerIndex(String userAnswerIndex) {
        this.userAnswerIndex = userAnswerIndex;
    }

    public void setMcqId(String mcqId) {
        this.mcqId = mcqId;
    }

    public void setAnswerIndex(String answerIndex) {
        this.answerIndex = answerIndex;
    }

    public void setMcqStatus(int mcqStatus) {
        this.mcqStatus = mcqStatus;
    }

    public void setMcqIndex(String mcqIndex) {
        this.mcqIndex = mcqIndex;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public String getUserAnswerIndex() {
        return userAnswerIndex;
    }

    public String getMcqId() {
        return mcqId;
    }

    public String getMcqIndex() {
        return mcqIndex;
    }

    public int getMcqStatus() {
        return mcqStatus;
    }

    public String getAnswerIndex() {
        return answerIndex;
    }

    public String getOptionText (int index) {

        try {
            return options[index].getTextValue();
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }

    }

    public String getOptionImageUrl (int index) {
        try {
            return options[index].getImageValue();
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getOptionIndex (int index) {
        try {
            return options[index].getIndex();
        } catch (Exception e) {
            MakeLog.exception(e);
            return null;
        }
    }

    public String getMcqQuestionText () {
        return question.getTextValue();
    }

    public String getMcqQuestionImageUrl () {
        return question.getImageValue();
    }

    public String getMcqQuestionIndex () {
        return question.getIndex();
    }

    static class McqRow {

        private String textValue, imageValue, index;

        public McqRow (String textValue, String imageValue, String index) {
            this.textValue = textValue;
            this.imageValue = imageValue;
            this.index = index;
        }

        public McqRow (String textValue, String imageValue) {
            this.textValue = textValue;
            this.imageValue = imageValue;
        }

        public String getIndex() {
            return index;
        }

        public String getImageValue() {
            return imageValue;
        }

        public String getTextValue() {
            return textValue;
        }

    }

}
