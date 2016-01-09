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

    public static String images2String(String[] posters) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0 ; i < posters.length; ++i) {
            if (i > 0) {
                builder.append(POSTER_SEPARATOR);
            }
            builder.append(posters[i]);
        }

        return builder.toString();
    }

    public static String[] string2Images(String value) {
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

    public static int eventType2CommentTargetType(int eventType) {
        switch (eventType) {
            case Constants.EventType.CONCERT:
                return Constants.CommentTargetType.CONCERT;
            case Constants.EventType.PARTY:
                return Constants.CommentTargetType.PARTY;
            case Constants.EventType.SPECTACLE:
                return Constants.CommentTargetType.SPECTACLE;
            case Constants.EventType.EXHIBITION:
                return Constants.CommentTargetType.EXHIBITION;
            case Constants.EventType.SPORT:
                return Constants.CommentTargetType.SPORT;
            case Constants.EventType.WORKSHOP:
                return Constants.CommentTargetType.WORKSHOP;
            default:
                return Constants.CommentTargetType.UNKNOWN;
        }
    }

    public static int placeType2CommentTargetType(int placeType) {
        switch (placeType) {
            case Constants.PlaceType.THEATRE:
                return Constants.CommentTargetType.THEATRE;
            case Constants.PlaceType.CONCERT_HALL:
                return Constants.CommentTargetType.CONCERT_HALL;
            case Constants.PlaceType.CLUB:
                return Constants.CommentTargetType.CLUB;
            case Constants.PlaceType.MUSEUM:
                return Constants.CommentTargetType.MUSEUM;
            case Constants.PlaceType.GALLERY:
                return Constants.CommentTargetType.GALLERY;
            case Constants.PlaceType.ZOO:
                return Constants.CommentTargetType.ZOO;
            case Constants.PlaceType.QUEST:
                return Constants.CommentTargetType.QUEST;
            case Constants.PlaceType.RESTAURANT:
                return Constants.CommentTargetType.RESTAURANT;
            case Constants.PlaceType.CAFE:
                return Constants.CommentTargetType.CAFE;
            case Constants.PlaceType.PIZZA:
                return Constants.CommentTargetType.PIZZA;
            case Constants.PlaceType.SUSHI:
                return Constants.CommentTargetType.SUSHI;
            case Constants.PlaceType.KARAOKE:
                return Constants.CommentTargetType.KARAOKE;
            case Constants.PlaceType.SKATING_RINK:
                return Constants.CommentTargetType.SKATING_RINK;
            case Constants.PlaceType.BOWLING:
                return Constants.CommentTargetType.BOWLING;
            case Constants.PlaceType.BILLIARD:
                return Constants.CommentTargetType.BILLIARD;
            case Constants.PlaceType.SAUNA:
                return Constants.CommentTargetType.SAUNA;
            case Constants.PlaceType.BATH:
                return Constants.CommentTargetType.BATH;
            default:
                return Constants.CommentTargetType.UNKNOWN;
        }
    }

    public static String eventType2String(Context context, int eventType) {
        switch (eventType) {
            case Constants.EventType.CONCERT:
                return context.getString(R.string.menu_concert);
            case Constants.EventType.PARTY:
                return context.getString(R.string.menu_party);
            case Constants.EventType.SPECTACLE:
                return context.getString(R.string.menu_spectacle);
            case Constants.EventType.EXHIBITION:
                return context.getString(R.string.menu_exhibition);
            case Constants.EventType.SPORT:
                return context.getString(R.string.menu_sport);
            case Constants.EventType.WORKSHOP:
                return context.getString(R.string.menu_workshop);
            default:
                return "";
        }
    }

    public static String placeType2String(Context context, int placeType) {
        switch (placeType) {
            case Constants.PlaceType.THEATRE:
                return context.getString(R.string.menu_theatre);
            case Constants.PlaceType.CONCERT_HALL:
                return context.getString(R.string.menu_concert_hall);
            case Constants.PlaceType.CLUB:
                return context.getString(R.string.menu_club);
            case Constants.PlaceType.MUSEUM:
                return context.getString(R.string.menu_museum);
            case Constants.PlaceType.GALLERY:
                return context.getString(R.string.menu_gallery);
            case Constants.PlaceType.ZOO:
                return context.getString(R.string.menu_zoo);
            case Constants.PlaceType.QUEST:
                return context.getString(R.string.menu_quest);
            case Constants.PlaceType.RESTAURANT:
                return context.getString(R.string.menu_restaurant);
            case Constants.PlaceType.CAFE:
                return context.getString(R.string.menu_cafe);
            case Constants.PlaceType.PIZZA:
                return context.getString(R.string.menu_pizza);
            case Constants.PlaceType.SUSHI:
                return context.getString(R.string.menu_sushi);
            case Constants.PlaceType.KARAOKE:
                return context.getString(R.string.menu_karaoke);
            case Constants.PlaceType.SKATING_RINK:
                return context.getString(R.string.menu_skating_rink);
            case Constants.PlaceType.BOWLING:
                return context.getString(R.string.menu_bowling);
            case Constants.PlaceType.BILLIARD:
                return context.getString(R.string.menu_billiard);
            case Constants.PlaceType.SAUNA:
                return context.getString(R.string.menu_sauna);
            case Constants.PlaceType.BATH:
                return context.getString(R.string.menu_bath);
            default:
                return "";
        }
    }
}
