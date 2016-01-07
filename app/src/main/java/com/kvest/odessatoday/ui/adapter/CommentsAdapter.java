package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 10.08.14
 * Time: 12:23
 * To change this template use File | Settings | File Templates.
 */
public class CommentsAdapter extends CursorAdapter {
    public static final String[] PROJECTION = new String[]{Tables.Comments.Columns._ID, Tables.Comments.Columns.DATE,
                                                           Tables.Comments.Columns.NAME, Tables.Comments.Columns.TEXT,
                                                           Tables.Comments.Columns.SYNC_STATUS};

    private int dateColumnIndex = -1;
    private int nameColumnIndex = -1;
    private int textColumnIndex = -1;
    private int syncStatusColumnIndex = -1;
    private SimpleDateFormat dateFormat;

    private int needUploadBgColor;

    public CommentsAdapter(Context context) {
        super(context, null, 0);

        initResources(context);
        dateFormat = new SimpleDateFormat(context.getString(R.string.comments_date_format));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.comments_list_item, viewGroup, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.date = (TextView)view.findViewById(R.id.comment_date);
        holder.name = (TextView)view.findViewById(R.id.comment_author_name);
        holder.text = (TextView)view.findViewById(R.id.comment_text);
        holder.inProgress = (ProgressBar)view.findViewById(R.id.in_progress);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();

        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }

        long dateMillis = TimeUnit.SECONDS.toMillis(cursor.getLong(dateColumnIndex));
        holder.date.setText(dateFormat.format(dateMillis));
        holder.name.setText(Html.fromHtml(cursor.getString(nameColumnIndex)));
        holder.text.setText(Html.fromHtml(cursor.getString(textColumnIndex)));

        boolean needUpload = (cursor.getInt(syncStatusColumnIndex) & Constants.SyncStatus.NEED_UPLOAD) == Constants.SyncStatus.NEED_UPLOAD;
        holder.inProgress.setVisibility(needUpload ? View.VISIBLE : View.INVISIBLE);
        if (needUpload) {
            view.setBackgroundColor(needUploadBgColor);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private boolean isColumnIndexesCalculated() {
        return (dateColumnIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        dateColumnIndex = cursor.getColumnIndex(Tables.Comments.Columns.DATE);
        nameColumnIndex = cursor.getColumnIndex(Tables.Comments.Columns.NAME);
        textColumnIndex = cursor.getColumnIndex(Tables.Comments.Columns.TEXT);
        syncStatusColumnIndex = cursor.getColumnIndex(Tables.Comments.Columns.SYNC_STATUS);
    }

    private void initResources(Context context) {
        // The attributes you want retrieved
        int[] attrs = {R.attr.CommentNeedUploadBgColor};

        // Parse style, using Context.obtainStyledAttributes()
        TypedArray ta = context.obtainStyledAttributes(attrs);

        try {
            // Fetching the resources defined in the style
            needUploadBgColor = ta.getColor(0, Color.LTGRAY);
        } finally {
            ta.recycle();
        }
    }

    private static class ViewHolder {
        private TextView date;
        private TextView name;
        private TextView text;
        private ProgressBar inProgress;
    }
}
