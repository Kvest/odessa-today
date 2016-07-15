package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 01.06.14
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public class FilmsAdapter extends CursorAdapter {
    public static final String[] PROJECTION = new String[]{Tables.Films.Columns.FILM_ID + " as " + Tables.Films.Columns._ID,
                                                           Tables.Films.Columns.IMAGE, Tables.Films.Columns.NAME,
                                                           Tables.Films.Columns.GENRE, Tables.Films.Columns.FILM_DURATION,
                                                           Tables.Films.Columns.GENRE, Tables.Films.Columns.RATING,
                                                           Tables.Films.Columns.COMMENTS_COUNT, Tables.Films.Columns.IS_PREMIERE};
    private int imageColumnIndex = -1;
    private int nameColumnIndex = -1;
    private int genreColumnIndex = -1;
    private int ratingColumnIndex = -1;
    private int filmDurationColumnIndex = -1;
    private int commentsCountColumnIndex = -1;
    private int isPremiereColumnIndex = -1;

    private int evenItemBgResId, oddItemBgResId;
    private int noImageResId, loadingImageResId;

    public FilmsAdapter(Context context) {
        super(context, null, 0);

        initResources(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.films_list_item, viewGroup, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.image = (NetworkImageView)view.findViewById(R.id.film_poster);
        holder.image.setDefaultImageResId(loadingImageResId);
        holder.image.setErrorImageResId(noImageResId);
        holder.name = (TextView)view.findViewById(R.id.film_name);
        holder.genre = (TextView)view.findViewById(R.id.genre);
        holder.rating = (RatingBar)view.findViewById(R.id.film_rating);
        holder.filmDuration = (TextView) view.findViewById(R.id.film_duration);
        holder.commentsCount = (TextView) view.findViewById(R.id.comments_count);
        holder.isPremiere = (TextView)view.findViewById(R.id.is_premiere);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();

        //set view background
        if (cursor.getPosition() % 2 == 0) {
            view.setBackgroundResource(evenItemBgResId);
        } else {
            view.setBackgroundResource(oddItemBgResId);
        }

        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }
        holder.name.setText(cursor.getString(nameColumnIndex));
        holder.genre.setText(cursor.getString(genreColumnIndex));
        holder.genre.setVisibility(TextUtils.isEmpty(holder.genre.getText()) ? View.GONE : View.VISIBLE);

        int filmDurationValue = cursor.getInt(filmDurationColumnIndex);
        if (filmDurationValue > 0) {
            holder.filmDuration.setText(context.getString(R.string.film_duration, filmDurationValue));
        } else {
            holder.filmDuration.setText(R.string.film_duration_unknown);
        }

        holder.rating.setRating(cursor.getFloat(ratingColumnIndex));
        holder.commentsCount.setText(Utils.createCountString(context, cursor.getInt(commentsCountColumnIndex), Utils.COMMENTS_COUNT_PATTERNS));
        holder.image.setImageUrl(cursor.getString(imageColumnIndex), TodayApplication.getApplication().getVolleyHelper().getImageLoader());
        holder.isPremiere.setVisibility(cursor.getInt(isPremiereColumnIndex) == Constants.Premiere.IS_PREMIERE ? View.VISIBLE : View.GONE);
    }

    private void initResources(Context context) {
        // The attributes you want retrieved
        int[] attrs = {R.attr.ListEvenItemBgRes,
                       R.attr.ListOddItemBgRes,
                       R.attr.NoImage,
                       R.attr.LoadingImage};

        // Parse style, using Context.obtainStyledAttributes()
        TypedArray ta = context.obtainStyledAttributes(attrs);

        try {
            // Fetching the resources defined in the style
            evenItemBgResId = ta.getResourceId(0, 0);
            oddItemBgResId = ta.getResourceId(1, 0);
            noImageResId = ta.getResourceId(2, -1);
            loadingImageResId = ta.getResourceId(3, -1);
        } finally {
            ta.recycle();
        }
    }

    private boolean isColumnIndexesCalculated() {
        return (imageColumnIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        imageColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.IMAGE);
        nameColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.NAME);
        genreColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.GENRE);
        filmDurationColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.FILM_DURATION);
        ratingColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.RATING);
        commentsCountColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.COMMENTS_COUNT);
        isPremiereColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.IS_PREMIERE);
    }

    private static class ViewHolder {
        public NetworkImageView image;
        private TextView name;
        private TextView genre;
        private RatingBar rating;
        private TextView filmDuration;
        private TextView commentsCount;
        private TextView isPremiere;
    }
}
