package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.Sector;
import com.kvest.odessatoday.datamodel.TicketInfo;
import com.kvest.odessatoday.io.network.VolleyHelper;
import com.kvest.odessatoday.io.network.request.GetEventTicketsRequest;
import com.kvest.odessatoday.io.network.response.GetEventTicketsResponse;
import com.kvest.odessatoday.ui.animation.HeightResizeAnimation;
import com.kvest.odessatoday.utils.FontUtils;
import com.kvest.odessatoday.utils.KeyboardUtils;
import com.kvest.odessatoday.utils.SettingsSPStorage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by kvest on 15.08.16.
 */
public class OrderTicketsFragment extends BaseFragment implements Response.ErrorListener, Response.Listener<GetEventTicketsResponse> {
    private static final String ARGUMENT_EVENT_ID = "com.kvest.odessatoday.argument.EVENT_ID";
    private static final String GET_TICKETS_INFO_TAG = "com.kvest.odessatoday.tag.GET_TICKETS_INFO";
    private static final float ANIMATION_ACCELERATION_FRACTION = 1.5f; //imperatively selected value
    private static final long ANIMATION_DURATION = 400;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM, EEEE, HH:mm");

    private List<TicketInfo> tickets;

    private EditText date;
    private LinearLayout sectors;
    private RadioButton selectedSector;
    private View.OnClickListener onSectorNameClickListener;
    private EditText ticketsCount;
    private EditText name;
    private EditText phone;
    private TextView deliveryLabel;
    private PopupMenu popupMenu;

    private String currencyStr;

    public static OrderTicketsFragment newInstance(long eventId) {
        Bundle arguments = new Bundle(1);
        arguments.putLong(ARGUMENT_EVENT_ID, eventId);

        OrderTicketsFragment orderTicketsFragment = new OrderTicketsFragment();
        orderTicketsFragment.setArguments(arguments);
        return orderTicketsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadTicketsInfo();
    }

    private void loadTicketsInfo() {
        VolleyHelper volleyHelper = TodayApplication.getApplication().getVolleyHelper();

        //cancel previous request
        volleyHelper.cancelAll(GET_TICKETS_INFO_TAG);

        //create request
        GetEventTicketsRequest getEventTicketsRequest = new GetEventTicketsRequest(getEventId(), this, this);
        getEventTicketsRequest.setTag(GET_TICKETS_INFO_TAG);

        //start new request
        volleyHelper.addRequest(getEventTicketsRequest);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_tickets_fragmen, container, false);

        initResources();
        init(view);

