package com.kvest.odessatoday.ui.activity;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.request.GetTodayFilmsRequest;
import com.kvest.odessatoday.io.response.GetTodayFilmsResponse;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.ui.fragment.FilmsFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MyActivity extends TodayBaseActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        test();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                FilmsFragment filmsFragment = FilmsFragment.getInstance(true);
                transaction.add(R.id.fragment_container, filmsFragment);
            } finally {
                transaction.commit();
            }
        }
    }

    private void test() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                RequestFuture<GetTodayFilmsResponse> future = RequestFuture.newFuture();
//                GetTodayFilmsRequest req = new GetTodayFilmsRequest(future, future);
//                TodayApplication.getApplication().getVolleyHelper().addRequest(req);
//
//                try {
//                    GetTodayFilmsResponse response = future.get(); // this will block
//                    response.isSuccessful();
//                } catch (InterruptedException e) {
//                    // exception handling
//                } catch (ExecutionException e) {
//                    // exception handling
//                    e.getLocalizedMessage();
//                }
//            }
//        }).start();


//        GetTodayFilmsRequest req = new GetTodayFilmsRequest(new Response.Listener<GetTodayFilmsResponse>() {
//            @Override
//            public void onResponse(GetTodayFilmsResponse response) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
//        TodayApplication.getApplication().getVolleyHelper().addRequest(req);



//        Uri filmsUri = Uri.withAppendedPath(TodayProviderContract.BASE_CONTENT_URI, TodayProviderContract.FILMS_PATH);
//        getContentResolver().delete(filmsUri, null, null);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss");
//        Date d = new Date(1401310800000L);
//        Log.d("KVEST_TAG", "d=" + sdf.format(d));
    }
}
