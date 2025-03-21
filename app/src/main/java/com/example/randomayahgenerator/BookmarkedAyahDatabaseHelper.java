package com.example.randomayahgenerator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class BookmarkedAyahDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "quran_data.db";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "ayahs";
    public static final String COLUMN_AYAH = "ayah";
    public static final String COLUMN_AYAH_NUMBER = "ayah_number";
    public static final String COLUMN_SURAH = "sura";
    public static final String COLUMN_SURAH_NUMBER = "surah_number";
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
                COLUMN_SURAH + " TEXT NOT NULL, " +
                COLUMN_SURAH_NUMBER + " INTEGER DEFAULT 0," +
                COLUMN_PLAY_COUNT + " INTEGER DEFAULT 0)";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_SURAH_NUMBER + " INTEGER DEFAULT 0");
            populateSuraNumber(db);
        }
    }

    private void populateSuraNumber(SQLiteDatabase db) {
        try (QuranDatabaseHelper quranHelper = new QuranDatabaseHelper(context)) {
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String surahName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURAH));
                    int surahNumber = getSurahNumber(quranHelper, surahName);
                    int ayahId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_SURAH_NUMBER, surahNumber);
                    db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(ayahId)});
                }
                cursor.close();
            }
        }
    }

    private int getSurahNumber(QuranDatabaseHelper quranHelper, String surahName) {
        SQLiteDatabase db = quranHelper.getReadableDatabase();
        String[] columns = new String[]{QuranDatabaseHelper.COLUMN_SURAH_NUMBER};
        String[] selectionArgs = new String[]{surahName};
        String selection = QuranDatabaseHelper.COLUMN_SURAH + " = ?";
        Cursor cursor = db.query(
                QuranDatabaseHelper.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        int surahNumber = 0;
        if (cursor != null && cursor.moveToFirst()) {
            surahNumber = cursor.getInt(cursor.getColumnIndexOrThrow(QuranDatabaseHelper.COLUMN_SURAH_NUMBER));
            cursor.close();
        }
        db.close();
        return surahNumber;
    }

    public boolean addAyah(String ayah, int ayahNumber, String surah) {
        SQLiteDatabase db = getWritableDatabase();
        String[] selectionArgs = new String[]{ayah, String.valueOf(ayahNumber), surah};
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COLUMN_AYAH + " = ? AND " + COLUMN_AYAH_NUMBER + " = ? AND " + COLUMN_SURAH + " = ?",
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
            values.put(COLUMN_SURAH, surah);
            db.insert(TABLE_NAME, null, values);
            return true;
        }

        cursor.close();
        db.close();
        return false;
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
                result.put(COLUMN_SURAH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURAH)));
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

        List<Integer> allIds = getAllIds(db);
        if (allIds.isEmpty()) {
            db.close();
            return results;
        }

        Set<Integer> selectedIds = getRandomIds(allIds, count);
        String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " IN (" + getPlaceHolders(selectedIds) + ")";
        String[] selectionArgs = getSelectionArgs(selectedIds);
        Cursor cursor = db.rawQuery(sqlQuery, selectionArgs);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> row = new HashMap<>();
                row.put(COLUMN_ID, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                row.put(COLUMN_AYAH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AYAH)));
                row.put(COLUMN_AYAH_NUMBER, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AYAH_NUMBER)));
                row.put(COLUMN_SURAH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURAH)));
                row.put(COLUMN_SURAH_NUMBER, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SURAH_NUMBER)));
                row.put(COLUMN_PLAY_COUNT, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAY_COUNT)));
                results.add(row);
            }
            cursor.close();
        }

        db.close();
        return results;
    }

    private List<Integer> getAllIds(SQLiteDatabase db) {
        List<Integer> allIds = new ArrayList<>();
        Cursor idCursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_NAME, null);

        if (idCursor != null) {
            while (idCursor.moveToNext()) {
                allIds.add(idCursor.getInt(0));
            }
            idCursor.close();
        }
        return allIds;
    }

    private Set<Integer> getRandomIds(List<Integer> allIds, int count) {
        Set<Integer> selectedIds = new HashSet<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            Integer randomId;
            do {
                int randomIndex = random.nextInt(allIds.size());
                randomId = allIds.get(randomIndex);
            } while (selectedIds.contains(randomId));

            selectedIds.add(randomId);
        }
        return selectedIds;
    }

    private String getPlaceHolders(Set<Integer> selectedIds) {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < selectedIds.size(); i++) {
            placeholders.append(i == 0 ? "?" : ",?");
        }
        return placeholders.toString();
    }

    private String[] getSelectionArgs(Set<Integer> selectedIds) {
        String[] selectionArgs = new String[selectedIds.size()];
        int index = 0;
        for (Integer id : selectedIds) {
            selectionArgs[index++] = String.valueOf(id);
        }
        return selectionArgs;
    }

    public List<Map<String, Object>> getAllAyahs(int numberOfRows, int offset) {
        SQLiteDatabase db = getReadableDatabase();
        List<Map<String, Object>> results = new ArrayList<>();

        String orderBy = COLUMN_SURAH_NUMBER + " ASC";
        String limit = offset + "," + numberOfRows;
        if (numberOfRows == -1 || offset == -1) {
            limit = null;
        }
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, orderBy, limit);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> row = new HashMap<>();
                row.put(COLUMN_ID, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                row.put(COLUMN_AYAH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AYAH)));
                row.put(COLUMN_AYAH_NUMBER, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AYAH_NUMBER)));
                row.put(COLUMN_SURAH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURAH)));
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
                " AND " + COLUMN_SURAH + " = '" + surah + "'";
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

    public List<Map<String, Object>> getMostPlayedAyahs(int count) {
        SQLiteDatabase db = getReadableDatabase();
        List<Map<String, Object>> results = new ArrayList<>();

        String sqlQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_PLAY_COUNT + " DESC LIMIT " + count;
        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> row = new HashMap<>();
                row.put(COLUMN_ID, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                row.put(COLUMN_AYAH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AYAH)));
                row.put(COLUMN_AYAH_NUMBER, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AYAH_NUMBER)));
                row.put(COLUMN_SURAH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURAH)));
                row.put(COLUMN_PLAY_COUNT, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAY_COUNT)));
                results.add(row);
            }
        }

        cursor.close();
        db.close();
        return results;
    }

    public List<Map<String, Object>> getMostPlayedSurah(int count) {
        SQLiteDatabase db = getReadableDatabase();
        List<Map<String, Object>> results = new ArrayList<>();

        String sqlQuery = "SELECT "
                + COLUMN_SURAH + ", SUM("
                + COLUMN_PLAY_COUNT + ") AS " + COLUMN_PLAY_COUNT
                + " FROM " + TABLE_NAME
                + " GROUP BY " + COLUMN_SURAH
                + " ORDER BY " + COLUMN_PLAY_COUNT + " DESC LIMIT " + count;

        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> row = new HashMap<>();
                row.put(COLUMN_SURAH, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURAH)));
                row.put(COLUMN_PLAY_COUNT, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAY_COUNT)));
                results.add(row);
            }
        }

        cursor.close();
        db.close();
        return results;
    }

    public int getTotalPlayCount() {
        SQLiteDatabase db = getReadableDatabase();
        String sqlQuery = "SELECT SUM(" + COLUMN_PLAY_COUNT + ") FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sqlQuery, null);

        int totalPlayCount = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                totalPlayCount = cursor.getInt(0);
            }
            cursor.close();
        }

        db.close();
        return totalPlayCount;
    }

    public void addFullSurah(String surah) {
        try (QuranDatabaseHelper quranDatabaseHelper = new QuranDatabaseHelper(this.context)) {
            Map<String, List<Integer>> surahAyahMapping = quranDatabaseHelper.getSurahAyahMapping();
            List<Integer> ayahNumbers = surahAyahMapping.get(surah);
            if (ayahNumbers == null) {
                return;
            }

            boolean isSurahAlreadyAdded = true;
            for (Integer ayahNumber : ayahNumbers) {
                String ayahText = quranDatabaseHelper.getAyahText(surah, ayahNumber);
                // addAyah will take care of the fact that an ayah can already be in the database
                boolean isAdded = addAyah(ayahText, ayahNumber, surah);
                if (isAdded) {
                    isSurahAlreadyAdded = false;
                }
            }

            if (isSurahAlreadyAdded) {
                Toast.makeText(this.context, "Surah already exists!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.context, "Surah added successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int getCountOfAyahs() {
        SQLiteDatabase db = getReadableDatabase();
        String sqlQuery = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sqlQuery, null);

        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return count;
    }
}
