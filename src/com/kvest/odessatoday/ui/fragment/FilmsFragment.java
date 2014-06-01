package com.kvest.odessatoday.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.adapter.FilmsAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 01.06.14
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class FilmsFragment extends Fragment {
    private static final String ARGUMENT_FOR_TODAY = "com.kvest.odessatoday.ui.fragment.FilmsFragment.FOR_TODAY";
    private static final int FILMS_LOADER_ID = 1;

    private ListView filmsList;
    private FilmsAdapter adapter;

    public static FilmsFragment getInstance(boolean isForToday) {
        Bundle arguments = new Bundle(1);
        arguments.putBoolean(ARGUMENT_FOR_TODAY, isForToday);

        FilmsFragment result = new FilmsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.films_fragment, container, false);

        initComponents(root);

        return root;
    }

    private void initComponents(View root) {
        //get list view for films
        filmsList = (ListView) root.findViewById(R.id.films_list);

        //create and set an adapter
        adapter = new FilmsAdapter(getActivity(), FilmsAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        filmsList.setAdapter(adapter);
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        //load cursor
//        getLoaderManager().initLoader(FILMS_LOADER_ID, null, this);
//    }
}
