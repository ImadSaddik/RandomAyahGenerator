package com.example.randomayahgenerator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BookmarkedAyahDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "quran_data.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "ayahs";
    public static final String COLUMN_AYAH = "ayah";
    public static final String COLUMN_AYAH_NUMBER = "ayah_number";
    public static final String COLUMN_SURA = "sura";
    public static final String COLUMN_PLAY_COUNT = "play_count";
    public static final String COLUMN_ID = "_id";
    private Context context;

    public BookmarkedAyahDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
        String[] selectionArgs = new String[]{ayah, String.valueOf(ayahNumber), surah};
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COLUMN_AYAH + " = ? AND " + COLUMN_AYAH_NUMBER + " = ? AND " + COLUMN_SURA + " = ?",
                selectionArgs,
                null,
                null,
                null
        );

        boolean ifRowExists = cursor.getCount() != 0;
        if (!ifRowExists) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_AYAH, ayah);
            values.put(COLUMN_AYAH_NUMBER, ayahNumber);
            values.put(COLUMN_SURA, surah);
            db.insert(TABLE_NAME, null, values);
            Toast.makeText(this.context, "Ayah added successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.context, "Ayah already exists!", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    public Map<String, Object> getAyahByID(int ayahID) {
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = new String[]{String.valueOf(ayahID)};
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COLUMN_ID + " = ?",
                selectionArgs,
                null,
                null,
                null
        );

        Map<String, Object> result = new HashMap<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.put(COLUMN_AYAH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AYAH)));
                result.put(COLUMN_AYAH_NUMBER, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AYAH_NUMBER)));
                result.put(COLUMN_SURA, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURA)));
                result.put(COLUMN_PLAY_COUNT, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAY_COUNT)));
            }
            cursor.close();
        }
        db.close();
        return result;
    }

    public List<Map<String, Object>> getRandomAyahs(int count) {
        SQLiteDatabase db = getReadableDatabase();
        List<Map<String, Object>> results = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String sqlQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY RANDOM() LIMIT " + count;
            Cursor cursor = db.rawQuery(sqlQuery, null);

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

    public List<Map<String, Object>> getAllAyahs() {
        SQLiteDatabase db = getReadableDatabase();
        List<Map<String, Object>> results = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> row = new HashMap<>();
                row.put(COLUMN_ID, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
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

    public void incrementPlayCount(String surah, int ayahNumber) {
        SQLiteDatabase db = getWritableDatabase();
        String sqlQuery = "UPDATE " + TABLE_NAME +
                " SET " + COLUMN_PLAY_COUNT + " = " + COLUMN_PLAY_COUNT + " + 1" +
                " WHERE " + COLUMN_AYAH_NUMBER + " = " + ayahNumber +
                " AND " + COLUMN_SURA + " = '" + surah + "'";
        db.execSQL(sqlQuery);
        db.close();
    }

    public boolean isTableContainingAtLeast2Records() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        boolean hasAtLeast2Records = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                hasAtLeast2Records = count > 1;
            }
            cursor.close();
        }
        db.close();
        return hasAtLeast2Records;
    }

    public void deleteAllRowsFromTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public void deleteRowById(int rowId) {
        SQLiteDatabase db = getWritableDatabase();
        String[] selectedArgs = new String[]{String.valueOf(rowId)};
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", selectedArgs);
        db.close();
    }
}
