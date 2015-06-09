package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.datamodel.Cinema;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.CinemaTimetableAdapter;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
import static com.kvest.odessatoday.utils.LogUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 04.11.14
 * Time: 21:45
 * To change this template use File | Settings | File Templates.
 */
public class CinemaDetailsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,CalendarFragment.OnDateSelectedListener {
    private static final String ARGUMENT_CINEMA_ID = "com.kvest.odessatoday.argument.CINEMA_ID";
    public static final String[] COMMENTS_COUNT_PROJECTION = new String[]{Tables.Comments.COMMENTS_COUNT};

    private static final String DATE_FORMAT_PATTERN = " dd MMM. yyyy, ";
    private static final String WEEK_DAY_FORMAT_PATTERN = "cccc";
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    private final SimpleDateFormat WEEK_DAY_FORMAT = new SimpleDateFormat(WEEK_DAY_FORMAT_PATTERN);

    private static final int CINEMA_LOADER_ID = 1;
    private static final int COMMENTS_COUNT_LOADER_ID = 2;
    private static final int TIMETABLE_LOADER_ID = 3;

    private TextView cinemaName;
    private View phonesContainer;
    private TextView phones;
    private View addressContainer;
    private TextView address;
    private Button showComments;
    private Button showPhotos;
    private ListView timetableList;
    private String addressValue = "";
    private String[] photoUrls = null;

    private TextView isToday;
    private TextView dateTextView;
    private TextView weekDayTextView;
    private long shownDate;

    private CinemaTimetableAdapter cinemaTimetableAdapter;

    private final Calendar calendar = Calendar.getInstance();
    private FrameLayout calendarContainer;

    private Animation showCalendarAnimation;
    private Animation hideCalendarAnimation;

    private CinemaDetailsActionsListener cinemaDetailsActionsListener;

    public static CinemaDetailsFragment getInstance(long cinemaId) {
        Bundle arguments = new Bundle(1);
        arguments.putLong(ARGUMENT_CINEMA_ID, cinemaId);

        CinemaDetailsFragment result = new CinemaDetailsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shownDate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cinema_details_fragment, container, false);
        View headerView = inflater.inflate(R.layout.cinema_details_header, null);
        View dateHeaderView = inflater.inflate(R.layout.cinema_details_date_header, null);

        init(rootView, headerView, dateHeaderView);

