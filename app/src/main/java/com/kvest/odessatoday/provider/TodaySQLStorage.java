package com.kvest.odessatoday.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.kvest.odessatoday.utils.LogUtils.LOGD;
import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.05.14
 * Time: 22:38
 * To change this template use File | Settings | File Templates.
 */
class TodaySQLStorage extends SQLiteOpenHelper {
    private static final String TAG = "TodaySQLStorage";
    private static final String DATABASE_NAME = "odessa_today.db";

    private static final int DATABASE_VERSION_V1 = 101;  // 0.4
    private static final int DATABASE_VERSION_V0_5 = 102;  // 0.5
    private static final int DATABASE_VERSION = DATABASE_VERSION_V0_5;

    public TodaySQLStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Films.CREATE_TABLE_SQL);
        db.execSQL(FilmsTimetable.CREATE_TABLE_SQL);
        db.execSQL(Cinemas.CREATE_TABLE_SQL);
        db.execSQL(Comments.CREATE_TABLE_SQL);
        db.execSQL(CommentsRating.CREATE_TABLE_SQL);
        db.execSQL(AnnouncementsMetadata.CREATE_TABLE_SQL);
        db.execSQL(CinemaTimetableView.CREATE_VIEW_SQL);
        db.execSQL(AnnouncementFilmsView.CREATE_VIEW_SQL);
        db.execSQL(FilmsFullTimetableView.CREATE_VIEW_SQL);
        db.execSQL(Places.CREATE_TABLE_SQL);
        db.execSQL(Events.CREATE_TABLE_SQL);
        db.execSQL(EventsTimetable.CREATE_TABLE_SQL);
        db.execSQL(EventsTimetableView.CREATE_VIEW_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGD(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
        if (oldVersion < DATABASE_VERSION_V0_5) {
            //upgrade cinemas table
            db.execSQL(Cinemas.DROP_TABLE_SQL);
            db.execSQL(Cinemas.CREATE_TABLE_SQL);

            db.execSQL(CommentsRating.CREATE_TABLE_SQL);
            db.execSQL(Places.CREATE_TABLE_SQL);
            db.execSQL(Events.CREATE_TABLE_SQL);
            db.execSQL(EventsTimetable.CREATE_TABLE_SQL);
            db.execSQL(EventsTimetableView.CREATE_VIEW_SQL);
        }
    }
}
