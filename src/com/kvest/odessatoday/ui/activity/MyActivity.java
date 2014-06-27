package com.kvest.odessatoday.ui.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.request.GetCinemasRequest;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.fragment.FilmsFragment;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MyActivity extends TodayBaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
                                                             DatePickerDialog.OnDateSetListener {
    private static final int START_YEAR = 1900;
    private static final int SELECT_DATE_DIALOG_ID = 0;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.select_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(SELECT_DATE_DIALOG_ID);
            }
        });
        test();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                FilmsFragment filmsFragment = FilmsFragment.getInstance(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()), true);
                transaction.add(R.id.fragment_container, filmsFragment);
            } finally {
                transaction.commit();
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == SELECT_DATE_DIALOG_ID) {
            return new DatePickerDialog(this,  this, 2014, 5, 30);
        }

        return super.onCreateDialog(id);
    }

    private void printTable(SQLiteDatabase db, String tableName) {
        Cursor c = db.query(tableName, null, null, null, null, null, null);
        try {
            printCursor(c);
        } finally {
            c.close();
        }
    }

    private void printCursor(Cursor c) {
        String s = "";
        Log.d("KVEST_TAG", "----------------------------------");
        for (int i = 0; i < c.getColumnCount(); ++i) {
            s += c.getColumnName(i) + "\t";
        }
        Log.d("KVEST_TAG", s);
        Log.d("KVEST_TAG", "----------------------------------");
        c.moveToFirst();

        while (!c.isAfterLast()) {
            s= "";
            for (int i = 0; i < c.getColumnCount(); ++i) {
                s += c.getString(i) + "\t";
            }
            Log.d("KVEST_TAG", s);
            c.moveToNext();
        }
        Log.d("KVEST_TAG", "----------------------------------");
    }

    private void addFilm(SQLiteDatabase db, int id, String name) {
        ContentValues cv = new ContentValues();
        cv.put(TodayProviderContract.Tables.Films.Columns.FILM_ID, id);
        cv.put(TodayProviderContract.Tables.Films.Columns.NAME, name);
        db.insert(TodayProviderContract.Tables.Films.TABLE_NAME, null, cv);
    }

    private void test() {
//        GetCinemasRequest req = new GetCinemasRequest(null, null);
//        TodayApplication.getApplication().getVolleyHelper().addRequest(req);
//        NetworkService.loadTodayFilms(this);
//        TodaySQLStorage sqlStorage = new TodaySQLStorage(this);
//        SQLiteDatabase db = sqlStorage.getWritableDatabase();
//        db.delete(TodayProviderContract.Tables.Films.TABLE_NAME, null, null);
//        db.delete(TodayProviderContract.Tables.FilmsTimetable.TABLE_NAME, null, null);
//        printTable(db, TodayProviderContract.Tables.Films.TABLE_NAME);
//        printTable(db, TodayProviderContract.Tables.FilmsTimetable.TABLE_NAME);
//
//        addFilm(db, 2, "Film 2");
//
//        for (int i = 0; i < 2; ++i) {
//            ContentValues cv = new ContentValues();
//            cv.put(TodayProviderContract.Tables.FilmsTimetable.Columns.CINEMA_ID, i);
//            cv.put(TodayProviderContract.Tables.FilmsTimetable.Columns.DATE, 100);
//            cv.put(TodayProviderContract.Tables.FilmsTimetable.Columns.FILM_ID, 2);
//            cv.put(TodayProviderContract.Tables.FilmsTimetable.Columns.PRICES, "PRICES" + i);
//            cv.put(TodayProviderContract.Tables.FilmsTimetable.Columns.FORMAT, "FORMAT" + i);
//            db.insert(TodayProviderContract.Tables.FilmsTimetable.TABLE_NAME, null, cv);
//        }
//        printTable(db, TodayProviderContract.Tables.Films.TABLE_NAME);
//        printTable(db, TodayProviderContract.Tables.FilmsTimetable.TABLE_NAME);
//
//        ContentValues cv = new ContentValues();
//        cv.put(TodayProviderContract.Tables.Films.Columns.FILM_ID, 7);
//        cv.put(TodayProviderContract.Tables.Films.Columns.NAME, "new name");
//        db.update(TodayProviderContract.Tables.Films.TABLE_NAME,  cv,null, null);
//        printTable(db, TodayProviderContract.Tables.Films.TABLE_NAME);
//        printTable(db, TodayProviderContract.Tables.FilmsTimetable.TABLE_NAME);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                RequestFuture<GetFilmsResponse> future = RequestFuture.newFuture();
//                GetFilmsRequest req = new GetFilmsRequest(future, future);
//                TodayApplication.getApplication().getVolleyHelper().addRequest(req);
//
//                try {
//                    GetFilmsResponse response = future.get(); // this will block
//                    response.isSuccessful();
//                } catch (InterruptedException e) {
//                    // exception handling
//                } catch (ExecutionException e) {
//                    // exception handling
//                    e.getLocalizedMessage();
//                }
//            }
//        }).start();


//        GetFilmsRequest req = new GetFilmsRequest(new Response.Listener<GetFilmsResponse>() {
//            @Override
//            public void onResponse(GetFilmsResponse response) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
//        TodayApplication.getApplication().getVolleyHelper().addRequest(req);



//        Uri filmsUri = Uri.withAppendedPath(TodayProviderContract.BASE_CONTENT_URI, TodayProviderContract.FILMS_PATH);
//        getContentResolver().delete(filmsUri, null, null);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss");
//        Date d = new Date(1401310800000L);
//        Log.d("KVEST_TAG", "d=" + sdf.format(d));
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        if (savedInstanceState == null) {
//            getLoaderManager().initLoader(1, null, this);
//        }
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, TodayProviderContract.FILMS_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("KVEST_TAG", "new cursor = " + data.getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("KVEST_TAG", "reset");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //calculate date
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        long date = cal.getTimeInMillis();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            FilmsFragment filmsFragment = FilmsFragment.getInstance(TimeUnit.MILLISECONDS.toSeconds(date), false);
            transaction.replace(R.id.fragment_container, filmsFragment);
        } finally {
            transaction.commit();
        }
    }
}
