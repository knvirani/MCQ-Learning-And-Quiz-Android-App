package com.fourshape.a4mcqplus.app_db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.fourshape.a4mcqplus.utils.MakeLog;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBHandler extends SQLiteOpenHelper {

    private final static String TAG = "DBHandler";
    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "4mcq_plus.db";
    private final static String TABLE_NAME = "result_history";

    private final static String CREATE_TABlE = "CREATE TABLE " + TABLE_NAME
            + "( " + DBCols.ID + " INTEGER PRIMARY KEY,"
            + DBCols.TEST_TITLE + " TEXT,"
            + DBCols.TEST_SUB_TITLE + " TEXT,"
            + DBCols.TEST_NAME + " TEXT,"
            + DBCols.TEST_TIME + " INTEGER,"
            + DBCols.TEST_TOTAL_MCQS + " INTEGER,"
            + DBCols.TEST_CORRECT_ATTEMPTS + " INTEGER,"
            + DBCols.TEST_INCORRECT_ATTEMPTS + " INTEGER,"
            + DBCols.TEST_OPTION_E_ATTEMPTS + " INTEGER )";

    public DBHandler(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void addRecord (String testTitle, String testSubTitle, String testName, int totalMcqs, int testTime, int testCorrectAttempts, int testIncorrectAttempts, int testOptionEAttempts) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBCols.TEST_TITLE, testTitle);
        values.put(DBCols.TEST_SUB_TITLE, testSubTitle);
        values.put(DBCols.TEST_NAME, testName);
        values.put(DBCols.TEST_TOTAL_MCQS, totalMcqs);
        values.put(DBCols.TEST_TIME, testTime);
        values.put(DBCols.TEST_CORRECT_ATTEMPTS, testCorrectAttempts);
        values.put(DBCols.TEST_INCORRECT_ATTEMPTS, testIncorrectAttempts);
        values.put(DBCols.TEST_OPTION_E_ATTEMPTS, testOptionEAttempts);

        MakeLog.info(TAG, values.toString());

        db.insert(TABLE_NAME, null, values);
        db.close();

    }

    public String loadData (String params) {
        if (params.equals("load_dashboard")) {
            return testHistoryDashBoard();
        } else {
            return getTestHistoryRecords(params);
        }
    }

    @SuppressLint("Range")
    private String testHistoryDashBoard () {

        JSONObject jsonObject = new JSONObject();

        try {

            SQLiteDatabase database = this.getReadableDatabase();

            String SELECT_TOTAL_TESTS = "SELECT " + DBCols.ID + " FROM " + TABLE_NAME;
            String TOTAL_TEST_TIME = "SELECT SUM(" +DBCols.TEST_TIME + ") AS TOTAL_TEST_TIME FROM " + TABLE_NAME;
            Cursor cursor = database.rawQuery(SELECT_TOTAL_TESTS, null);

            if (cursor != null) {
                jsonObject.put(DBCols.TEST_TOTAL_NUMBERS, cursor.getCount());
            } else {
                MakeLog.error(TAG, "NULL Cursor count while counting all records.");
                jsonObject.put(DBCols.TEST_TOTAL_NUMBERS, 0);
            }

            if (cursor != null)
                cursor.close();

            cursor = database.rawQuery(TOTAL_TEST_TIME, null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    jsonObject.put(DBCols.TEST_TOTAL_HOURS, cursor.getInt(cursor.getColumnIndex("TOTAL_TEST_TIME")));
                } else {
                    MakeLog.error(TAG, "Cursor count is 0 while summation of total test time.");
                    jsonObject.put(DBCols.TEST_TOTAL_HOURS, 0);
                }
            } else {
                MakeLog.error(TAG, "NULL cursor while counting total test time.");
                jsonObject.put(DBCols.TEST_TOTAL_HOURS, 0);
            }

            if (cursor != null){
                cursor.close();
            }
            database.close();

        } catch (Exception e) {
            MakeLog.exception(e);

        }

        return jsonObject.toString();
    }

    @SuppressLint("Range")
    private String getTestHistoryRecords (String sqlQuery) {

        if (sqlQuery == null) {
            MakeLog.error(TAG, "NULL SQL Query supplied.");
            return null;
        }

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery(sqlQuery, null);

        if (cursor == null) {
            MakeLog.error(TAG, "NULL cursor");
            return null;
        }

        if (cursor.getCount() == 0) {
            MakeLog.error(TAG,"0 records in cursor count.");
            return null;
        }
        JSONArray jsonArray = new JSONArray();

        try {

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                JSONObject tempObj = new JSONObject();

                String testTitle = cursor.getString(cursor.getColumnIndex(DBCols.TEST_TITLE));
                String testSubTitle = cursor.getString(cursor.getColumnIndex(DBCols.TEST_SUB_TITLE));
                String testName = cursor.getString(cursor.getColumnIndex(DBCols.TEST_NAME));
                int totalMcqs = cursor.getInt(cursor.getColumnIndex(DBCols.TEST_TOTAL_MCQS));
                int testTime = cursor.getInt(cursor.getColumnIndex(DBCols.TEST_TIME));
                int testCorrectAttempts = cursor.getInt(cursor.getColumnIndex(DBCols.TEST_CORRECT_ATTEMPTS));
                int testIncorrectAttempts = cursor.getInt(cursor.getColumnIndex(DBCols.TEST_INCORRECT_ATTEMPTS));
                int testOptionEAttempts = cursor.getInt(cursor.getColumnIndex(DBCols.TEST_OPTION_E_ATTEMPTS));

                tempObj.put(DBCols.TEST_TITLE, testTitle);
                tempObj.put(DBCols.TEST_SUB_TITLE, testSubTitle);
                tempObj.put(DBCols.TEST_NAME, testName);
                tempObj.put(DBCols.TEST_TOTAL_MCQS, totalMcqs);
                tempObj.put(DBCols.TEST_TIME, testTime);
                tempObj.put(DBCols.TEST_CORRECT_ATTEMPTS, testCorrectAttempts);
                tempObj.put(DBCols.TEST_INCORRECT_ATTEMPTS, testIncorrectAttempts);
                tempObj.put(DBCols.TEST_OPTION_E_ATTEMPTS, testOptionEAttempts);

                jsonArray.put(tempObj);
                cursor.moveToNext();

            }

        } catch (Exception e) {
            MakeLog.exception(e);
        }

        try {
            cursor.close();
            database.close();
            return jsonArray.toString();
        } catch (Exception e) {
            MakeLog.exception(e);
            MakeLog.error(TAG, "Unable to close cursor and/or database");
            return null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABlE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
