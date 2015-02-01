package com.kvest.odessatoday.ui.fragment;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.activity.CinemaDetailsActivity;
import com.kvest.odessatoday.ui.adapter.TimetableAdapter;
import com.kvest.odessatoday.utils.TimeUtils;
import com.kvest.odessatoday.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 19.07.14
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class FilmDetailsFragment extends BaseFilmDetailsFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARGUMENT_TIMETABLE_DATE = "com.kvest.odessatoday.argument.TIMETABLE_DATE";

    private static final String MIN_MAX_PRICES_SEPARATOR = "-";
    private static final Pattern PRICES_PATTERN = Pattern.compile("(\\d+)");
    private static final int PRICES_GROUP = 1;

    private static final String DATE_FORMAT_PATTERN = " dd MMM. yyyy, ";
    private static final String WEEK_DAY_FORMAT_PATTERN = "cccc";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    private static final SimpleDateFormat WEEK_DAY_FORMAT = new SimpleDateFormat(WEEK_DAY_FORMAT_PATTERN);

    private static final int FILM_LOADER_ID = 1;
    private static final int TIMETABLE_LOADER_ID = 2;

    private ImageView minMaxPricesIcon;
    private TextView minMaxPricesView;
    private TimetableAdapter timetableAdapter;
    private String shareTitle, shareText;

    private TextView isToday;
    private TextView dateTextView;
    private TextView weekDayTextView;
    private long shownTimetableDate;

    public static FilmDetailsFragment getInstance(long filmId, long timetableDate) {
        Bundle arguments = new Bundle(2);
        arguments.putLong(ARGUMENT_FILM_ID, filmId);
        arguments.putLong(ARGUMENT_TIMETABLE_DATE, timetableDate);

        FilmDetailsFragment result = new FilmDetailsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.film_details_fragment, container, false);
        View headerView = inflater.inflate(R.layout.film_details_header, null);

        shownTimetableDate = getTimetableDate();

        initFilmInfoView(headerView);
        initTimetableList((ListView) rootView, headerView);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.film_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                new CacheImageAsyncTask(Long.toString(getFilmId())).execute(filmPoster.getDrawable());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initFilmInfoView(View view) {
        super.initFilmInfoView(view);

        minMaxPricesIcon = (ImageView) view.findViewById(R.id.min_max_prices_icon);
        minMaxPricesView = (TextView)view.findViewById(R.id.min_max_prices);
        isToday = (TextView)view.findViewById(R.id.is_today);
        dateTextView = (TextView)view.findViewById(R.id.date);
        weekDayTextView = (TextView)view.findViewById(R.id.week_day);
    }

    private void initTimetableList(ListView rootView, View headerView) {
        rootView.addHeaderView(headerView);
        timetableAdapter = new TimetableAdapter(getActivity());
        rootView.setAdapter(timetableAdapter);

        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long cinemaId = timetableAdapter.getCinemaId(view, position, id);
                showCinema(cinemaId);
            }
        });

        //set date
        isToday.setVisibility(TimeUtils.isCurrentDay(shownTimetableDate) ? View.VISIBLE : View.GONE);
        dateTextView.setText(DATE_FORMAT.format(TimeUnit.SECONDS.toMillis(shownTimetableDate)));
        weekDayTextView.setText(WEEK_DAY_FORMAT.format(TimeUnit.SECONDS.toMillis(shownTimetableDate)).toLowerCase());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Request full timetable for the film and comments
        NetworkService.loadTimetable(getActivity(), getFilmId());
        NetworkService.loadFilmComments(getActivity(), getFilmId());

        getLoaderManager().initLoader(FILM_LOADER_ID, null, this);
        getLoaderManager().initLoader(TIMETABLE_LOADER_ID, null, this);
    }

    private long getTimetableDate() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_TIMETABLE_DATE, 0);
        } else {
            return 0;
        }
    }

    private void showCinema(long cinemaId) {
        startActivity(CinemaDetailsActivity.getStartIntent(getActivity(), cinemaId));
    }

    private void setMinMaxPrices(Cursor cursor) {
        String minMaxPrices = calculateMinMaxPrices(cursor);
        if (!TextUtils.isEmpty(minMaxPrices)) {
            minMaxPricesView.setText(minMaxPrices);

            minMaxPricesView.setVisibility(View.VISIBLE);
            minMaxPricesIcon.setVisibility(View.VISIBLE);
        } else {
            minMaxPricesView.setVisibility(View.GONE);
            minMaxPricesIcon.setVisibility(View.GONE);
        }
    }

    private String calculateMinMaxPrices(Cursor cursor) {
        //get column index
        int pricesColumnIndex = cursor.getColumnIndex(Tables.FilmsFullTimetableView.Columns.PRICES);
        if (pricesColumnIndex == -1) {
            return "";
        }

        //calculate min-max values
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Matcher matcher = PRICES_PATTERN.matcher(cursor.getString(pricesColumnIndex));
            while (matcher.find()) {
                int price = Integer.parseInt(matcher.group(PRICES_GROUP));
                min = Math.min(price, min);
                max = Math.max(price, max);
            }

            cursor.moveToNext();
        }

        if (min == Integer.MAX_VALUE && max == Integer.MIN_VALUE) {
            return "";
        }

        if (min == Integer.MAX_VALUE) {
            return Integer.toString(max);
        }

        if (max == Integer.MIN_VALUE) {
            return Integer.toString(min);
        }

        if (max == min) {
            return Integer.toString(max);
        }

        return Integer.toString(min) + MIN_MAX_PRICES_SEPARATOR + Integer.toString(max);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FILM_LOADER_ID :
                return DataProviderHelper.getFilmLoader(getActivity(), getFilmId(), null);
            case TIMETABLE_LOADER_ID :
                long endDate = TimeUtils.getEndOfTheDay(shownTimetableDate);
                return DataProviderHelper.getFilmsFullTimetableLoader(getActivity(), getFilmId(), shownTimetableDate, endDate,
                                                                  TimetableAdapter.PROJECTION, Tables.FilmsFullTimetableView.TIMETABLE_ORDER_ASC);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case FILM_LOADER_ID :
                setFilmData(cursor);
                break;
            case TIMETABLE_LOADER_ID :
                setMinMaxPrices(cursor);
                timetableAdapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case TIMETABLE_LOADER_ID :
                timetableAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void setFilmData(Cursor cursor) {
        super.setFilmData(cursor);

        //generate data for share
        if (!cursor.isAfterLast()) {
            String filmNameValue = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.NAME));
            shareTitle = filmNameValue;
            shareText = cursor.getString(cursor.getColumnIndex(Tables.Films.Columns.SHARE_TEXT));
        }
    }

    private void share(String imageFilePath) {
        Context context = getActivity();
        if (context != null) {
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND);
            sharingIntent.setType(imageFilePath != null ? "image/*" : "text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            if (imageFilePath != null) {
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Utils.getImageContentUri(context, imageFilePath));
            }
            startActivity(Intent.createChooser(sharingIntent, getResources().getText(R.string.share)));
        }
    }

    private class CacheImageAsyncTask extends AsyncTask<Drawable, Void, String> {
        //minimum 3 sec of the cachring process to avoid a blinking of the load dialog
        private static final long MIN_PROCESS_DURATION = 2000L;
        private static final String CACHE_IMAGE_FORMAT = ".png";

        private String fileName;
        private ProgressDialog progressDialog;

        public CacheImageAsyncTask(String fileName) {
            super();

            this.fileName = fileName + CACHE_IMAGE_FORMAT;
        }

        @Override
        protected void onPreExecute() {
            //show progress dialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.image_caching_progress));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String filePath) {
            //hide progress dialog
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            progressDialog = null;

            share(filePath);
        }

        @Override
        protected String doInBackground(Drawable... params) {
            long startTime = System.currentTimeMillis();

            String result = null;
            Drawable drawable = params[0];

            if (drawable != null) {
                Rect bounds = drawable.getBounds();
                Bitmap bitmap = Bitmap.createBitmap(bounds.width(),bounds.height(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.draw(canvas);
                OutputStream out = null;
                try {
                    File file = new File(getActivity().getExternalCacheDir(), fileName);
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

            //artificial delay to avoid a blinking of the load dialog
            long delayDuration = MIN_PROCESS_DURATION - (System.currentTimeMillis() - startTime);
            if (delayDuration > 0) {
                try {
                    Thread.sleep(delayDuration);
                } catch (InterruptedException e) {}
            }

            return result;
        }
    }
}
