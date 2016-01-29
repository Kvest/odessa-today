package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.activity.DateSelectionListener;
import com.kvest.odessatoday.ui.activity.MainActivity;
import com.kvest.odessatoday.ui.activity.MainMenuController;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.FontUtils;
import com.kvest.odessatoday.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by kvest on 12.12.15.
 */
public class EventsWithPlacesFragment extends BaseFragment implements MainActivity.ToolbarExtendable,
                                                                      DateChangedListener,
                                                                      DateSelectionListener {
    private static final String ARGUMENT_EVENT_TYPE = "com.kvest.odessatoday.argument.EVENT_TYPE";
    private static final String ARGUMENT_PLACE_TYPE = "com.kvest.odessatoday.argument.PLACE_TYPE";
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM, cc.");

    private static final int EVENTS_LIST_FRAGMENT_POSITION = 0;
    private static final int PLACES_LIST_FRAGMENT_POSITION = 1;
    private static final int ANNOUNCEMENTS_LIST_FRAGMENT_POSITION = 2;

    private MainMenuController mainMenuController;

    private ViewPager fragmentsPager;
    private RadioGroup categorySelector;
    private EventsWithPlacesPagerAdapter pagerAdapter;

    private View toolbarExtension;
    private TextView extensionTitle;
    private View previousDay;
    private View nextDay;

    public static EventsWithPlacesFragment newInstance(int eventType, int placeType) {
        Bundle arguments = new Bundle(2);
        arguments.putInt(ARGUMENT_EVENT_TYPE, eventType);
        arguments.putInt(ARGUMENT_PLACE_TYPE, placeType);

        EventsWithPlacesFragment result = new EventsWithPlacesFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.events_with_places_fragment, container, false);

        init(root);

        //reset date change listener
        if (savedInstanceState != null) {
            EventsListFragment eventsListFragment = getEventsListFragment();
            if (eventsListFragment != null) {
                eventsListFragment.setDateChangedListener(this);
            }
        }

        return root;
    }

    private void init(View view) {
        Context context = getActivity();
        fragmentsPager = (ViewPager) view.findViewById(R.id.view_pager);
        fragmentsPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pageNumber) {
                if (mainMenuController != null) {
                    if (pageNumber == 0) {
                        //unlock menu
                        mainMenuController.unlockMenuSliding();
                    } else {
                        //lock menu
                        mainMenuController.lockMenuSliding();
                    }
                }

                //set selected tab
                switch (pageNumber) {
                    case EVENTS_LIST_FRAGMENT_POSITION:
                        categorySelector.check(R.id.selector_events);
                        break;
                    case PLACES_LIST_FRAGMENT_POSITION:
                        categorySelector.check(R.id.selector_places);
                        break;
                    case ANNOUNCEMENTS_LIST_FRAGMENT_POSITION:
                        categorySelector.check(R.id.selector_announcements);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        pagerAdapter = new EventsWithPlacesPagerAdapter(getChildFragmentManager(), getEventType(), getPlaceType());
        fragmentsPager.setAdapter(pagerAdapter);

        //category selector
        categorySelector = (RadioGroup)view.findViewById(R.id.category_selector);
        categorySelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.selector_events:
                        fragmentsPager.setCurrentItem(EVENTS_LIST_FRAGMENT_POSITION, true);
                        setToolbarExtensionVisibility(View.VISIBLE);
                        break;
                    case R.id.selector_places:
                        fragmentsPager.setCurrentItem(PLACES_LIST_FRAGMENT_POSITION, true);
                        setToolbarExtensionVisibility(View.GONE);
                        break;
                    case R.id.selector_announcements:
                        fragmentsPager.setCurrentItem(ANNOUNCEMENTS_LIST_FRAGMENT_POSITION, true);
                        setToolbarExtensionVisibility(View.GONE);
                        break;
                }
            }
        });

        //set title and typeface for the selector's RadioButtons
        Typeface helveticaneuecyrBold = FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_BOLD_FONT);
        RadioButton eventsSelector = (RadioButton)view.findViewById(R.id.selector_events);
        setEventsSelectorTitle(eventsSelector);
        eventsSelector.setTypeface(helveticaneuecyrBold);
        RadioButton placesSelector = (RadioButton)view.findViewById(R.id.selector_places);
        setPlacesSelectorTitle(placesSelector);
        placesSelector.setTypeface(helveticaneuecyrBold);
        RadioButton announcementsSelector = (RadioButton)view.findViewById(R.id.selector_announcements);
        announcementsSelector.setTypeface(helveticaneuecyrBold);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mainMenuController = (MainMenuController) activity;
        } catch (ClassCastException cce) {
            cce.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mainMenuController != null) {
            mainMenuController.unlockMenuSliding();
        }
        mainMenuController = null;
    }

    @Override
    public void onDateSelected(long date) {
        EventsListFragment eventsListFragment = getEventsListFragment();
        if (eventsListFragment != null) {
            eventsListFragment.onDateSelected(date);
        }
    }

    @Override
    public void onDateChanged(long date) {
        int previousDayVisibility = View.VISIBLE;
        if (TimeUtils.isCurrentDay(date)) {
            extensionTitle.setText(R.string.odessa_today);
            previousDayVisibility = View.INVISIBLE;
        } else if (TimeUtils.isTomorrow(date)) {
            extensionTitle.setText(R.string.odessa_tomorrow);
        } else {
            extensionTitle.setText(DATE_FORMAT.format(TimeUnit.SECONDS.toMillis(date)));
        }

        previousDay.setVisibility(previousDayVisibility);
    }

    private EventsListFragment getEventsListFragment() {
        //workaround
        return (EventsListFragment)getChildFragmentManager().findFragmentByTag("android:switcher:" + fragmentsPager.getId() + ":" + EVENTS_LIST_FRAGMENT_POSITION);
    }

    private void setToolbarExtensionVisibility(int visibility) {
        if (toolbarExtension != null) {
            toolbarExtension.setVisibility(visibility);
        }
    }

    @Override
    public int getExtensionLayoutId() {
        return R.layout.toolbar_extension_with_calendar;
    }

    @Override
    public void setExtensionView(View extension ) {
        toolbarExtension = extension;
        extensionTitle = (TextView) toolbarExtension.findViewById(R.id.title);
        previousDay = toolbarExtension.findViewById(R.id.previous_day);
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsListFragment eventsListFragment = getEventsListFragment();
                if (eventsListFragment != null) {
                    eventsListFragment.showPreviousDay();
                }
            }
        });
        nextDay = toolbarExtension.findViewById(R.id.next_day);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsListFragment eventsListFragment = getEventsListFragment();
                if (eventsListFragment != null) {
                    eventsListFragment.showNextDay();
                }
            }
        });
    }

    private void setEventsSelectorTitle(RadioButton eventsSelector) {
        switch (getEventType()) {
            case Constants.EventType.CONCERT:
                eventsSelector.setText(R.string.selector_concerts);
                break;
            case Constants.EventType.PARTY:
                eventsSelector.setText(R.string.selector_parties);
                break;
            case Constants.EventType.SPECTACLE:
                eventsSelector.setText(R.string.selector_spectacles);
                break;
            case Constants.EventType.EXHIBITION:
                eventsSelector.setText(R.string.selector_exhibitions);
                break;
        }
    }

    private void setPlacesSelectorTitle(RadioButton placesSelector) {
        switch (getPlaceType()) {
            case Constants.PlaceType.THEATRE:
                placesSelector.setText(R.string.selector_theatres);
                break;
            case Constants.PlaceType.CONCERT_HALL:
                placesSelector.setText(R.string.selector_concert_halls);
                break;
            case Constants.PlaceType.CLUB:
                placesSelector.setText(R.string.selector_clubs);
                break;
            case Constants.PlaceType.MUSEUM:
                placesSelector.setText(R.string.selector_museums);
                break;
        }
    }

    private int getEventType() {
        Bundle arguments = getArguments();

        return (arguments != null) ? arguments.getInt(ARGUMENT_EVENT_TYPE, -1) : -1;
    }

    private int getPlaceType() {
        Bundle arguments = getArguments();

        return (arguments != null) ? arguments.getInt(ARGUMENT_PLACE_TYPE, -1) : -1;
    }

    public class EventsWithPlacesPagerAdapter extends FragmentPagerAdapter {
        private static final int FRAGMENTS_COUNT = 3;

        private int eventType;
        private int placeType;

        public EventsWithPlacesPagerAdapter(FragmentManager fm, int eventType, int placeType) {
            super(fm);

            this.eventType = eventType;
            this.placeType = placeType;
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case EVENTS_LIST_FRAGMENT_POSITION :
                    EventsListFragment eventsListFragment = EventsListFragment.newInstance(eventType, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()), false);
                    eventsListFragment.setDateChangedListener(EventsWithPlacesFragment.this);
                    return eventsListFragment;
                case PLACES_LIST_FRAGMENT_POSITION :
                    return PlacesListFragment.newInstance(placeType);
                case ANNOUNCEMENTS_LIST_FRAGMENT_POSITION :
                    long tomorrow = TimeUtils.getTomorrow(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    EventsListFragment announcementsListFragment = EventsListFragment.newInstance(eventType, tomorrow, true);
                    return announcementsListFragment;
                default :
                    return null;
            }
        }

        @Override
        public int getCount() {
            return FRAGMENTS_COUNT;
        }
    }
}
