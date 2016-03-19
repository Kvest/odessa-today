package com.kvest.odessatoday.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.adapter.PhotoSlideAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 14.12.14
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
public class PhotoSlideFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    private static final String ARGUMENT_URLS = "com.kvest.odessatoday.argument.URLS";
    private static final String ARGUMENT_SELECTED_URL = "com.kvest.odessatoday.argument.SELCTED_URL";
    private ViewPager viewPager;
    private TextView photoNumber;

    public static PhotoSlideFragment newInstance(String[] photoURLs, int selectedUrl) {
        Bundle arguments = new Bundle(2);
        arguments.putStringArray(ARGUMENT_URLS, photoURLs);
        arguments.putInt(ARGUMENT_SELECTED_URL, selectedUrl);

        PhotoSlideFragment result = new PhotoSlideFragment();
        result.setArguments(arguments);
        return result;
    }

    public static PhotoSlideFragment newInstance(String[] photoURLs) {
        Bundle arguments = new Bundle(1);
        arguments.putStringArray(ARGUMENT_URLS, photoURLs);

        PhotoSlideFragment result = new PhotoSlideFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photo_slide_fragment, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        photoNumber = (TextView)rootView.findViewById(R.id.photo_number);

        PhotoSlideAdapter adapter = new PhotoSlideAdapter(getActivity(), getPhotoURLs());

        viewPager = (ViewPager)rootView.findViewById(R.id.images_pager);
        viewPager.setAdapter(adapter);
        int selectedPhotoNumber = getSelectedUrl();
        viewPager.setCurrentItem(selectedPhotoNumber);
        viewPager.addOnPageChangeListener(this);

        if (adapter.getCount() > 1) {
            photoNumber.setText(getString(R.string.x_from_y, selectedPhotoNumber + 1, viewPager.getAdapter().getCount()));
        } else {
            photoNumber.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        viewPager.removeOnPageChangeListener(this);
    }

    private String[] getPhotoURLs() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getStringArray(ARGUMENT_URLS);
        } else {
            return new String[]{};
        }
    }

    private int getSelectedUrl() {
        Bundle arguments = getArguments();
        return arguments != null ? arguments.getInt(ARGUMENT_SELECTED_URL, 0) : 0;
    }

    @Override
    public void onPageSelected(int position) {
        photoNumber.setText(getString(R.string.x_from_y, position + 1, viewPager.getAdapter().getCount()));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageScrollStateChanged(int state) {}
}
