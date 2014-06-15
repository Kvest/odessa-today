package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;

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
                                                           Tables.Films.Columns.GENRE, Tables.Films.Columns.DIRECTOR, Tables.Films.Columns.ACTORS,
                                                           Tables.Films.Columns.RATING, Tables.Films.Columns.COMMENTS_COUNT };

    public FilmsAdapter(Context context) {
        super(context, null, 0);
    }

    private int imageColumnIndex = -1;
    private int nameColumnIndex = -1;
    private int genreColumnIndex = -1;
    private int directorColumnIndex = -1;
    private int actorsColumnIndex = -1;
    private int ratingColumnIndex = -1;
    private int commentsCountColumnIndex = -1;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.films_list_item, viewGroup, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.image = (NetworkImageView)view.findViewById(R.id.film_image);
        holder.name = (TextView)view.findViewById(R.id.film_name);
        holder.genre = (TextView)view.findViewById(R.id.genre);
        holder.rating = (RatingBar)view.findViewById(R.id.film_rating);
        holder.commentsCount = (TextView)view.findViewById(R.id.comments_count);
        holder.director = (TextView)view.findViewById(R.id.director);
        holder.actors = (TextView)view.findViewById(R.id.actors);
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
        holder.rating.setRating(cursor.getInt(ratingColumnIndex));
        holder.commentsCount.setText(Integer.toString(cursor.getInt(commentsCountColumnIndex)));
        holder.director.setText(cursor.getString(directorColumnIndex));
        holder.actors.setText(cursor.getString(actorsColumnIndex));
        holder.image.setImageUrl(cursor.getString(imageColumnIndex),
                                 TodayApplication.getApplication().getVolleyHelper().getImageLoader());
    }

    private boolean isColumnIndexesCalculated() {
        return (imageColumnIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        imageColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.IMAGE);
        nameColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.NAME);
        genreColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.GENRE);
        directorColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.DIRECTOR);
        actorsColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.ACTORS);
        ratingColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.RATING);
        commentsCountColumnIndex = cursor.getColumnIndex(Tables.Films.Columns.COMMENTS_COUNT);
    }

    private String getString(Context context, int resId, Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }

    private static class ViewHolder {
        public NetworkImageView image;
        private TextView name;
        private TextView director;
        private TextView actors;
        private TextView genre;
        private RatingBar rating;
        private TextView commentsCount;
    }
}
