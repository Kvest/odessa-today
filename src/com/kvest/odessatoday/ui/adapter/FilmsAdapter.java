package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
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

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 01.06.14
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public class FilmsAdapter extends CursorAdapter {
    public static final String[] PROJECTION = new String[]{Tables.Films.Columns._ID, Tables.Films.Columns.IMAGE, Tables.Films.Columns.NAME,
                                                           Tables.Films.Columns.GENRE, Tables.Films.Columns.RATING, Tables.Films.Columns.COMMENTS_COUNT,
                                                           Tables.Films.Columns.IS_PREMIERE};

    public FilmsAdapter(Context context) {
        super(context, null, 0);
    }

    private int imageColumnIndex = -1;
    private int nameColumnIndex = -1;
    private int genreColumnIndex = -1;
    private int ratingColumnIndex = -1;
    private int commentsCountColumnIndex = -1;
    private int isPremiereColumnIndex = -1;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.films_list_item, viewGroup, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.image = (NetworkImageView)view.findViewById(R.id.film_image);
        holder.image.setDefaultImageResId(R.drawable.loading_poster);
        holder.image.setErrorImageResId(R.drawable.no_poster);
        holder.name = (TextView)view.findViewById(R.id.film_name);
        holder.genre = (TextView)view.findViewById(R.id.genre);
        holder.rating = (RatingBar)view.findViewById(R.id.film_rating);
        holder.commentsCount = (TextView)view.findViewById(R.id.comments_count);
        holder.isPremiere = (TextView)view.findViewById(R.id.is_premiere);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();

        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }
        holder.name.setText(cursor.getString(nameColumnIndex));
        holder.genre.setText(cursor.getString(genreColumnIndex));
        holder.genre.setVisibility(TextUtils.isEmpty(holder.genre.getText()) ? View.GONE : View.VISIBLE);
        holder.rating.setRating(cursor.getFloat(ratingColumnIndex));
        holder.commentsCount.setText(Integer.toString(cursor.getInt(commentsCountColumnIndex)));
        holder.image.setImageUrl(cursor.getString(imageColumnIndex),
                                 TodayApplication.getApplication().getVolleyHelper().getImageLoader());
        holder.isPremiere.setVisibility(cursor.getInt(isPremiereColumnIndex) == Constants.Premiere.IS_PREMIERE ? View.VISIBLE : View.GONE);
    }

    private boolean isColumnIndexesCalculated() {
        return (imageColumnIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        imageColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.IMAGE);
        nameColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.NAME);
        genreColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.GENRE);
        ratingColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.RATING);
        commentsCountColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.COMMENTS_COUNT);
        isPremiereColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.IS_PREMIERE);
    }

    private String getString(Context context, int resId, Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }

    private static class ViewHolder {
        public NetworkImageView image;
        private TextView name;
        private TextView genre;
        private RatingBar rating;
        private TextView commentsCount;
        private TextView isPremiere;
    }
}
