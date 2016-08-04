package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.ui.widget.RoundNetworkImageView;

/**
 * Created by kvest on 18.03.16.
 */
public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.ViewHolder> {
    private String[] photoURLs;
    private final LayoutInflater inflater;
    private final ImageLoader imageLoader;
    private int noImageResId, loadingImageResId;
    private OnItemSelectedListener onItemSelectedListener;

    public PhotoGalleryAdapter(Context context, String[] photoURLs) {
        this.photoURLs = photoURLs;

        inflater = LayoutInflater.from(context);

        initResources(context);

        //store reference to the image loader
        imageLoader = TodayApplication.getApplication().getVolleyHelper().getImageLoader();
    }

    private void initResources(Context context) {
        // The attributes you want retrieved
        int[] attrs = {R.attr.NoImage, R.attr.LoadingImage};

        // Parse style, using Context.obtainStyledAttributes()
        TypedArray ta = context.obtainStyledAttributes(attrs);

        try {
            // Fetching the resources defined in the style
            noImageResId = ta.getResourceId(ta.getIndex(0), -1);
            loadingImageResId = ta.getResourceId(ta.getIndex(1), -1);
        } finally {
            ta.recycle();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RoundNetworkImageView view = (RoundNetworkImageView) inflater.inflate(R.layout.photo_gallery_item, parent, false);
        view.setDefaultImageResId(loadingImageResId);
        view.setErrorImageResId(noImageResId);
        return new ViewHolder(view, onItemSelectedListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageUrl(photoURLs[position], imageLoader);
    }

    @Override
    public int getItemCount() {
        return photoURLs.length;
    }

    public void setPhotoURLs(String[] photoURLs) {
        this.photoURLs = photoURLs;

        notifyDataSetChanged();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RoundNetworkImageView imageView;
        private OnItemSelectedListener onItemSelectedListener;

        public ViewHolder(RoundNetworkImageView view, OnItemSelectedListener onItemSelectedListener) {
            super(view);

            imageView = view;
            imageView.setOnClickListener(this);

            this.onItemSelectedListener = onItemSelectedListener;
        }

        @Override
        public void onClick(View v) {
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(v, getAdapterPosition(), getItemId());
            }
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(View view, int position, long id);
    }
}
