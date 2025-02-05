package com.example.randomayahgenerator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseUtils {
    public static void triggerSaveDatabase(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/x-sqlite3");

        String destinationFileName = BookmarkedAyahDatabaseHelper.DATABASE_NAME;
        intent.putExtra(Intent.EXTRA_TITLE, destinationFileName);

        activity.startActivityForResult(intent, ResultCodes.REQUEST_CODE_SAVE_DATABASE);
    }

    public static void saveDatabase(Activity activity, Uri backupUri) {
        File currentDB = activity.getDatabasePath(BookmarkedAyahDatabaseHelper.DATABASE_NAME);

        if (currentDB.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(currentDB);
                OutputStream outputStream = activity.getContentResolver().openOutputStream(backupUri);

                copyFile(inputStream, outputStream);

                inputStream.close();
                outputStream.close();

                Toast.makeText(activity, "Database saved successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Database save failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static void triggerLoadDatabase(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"application/x-sqlite3", "application/octet-stream"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        activity.startActivityForResult(intent, ResultCodes.REQUEST_CODE_LOAD_DATABASE);
    }

    public static void loadDatabase(Activity activity, Uri backupUri) {
        File currentDB = activity.getDatabasePath(BookmarkedAyahDatabaseHelper.DATABASE_NAME);

        if (currentDB.exists()) {
            try {
                InputStream inputStream = activity.getContentResolver().openInputStream(backupUri);
                OutputStream outputStream = new FileOutputStream(currentDB);

                copyFile(inputStream, outputStream);

                inputStream.close();
                outputStream.close();

                Toast.makeText(activity, "Database loaded successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Database load failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
