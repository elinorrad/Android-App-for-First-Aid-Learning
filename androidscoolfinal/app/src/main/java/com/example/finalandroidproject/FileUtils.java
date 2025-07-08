package com.example.finalandroidproject;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Utility class for file-related operations.
 * Used to extract file paths from URIs selected via file picker.
 */
public class FileUtils {

    /**
     * Copies the contents of the given Uri to a temporary file
     * in the app's cache directory and returns the file path.
     *
     * INPUT:
     * - context: The context used to access content resolver and cache directory
     * - uri: The Uri of the selected file (e.g. from file picker)
     *
     * OUTPUT:
     * - Absolute path to a temporary copy of the file (String)
     * - Returns null if the Uri is invalid or an error occurs
     */
    public static String getPath(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File tempFile = new File(context.getCacheDir(), getFileName(context, uri));
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extracts the file name from a Uri.
     * Supports both "content://" and "file://" schemes.
     *
     * INPUT:
     * - context: The context used to query the Uri
     * - uri: The Uri to extract the file name from
     *
     * OUTPUT:
     * - The file name as a String
     */
    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
