package com.example.randomayahgenerator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class QuranDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "quraan.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;

    public QuranDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        copyDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // No need to implement this, since we are reading the database from the assets folder
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now, there is no need to upgrade the database.
    }

    private void copyDatabase() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                InputStream myInput = context.getAssets().open(DATABASE_NAME);
                String outFileName = dbFile.getAbsolutePath();
                OutputStream myOutput = new FileOutputStream(outFileName);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();

                Log.d("Database", "Database copied successfully");
            } catch (IOException e) {
                Log.e("Database", "Error copying database", e);
                throw new Error("Error copying database");
            }
        } else {
            Log.d("Database", "Database already exists");
        }
    }

    public List<String> getListOfSurahNames() {
        List<String> surahNames = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                "data",
                new String[]{"Surah"},
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String surahName = cursor.getString(cursor.getColumnIndexOrThrow("Surah"));
                if (!surahNames.contains(surahName)) {
                    surahNames.add(surahName);
                }
            }
            cursor.close();
        }
        db.close();
        return surahNames;
    }

    public Map<String, List<Integer>> getSurahAyahMapping() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                "data",
                null,
                null,
                null,
                null,
                null,
                null
        );

        Map<String, List<Integer>> surahAyahMap = new HashMap<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String surah = cursor.getString(cursor.getColumnIndexOrThrow("Surah"));
                int ayahNumber = cursor.getInt(cursor.getColumnIndexOrThrow("AyahNumber"));

                if (!surahAyahMap.containsKey(surah)) {
                    surahAyahMap.put(surah, new ArrayList<>());
                }

                List<Integer> ayahNumbers = surahAyahMap.get(surah);
                if (ayahNumbers != null && !ayahNumbers.contains(ayahNumber)) {
                    ayahNumbers.add(ayahNumber);
                }
            }
            cursor.close();
        }
        db.close();
        return surahAyahMap;
    }

    public String getAyahText(String surah, int ayahNumber) {
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = new String[]{surah, String.valueOf(ayahNumber)};

        Cursor cursor = db.query(
                "data",
                null,
                "Surah = ? AND AyahNumber = ?",
                selectionArgs,
                null,
                null,
                null
        );

        String ayahText = "";
        if (cursor != null && cursor.moveToFirst()) {
            ayahText = cursor.getString(cursor.getColumnIndexOrThrow("Ayah"));
        }
        cursor.close();
        db.close();

        return ayahText;
    }
}
