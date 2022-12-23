package com.fourshape.a4mcqplus.app_db;

public class TestHistoryRecord {

    private String testTitle, testSubTitle, testName;
    private int testTime, testCorrectAttempts, testIncorrectAttempts, testOptionEAttempts;

    public TestHistoryRecord(String testTitle, String testSubTitle, String testName, int testTime, int testCorrectAttempts, int testIncorrectAttempts, int testOptionEAttempts) {
        this.testTitle = testTitle;
        this.testSubTitle = testSubTitle;
        this.testName = testName;
        this.testTime = testTime;
        this.testCorrectAttempts = testCorrectAttempts;
        this.testIncorrectAttempts = testIncorrectAttempts;
        this.testOptionEAttempts = testOptionEAttempts;
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

    public int getTestTime() {
        return testTime;
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
}
