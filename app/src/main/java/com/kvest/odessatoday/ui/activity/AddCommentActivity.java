package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.AddCommentFragment;

/**
 * Created by roman on 1/13/16.
 */
public class AddCommentActivity extends BaseActivity {
    private static final String EXTRA_TARGET_ID = "com.kvest.odessatoday.extra.TARGET_ID";
    private static final String EXTRA_TARGET_TYPE = "com.kvest.odessatoday.extra.TARGET_TYPE";

    public static void start(Context context, long targetId, int targetType) {
        Intent intent = new Intent(context, AddCommentActivity.class);
        intent.putExtra(EXTRA_TARGET_ID, targetId);
        intent.putExtra(EXTRA_TARGET_TYPE, targetType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_with_toolbar_layout);

        //setup action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setupToolbar(toolbar);

        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            long targetId = intent.getLongExtra(EXTRA_TARGET_ID, -1);
            int targetType = intent.getIntExtra(EXTRA_TARGET_TYPE, -1);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                AddCommentFragment addCommentFragment = AddCommentFragment.newInstance(targetId, targetType);
                transaction.add(R.id.fragment_container, addCommentFragment);
            } finally {
                transaction.commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_comment_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
