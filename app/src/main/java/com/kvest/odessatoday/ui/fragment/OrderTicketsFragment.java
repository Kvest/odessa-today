package com.kvest.odessatoday.ui.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.view.ContextThemeWrapper;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.PhoneCodeMetadata;
import com.kvest.odessatoday.datamodel.Sector;
import com.kvest.odessatoday.datamodel.TicketInfo;
import com.kvest.odessatoday.io.network.VolleyHelper;
import com.kvest.odessatoday.io.network.request.GetEventTicketsRequest;
import com.kvest.odessatoday.io.network.request.OrderTicketsRequest;
import com.kvest.odessatoday.io.network.response.GetEventTicketsResponse;
import com.kvest.odessatoday.io.network.response.OrderTicketsResponse;
import com.kvest.odessatoday.ui.adapter.PhoneCodeAdapter;
import com.kvest.odessatoday.ui.animation.HeightResizeAnimation;
import com.kvest.odessatoday.ui.animation.SimpleAnimatorListener;
import com.kvest.odessatoday.ui.dialog.MessageDialogFragment;
import com.kvest.odessatoday.ui.dialog.ProgressDialogFragment;
import com.kvest.odessatoday.ui.widget.MaskedEditText;
import com.kvest.odessatoday.utils.FontUtils;
import com.kvest.odessatoday.utils.KeyboardUtils;
import com.kvest.odessatoday.utils.SettingsSPStorage;
import com.kvest.odessatoday.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by kvest on 15.08.16.
 */
public class OrderTicketsFragment extends BaseFragment implements Response.ErrorListener, Response.Listener<GetEventTicketsResponse>, DialogInterface.OnDismissListener {
    private static final String ARGUMENT_EVENT_ID = "com.kvest.odessatoday.argument.EVENT_ID";
    private static final String GET_TICKETS_INFO_TAG = "com.kvest.odessatoday.tag.GET_TICKETS_INFO";
    private static final float ANIMATION_ACCELERATION_FRACTION = 1.5f; //imperatively selected value
    private static final long ANIMATION_DURATION = 400;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM, EEEE, HH:mm");
    private static final long REQUEST_MIN__TIME = 2000;
    private static final int PHONE_TARGET_LENGTH = 9;

    private List<TicketInfo> tickets;

    private View progress;
    private View orderPanel;
    private TextView errorLabel;
    private EditText date;
    private LinearLayout sectors;
    private RadioButton selectedSector;
    private View.OnClickListener onSectorNameClickListener;
    private EditText ticketsCount;
    private EditText name;
    private Spinner phoneCode;
    private PhoneCodeAdapter phoneCodeAdapter;
    private MaskedEditText phone;
    private TextView deliveryLabel;
    private PopupMenu popupMenu;
    private ProgressDialogFragment waitDialog;

    private String currencyStr;
    private int dateArrowColor, headerImageColor;
    private long loadTicketsInfoStartTime, orderTicketsStartTime;

    private HideOrderTicketsListener hideOrderTicketsListener;

    private String ticketsOrderedMessage = null;

    private Handler handler = new Handler();

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
        showProgress();

        VolleyHelper volleyHelper = TodayApplication.getApplication().getVolleyHelper();

        //cancel previous request
        volleyHelper.cancelAll(GET_TICKETS_INFO_TAG);

        //create request
        GetEventTicketsRequest getEventTicketsRequest = new GetEventTicketsRequest(getEventId(), this, this);
        getEventTicketsRequest.setTag(GET_TICKETS_INFO_TAG);

        //start new request
        volleyHelper.addRequest(getEventTicketsRequest);