        return view;
    }

    private void initResources() {
        currencyStr = getString(R.string.currency);
    }

    private void init(View view) {
        date = (EditText) view.findViewById(R.id.date);
        sectors = (LinearLayout)view.findViewById(R.id.sectors);
        ticketsCount = (EditText) view.findViewById(R.id.tickets_count);
        name = (EditText) view.findViewById(R.id.name);
        phone = (EditText) view.findViewById(R.id.phone);
        deliveryLabel = (TextView)view.findViewById(R.id.delivery_label);

        view.findViewById(R.id.order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderTickets();
            }
        });

        onSectorNameClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uncheck previous sector
                selectedSector.setChecked(false);

                //check new sector
                selectedSector = (RadioButton) v;
                selectedSector.setChecked(true);
            }
        };

        //set user name and phone
        Context context = getActivity();

        //loaded remembered name and phone
        name.setText(SettingsSPStorage.getUserName(context));
        phone.setText(SettingsSPStorage.getPhone(context));

        //set fonts
        Typeface helveticaneuecyrRoman = FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_ROMAN_FONT);
        date.setTypeface(helveticaneuecyrRoman);
        ticketsCount.setTypeface(helveticaneuecyrRoman);
        name.setTypeface(helveticaneuecyrRoman);
        phone.setTypeface(helveticaneuecyrRoman);
    }

    private void orderTickets() {
        if (!isOrderDataValid()) {
            return;
        }

        Activity activity = getActivity();
        if (activity != null) {
            //TODO
            Long sectorId = (Long)selectedSector.getTag();

            hideKeyboard(activity);

            rememberUserNameAndPhone(activity);
        }
    }

    private boolean isOrderDataValid() {
        boolean isDataValid = true;

        if (TextUtils.isEmpty(date.getText())) {
            date.setError(getString(R.string.date_not_selected_error));
            isDataValid = false;
        }

        if (TextUtils.isEmpty(ticketsCount.getText())) {
            ticketsCount.setError(getString(R.string.fill_field));
            isDataValid = false;
        }

        if (TextUtils.isEmpty(name.getText())) {
            name.setError(getString(R.string.fill_field));
            isDataValid = false;
        }

        if (TextUtils.isEmpty(phone.getText())) {
            phone.setError(getString(R.string.fill_field));
            isDataValid = false;
        }

        return isDataValid;
    }

    private void rememberUserNameAndPhone(Context context) {
        SettingsSPStorage.setUserName(context, name.getText().toString());
        SettingsSPStorage.setPhone(context, phone.getText().toString());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden) {
            onHide();
        } else {
            onShow();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        TodayApplication.getApplication().getVolleyHelper().cancelAll(GET_TICKETS_INFO_TAG);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        View view = getView().findViewById(R.id.order_panel);
        int finalHeight = view.getLayoutParams().height;

        if (enter) {
            //calculate target height
            view.measure(view.getLayoutParams().width, view.getLayoutParams().height);
            view.getLayoutParams().height = 1;
        }

        int statHeight = enter ? 0 : view.getHeight();
        int finishHeight = enter ? view.getMeasuredHeight() : 0;
        Animation animation = new HeightResizeAnimation(view, statHeight, finishHeight, finalHeight);
        animation.setInterpolator(new AccelerateInterpolator(ANIMATION_ACCELERATION_FRACTION));
        animation.setDuration(ANIMATION_DURATION);

        return animation;
    }

    private void onShow() {
        clearAllFields();

        //reload tickets info if it wasn't loaded yet
        if (tickets == null) {
            loadTicketsInfo();
        }

        //TODO
    }

    private void clearAllFields() {
        //clear date and sectors only if don't have sector's info yet
        //                               or if we can order tickets or then for one date
        if (tickets == null || tickets.size() > 1) {
            date.setText("");
            date.setError(null);

            clearSectors();
        }

        ticketsCount.setText("");
        ticketsCount.setError(null);

        String userNameValue = "";
        String phoneValue = "";
        Context context = getActivity();
        if (context != null) {
            userNameValue = SettingsSPStorage.getUserName(context);
            phoneValue = SettingsSPStorage.getPhone(context);
        }

        name.setText(userNameValue);
        name.setError(null);

        phone.setText(phoneValue);
        phone.setError(null);
    }

    private void onHide() {
        //TODO

        //hide keyboard
        Activity activity = getActivity();
        if (activity != null) {
            hideKeyboard(activity);
        }
    }

    private void hideKeyboard(Activity activity) {
        View currentFocuse = activity.getCurrentFocus();
        if (currentFocuse != null) {
            KeyboardUtils.hideKeyboard(activity, currentFocuse);
        }
    }

    private void fillSectors(Sector[] sectorsData) {
        Context context = sectors.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        Typeface helveticaneuecyrRoman = FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_ROMAN_FONT);

        clearSectors();

        for (Sector sector : sectorsData) {
            //create item's view
            View sectorView = inflater.inflate(R.layout.sector_item, sectors, false);

            //set name and ids
            RadioButton sectorName = (RadioButton)sectorView.findViewById(R.id.sector_name);
            sectorName.setChecked(false);
            sectorName.setText(sector.name);
            sectorName.setTag(Long.valueOf(sector.id));
            sectorName.setOnClickListener(onSectorNameClickListener);
            if (selectedSector == null) {
                selectedSector = sectorName;
                selectedSector.setChecked(true);
            }
            sectorName.setTypeface(helveticaneuecyrRoman);

            //set price
            ((TextView)sectorView.findViewById(R.id.price)).setText(sector.price + " " + currencyStr);

            sectors.addView(sectorView);
        }
    }

    private long getEventId() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_EVENT_ID, -1);
        } else {
            return -1;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //TODO
    }

    @Override
    public void onResponse(GetEventTicketsResponse response) {
        if (response.isSuccessful()) {
            tickets = response.data.tickets;
            deliveryLabel.setText(response.data.deliveryStr);

            fillDates();
        } else {
            //TODO
        }
    }

    private void fillDates() {
        if (tickets.isEmpty()) {
            return;
        }

        if (tickets.size() == 1) {
            //only one date - no choice
            onDateItemSelected(0);
        } else {
            //clear date
            date.setText("");
            date.setError(null);

            clearSectors();

            setupDateSelector();
            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
        }
    }

    private void setupDateSelector() {
        popupMenu = new PopupMenu(getActivity(), date, Gravity.FILL_HORIZONTAL);
        for (int i = 0; i < tickets.size(); i++) {
            TicketInfo ticketInfo = tickets.get(i);
            long dateValue = TimeUnit.SECONDS.toMillis(ticketInfo.date);
            popupMenu.getMenu().add(0, i, i,  DATE_FORMAT.format(dateValue));
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onDateItemSelected(item.getItemId());

                return true;
            }
        });
    }

    private void onDateItemSelected(int index) {
        TicketInfo ticketInfo = tickets.get(index);

        long dateValue = TimeUnit.SECONDS.toMillis(ticketInfo.date);
        date.setText(DATE_FORMAT.format(dateValue));
        date.setError(null);

        fillSectors(ticketInfo.sectors);
    }

    private void clearSectors() {
        sectors.removeAllViews();
        selectedSector = null;
    }
}