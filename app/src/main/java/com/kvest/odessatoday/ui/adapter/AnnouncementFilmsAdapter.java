package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 29.12.14
 * Time: 20:58
 * To change this template use File | Settings | File Templates.
 */
public class AnnouncementFilmsAdapter extends CursorAdapter {
    private static final String PREMIERE_DATE_FORMAT_PATTERN = "dd MMMM yyyy";
    private final SimpleDateFormat PREMIERE_DATE_FORMAT = new SimpleDateFormat(PREMIERE_DATE_FORMAT_PATTERN);

    public static final String[] PROJECTION = new String[]{Tables.AnnouncementFilmsView.Columns.FILM_ID + " as " + Tables.AnnouncementFilmsView.Columns._ID,
                                                           Tables.AnnouncementFilmsView.Columns.IMAGE, Tables.AnnouncementFilmsView.Columns.NAME,
                                                           Tables.AnnouncementFilmsView.Columns.GENRE, Tables.AnnouncementFilmsView.Columns.RATING,
                                                           Tables.AnnouncementFilmsView.Columns.COMMENTS_COUNT, Tables.AnnouncementFilmsView.Columns.IS_PREMIERE,
                                                           Tables.AnnouncementFilmsView.Columns.FILM_DURATION, Tables.AnnouncementFilmsView.Columns.PREMIERE_DATE};

    private int imageColumnIndex = -1;
    private int nameColumnIndex = -1;
    private int genreColumnIndex = -1;
    private int ratingColumnIndex = -1;
    private int commentsCountColumnIndex = -1;
    private int isPremiereColumnIndex = -1;
    private int filmDurationColumnIndex = -1;
    private int premiereDateColumnIndex = -1;

    public AnnouncementFilmsAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.announcement_films_list_item, viewGroup, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.image = (NetworkImageView)view.findViewById(R.id.film_image);
        holder.image.setDefaultImageResId(R.drawable.loading_poster);
        holder.image.setErrorImageResId(R.drawable.no_poster);
        holder.name = (TextView)view.findViewById(R.id.film_name);
        holder.genre = (TextView)view.findViewById(R.id.genre);
        holder.premiereDate = (TextView)view.findViewById(R.id.premiere_date);
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
        holder.filmDurationIcon = (ImageView) view.findViewById(R.id.film_duration_icon);
        holder.filmDuration = (TextView) view.findViewById(R.id.film_duration);
        holder.commentsCount.setText(Integer.toString(cursor.getInt(commentsCountColumnIndex)));
        holder.image.setImageUrl(cursor.getString(imageColumnIndex), TodayApplication.getApplication().getVolleyHelper().getImageLoader());
        holder.isPremiere.setVisibility(cursor.getInt(isPremiereColumnIndex) == Constants.Premiere.IS_PREMIERE ? View.VISIBLE : View.GONE);

        int filmDurationValue = cursor.getInt(filmDurationColumnIndex);
        if (filmDurationValue > 0) {
            holder.filmDuration.setVisibility(View.VISIBLE);
            holder.filmDurationIcon.setVisibility(View.VISIBLE);
            holder.filmDuration.setText(context.getString(R.string.film_duration, filmDurationValue));
        } else {
            holder.filmDuration.setVisibility(View.GONE);
            holder.filmDurationIcon.setVisibility(View.GONE);
        }

        if (!cursor.isNull(premiereDateColumnIndex)) {
            long premiereDateValue = TimeUnit.SECONDS.toMillis(cursor.getLong(premiereDateColumnIndex));
            String text = context.getString(R.string.premiere_date, PREMIERE_DATE_FORMAT.format(premiereDateValue));

            holder.premiereDate.setVisibility(View.VISIBLE);
            holder.premiereDate.setText(text);
        } else {
            holder.premiereDate.setVisibility(View.GONE);
        }
    }

    private boolean isColumnIndexesCalculated() {
        return (imageColumnIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        imageColumnIndex = cursor.getColumnIndex(Tables.AnnouncementFilmsView.Columns.IMAGE);
        nameColumnIndex = cursor.getColumnIndex(Tables.AnnouncementFilmsView.Columns.NAME);
        genreColumnIndex = cursor.getColumnIndex(Tables.AnnouncementFilmsView.Columns.GENRE);
        ratingColumnIndex = cursor.getColumnIndex(Tables.AnnouncementFilmsView.Columns.RATING);
        commentsCountColumnIndex = cursor.getColumnIndex(Tables.AnnouncementFilmsView.Columns.COMMENTS_COUNT);
        isPremiereColumnIndex = cursor.getColumnIndex(Tables.AnnouncementFilmsView.Columns.IS_PREMIERE);
        filmDurationColumnIndex = cursor.getColumnIndex(Tables.AnnouncementFilmsView.Columns.FILM_DURATION);
        premiereDateColumnIndex  = cursor.getColumnIndex(Tables.AnnouncementFilmsView.Columns.PREMIERE_DATE);
    }

    private static class ViewHolder {
        public NetworkImageView image;
        private TextView name;
        private TextView genre;
        private TextView premiereDate;
        private RatingBar rating;
        private ImageView filmDurationIcon;
        private TextView filmDuration;
        private TextView commentsCount;
        private TextView isPremiere;
    }
}