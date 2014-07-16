package com.kvest.odessatoday.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.kvest.odessatoday.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 08.07.14
 * Time: 21:03
 * To change this template use File | Settings | File Templates.
 */
public class CalendarFragment extends Fragment {
    private static final String SELECTED_DATE_EXTRA = "com.kvest.odessatoday.EXTRAS.SELECTED_DATE";


    private TextView shownMonthLabel;
    private TableLayout days;

    public static CalendarFragment getInstance(long selectedDate) {
        //cut hours, minutes,...
        selectedDate -= (selectedDate % TimeUnit.DAYS.toMillis(1));

        Bundle arguments = new Bundle(1);
        arguments.putLong(SELECTED_DATE_EXTRA, selectedDate);

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

    private void init(View root) {
        //days = (TableLayout) root.findViewById(R.id.days);
        shownMonthLabel = (TextView) root.findViewById(R.id.shown_month_label);

        CalendarAdapter adapter = new CalendarAdapter(root.getContext());
        adapter.setContent(getSelectedDate());
        ((GridView)root.findViewById(R.id.calendar_grid)).setAdapter(adapter);
    }

//    private void setDays(TableLayout container, long date) {
//        //setup calendar
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(date);
//
//        //get current month
//        int month = calendar.get(Calendar.MONTH);
//
//        //Go to first day in month
//        calendar.set(Calendar.DAY_OF_MONTH, 1);
//
//        String s = "";
//        //go to monday and get days in this week from previous month
//        calendar.setTimeInMillis(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(shiftDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))));
//        while (calendar.get(Calendar.MONTH) != month) {
//            s += (calendar.get(Calendar.DAY_OF_MONTH) + ", ");
//
//            //go to nest day
//            calendar.setTimeInMillis(calendar.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
//        }
//
//        //add dates from the target month
//        int dayOfWeek;
//        while (calendar.get(Calendar.MONTH) == month) {
//            s += (calendar.get(Calendar.DAY_OF_MONTH) + ", ");
//
//            //if it is last day of the week
//            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
//                Log.d("KVEST_TAG", s);
//                s = "";
//            }
//            //go to nest day
//            calendar.setTimeInMillis(calendar.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
//        }
//
//        //part of the next month
//        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
//            s += (calendar.get(Calendar.DAY_OF_MONTH) + ", ");
//
//            //go to nest day
//            calendar.setTimeInMillis(calendar.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
//        }
//
//        Log.d("KVEST_TAG", s);
//        //TODO
//    }

    private View getDayView(Context context, String text, int type) {
        TextView day = new TextView(context);
        day.setText(text);

        return day;
    }

    private long getSelectedDate() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(SELECTED_DATE_EXTRA, 0);
        } else {
            return 0;
        }
    }

    private static class CalendarDay {
        public static final int DAY_TYPE_ACTIVE = 0;
        public static final int DAY_TYPE_PASSED = 1;
        public static final int DAY_TYPE_ANOTHER_MONTH = 2;

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
        private static final int VIEW_TYPE_COUNT = 3;

        private Context context;
        private List<CalendarDay> days;
        private Calendar calendar;

        public CalendarAdapter(Context context) {
            super();

            this.context = context;
            days = new ArrayList<CalendarDay>();
            calendar = Calendar.getInstance();
        }

        public void setContent(long date)  {
            days.clear();

            calendar.setTimeInMillis(date);

            //get current month
            int month = calendar.get(Calendar.MONTH);

            //Go to first day in month
            calendar.set(Calendar.DAY_OF_MONTH, 1);

            //go to monday and get days in this week from previous month
            calendar.setTimeInMillis(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(shiftDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))));
            while (calendar.get(Calendar.MONTH) != month) {
                days.add(new CalendarDay(calendar.getTimeInMillis(), calendar.get(Calendar.DAY_OF_MONTH), CalendarDay.DAY_TYPE_ANOTHER_MONTH));

                //go to nest day
                calendar.setTimeInMillis(calendar.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
            }

            //add dates from the target month
            int dayOfWeek;
            while (calendar.get(Calendar.MONTH) == month) {
                days.add(new CalendarDay(calendar.getTimeInMillis(), calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.getTimeInMillis() < date ? CalendarDay.DAY_TYPE_PASSED : CalendarDay.DAY_TYPE_ACTIVE));

                //go to nest day
                calendar.setTimeInMillis(calendar.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
            }

            //part of the next month
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                days.add(new CalendarDay(calendar.getTimeInMillis(), calendar.get(Calendar.DAY_OF_MONTH), CalendarDay.DAY_TYPE_ANOTHER_MONTH));

                //go to nest day
                calendar.setTimeInMillis(calendar.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
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
                convertView = new TextView(context);
                switch (getItemViewType(position)) {
                    case CalendarDay.DAY_TYPE_ACTIVE :
                        convertView.setBackgroundColor(Color.GREEN);
                        break;
                    case CalendarDay.DAY_TYPE_PASSED :
                        convertView.setBackgroundColor(Color.BLUE);
                        break;
                    case CalendarDay.DAY_TYPE_ANOTHER_MONTH :
                        convertView.setBackgroundColor(Color.RED);
                        break;
                }
            }

            ((TextView)convertView).setText(Integer.toString(days.get(position).dayNumber));
            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            return days.get(position).type;
        }

        //convert to post-Soviet style(week starts from monday = 0 till sunday = 6)
        private int shiftDayOfWeek(int day) {
            return (day + 5) % 7;
        }
    }
}
