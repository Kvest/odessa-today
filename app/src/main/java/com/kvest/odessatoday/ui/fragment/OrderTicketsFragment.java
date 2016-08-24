package com.kvest.odessatoday.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private long[] ids;

    private EditText date;
    private RadioGroup sectors;
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
        sectors = (RadioGroup)view.findViewById(R.id.sectors);
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
    }

    private void orderTickets() {
        if (!isOrderDataValid()) {
            return;
        }

        //TODO
        Log.d("KVEST_TAG", "order=" + ids[sectors.getCheckedRadioButtonId()]);
    }

    private boolean isOrderDataValid() {
        //TODO

        return true;
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
        //reload tickets info if it wasn't loaded yet
        if (tickets == null) {
            loadTicketsInfo();
        }

        //TODO
    }

    private void onHide() {
        //TODO
    }

    private void fillSectors(Sector[] sectorsData) {
        LayoutInflater inflater = LayoutInflater.from(sectors.getContext());

        sectors.removeAllViews();

        boolean firstItem = true;
        int i = 0;
        ids = new long[sectorsData.length];
        for (Sector sector : sectorsData) {
            //create item's view
            View sectorView = inflater.inflate(R.layout.sector_item, sectors, false);

            //set name and ids
            RadioButton sectorName = (RadioButton)sectorView.findViewById(R.id.sector_name);
            sectorName.setChecked(firstItem);
            sectorName.setText(sector.name);
            sectorName.setId(i);
            ids[i++] = sector.id;

            //set price
            ((TextView)sectorView.findViewById(R.id.price)).setText(sector.price + " " + currencyStr);

            sectors.addView(sectorView);

            firstItem = false;
        }

        sectors.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("KVEST_TAG", "checkedId=" + checkedId);
            }
        });
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

            //clear sectors
            sectors.removeAllViews();

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

        fillSectors(ticketInfo.sectors);
    }
}
