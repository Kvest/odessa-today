package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.EventDetailsFragment;

/**
 * Created by kvest on 18.12.15.
 */
public class EventDetailsActivity extends BaseActivity implements EventDetailsFragment.OnShowEventCommentsListener {
    private static final String EXTRA_EVENT_ID = "com.kvest.odessatoday.extra.EVENT_ID";

    public static void start(Context context, long eventId) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_with_toolbar_layout);

        //setup action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupToolbar(toolbar);

        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            long eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                EventDetailsFragment fragment = EventDetailsFragment.getInstance(eventId);
                transaction.add(R.id.fragment_container, fragment);
            } finally {
                transaction.commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                finish();
            } else {
                getFragmentManager().popBackStack();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShowEventComments(long eventId) {
        //TODO
    }
}
