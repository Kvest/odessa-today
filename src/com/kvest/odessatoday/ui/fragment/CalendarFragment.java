package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.utils.LogUtils.*;
/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 08.07.14
 * Time: 21:03
 * To change this template use File | Settings | File Templates.
 */
public class CalendarFragment extends Fragment {
    private static final long ONE_DAY_MILLIS = TimeUnit.DAYS.toMillis(1);
    private static final String ARGUMENT_SELECTED_DATE = "com.kvest.odessatoday.argument.SELECTED_DATE";
    private static final String DATE_FORMAT_PATTERN = "LLLL yyyy";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    private Calendar calendar;
    private CalendarAdapter adapter;
    private int shownMonthNumber;
    private int currentMonthNumber;
    private TextView shownMonthLabel;
    private Button showPreviousMonthButton;

    private OnDateSelectedListener onDateSelectedListener;

    public static CalendarFragment getInstance(long selectedDate) {
        //convert to UTC milliseconds
        selectedDate = TimeUnit.SECONDS.toMillis(selectedDate);
        //cut hours, minutes, ...
        selectedDate -= (selectedDate % ONE_DAY_MILLIS);

        Bundle arguments = new Bundle(1);
        arguments.putLong(ARGUMENT_SELECTED_DATE, selectedDate);

        CalendarFragment result = new CalendarFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.calendar_fragment, container, false);

