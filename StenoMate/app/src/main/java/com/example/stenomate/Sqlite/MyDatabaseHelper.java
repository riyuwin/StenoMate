package com.example.stenomate.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "Steno.db";
    private static final int DATABASE_VERSION = 1;

    // Table and Columns
    public static final String TABLE_ASSESSMENT_NAME = "assessments";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ASSESSMENT_PERCENTAGE = "percentage";
    public static final String COLUMN_LESSON_NUMBER = "lesson_number";
    public static final String COLUMN_ASSESSMENT_DATETIME = "datetime";
    public static final String COLUMN_ASSESSMENT_ANSWER = "answer";


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ASSESSMENT_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LESSON_NUMBER + " TEXT, " +
                COLUMN_ASSESSMENT_PERCENTAGE + " TEXT, " +
                COLUMN_ASSESSMENT_ANSWER + " TEXT, " +
                COLUMN_ASSESSMENT_DATETIME + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }


    // Called when database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENT_NAME);
        onCreate(db);
    }

    // Insert Data
    public boolean insertAssessment(int lessonNumber, float percentage, String answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Get current datetime as a string (e.g., "2025-07-03 14:55:30")
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        values.put(COLUMN_LESSON_NUMBER, lessonNumber);
        values.put(COLUMN_ASSESSMENT_PERCENTAGE, percentage);
        values.put(COLUMN_ASSESSMENT_ANSWER, answer);
        values.put(COLUMN_ASSESSMENT_DATETIME, currentDateTime); // ✅ Correct value here

        long result = db.insert(TABLE_ASSESSMENT_NAME, null, values);
        return result != -1;
    }


    // Read All Lesson Passed
    public Cursor getAllLessonPassed() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ASSESSMENT_NAME +
                " WHERE " + COLUMN_ASSESSMENT_PERCENTAGE + " >= 75";
        return db.rawQuery(query, null);
    }


    // Read All Data
    public Cursor getAllAssessments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ASSESSMENT_NAME, null);
    }


}