        return rootView;
    }

    private void init(View rootView, View headerView, View dateHeaderView) {
        cinemaName = (TextView) headerView.findViewById(R.id.cinema_name);
        phonesContainer = headerView.findViewById(R.id.phones_container);
        phones = (TextView) headerView.findViewById(R.id.cinema_phones);
        addressContainer = headerView.findViewById(R.id.address_container);
        address = (TextView) headerView.findViewById(R.id.cinema_address);
        ImageButton showOnMapButton = (ImageButton) headerView.findViewById(R.id.show_on_map);
        showComments = (Button) headerView.findViewById(R.id.show_comments);
        showPhotos = (Button) headerView.findViewById(R.id.show_photos);
        isToday = (TextView)dateHeaderView.findViewById(R.id.is_today);
        dateTextView = (TextView)dateHeaderView.findViewById(R.id.date);
        weekDayTextView = (TextView)dateHeaderView.findViewById(R.id.week_day);

        //set shown date UI
        updateShownDateUI();

        //setup timetable list
        timetableList = (ListView)rootView.findViewById(R.id.cinema_details_list);
        timetableList.addHeaderView(headerView);
        timetableList.addHeaderView(dateHeaderView);
        cinemaTimetableAdapter = new CinemaTimetableAdapter(getActivity());
        timetableList.setAdapter(cinemaTimetableAdapter);
        
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
        dateHeaderView.findViewById(R.id.show_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCalendarShown()) {
                    hideCalendar();
                } else {
                    showCalendar();
                }
            }
        });

        setupCalendar(rootView);
    }

    private void setupCalendar(View rootView) {
        calendarContainer = (FrameLayout)rootView.findViewById(R.id.calendar_container);
        calendarContainer.setVisibility(View.INVISIBLE);
        calendarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCalendar();
            }
        });

        showCalendarAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        hideCalendarAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        hideCalendarAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                calendarContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void setShownDate(long shownDate) {
        this.shownDate = shownDate;

        updateShownDateUI();

        //start loading data
        NetworkService.loadFilms(getActivity(), shownDate, TimeUtils.getEndOfTheDay(shownDate), getCinemaId());

        //restart loader
        getLoaderManager().restartLoader(TIMETABLE_LOADER_ID, null, this);
    }

    private void updateShownDateUI() {
        isToday.setVisibility(TimeUtils.isCurrentDay(shownDate) ? View.VISIBLE : View.GONE);
        dateTextView.setText(DATE_FORMAT.format(TimeUnit.SECONDS.toMillis(shownDate)));
        weekDayTextView.setText(WEEK_DAY_FORMAT.format(TimeUnit.SECONDS.toMillis(shownDate)).toLowerCase());

    }

    private void showCalendar() {
        //set fragment
        if (calendarContainer != null && !isCalendarShown()) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(shownDate));
                CalendarFragment calendarFragment = CalendarFragment.getInstance(calendar.get(Calendar.DAY_OF_MONTH),
                                                                                 calendar.get(Calendar.MONTH),
                                                                                 calendar.get(Calendar.YEAR));
                calendarFragment.setOnDateSelectedListener(this);
                transaction.replace(R.id.calendar_container, calendarFragment);
            } finally {
                transaction.commit();
            }

            //set calendar visible
            calendarContainer.setVisibility(View.VISIBLE);

            //animate
            calendarContainer.clearAnimation();
            calendarContainer.startAnimation(showCalendarAnimation);
        }
    }

    private void hideCalendar() {
        //animate
        calendarContainer.clearAnimation();
        calendarContainer.startAnimation(hideCalendarAnimation);
    }

    private boolean isCalendarShown() {
        return calendarContainer.getVisibility() == View.VISIBLE;
    }

    private void showPhotos() {
        if (cinemaDetailsActionsListener != null && photoUrls != null) {
            cinemaDetailsActionsListener.onShowCinemaPhotos(photoUrls);
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

    public boolean onBackPressed() {
        if (isCalendarShown()) {
            hideCalendar();

            return true;
        }

        return false;
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
        NetworkService.loadFilms(getActivity(), shownDate, TimeUtils.getEndOfTheDay(shownDate), getCinemaId());
        NetworkService.loadCinemaComments(getActivity(), getCinemaId());

        getLoaderManager().initLoader(CINEMA_LOADER_ID, null, this);
        getLoaderManager().initLoader(COMMENTS_COUNT_LOADER_ID, null, this);
        getLoaderManager().initLoader(TIMETABLE_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CINEMA_LOADER_ID :
                return DataProviderHelper.getCinemaLoader(getActivity(), getCinemaId(), null);
            case COMMENTS_COUNT_LOADER_ID :
                return DataProviderHelper.getCommentsLoader(getActivity(), getCinemaId(), Constants.CommentTargetType.CINEMA,
                                                            COMMENTS_COUNT_PROJECTION, null);
            case TIMETABLE_LOADER_ID :
                long endDate = TimeUtils.getEndOfTheDay(shownDate);
                return DataProviderHelper.getCinemaTimetableLoader(getActivity(), getCinemaId(), shownDate, endDate,
                        CinemaTimetableAdapter.PROJECTION , Tables.CinemaTimetableView.TIMETABLE_ORDER_ASC);
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
            case TIMETABLE_LOADER_ID :
                cinemaTimetableAdapter.setCursor(data);
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
            photoUrls = tmp != null ? tmp.split(Cinema.IMAGES_SEPARATOR) : null;
            int photosCount = photoUrls != null ? photoUrls.length : 0;
            showPhotos.setText(Html.fromHtml(getString(R.string.cinema_photos, photosCount)));
            showPhotos.setEnabled(photosCount > 0);
        }
    }

    private void setCommentsCount(Cursor cursor) {
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            int commentsCount = cursor.getInt(cursor.getColumnIndex(Tables.Comments.COMMENTS_COUNT));
            showComments.setText(Html.fromHtml(getString(R.string.comments_with_count, commentsCount)));
        }
    }

    @Override
    public void onDateSelected(int day, int month, int year) {
        //hide calendar
        hideCalendar();

        //show films by selected date
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        setShownDate(TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis()));
    }

    public interface CinemaDetailsActionsListener {
        public void onShowCinemaComments(long cinemaId);
        public void onShowCinemaPhotos(String[] photoURLs);
    }
}
