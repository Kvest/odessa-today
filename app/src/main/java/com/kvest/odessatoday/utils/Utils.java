package com.kvest.odessatoday.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 18.08.14
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    public static String getDeviceId(Context context)
    {
        //Serial number
        String deviceId = Build.SERIAL;
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }

        //IMEI
        TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tManager.getDeviceId();
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
}