        loadTicketsInfoStartTime = System.currentTimeMillis();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_tickets_fragmen, container, false);

        initResources();
        init(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ticketsOrderedMessage != null)  {
            showMessageDialog(ticketsOrderedMessage);

            ticketsOrderedMessage = null;
        }
    }

    public void setHideOrderTicketsListener(HideOrderTicketsListener hideOrderTicketsListener) {
        this.hideOrderTicketsListener = hideOrderTicketsListener;
    }

    private void showProgress() {
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
        if (orderPanel != null) {
            orderPanel.setVisibility(View.GONE);
        }
        if (errorLabel != null) {
            errorLabel.setVisibility(View.GONE);
        }
    }

    private void showOrderPanel() {
        if (orderPanel != null) {
            orderPanel.setVisibility(View.VISIBLE);
        }
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
        if (errorLabel != null) {
            errorLabel.setVisibility(View.GONE);
        }
    }

    private void showMessage(@StringRes int messageId, @ColorRes int bgColorId) {
        if (errorLabel != null) {
            errorLabel.setVisibility(View.VISIBLE);
            errorLabel.setText(messageId);
            errorLabel.setBackgroundResource(bgColorId);
        }
        if (orderPanel != null) {
            orderPanel.setVisibility(View.GONE);
        }
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
    }

    private void initResources() {
        currencyStr = getString(R.string.currency);

        // The attributes you want retrieved
        int[] attrs = {R.attr.DateArrowColor, R.attr.OrderTicketsHeaderContentColor};

        // Parse style, using Context.obtainStyledAttributes()
        TypedArray ta = getContext().obtainStyledAttributes(attrs);

        try {
            // Fetching the resources defined in the style
            dateArrowColor = ta.getColor(ta.getIndex(0), Color.BLACK);
            headerImageColor = ta.getColor(ta.getIndex(1), Color.BLACK);
        } finally {
            ta.recycle();
        }
    }

    private void init(View view) {
        progress = view.findViewById(R.id.progress);
        orderPanel = view.findViewById(R.id.order_panel);
        errorLabel = (TextView) view.findViewById(R.id.error_label);
        TextView header = (TextView) view.findViewById(R.id.order_tickets_header);
        date = (EditText) view.findViewById(R.id.date);
        sectors = (LinearLayout) view.findViewById(R.id.sectors);
        ticketsCount = (EditText) view.findViewById(R.id.tickets_count);
        name = (EditText) view.findViewById(R.id.name);
        phoneCode = (Spinner) view.findViewById(R.id.phone_code);
        phone = (MaskedEditText) view.findViewById(R.id.phone);
        deliveryLabel = (TextView) view.findViewById(R.id.delivery_label);

        phoneCodeAdapter = new PhoneCodeAdapter(getActivity());
        phoneCode.setAdapter(phoneCodeAdapter);

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

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseFragment();
            }
        });

        //loaded remembered name and phone
        Context context = getActivity();
        name.setText(SettingsSPStorage.getUserName(context));
        phone.setText(SettingsSPStorage.getPhone(context));
        int phoneCodeId = SettingsSPStorage.getPhoneCodeId(context);
        phoneCode.setSelection(phoneCodeAdapter.getPositionById(phoneCodeId));

        //set fonts
        Typeface helveticaneuecyrRoman = FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_ROMAN_FONT);
        date.setTypeface(helveticaneuecyrRoman);
        ticketsCount.setTypeface(helveticaneuecyrRoman);
        name.setTypeface(helveticaneuecyrRoman);
        phone.setTypeface(helveticaneuecyrRoman);

        //"paint" drawables
        Utils.setDrawablesColor(dateArrowColor, date.getCompoundDrawables());
        Utils.setDrawablesColor(headerImageColor, header.getCompoundDrawables());
    }

    private void collapseFragment() {
        View targetView = getView();

        ValueAnimator resizeAnimator = createResizeAnimator(targetView, targetView.getHeight(), 0);
        resizeAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (hideOrderTicketsListener != null) {
                    hideOrderTicketsListener.onHideOrderTickets();
                }
            }
        });

        resizeAnimator.start();
    }

    private ValueAnimator createResizeAnimator(final View targetView, int startHeight, int endHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(startHeight, endHeight);
        animator.setInterpolator(new AccelerateInterpolator(ANIMATION_ACCELERATION_FRACTION));
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                targetView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                targetView.requestLayout();
            }
        });

        return animator;
    }

    private void orderTickets() {
        if (!isOrderDataValid()) {
            return;
        }

        Activity activity = getActivity();
        if (activity != null) {
            if (selectedSector != null) {
                //show progress dialog
                showWaitDialog();

                //create request params
                OrderTicketsRequest.OrderInfo orderInfo = new OrderTicketsRequest.OrderInfo();
                orderInfo.sectorId = (Long) selectedSector.getTag();
                orderInfo.ticketsCount = Integer.parseInt(ticketsCount.getText().toString());
                orderInfo.name = name.getText().toString();
                orderInfo.phone = ((PhoneCodeMetadata)phoneCode.getSelectedItem()).code +  phone.getRawText();

                sendOrderTicketsRequest(getEventId(), orderInfo);

                hideKeyboard(activity);

                rememberUserNameAndPhone(activity);
            } else {
                showErrorSnackbar(activity, R.string.order_tickets_error);
            }
        }
    }

    private void showWaitDialog() {
        if (waitDialog == null) {
            waitDialog = ProgressDialogFragment.newInstance(false, false);
        }

        waitDialog.show(getFragmentManager(), null);
    }

    private void hideWaitDialog() {
        if (waitDialog != null) {
            waitDialog.dismissAllowingStateLoss();
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

        if (phone.getRawText().length() != PHONE_TARGET_LENGTH) {
            phone.setError(getString(R.string.fill_field));
            isDataValid = false;
        }

        return isDataValid;
    }

    private void rememberUserNameAndPhone(Context context) {
        SettingsSPStorage.setUserName(context, name.getText().toString());
        SettingsSPStorage.setPhoneCodeId(context, (int)phoneCode.getSelectedItemId());
        SettingsSPStorage.setPhone(context, phone.getRawText().toString());
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
    public void onDetach() {
        super.onDetach();

        onSectorNameClickListener = null;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            return null;
        }

        View view = getView();
        int finalHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        //calculate target height
        view.measure(view.getLayoutParams().width, view.getLayoutParams().height);
        view.getLayoutParams().height = 1;

        int statHeight = 0;
        int finishHeight = view.getMeasuredHeight();
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
        int phoneCodeId = -1;

        Context context = getActivity();
        if (context != null) {
            userNameValue = SettingsSPStorage.getUserName(context);
            phoneValue = SettingsSPStorage.getPhone(context);
            phoneCodeId = SettingsSPStorage.getPhoneCodeId(context);
        }

        name.setText(userNameValue);
        name.setError(null);

        phoneCode.setSelection(phoneCodeAdapter.getPositionById(phoneCodeId));

        phone.setText(phoneValue);
        phone.setError(null);
    }

    private void onHide() {
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

        if (sectorsData == null) {
            return;
        }

        for (Sector sector : sectorsData) {
            //create item's view
            View sectorView = inflater.inflate(R.layout.sector_item, sectors, false);

            //set name and ids
            RadioButton sectorName = (RadioButton) sectorView.findViewById(R.id.sector_name);
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
            ((TextView) sectorView.findViewById(R.id.price)).setText(sector.price + " " + currencyStr);

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
        //we show the progress at least REQUEST_MIN__TIME to avoid flickering
        long delay = Math.max(REQUEST_MIN__TIME - (System.currentTimeMillis() - loadTicketsInfoStartTime), 0);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showMessage(R.string.load_order_tickets_info_error, R.color.order_tickets_message_error_bg_color);
            }
        }, delay);
    }

    @Override
    public void onResponse(GetEventTicketsResponse response) {
        if (response.isSuccessful()) {
            tickets = response.data.tickets;
            deliveryLabel.setText(response.data.deliveryStr);

            fillDates();

            //we show the progress at least REQUEST_MIN__TIME to avoid flickering
            long delay = Math.max(REQUEST_MIN__TIME - (System.currentTimeMillis() - loadTicketsInfoStartTime), 0);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOrderPanel();
                }
            }, delay);
        } else {
            //we show the progress at least REQUEST_MIN__TIME to avoid flickering
            long delay = Math.max(REQUEST_MIN__TIME - (System.currentTimeMillis() - loadTicketsInfoStartTime), 0);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMessage(R.string.load_order_tickets_info_error, R.color.order_tickets_message_error_bg_color);
                }
            }, delay);
        }
    }

    private void fillDates() {
        if (tickets == null || tickets.isEmpty()) {
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

    private void sendOrderTicketsRequest(long eventId, OrderTicketsRequest.OrderInfo orderInfo) {
        //create request
        OrderTicketsRequest request = new OrderTicketsRequest(eventId, orderInfo, new Response.Listener<OrderTicketsResponse>() {
            @Override
            public void onResponse(OrderTicketsResponse response) {
                if (response.isSuccessful()) {
                    //tickets successfully ordered
                    onTicketsOrdered(response.data);
                } else {
                    onOrderTicketsError();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onOrderTicketsError();
            }
        });

        //send request
        VolleyHelper volleyHelper = TodayApplication.getApplication().getVolleyHelper();
        volleyHelper.addRequest(request);

        orderTicketsStartTime = System.currentTimeMillis();
    }

    private void onOrderTicketsError() {
        //we show the progress at least REQUEST_MIN__TIME to avoid flickering
        long delay = Math.max(REQUEST_MIN__TIME - (System.currentTimeMillis() - orderTicketsStartTime), 0);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideWaitDialog();

                showMessage(R.string.order_tickets_error, R.color.order_tickets_message_error_bg_color);

                //make to reload tickets info again
                tickets = null;
            }
        }, delay);
    }

    private void onTicketsOrdered(final String message) {
        long delay = Math.max(REQUEST_MIN__TIME - (System.currentTimeMillis() - orderTicketsStartTime), 0);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideWaitDialog();

                if (isResumed()) {
                    showMessageDialog(message);
                } else {
                    ticketsOrderedMessage = message;
                }

                //make to reload tickets info again
                tickets = null;
            }
        }, delay);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        collapseFragment();
    }

    private void showMessageDialog(String message) {
        MessageDialogFragment messageDialog = MessageDialogFragment.newInstance(message);
        messageDialog.setOnDismissListener(this);
        messageDialog.show(getFragmentManager(), null);
    }

    private void setupDateSelector() {
        Context wrapper = new ContextThemeWrapper(getActivity(), R.style.DatePopupMenu);
        popupMenu = new PopupMenu(wrapper, date, Gravity.FILL_HORIZONTAL);
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

    public interface HideOrderTicketsListener {
        void onHideOrderTickets();
    }
}
