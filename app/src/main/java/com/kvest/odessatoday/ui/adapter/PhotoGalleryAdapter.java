package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.ui.widget.RoundNetworkImageView;

/**
 * Created by kvest on 18.03.16.
 */
public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.ViewHolder> {
    private Context context;
    private String[] photoURLs;
    private final LayoutInflater inflater;
    private final ImageLoader imageLoader;
    private int noImageResId, loadingImageResId;

    public PhotoGalleryAdapter(Context context, String[] photoURLs) {
        this.context = context;
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
            noImageResId = ta.getResourceId(0, -1);
            loadingImageResId = ta.getResourceId(1, -1);
        } finally {
            ta.recycle();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RoundNetworkImageView view = (RoundNetworkImageView) inflater.inflate(R.layout.photo_gallery_item, parent, false);
        view.setDefaultImageResId(loadingImageResId);
        view.setErrorImageResId(noImageResId);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageUrl(photoURLs[position], imageLoader);
    }

    @Override
    public int getItemCount() {
        return photoURLs.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RoundNetworkImageView imageView;

        public ViewHolder(RoundNetworkImageView view) {
            super(view);

            imageView = view;
        }
    }
}
