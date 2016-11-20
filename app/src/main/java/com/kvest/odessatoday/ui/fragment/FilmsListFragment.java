package com.kvest.odessatoday.ui.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.io.network.event.FilmsLoadedEvent;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.FilmsAdapter;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.KeyboardUtils;
import com.kvest.odessatoday.utils.TimeUtils;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.utils.LogUtils.*;
/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 01.06.14
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class FilmsListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
                                                               SwipeRefreshLayout.OnRefreshListener {
    private static final long STOP_REFRESHING_DELAY = 2000L;
    private static final String KEY_DATE = "com.kvest.odessatoday.key.DATE";
    private static final String ARGUMENT_DATE = "com.kvest.odessatoday.argiment.DATE";
    private static final int FILMS_LOADER_ID = 1;

    //date of the shown films in seconds
    private long date = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    private DateChangedListener dateChangedListener;

    private FilmsAdapter adapter;

    private ShowCalendarListener showCalendarListener;
    private FilmSelectedListener filmSelectedListener;

    private View noFilmsLabel;

    private FloatingActionButton filterButton;
    private View filterPanel;
    private View closeFilterPanelButton;

    private SwipeRefreshLayout refreshLayout;
    private Handler handler = new Handler();
    private FilterPanelAnimationListener filterPanelAnimationListener;

    private TextView filterValue;

    public static FilmsListFragment newInstance(long date) {
        Bundle arguments = new Bundle(1);
        arguments.putLong(ARGUMENT_DATE, date);

        FilmsListFragment result = new FilmsListFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.calendar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_calendar:
                onShowCalendar();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_DATE)) {
            setDate(savedInstanceState.getLong(KEY_DATE));
        } else {
            //get initial data
            Bundle arguments = getArguments();
            if (arguments != null) {
                setDate(arguments.getLong(ARGUMENT_DATE, date));
            }
        }

        //load films data
        loadFilmsData(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        BusProvider.getInstance().register(this);

        View root = inflater.inflate(R.layout.films_list_fragment, container, false);

        init(root);

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            showCalendarListener = (ShowCalendarListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for FilmsListFragment should implements ShowCalendarListener");
        }

        try {
            filmSelectedListener = (FilmSelectedListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for FilmsListFragment should implements FilmsListFragment.FilmSelectedListener");
        }
    }

    private void loadFilmsData(Context context) {
        long startDate = date;
        long endDate = TimeUtils.getEndOfTheDay(startDate);

        //show progress
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(true);
                }
            }
        });

        NetworkService.loadFilms(context, startDate, endDate);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        showCalendarListener = null;
        filmSelectedListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        dateChangedListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop progress
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(FILMS_LOADER_ID, null, this);

        //send data once again after recreate to the date listener
        if (savedInstanceState != null) {
            if (dateChangedListener != null) {
                dateChangedListener.onDateChanged(this.date);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(KEY_DATE, date);
    }

    public DateChangedListener getDateChangedListener() {
        return dateChangedListener;
    }

    public void setDateChangedListener(DateChangedListener dateChangedListener) {
        this.dateChangedListener = dateChangedListener;
    }

    private void onShowCalendar() {
        if (showCalendarListener != null) {
            showCalendarListener.onShowCalendar(date);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FILMS_LOADER_ID :
                long startDate = date;
                long endDate = TimeUtils.getEndOfTheDay(startDate);
                String filter = filterValue.getText().toString();

                return DataProviderHelper.getFilmsForPeriodLoader(getActivity(), startDate, endDate,
                                                                  filter, FilmsAdapter.PROJECTION);
            default :
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case FILMS_LOADER_ID :
                adapter.swapCursor(cursor);
                if (cursor.getCount() > 0) {
                    noFilmsLabel.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case FILMS_LOADER_ID :
                adapter.swapCursor(null);
                break;
        }
    }

    private void init(View root) {
        filterPanelAnimationListener = new FilterPanelAnimationListener();

        noFilmsLabel = root.findViewById(R.id.no_films);

        refreshLayout = (SwipeRefreshLayout)root.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.application_green);

        //get list view for films
        ListView filmsList = (ListView) root.findViewById(R.id.films_list);
        filmsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (filmSelectedListener != null) {
                    filmSelectedListener.onFilmSelected(id, date);
                }
            }
        });

        //create and set an adapter
        adapter = new FilmsAdapter(getActivity());
        filmsList.setAdapter(adapter);

        filterButton = (FloatingActionButton) root.findViewById(R.id.filter);
        filterPanel = root.findViewById(R.id.filter_panel);
        closeFilterPanelButton = root.findViewById(R.id.close_filter_panel);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPanel();
            }
        });
        closeFilterPanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFilterPanel();
            }
        });

        filterValue = (TextView) root.findViewById(R.id.filter_value);
        filterValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.setFilterKey(s.toString());
                //reload content
                getLoaderManager().restartLoader(FILMS_LOADER_ID, null, FilmsListFragment.this);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void onDateSelected(long date) {
        setDate(Math.max(date, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));

        //reload content
        getLoaderManager().restartLoader(FILMS_LOADER_ID, null, this);

        //load films data
        Activity activity = getActivity();
        if (activity != null) {
            loadFilmsData(activity);
        }
    }

    public void showNextDay() {
        long nextDay = TimeUtils.getBeginningOfTheDay(date) + TimeUnit.DAYS.toSeconds(1);
        onDateSelected(nextDay);
    }

    public void showPreviousDay() {
        long previousDay = TimeUtils.getBeginningOfTheDay(date) - TimeUnit.DAYS.toSeconds(1);
        onDateSelected(previousDay);
    }

    private void setDate(long date) {
        this.date = date;

        if (dateChangedListener != null) {
            dateChangedListener.onDateChanged(this.date);
        }
    }

    @Override
    public void onRefresh() {
        //reload films data
        Activity activity = getActivity();
        if (activity != null) {
            loadFilmsData(activity);
        }
    }

    @Subscribe
    public void onFilmsLoaded(FilmsLoadedEvent event) {
        //event dispatched not in the UI thread
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, event.isSuccessful() ? STOP_REFRESHING_DELAY : 0L);

        Activity activity = getActivity();
        if (!event.isSuccessful() && activity != null) {
            showErrorSnackbar(activity, R.string.error_loading_films);
        }

        //check films exists
        if (event.isSuccessful() && event.startDate == date && event.filmsCount == 0) {
            noFilmsLabel.post(new Runnable() {
                @Override
                public void run() {
                    noFilmsLabel.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void showFilterPanel() {
        filterValue.requestFocus();

        filterButton.hide();
        filterPanel.animate()
                .translationY(0)
                .setListener(null)
                .start();
    }

    private void hideFilterPanel() {
        KeyboardUtils.hideKeyboard(getContext(), filterValue);

        //clear filter
        filterValue.setText("");
        getLoaderManager().restartLoader(FILMS_LOADER_ID, null, this);

        filterPanel.animate()
                .translationY(filterPanel.getMeasuredHeight())
                .setListener(filterPanelAnimationListener)
                .start();
    }

    public class FilterPanelAnimationListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationEnd(Animator animation) {
            filterButton.show();
        }

        @Override
        public void onAnimationStart(Animator animation) {}

        @Override
        public void onAnimationCancel(Animator animation) {}

        @Override
        public void onAnimationRepeat(Animator animation) {}
    }
}
