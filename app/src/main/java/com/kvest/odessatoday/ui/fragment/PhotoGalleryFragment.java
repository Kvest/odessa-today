package com.kvest.odessatoday.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.io.network.event.UploadPhotoEvent;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.PhotoGalleryAdapter;
import com.kvest.odessatoday.ui.dialog.ProgressDialogFragment;
import com.kvest.odessatoday.ui.widget.GridAutofitLayoutManager;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Utils;
import com.squareup.otto.Subscribe;

/**
 * Created by roman on 3/18/16.
 */
public class PhotoGalleryFragment extends BaseFragment implements PhotoGalleryAdapter.OnItemSelectedListener {
    private final String[] PROJECTION = {MediaStore.Images.Media.DATA};
    private static final int PICK_PHOTO_REQUEST = 1;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION = 1;

    private static final String ARGUMENT_URLS = "com.kvest.odessatoday.argument.URLS";
    private static final String ARGUMENT_TITLE = "com.kvest.odessatoday.argument.TITLE";
    private static final String ARGUMENT_TARGET_ID = "com.kvest.odessatoday.argument.TARGET_ID";
    private static final String ARGUMENT_TARGET_TYPE = "com.kvest.odessatoday.argument.TARGET_TYPE";

    private OnPhotoSelectedListener onPhotoSelectedListener;
    private ProgressDialogFragment progressDialog;

    public static PhotoGalleryFragment newInstance(String[] photoURLs, String title, long targetId, int targetType) {
        Bundle arguments = new Bundle(4);
        arguments.putStringArray(ARGUMENT_URLS, photoURLs);
        arguments.putString(ARGUMENT_TITLE, title);
        arguments.putLong(ARGUMENT_TARGET_ID, targetId);
        arguments.putInt(ARGUMENT_TARGET_TYPE, targetType);

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

        Activity activity = getActivity();
        activity.setTitle(getTitle());

        TextView photosCount = (TextView)rootView.findViewById(R.id.photos_count);
        photosCount.setText(Utils.createCountString(activity, photoURLs.length, Utils.PHOTOS_COUNT_PATTERNS));

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.photos);

        //set layout manager for recycler view
        int columnWidth = getResources().getDimensionPixelSize(R.dimen.gallery_image_width) +
                          2 * getResources().getDimensionPixelSize(R.dimen.gallery_padding);
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(activity, columnWidth, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        PhotoGalleryAdapter adapter = new PhotoGalleryAdapter(rootView.getContext(), photoURLs);
        adapter.setOnItemSelectedListener(this);
        recyclerView.setAdapter(adapter);

        rootView.findViewById(R.id.propose_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proposePhoto();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);

        hideWaitDialog();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onPhotoSelectedListener = (OnPhotoSelectedListener) activity;
        } catch (ClassCastException cce) {}
    }

    @Override
    public void onDetach() {
        super.onDetach();

        onPhotoSelectedListener = null;
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

    private long getTargetId() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_TARGET_ID);
        } else {
            return -1;
        }
    }

    private int getTargetType() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getInt(ARGUMENT_TARGET_TYPE);
        } else {
            return -1;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
            sendPhoto(intent.getData());
        }
    }

    private void sendPhoto(Uri data) {
        Context context = getActivity();
        if (context == null) {
            return;
        }

        showWaitDialog();

        String photoPath = getPhotoPath(context, data);
        NetworkService.uploadPhoto(context, getTargetId(), getTargetType(), photoPath);
    }

    private void showWaitDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialogFragment.newInstance(false);
        }

        progressDialog.show(getFragmentManager(), null);
    }

    private void hideWaitDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showUploadPhotoError() {
        Activity activity = getActivity();
        if (activity != null) {
            showErrorSnackbar(activity, R.string.error_uploading_photo);
        }
    }

    @Subscribe
    public void onUploadPhotoEvent(UploadPhotoEvent event) {
        hideWaitDialog();

        if (!event.isSuccessful()) {
            showUploadPhotoError();
        }
    }

    private String getPhotoPath(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, PROJECTION, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    private void proposePhoto() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        //check read external storage permission
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION);
        } else {
            pickPhoto();
        }
    }

    private void pickPhoto() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto, PICK_PHOTO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickPhoto();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //"never ask again" selected
                    showErrorSnackbar(getActivity(), R.string.error_propose_photo_no_permission_strict);
                } else {
                    showErrorSnackbar(getActivity(), R.string.error_propose_photo_no_permission);
                }
            }
        }
    }

    @Override
    public void onItemSelected(View view, int position, long id) {
        if (onPhotoSelectedListener != null) {
            onPhotoSelectedListener.onPhotoSelected(getPhotoURLs(), position);
        }
    }

    public interface OnPhotoSelectedListener {
        void onPhotoSelected(String[] photoURLs, int position);
    }
}
