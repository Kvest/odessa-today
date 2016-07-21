package com.kvest.odessatoday.io.network.handler;

import android.content.*;
import android.os.RemoteException;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.AnnouncementFilm;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.event.AnnouncementFilmsLoadedEvent;
import com.kvest.odessatoday.io.network.request.GetAnnouncementsRequest;
import com.kvest.odessatoday.io.network.response.GetAnnouncementsResponse;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.provider.TodayProviderContract.ANNOUNCEMENT_FILMS_URI;
import static com.kvest.odessatoday.provider.TodayProviderContract.CONTENT_AUTHORITY;
import static com.kvest.odessatoday.provider.TodayProviderContract.FILMS_URI;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by Kvest on 10.01.2015.
 */
public class LoadAnnouncementsHandler extends RequestHandler {
    private static final int DEFAULT_ANNOUNCEMENTS_LIMIT = 30;

    @Override
    public void processIntent(Context context, Intent intent) {
        int offset = 0;
        int totalCount = 0;
        boolean deletePreviousAnnouncement = true;
        boolean loadedWithoutErrors = true;
        do {
            RequestFuture<GetAnnouncementsResponse> future = RequestFuture.newFuture();
            GetAnnouncementsRequest request = new GetAnnouncementsRequest(offset, DEFAULT_ANNOUNCEMENTS_LIMIT, NetworkContract.AnnouncementRequest.ORDER_ASC, future, future);
            TodayApplication.getApplication().getVolleyHelper().addRequest(request);
            try {
                GetAnnouncementsResponse response = future.get();
                if (response.isSuccessful()) {
                    saveAnnouncementFilms(context, response.data.films, deletePreviousAnnouncement);

                    //recalculate values
                    deletePreviousAnnouncement = false;
                    offset += response.data.films.size();
                    totalCount = response.data.total_count;
                } else {
                    LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                    loadedWithoutErrors = false;
                    //notify listeners about unsuccessful loading cinemas
                    BusProvider.getInstance().post(new AnnouncementFilmsLoadedEvent(false, response.error));

                    break;
                }
            } catch (InterruptedException e) {
                LOGE(Constants.TAG, e.getLocalizedMessage());

                loadedWithoutErrors = false;
                //notify listeners about unsuccessful loading cinemas
                BusProvider.getInstance().post(new AnnouncementFilmsLoadedEvent(false, e.getLocalizedMessage()));

                break;
            } catch (ExecutionException e) {
                LOGE(Constants.TAG, e.getLocalizedMessage());

                loadedWithoutErrors = false;
                //notify listeners about unsuccessful loading cinemas
                BusProvider.getInstance().post(new AnnouncementFilmsLoadedEvent(false, e.getLocalizedMessage()));

                break;
            }
        } while (offset < totalCount);

        if (loadedWithoutErrors) {
            //notify listeners about successful loading cinemas
            BusProvider.getInstance().post(new AnnouncementFilmsLoadedEvent(true, null));
        }
    }

    private void saveAnnouncementFilms(Context context, List<AnnouncementFilm> films, boolean deletePreviousAnnouncement) {
        int count = films != null ? films.size() : 0;
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(count * 2 + (deletePreviousAnnouncement ? 1 : 0));

        if (deletePreviousAnnouncement) {
            ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(ANNOUNCEMENT_FILMS_URI).build();
            operations.add(deleteOperation);
        }

        if (films != null) {
            for (AnnouncementFilm film : films) {
                //insert film
                operations.add(ContentProviderOperation.newInsert(FILMS_URI).withValues(film.getContentValues()).build());

                //insert film id and premiere date to metadata
                ContentValues cv = new ContentValues(2);
                cv.put(TodayProviderContract.Tables.AnnouncementsMetadata.Columns.FILM_ID, film.id);
                if (film.premiere_date > 0) {
                    cv.put(TodayProviderContract.Tables.AnnouncementsMetadata.Columns.PREMIERE_DATE, film.premiere_date);
                }
                operations.add(ContentProviderOperation.newInsert(ANNOUNCEMENT_FILMS_URI).withValues(cv).build());
            }
        }

        //apply
        try {
            context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
            re.printStackTrace();
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
            oae.printStackTrace();
        }
    }
}
