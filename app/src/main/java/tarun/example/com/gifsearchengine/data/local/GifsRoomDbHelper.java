package tarun.example.com.gifsearchengine.data.local;

import tarun.example.com.gifsearchengine.data.model.room.UserRatedGif;

/**
 * A helper interface to declare all data calls to Local database.
 */
public interface GifsRoomDbHelper {

    void fetchRatedGifById(String id, GifsRoomDbHelperImpl.getRatedGifByIdAsyncTask.GetRatedGifAsyncResponseListener responseCallback);

    void insertRatedGif(UserRatedGif ratedGif);

    void updateRatedGif(UserRatedGif ratedGif);

}
