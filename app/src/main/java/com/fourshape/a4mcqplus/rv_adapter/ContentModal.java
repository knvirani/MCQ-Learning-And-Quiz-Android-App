package com.fourshape.a4mcqplus.rv_adapter;

import com.google.android.gms.ads.nativead.NativeAd;

public class ContentModal {

    private int contentType;
    // For Preloaded-Native Ad

    private int appLogoId;
    private String appTitle, appPackage;

    public ContentModal (int contentType, int appLogoId, String appTitle, String appPackage, boolean isForAppsList) {
        this.contentType = contentType;
        this.appLogoId = appLogoId;
        this.appTitle = appTitle;
        this.appPackage = appPackage;
    }

    private String examTitle, examCode;
    public ContentModal (int contentType, String examTitle, String examCode, int totalMcqs, boolean isExamTitleRow) {
        this.contentType = contentType;
        this.examTitle = examTitle;
        this.examCode = examCode;
        this.totalMcqs = totalMcqs;
    }

    private NativeAd nativeAd;
    public ContentModal (int contentType, NativeAd nativeAd) {
        this.contentType = contentType;
        this.nativeAd = nativeAd;
    }

    private int testIndex, testFinishTime, testCorrectAttempts, testIncorrectAttempts, testOptionEAttempts;
    public ContentModal (int contentType, int testIndex, int testFinishTime, int totalMcqs, String testTitle, String testSubTitle, String testName, int testCorrectAttempts, int testIncorrectAttempts, int testOptionEAttempts) {
        this.contentType = contentType;
        this.testIndex = testIndex;
        this.testFinishTime = testFinishTime;
        this.testTitle = testTitle;
        this.testSubTitle = testSubTitle;
        this.testName = testName;
        this.totalMcqs = totalMcqs;
        this.testCorrectAttempts = testCorrectAttempts;
        this.testIncorrectAttempts = testIncorrectAttempts;
        this.testOptionEAttempts = testOptionEAttempts;
    }

    private int totalTests, totalHours;
    public ContentModal (int contentType, int totalTests, int totalHours) {
        this.contentType = contentType;
        this.totalTests = totalTests;
        this.totalHours = totalHours;
    }

    private String answerIndex, userAnswerIndex, mcqId;
    public ContentModal (int contentType, String answerIndex, String userAnswerIndex, String mcqId) {
        this.contentType = contentType;
        this.answerIndex = answerIndex;
        this.userAnswerIndex = userAnswerIndex;
        this.mcqId = mcqId;
    }

    private int timeTaken;
    private boolean answerTruth;
    public ContentModal (int contentType, int timeTaken, String answerIndex, String userAnswerIndex) {
        this.contentType = contentType;
        this.timeTaken = timeTaken;
        this.answerIndex = answerIndex;
        this.userAnswerIndex = userAnswerIndex;
    }

    private String testTitle;
    private String testSubTitle;
    private String testName;
    private int totalMcqs;
    private int totalCorrectAttempts;
    private int totalIncorrectAttempts;
    private int totalOptionEAttempts;
    private int totalTimeTaken;
    // For Test Result Time Taken
    public ContentModal (int contentType, String testTitle, String testSubTitle, String testName, int totalTimeTaken, int totalMcqs, int totalCorrectAttempts, int totalIncorrectAttempts, int totalOptionEAttempts) {
        this.contentType = contentType;
        this.testTitle = testTitle;
        this.testSubTitle = testSubTitle;
        this.testName = testName;
        this.totalMcqs = totalMcqs;
        this.totalCorrectAttempts = totalCorrectAttempts;
        this.totalIncorrectAttempts = totalIncorrectAttempts;
        this.totalOptionEAttempts = totalOptionEAttempts;
        this.totalTimeTaken = totalTimeTaken;
    }

    // For Divider, Native Ad
    public ContentModal (int contentType) {
        this.contentType = contentType;
    }

    // For Challenge
    public ContentModal (int contentType, String mcqId, boolean shouldReport) {
        this.contentType = contentType;
        this.mcqId = mcqId;
    }