        init(root);

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onDateSelectedListener = (OnDateSelectedListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for CalendarFragment should implements CalendarFragment.OnDateSelectedListener");
        }
    }

    private void init(View root) {
        calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        currentMonthNumber = getMonthNumber(System.currentTimeMillis());

        //days = (TableLayout) root.findViewById(R.id.days);
        shownMonthLabel = (TextView) root.findViewById(R.id.shown_month_label);

        //setup adapter
        adapter = new CalendarAdapter(root.getContext());
        GridView daysGridView = (GridView)root.findViewById(R.id.calendar_grid);
        daysGridView.setAdapter(adapter);
        daysGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CalendarDay calendarDay = (CalendarDay)adapter.getItem(position);
                if ((calendarDay.type == CalendarDay.DAY_TYPE_ACTIVE || calendarDay.type == CalendarDay.DAY_TYPE_SELECTED)
                     && onDateSelectedListener != null) {
                    //convert date to local seconds
                    long resultDate = TimeUnit.MILLISECONDS.toSeconds(calendarDay.date);

                    onDateSelectedListener.onDateSelected(resultDate);
                }
            }
        });

        //setup buttons
        root.findViewById(R.id.next_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextMonth();
            }
        });
        showPreviousMonthButton = (Button) root.findViewById(R.id.previous_month);
        showPreviousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousMonth();
            }
        });

        //show calendar starting from the month with selected day
        showMonth(getMonthNumber(getSelectedDate()));
    }

    private void showNextMonth() {
        //increment month
        ++shownMonthNumber;

        showMonth(shownMonthNumber);
    }

    private void showPreviousMonth(){
        //decrement month
        --shownMonthNumber;

        showMonth(shownMonthNumber);
    }

    private boolean canShowPreviousMonth() {
        return ((shownMonthNumber - 1) >= currentMonthNumber);
    }

    private long getSelectedDate() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_SELECTED_DATE, 0);
        } else {
            return 0;
        }
    }

    private void showMonth(int monthNumber) {
        //store month number
        shownMonthNumber = monthNumber;

        //convert month number to the date
        long monthDate = getDateByMonthNumber(monthNumber);

        adapter.setContent(monthDate, getSelectedDate());

        shownMonthLabel.setText(DATE_FORMAT.format(monthDate));

        //set previous button availability
        showPreviousMonthButton.setEnabled(canShowPreviousMonth());
    }

    /**
     * Method returns month number
     * @param date Date to convert
     * @return Month number since 0 year
     */
    private int getMonthNumber(long date) {
        calendar.setTimeInMillis(date);

        return calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH);
    }

    private long getDateByMonthNumber(int monthNumber) {
        calendar.clear();
        calendar.set(Calendar.YEAR, monthNumber / 12);
        calendar.set(Calendar.MONTH, monthNumber % 12);

        return calendar.getTimeInMillis();
    }

    public interface OnDateSelectedListener {
        public void onDateSelected(long date);
    }

    private static class CalendarDay {
        public static final int DAY_TYPE_ACTIVE = 0;
        public static final int DAY_TYPE_PASSED = 1;
        public static final int DAY_TYPE_ANOTHER_MONTH = 2;
        public static final int DAY_TYPE_SELECTED = 3;

        public long date;
        public int dayNumber;
        public int type;

        private CalendarDay(long date, int dayNumber, int type) {
            this.date = date;
            this.dayNumber = dayNumber;
            this.type = type;
        }
    }

    private static class CalendarAdapter extends BaseAdapter {
        private static final float DAY_TEXT_SIZE = 18f;

        private Context context;
        private List<CalendarDay> days;
        private Calendar calendar;
        private int otherMonthTextColor;
        private int passedDaysTextColor;
        private int activeDaysTextColor;

        public CalendarAdapter(Context context) {
            super();

            this.context = context;
            days = new ArrayList<CalendarDay>();
            calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

            otherMonthTextColor = context.getResources().getColor(R.color.calendar_other_month_text_color);
            passedDaysTextColor = context.getResources().getColor(R.color.calendar_passed_days_text_color);
            activeDaysTextColor = context.getResources().getColor(R.color.calendar_active_days_text_color);
        }

        public void setContent(long monthToShow, long selectedDate)  {
            days.clear();

            calendar.setTimeInMillis(monthToShow);

            //get current month
            int month = calendar.get(Calendar.MONTH);

            //Go to first day in month
            calendar.set(Calendar.DAY_OF_MONTH, 1);

            //go to monday and get days in this week from previous month
            calendar.setTimeInMillis(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(shiftDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))));
            while (calendar.get(Calendar.MONTH) != month) {
                days.add(new CalendarDay(calendar.getTimeInMillis(), calendar.get(Calendar.DAY_OF_MONTH), CalendarDay.DAY_TYPE_ANOTHER_MONTH));

                //go to next day
                calendar.setTimeInMillis(calendar.getTimeInMillis() + ONE_DAY_MILLIS);
            }

            //add dates from the target month
            int type;
            long currentDate = System.currentTimeMillis();
            //delete hours from date
            currentDate =  currentDate - (currentDate % ONE_DAY_MILLIS);

            while (calendar.get(Calendar.MONTH) == month) {
                if (calendar.getTimeInMillis() == selectedDate) {
                    type = CalendarDay.DAY_TYPE_SELECTED;
                }else if (calendar.getTimeInMillis() < currentDate) {
                    type = CalendarDay.DAY_TYPE_PASSED;
                } else {
                    type = CalendarDay.DAY_TYPE_ACTIVE;
                }
                days.add(new CalendarDay(calendar.getTimeInMillis(), calendar.get(Calendar.DAY_OF_MONTH), type));

                //go to the next day
                calendar.setTimeInMillis(calendar.getTimeInMillis() + ONE_DAY_MILLIS);
            }

            //part of the next month
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                days.add(new CalendarDay(calendar.getTimeInMillis(), calendar.get(Calendar.DAY_OF_MONTH), CalendarDay.DAY_TYPE_ANOTHER_MONTH));

                //go to nest day
                calendar.setTimeInMillis(calendar.getTimeInMillis() + ONE_DAY_MILLIS);
            }

            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return days.size();
        }

        @Override
        public Object getItem(int position) {
            return days.get(position);
        }

        @Override
        public long getItemId(int position) {
            return days.get(position).date;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = buildView();
            }

            CalendarDay calendarDay = days.get(position);
            //set bg color
            if (calendarDay.type == CalendarDay.DAY_TYPE_SELECTED) {
                convertView.setBackgroundResource(R.drawable.calendar_selected_day_bg);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            //set text color
            switch (calendarDay.type) {
                case CalendarDay.DAY_TYPE_ACTIVE :
                case CalendarDay.DAY_TYPE_SELECTED :
                    ((TextView)convertView).setTextColor(activeDaysTextColor);
                    break;
                case CalendarDay.DAY_TYPE_PASSED :
                    ((TextView)convertView).setTextColor(passedDaysTextColor);
                    break;
                case CalendarDay.DAY_TYPE_ANOTHER_MONTH :
                    ((TextView)convertView).setTextColor(otherMonthTextColor);
                    break;
            }
            //set text
            ((TextView)convertView).setText(Integer.toString(calendarDay.dayNumber));

            return convertView;
        }

        private View buildView() {
            TextView view = new TextView(context);
            view.setTypeface(Typeface.create(view.getTypeface(), Typeface.BOLD));
            view.setTextSize(DAY_TEXT_SIZE);
            view.setGravity(Gravity.CENTER);

            return view;
        }

        //convert to post-Soviet style(week starts from monday = 0 till sunday = 6)
        private int shiftDayOfWeek(int day) {
            return (day + 5) % 7;
        }
    }
}
