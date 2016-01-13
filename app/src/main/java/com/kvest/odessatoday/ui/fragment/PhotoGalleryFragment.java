package com.kvest.odessatoday.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.adapter.PhotoGalleryAdapter;
import com.kvest.odessatoday.ui.widget.CirclePagerIndicator;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 14.12.14
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
public class PhotoGalleryFragment extends BaseFragment {
    private static final String ARGUMENT_PHOTO_URLS = "com.kvest.odessatoday.argument.PHOTO_URLS";

    public static PhotoGalleryFragment newInstance(String[] photoURLs) {
        Bundle arguments = new Bundle(1);
        arguments.putStringArray(ARGUMENT_PHOTO_URLS, photoURLs);

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
        PhotoGalleryAdapter adapter = new PhotoGalleryAdapter(getActivity(), getPhotoURLs());

        ViewPager viewPager = (ViewPager)rootView.findViewById(R.id.images_pager);
        viewPager.setAdapter(adapter);

        CirclePagerIndicator pagerIndicator = (CirclePagerIndicator) rootView.findViewById(R.id.pager_indicator);
        pagerIndicator.setViewPager(viewPager);
    }

    private String[] getPhotoURLs() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getStringArray(ARGUMENT_PHOTO_URLS);
        } else {
            return new String[]{};
        }
    }
}