    // For Blank or No Result
    private boolean isEmptyResult;
    public ContentModal (int contentType, boolean isEmptyResult) {
        this.contentType = contentType;
        this.isEmptyResult = isEmptyResult;
    }

    public boolean isEmptyResult() {
        return isEmptyResult;
    }

    // For subject list
    private String itemTitle, itemCode;
    public ContentModal (int contentType, String itemTitle, String itemCode) {
        this.contentType = contentType;
        this.itemTitle = itemTitle;
        this.itemCode = itemCode;
    }

    // For Tags and Exams List
    private int itemQuantityCount;
    public ContentModal (int contentType, String itemTitle, String itemCode, int itemQuantityCount) {
        this.contentType = contentType;
        this.itemTitle = itemTitle;
        this.itemCode = itemCode;
        this.itemQuantityCount = itemQuantityCount;
    }

    // For year label
    private String yearTitle;
    public ContentModal (int contentType, String yearTitle) {
        this.contentType = contentType;
        this.yearTitle = yearTitle;
    }

    // For Test List
    private int itemLimit, itemOffset;
    private String itemAction;
    public ContentModal (int contentType, String itemTitle, String itemAction, String itemCode, int itemOffset, int itemLimit) {
        this.contentType = contentType;
        this.itemTitle = itemTitle;
        this.itemAction = itemAction;
        this.itemOffset = itemOffset;
        this.itemLimit = itemLimit;
        this.itemCode = itemCode;
    }

    // For MCQ Question, and MCQ Option
    public String itemId, itemImageUrl, itemIndex, itemAnswerIndex;
    public String optionRankIndex;

    // For MCQ Question
    public ContentModal (int contentType, String itemId, String itemIndex, String itemTitle, String itemImageUrl) {
        this.contentType = contentType;
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.itemImageUrl = itemImageUrl;
        this.itemIndex = itemIndex;
    }

    // For MCQ Answer
    public ContentModal (int contentType, String itemId, String itemIndex, String itemTitle, String itemImageUrl, String itemAnswerIndex, String optionRankIndex) {
        this.contentType = contentType;
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.itemImageUrl = itemImageUrl;
        this.itemIndex = itemIndex;
        this.optionRankIndex = optionRankIndex;
        this.itemAnswerIndex = itemAnswerIndex;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public int getAppLogoId() {
        return appLogoId;
    }

    public boolean getAnswerTruth () {
        return answerTruth;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public String getExamCode() {
        return examCode;
    }

    public NativeAd getNativeAd() {
        return nativeAd;
    }

    public int getTestIndex() {
        return testIndex;
    }

    public int getTestFinishTime() {
        return testFinishTime;
    }

    public int getTestCorrectAttempts() {
        return testCorrectAttempts;
    }

    public int getTestIncorrectAttempts() {
        return testIncorrectAttempts;
    }

    public int getTestOptionEAttempts() {
        return testOptionEAttempts;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public int getTotalTimeTaken() {
        return totalTimeTaken;
    }

    public String getAnswerIndex() {
        return answerIndex;
    }

    public String getUserAnswerIndex() {
        return userAnswerIndex;
    }

    public String getMcqId() {
        return mcqId;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public String getTestTitle() {
        return testTitle;
    }

    public String getTestSubTitle() {
        return testSubTitle;
    }

    public String getTestName() {
        return testName;
    }

    public int getTotalMcqs() {
        return totalMcqs;
    }

    public int getTotalCorrectAttempts() {
        return totalCorrectAttempts;
    }

    public int getTotalIncorrectAttempts() {
        return totalIncorrectAttempts;
    }

    public int getTotalOptionEAttempts() {
        return totalOptionEAttempts;
    }

    public String getItemIndex() {
        return itemIndex;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public String getItemAnswerIndex() {
        return itemAnswerIndex;
    }

    public String getOptionRankIndex() {
        return optionRankIndex;
    }

    public int getContentType() {
        return contentType;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getItemQuantityCount() {
        return itemQuantityCount;
    }

    public String getYearTitle() {
        return yearTitle;
    }

    public int getItemLimit() {
        return itemLimit;
    }

    public int getItemOffset() {
        return itemOffset;
    }

    public String getItemAction() {
        return itemAction;
    }

}
