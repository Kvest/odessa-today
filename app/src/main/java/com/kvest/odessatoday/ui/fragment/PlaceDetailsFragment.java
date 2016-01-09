package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.activity.EventDetailsActivity;
import com.kvest.odessatoday.ui.adapter.PlaceTimetableAdapter;
import com.kvest.odessatoday.ui.widget.CommentsCountView;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;

import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by kvest on 20.12.15.
 */
public class PlaceDetailsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String[] PLACE_PROJECTION = new String[]{Places.Columns.NAME, Places.Columns.ADDRESS,
                                                                  Places.Columns.PHONES, Places.Columns.DESCRIPTION,
                                                                  Places.Columns.COMMENTS_COUNT, Places.Columns.IMAGE,
                                                                  Places.Columns.LON, Places.Columns.LAT};

    private static final String ARGUMENT_PLACE_ID = "com.kvest.odessatoday.argument.PLACE_ID";
    private static final String ARGUMENT_PLACE_TYPE = "com.kvest.odessatoday.argument.PLACE_TYPE";
    private static final int PLACE_LOADER_ID = 0;
    private static final int TIMETABLE_LOADER_ID = 1;

    private TextView placeName;
    private TextView placePhones;
    private TextView placeAddress;
    private TextView placeDescription;
    private CommentsCountView actionCommentsCount;

    private ListView timetableList;
    private PlaceTimetableAdapter timetableAdapter;

    private String[] photoUrls = null;
    private double latitude = 0d;
    private double longitude = 0d;

    private int drawablesColor;

    private PlaceDetailsActionsListener placeDetailsActionsListener;

    public static PlaceDetailsFragment getInstance(long placeId, int placeType) {
        Bundle arguments = new Bundle(2);
        arguments.putLong(ARGUMENT_PLACE_ID, placeId);
        arguments.putInt(ARGUMENT_PLACE_TYPE, placeType);

        PlaceDetailsFragment result = new PlaceDetailsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.place_details_fragment, container, false);
        View headerView = inflater.inflate(R.layout.place_details_header, null);

        init(rootView, headerView);

        return rootView;
    }

    private void init(View rootView, View headerView) {
        initResources(getActivity());

        //store views
        placeName = (TextView)headerView.findViewById(R.id.place_name);
        placePhones = (TextView)headerView.findViewById(R.id.place_phones);
        placeAddress = (TextView)headerView.findViewById(R.id.place_address);
        placeDescription = (TextView)headerView.findViewById(R.id.place_description);
        actionCommentsCount = (CommentsCountView) headerView.findViewById(R.id.action_comments_count);

        //colorize drawables
        Utils.setDrawablesColor(drawablesColor, placePhones.getCompoundDrawables());
        Utils.setDrawablesColor(drawablesColor, placeAddress.getCompoundDrawables());

        //setup timetable list
        timetableList = (ListView)rootView.findViewById(R.id.place_details_list);
        timetableList.addHeaderView(headerView);
        timetableAdapter = new PlaceTimetableAdapter(getActivity());
        timetableList.setAdapter(timetableAdapter);

        timetableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEvent(id);
            }
        });
        headerView.findViewById(R.id.action_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaceOnMap();
            }
        });
        headerView.findViewById(R.id.action_comments_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComments();
            }
        });
        headerView.findViewById(R.id.action_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotos();
            }
        });
    }

    private void showEvent(long eventId) {
        EventDetailsActivity.start(getActivity(), eventId);
    }

    private void initResources(Context context) {
        // The attributes you want retrieved
        int[] attrs = {R.attr.PlaceDetailsDrawablesColor};

        // Parse style, using Context.obtainStyledAttributes()
        TypedArray ta = context.obtainStyledAttributes(attrs);

        try {
            drawablesColor = ta.getColor(0, Color.BLACK);
        } finally {
            ta.recycle();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Request full timetable for the place
        loadEvents(getActivity());

        getLoaderManager().initLoader(PLACE_LOADER_ID, null, this);
        getLoaderManager().initLoader(TIMETABLE_LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            placeDetailsActionsListener = (PlaceDetailsActionsListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for PlaceDetailsFragment should implements PlaceDetailsFragment.PlaceDetailsActionsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        placeDetailsActionsListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PLACE_LOADER_ID :
                return DataProviderHelper.getPlaceLoader(getActivity(), getPlaceType(),
                                                         getPlaceId(), PLACE_PROJECTION);
            case TIMETABLE_LOADER_ID :
                long startDate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                return DataProviderHelper.getPlaceTimetableLoader(getActivity(), getPlaceId(), startDate,
                                                                  PlaceTimetableAdapter.PROJECTION,
                                                                  EventsTimetableView.ORDER_EVENT_ASC_DATE_ASC);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case PLACE_LOADER_ID :
                setPlaceData(cursor);
                break;
            case TIMETABLE_LOADER_ID :
                timetableAdapter.setCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case TIMETABLE_LOADER_ID :
                timetableAdapter.setCursor(null);
                break;
        }
    }

    protected long getPlaceId() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_PLACE_ID, -1);
        } else {
            return -1;
        }
    }

    protected int getPlaceType() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getInt(ARGUMENT_PLACE_TYPE, -1);
        } else {
            return -1;
        }
    }

    private void loadEvents(Context context) {
        long startDate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        NetworkService.loadEvents(context, startDate, -1, getPlaceId());
    }

    private void showComments() {
        if (placeDetailsActionsListener != null) {
            placeDetailsActionsListener.onShowPlaceComments(getPlaceId(), getPlaceType(), placeName.getText().toString(),
                                                            actionCommentsCount.getCommentsCount());
        }
    }

    private void showPhotos() {
        if (placeDetailsActionsListener != null && photoUrls != null) {
            placeDetailsActionsListener.onShowPlacePhotos(photoUrls);
        }
    }

    private void showPlaceOnMap() {
        Uri geoLocation = Uri.parse("geo:0,0?q=" + latitude + "," +longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), R.string.map_not_found, Toast.LENGTH_SHORT);
        }
    }

    public void setPlaceData(Cursor cursor) {
        if (cursor.moveToFirst()) {
            String tmp = cursor.getString(cursor.getColumnIndex(Places.Columns.NAME));
            placeName.setText(tmp);
            Activity activity = getActivity();
            if (activity != null) {
                activity.setTitle(tmp);
            }

            tmp = cursor.getString(cursor.getColumnIndex(Places.Columns.PHONES));
            if (TextUtils.isEmpty(tmp)) {
                placePhones.setVisibility(View.GONE);
            } else {
                placePhones.setText(tmp);
                placePhones.setVisibility(View.VISIBLE);
            }

            tmp = cursor.getString(cursor.getColumnIndex(Places.Columns.ADDRESS));
            if (TextUtils.isEmpty(tmp)) {
                placeAddress.setVisibility(View.GONE);
            } else {
                placeAddress.setText(tmp);
                placeAddress.setVisibility(View.VISIBLE);
            }

            tmp = cursor.getString(cursor.getColumnIndex(Places.Columns.DESCRIPTION));
            if (TextUtils.isEmpty(tmp)) {
                placeDescription.setVisibility(View.GONE);
            } else {
                placeDescription.setText(tmp);
                placeDescription.setVisibility(View.VISIBLE);
            }

            int commentsCount = cursor.getInt(cursor.getColumnIndex(Places.Columns.COMMENTS_COUNT));
            actionCommentsCount.setCommentsCount(commentsCount);

            tmp = cursor.getString(cursor.getColumnIndex(Places.Columns.IMAGE));
            photoUrls = tmp != null ? Utils.string2Images(tmp) : null;

            //remember geo location
            longitude = cursor.getDouble(cursor.getColumnIndex(Places.Columns.LON));
            latitude = cursor.getDouble(cursor.getColumnIndex(Places.Columns.LAT));
        }
    }

    public interface PlaceDetailsActionsListener {
        void onShowPlaceComments(long placeId, int placeType, String placeName, int commentsCount);
        void onShowPlacePhotos(String[] photoURLs);
    }
}
