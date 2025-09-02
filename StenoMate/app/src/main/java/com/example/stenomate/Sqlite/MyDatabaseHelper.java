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
    private static final int DATABASE_VERSION = 2;

    // Table and Columns
    public static final String TABLE_ASSESSMENT_NAME = "assessments";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ASSESSMENT_PERCENTAGE = "percentage";
    public static final String COLUMN_LESSON_NUMBER = "lesson_number";
    public static final String COLUMN_LESSON_GROUP_NUMBER = "lesson_group_number";
    public static final String COLUMN_ASSESSMENT_DATETIME = "datetime";
    public static final String COLUMN_ASSESSMENT_ANSWER = "answer";

    // Dictation Table and Columns
    public static final String TABLE_DICTATION_NAME = "dictations";
    public static final String COLUMN_DICTATION_ID = "id";
    public static final String COLUMN_DICTATION_LESSON_NUMBER = "dictation_lesson_number";
    public static final String COLUMN_DICTATION_GROUP_NUMBER = "dictation_lesson_group_number";
    public static final String COLUMN_DICTATION_ATTEMPT = "dictation_attempt";
    public static final String COLUMN_DICTATION_DATETIME = "datetime";



    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ASSESSMENT_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LESSON_NUMBER + " TEXT, " +
                COLUMN_LESSON_GROUP_NUMBER + " TEXT, " +
                COLUMN_ASSESSMENT_PERCENTAGE + " TEXT, " +
                COLUMN_ASSESSMENT_ANSWER + " TEXT, " +
                COLUMN_ASSESSMENT_DATETIME + " TEXT)";
        db.execSQL(CREATE_TABLE);

        // Dictation Table
        String CREATE_DICTATION_TABLE = "CREATE TABLE " + TABLE_DICTATION_NAME + " (" +
                COLUMN_DICTATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DICTATION_LESSON_NUMBER + " TEXT, " +
                COLUMN_DICTATION_GROUP_NUMBER + " TEXT, " +
                COLUMN_DICTATION_ATTEMPT + " TEXT, " +
                COLUMN_DICTATION_DATETIME + " TEXT)";
        db.execSQL(CREATE_DICTATION_TABLE);
    }


    // Called when database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENT_NAME);
        onCreate(db);
    }

    // Insert Data
    public boolean insertAssessment(int lessonNumber, int lessonGroupNumber, float percentage, String answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Get current datetime as a string (e.g., "2025-07-03 14:55:30")
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        values.put(COLUMN_LESSON_NUMBER, lessonNumber);
        values.put(COLUMN_LESSON_GROUP_NUMBER, lessonGroupNumber);
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

    public boolean insertDictation(int lessonNumber, int groupNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT MAX(" + COLUMN_DICTATION_ATTEMPT + ") FROM " + TABLE_DICTATION_NAME +
                " WHERE " + COLUMN_DICTATION_LESSON_NUMBER + "=? AND " + COLUMN_DICTATION_GROUP_NUMBER + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(lessonNumber), String.valueOf(groupNumber)});

        int nextAttempt = 1;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            nextAttempt = cursor.getInt(0) + 1; // increment
        }
        cursor.close();

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put(COLUMN_DICTATION_LESSON_NUMBER, lessonNumber);
        values.put(COLUMN_DICTATION_GROUP_NUMBER, groupNumber);
        values.put(COLUMN_DICTATION_ATTEMPT, nextAttempt);
        values.put(COLUMN_DICTATION_DATETIME, currentDateTime);

        long result = db.insert(TABLE_DICTATION_NAME, null, values);
        return result != -1;
    }

    public Cursor getAllDictations() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_DICTATION_NAME, null);
    }

    public Cursor getAttemptsByLessonAndGroup(int lessonNum, int groupNum) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_DICTATION_NAME +
                        " WHERE dictation_lesson_number = ? AND dictation_lesson_group_number = ?" +
                        " ORDER BY id ASC",
                new String[]{String.valueOf(lessonNum), String.valueOf(groupNum)}
        );
    }



}
