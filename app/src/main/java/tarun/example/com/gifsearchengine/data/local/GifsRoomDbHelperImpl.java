package tarun.example.com.gifsearchengine.data.local;

import android.content.Context;
import android.os.AsyncTask;

import tarun.example.com.gifsearchengine.data.model.room.UserRatedGif;

/**
 * The implementation class corresponding to the {@link GifsRoomDbHelper} interface where all methods
 * related to local database data calls are defined.
 */
public class GifsRoomDbHelperImpl implements GifsRoomDbHelper {

    private final GifDao gifDao;

    public GifsRoomDbHelperImpl(Context appContext) {
        RatedGifsDatabase ratedGifsDatabase = RatedGifsDatabase.getDatabase(appContext);
        gifDao = ratedGifsDatabase.gifDao();
    }

    @Override
    public void fetchRatedGifById(String id, getRatedGifByIdAsyncTask.GetRatedGifAsyncResponseListener responseCallback) {
        new getRatedGifByIdAsyncTask(gifDao, responseCallback).execute(id);
    }

    @Override
    public void insertRatedGif(UserRatedGif ratedGif) {
        new insertGifAsyncTask(gifDao).execute(ratedGif);
    }

    @Override
    public void updateRatedGif(UserRatedGif ratedGif) {
        new updateGifAsyncTask(gifDao).execute(ratedGif);
    }

    /**
     * AsyncTask class to get the gif object stored in local db by id.
     */
    public static class getRatedGifByIdAsyncTask extends AsyncTask<String, Void, UserRatedGif> {

        private final GifDao dao;
        private final GetRatedGifAsyncResponseListener responseCallback;

        getRatedGifByIdAsyncTask(GifDao dao, GetRatedGifAsyncResponseListener responseCallback) {
            this.dao = dao;
            this.responseCallback = responseCallback;
        }

        @Override
        protected UserRatedGif doInBackground(final String... params) {
            return dao.getRatedGifByIdAsync(params[0]);
        }

        @Override
        protected void onPostExecute(UserRatedGif userRatedGif) {
            super.onPostExecute(userRatedGif);
            responseCallback.responseReceived(userRatedGif);
        }

        // Listener interface to send back the results.
        public interface GetRatedGifAsyncResponseListener {
            void responseReceived(UserRatedGif userRatedGif);
        }
    }

    /**
     * AsyncTask class to insert the gif object in local db.
     */
    private static class insertGifAsyncTask extends AsyncTask<UserRatedGif, Void, Void> {

        private final GifDao mAsyncTaskDao;

        insertGifAsyncTask(GifDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserRatedGif... params) {
            mAsyncTaskDao.insertRatedGif(params[0]);
            return null;
        }
    }

    /**
     * AsyncTask class to update the gif object stored in local db.
     */
    private static class updateGifAsyncTask extends AsyncTask<UserRatedGif, Void, Void> {

        private final GifDao mAsyncTaskDao;

        updateGifAsyncTask(GifDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserRatedGif... params) {
           mAsyncTaskDao.updateRatedGif(params[0]);
           return null;
        }
    }
}
