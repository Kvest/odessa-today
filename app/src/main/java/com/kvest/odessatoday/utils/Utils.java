package com.kvest.odessatoday.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;

import com.kvest.odessatoday.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 18.08.14
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    private static final String POSTER_SEPARATOR = ",";

    public static String getDeviceId(Context context)
    {
        //Serial number
        String deviceId = Build.SERIAL;
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }

        //Android id
        deviceId = Settings.System.getString(context.getContentResolver(),Settings.System.ANDROID_ID);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }

        return "unknown";
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (ni != null && ni.isConnected());
    }

    public static Uri getImageContentUri(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    public static String getCertificateSignature(Context context){
        try {
            Signature[] sigs = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sigs[0].toByteArray());
            String result = new String(Base64.encode(md.digest(), 0));

            return result.trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createCommentsString(Context context, int commentsCount) {
        int tmp = commentsCount % 100;
        if (tmp >= 11 && tmp <= 19) {
            return context.getString(R.string.comments_count, commentsCount);
        }
        tmp %= 10;
        if (tmp == 1) {
            return context.getString(R.string.comments_count1, commentsCount);
        }
        if (tmp >= 2 && tmp <= 4) {
            return context.getString(R.string.comments_count2, commentsCount);
        }

        return context.getString(R.string.comments_count, commentsCount);
    }

    public static void setDrawablesColor(int color, Drawable... drawables) {
        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] != null) {
                drawables[i].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    public static String posters2String(String[] posters) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0 ; i < posters.length; ++i) {
            if (i > 0) {
                builder.append(POSTER_SEPARATOR);
            }
            builder.append(posters[i]);
        }

        return builder.toString();
    }

    public static String[] string2Posters(String value) {
        if (!TextUtils.isEmpty(value)) {
            return value.split(POSTER_SEPARATOR);
        } else {
            return new String[0];
        }
    }

    public static String saveDrawable(Context context, Drawable drawable, String fileName) {
        String result = null;

        if (drawable != null) {
            Rect bounds = drawable.getBounds();
            Bitmap bitmap = Bitmap.createBitmap(bounds.width(),bounds.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);
            OutputStream out = null;
            try {
                File file = new File(context.getExternalCacheDir(), fileName);
                if (!file.exists()) {
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                }
                result = file.getAbsolutePath();
            } catch (IOException ioException) {
            } finally {
                if ( out != null ){
                    try {
                        out.close();
                    } catch (IOException e) {}
                }
            }
        }

        return result;
    }
}
