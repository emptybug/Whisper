package com.rabbee.whisper.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.rabbee.whisper.net.FileReceiveService;
import com.rabbee.whisper.obj.DataProtocol;
import com.rabbee.whisper.obj.DataStream;
import com.rabbee.whisper.obj.FileInfo;
import com.rabbee.whisper.obj.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Rabbee on 2016/7/12.
 */
public class FileUtils {

//    private static String mainDir = "/Whisper/";

    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }

        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean saveOneFile(Context context, String filename)
    {
        try{
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_APPEND);
            Log.d("FileUtils", "saveOneFile: 写入文件"  + filename);
            PrintStream ps = new PrintStream(fos);
            ps.write("Hello File!".getBytes());
            ps.close();
            return true;
        }
        catch (FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return false;
    }

}
