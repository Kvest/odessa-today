package com.kvest.odessatoday.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.adapter.PhotoGalleryAdapter;
import com.kvest.odessatoday.ui.widget.GridAutofitLayoutManager;

/**
 * Created by roman on 3/18/16.
 */
public class PhotoGalleryFragment extends BaseFragment {
    private static final String ARGUMENT_URLS = "com.kvest.odessatoday.argument.URLS";
    private static final String ARGUMENT_TITLE = "com.kvest.odessatoday.argument.TITLE";

    public static PhotoGalleryFragment newInstance(String[] photoURLs, String title) {
        Bundle arguments = new Bundle(2);
        arguments.putStringArray(ARGUMENT_URLS, photoURLs);
        arguments.putString(ARGUMENT_TITLE, title);

        PhotoGalleryFragment result = new PhotoGalleryFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photo_gallery_fragment, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        String[] photoURLs = getPhotoURLs();

        getActivity().setTitle(getTitle());

        TextView photosCount = (TextView)rootView.findViewById(R.id.photos_count);
        //TODO change
        photosCount.setText(photoURLs.length + " photos");

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.photos);

        //set layout manager for recycler view
        int columnWidth = getResources().getDimensionPixelSize(R.dimen.gallery_image_width) +
                          2 * getResources().getDimensionPixelSize(R.dimen.gallery_padding);
        GridAutofitLayoutManager lm = new GridAutofitLayoutManager(getActivity(), columnWidth, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);

        // specify an adapter (see also next example)
        PhotoGalleryAdapter adapter = new PhotoGalleryAdapter(rootView.getContext(), photoURLs);
        recyclerView.setAdapter(adapter);
    }

    private String[] getPhotoURLs() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getStringArray(ARGUMENT_URLS);
        } else {
            return new String[]{};
        }
    }

    private String getTitle() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getString(ARGUMENT_TITLE);
        } else {
            return "";
        }
    }
}
