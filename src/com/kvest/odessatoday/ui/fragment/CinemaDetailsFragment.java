package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.CinemasAdapter;
import com.kvest.odessatoday.utils.Constants;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
import static com.kvest.odessatoday.utils.LogUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 04.11.14
 * Time: 21:45
 * To change this template use File | Settings | File Templates.
 */
public class CinemaDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARGUMENT_CINEMA_ID = "com.kvest.odessatoday.argument.CINEMA_ID";
    public static final String[] COMMENTS_COUNT_PROJECTION = new String[]{Tables.Comments.COMMENTS_COUNT};
    private static final int CINEMA_LOADER_ID = 1;
    private static final int COMMENTS_COUNT_LOADER_ID = 2;

    private TextView cinemaName;
    private View phonesContainer;
    private TextView phones;
    private View addressContainer;
    private TextView address;
    private Button showComments;
    private Button showPhotos;
    private ListView timetableList;
    private String addressValue = "";

    private CinemaDetailsActionsListener cinemaDetailsActionsListener;

    public static CinemaDetailsFragment getInstance(long cinemaId) {
        Bundle arguments = new Bundle(1);
        arguments.putLong(ARGUMENT_CINEMA_ID, cinemaId);

        CinemaDetailsFragment result = new CinemaDetailsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cinema_details_fragment, container, false);
        View headerView = inflater.inflate(R.layout.cinema_details_header, null);

        init((ListView)rootView, headerView);

        return rootView;
    }

    private void init(ListView rootView, View headerView) {
        cinemaName = (TextView) headerView.findViewById(R.id.cinema_name);
        phonesContainer = headerView.findViewById(R.id.phones_container);
        phones = (TextView) headerView.findViewById(R.id.cinema_phones);
        addressContainer = headerView.findViewById(R.id.address_container);
        address = (TextView) headerView.findViewById(R.id.cinema_address);
        ImageButton showOnMapButton = (ImageButton) headerView.findViewById(R.id.show_on_map);
        showComments = (Button) headerView.findViewById(R.id.show_comments);
        showPhotos = (Button) headerView.findViewById(R.id.show_photos);

        timetableList = rootView;
        timetableList.addHeaderView(headerView);
        timetableList.setAdapter(new CinemasAdapter(getActivity()));
        
        showOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCinemaOnMap();
            }
        });
        showComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComments();
            }
        });
        showPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotos();
            }
        });
    }

    private void showPhotos() {
        if (cinemaDetailsActionsListener != null) {
            cinemaDetailsActionsListener.onShowCinemaPhotos(getCinemaId());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            cinemaDetailsActionsListener = (CinemaDetailsActionsListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for CinemaDetailsFragment should implements CinemaDetailsFragment.CinemaDetailsActionsListener");
        }
    }

    private void showComments() {
        if (cinemaDetailsActionsListener != null) {
            cinemaDetailsActionsListener.onShowCinemaComments(getCinemaId());
        }
    }

    private void showCinemaOnMap() {
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressValue);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), R.string.map_not_found, Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Request full timetable for the cinema for today and comments
        //NetworkService.loadTimetable(getActivity(), getCinemaId());
        NetworkService.loadCinemaComments(getActivity(), getCinemaId());

        getLoaderManager().initLoader(CINEMA_LOADER_ID, null, this);
        getLoaderManager().initLoader(COMMENTS_COUNT_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CINEMA_LOADER_ID :
                return DataProviderHelper.getCinemaLoader(getActivity(), getCinemaId(), null);
            case COMMENTS_COUNT_LOADER_ID :
                return DataProviderHelper.getCommentsLoader(getActivity(), getCinemaId(), Constants.CommentTargetType.CINEMA,
                                                            COMMENTS_COUNT_PROJECTION, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case CINEMA_LOADER_ID :
                setCinemaData(data);
                break;
            case COMMENTS_COUNT_LOADER_ID :
                setCommentsCount(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing to do
    }

    private long getCinemaId() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_CINEMA_ID, -1);
        } else {
            return -1;
        }
    }

    private void setCinemaData(Cursor cursor) {
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            cinemaName.setText(cursor.getString(cursor.getColumnIndex(Tables.Cinemas.Columns.NAME)));

            String tmp = cursor.getString(cursor.getColumnIndex(Tables.Cinemas.Columns.PHONES));
            if (!TextUtils.isEmpty(tmp)) {
                phonesContainer.setVisibility(View.VISIBLE);
                phones.setText(Html.fromHtml(tmp));
            } else {
                phonesContainer.setVisibility(View.GONE);
            }

            addressValue = cursor.getString(cursor.getColumnIndex(Tables.Cinemas.Columns.ADDRESS));
            if (!TextUtils.isEmpty(addressValue)) {
                addressContainer.setVisibility(View.VISIBLE);
                address.setText(addressValue);
            } else {
                addressContainer.setVisibility(View.GONE);
            }

            tmp = cursor.getString(cursor.getColumnIndex(Tables.Cinemas.Columns.IMAGE));
            int photosCount = tmp.split(",").length;
            showPhotos.setText(Html.fromHtml(getString(R.string.cinema_photos, photosCount)));
            showPhotos.setEnabled(photosCount > 0);
        }
    }

    private void setCommentsCount(Cursor cursor) {
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            int commentsCount = cursor.getInt(cursor.getColumnIndex(Tables.Comments.COMMENTS_COUNT));
            showComments.setText(Html.fromHtml(getString(R.string.cinema_comments, commentsCount)));
        }
    }

    public interface CinemaDetailsActionsListener {
        public void onShowCinemaComments(long cinemaId);
        public void onShowCinemaPhotos(long cinemaId);
    }
}
