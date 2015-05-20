package com.kvest.odessatoday.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.05.14
 * Time: 22:38
 * To change this template use File | Settings | File Templates.
 */
class TodaySQLStorage extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "odessa_today.db";

    private static final int DATABASE_VERSION_V1 = 101;  // 1.0
    private static final int DATABASE_VERSION = DATABASE_VERSION_V1;

    public TodaySQLStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Films.CREATE_TABLE_SQL);
        db.execSQL(FilmsTimetable.CREATE_TABLE_SQL);
        db.execSQL(Cinemas.CREATE_TABLE_SQL);
        db.execSQL(Comments.CREATE_TABLE_SQL);
        db.execSQL(AnnouncementsMetadata.CREATE_TABLE_SQL);
        db.execSQL(CinemaTimetableView.CREATE_VIEW_SQL);
        db.execSQL(AnnouncementFilmsView.CREATE_VIEW_SQL);
        db.execSQL(FilmsFullTimetableView.CREATE_VIEW_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Nothing to do
    }

//    @Override
//    public void onOpen(SQLiteDatabase db) {
//        super.onOpen(db);
//        if (!db.isReadOnly()) {
//            // Enable foreign key constraints
//            db.execSQL("PRAGMA foreign_keys=ON;");
//        }
//    }
}
