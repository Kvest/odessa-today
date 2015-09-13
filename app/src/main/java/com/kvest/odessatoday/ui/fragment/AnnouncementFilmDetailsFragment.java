package com.kvest.odessatoday.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.service.NetworkService;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.12.14
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public class AnnouncementFilmDetailsFragment extends BaseFilmDetailsFragment  implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String PREMIERE_DATE_FORMAT_PATTERN = "dd MMMM yyyy";
    private final SimpleDateFormat PREMIERE_DATE_FORMAT = new SimpleDateFormat(PREMIERE_DATE_FORMAT_PATTERN);
    private static final int FILM_LOADER_ID = 1;

    private TextView premiereDate;

    public static AnnouncementFilmDetailsFragment getInstance(long filmId) {
        Bundle arguments = new Bundle(1);
        arguments.putLong(ARGUMENT_FILM_ID, filmId);

        AnnouncementFilmDetailsFragment result = new AnnouncementFilmDetailsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.announcement_film_details_fragment, container, false);

        initFilmInfoView(rootView);

        return rootView;
    }

    @Override
    protected void initFilmInfoView(View view) {
        super.initFilmInfoView(view);

        premiereDate = (TextView)view.findViewById(R.id.premiere_date);
    }

    @Override
    public void setFilmData(Cursor cursor) {
        super.setFilmData(cursor);

        //generate data for share
        if (!cursor.isAfterLast()) {
            int premiereDateColumnIndex  = cursor.getColumnIndex(TodayProviderContract.Tables.AnnouncementFilmsView.Columns.PREMIERE_DATE);
            if (!cursor.isNull(premiereDateColumnIndex)) {
                long premiereDateValue = TimeUnit.SECONDS.toMillis(cursor.getLong(premiereDateColumnIndex));
                String text = getString(R.string.premiere_date, PREMIERE_DATE_FORMAT.format(premiereDateValue));

                premiereDate.setVisibility(View.VISIBLE);
                premiereDate.setText(Html.fromHtml(text));
            } else {
                premiereDate.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Request comments
        NetworkService.loadFilmComments(getActivity(), getFilmId());

        getLoaderManager().initLoader(FILM_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == FILM_LOADER_ID) {
            return DataProviderHelper.getAnnouncementFilmLoader(getActivity(), getFilmId(), null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case FILM_LOADER_ID :
                setFilmData(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing to do
    }
}
