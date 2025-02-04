package com.example.randomayahgenerator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "quran_data.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "ayahs";
    public static final String COLUMN_AYAH = "ayah";
    public static final String COLUMN_AYAH_NUMBER = "ayah_number";
    public static final String COLUMN_SURA = "sura";
    public static final String COLUMN_PLAY_COUNT = "play_count";
    public static final String COLUMN_ID = "_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AYAH + " TEXT NOT NULL, " +
                COLUMN_AYAH_NUMBER + " INTEGER NOT NULL, " +
                COLUMN_SURA + " TEXT NOT NULL, " +
                COLUMN_PLAY_COUNT + " INTEGER DEFAULT 0)";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // For now, there is no need to upgrade the database.
    }

    public void addAyah(String ayah, int ayahNumber, String surah) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AYAH, ayah);
        values.put(COLUMN_AYAH_NUMBER, ayahNumber);
        values.put(COLUMN_SURA, surah);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Map<String, Object>> getAyah(int ayahNumber) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_AYAH_NUMBER + " = ?",
                new String[]{String.valueOf(ayahNumber)}, null, null, null);

        List<Map<String, Object>> results = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> row = new HashMap<>();
                row.put(COLUMN_AYAH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AYAH)));
                row.put(COLUMN_AYAH_NUMBER, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AYAH_NUMBER)));
                row.put(COLUMN_SURA, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURA)));
                row.put(COLUMN_PLAY_COUNT, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAY_COUNT)));
                results.add(row);
            }
            cursor.close();
        }
        db.close();
        return results;
    }

    public List<Map<String, Object>> getRandomAyahs(int count) {
        SQLiteDatabase db = getReadableDatabase();
        List<Map<String, Object>> results = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                    COLUMN_ID + " = (SELECT MAX(" + COLUMN_ID + ") * RANDOM() + 1 FROM " + TABLE_NAME + ")", null);

            if (cursor != null && cursor.moveToFirst()) {
                Map<String, Object> row = new HashMap<>();
                row.put(COLUMN_ID, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                row.put(COLUMN_AYAH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AYAH)));
                row.put(COLUMN_AYAH_NUMBER, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AYAH_NUMBER)));
                row.put(COLUMN_SURA, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURA)));
                row.put(COLUMN_PLAY_COUNT, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAY_COUNT)));
                results.add(row);
                cursor.close();
            }
        }
        db.close();
        return results;
    }

    public void incrementPlayCount(int ayahNumber) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + COLUMN_PLAY_COUNT +
                " = " + COLUMN_PLAY_COUNT + " + 1 WHERE " + COLUMN_AYAH_NUMBER + " = " + ayahNumber);
        db.close();
    }

    public boolean isTableContainingAtLeast2Records() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        boolean hasAtLeast2Records = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                hasAtLeast2Records = count > 2;
            }
            cursor.close();
        }
        db.close();
        return hasAtLeast2Records;
    }
}
