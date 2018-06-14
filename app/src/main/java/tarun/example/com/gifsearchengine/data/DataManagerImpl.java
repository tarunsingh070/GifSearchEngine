package tarun.example.com.gifsearchengine.data;

import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;

import okhttp3.Callback;
import tarun.example.com.gifsearchengine.data.local.GifsRoomDbHelper;
import tarun.example.com.gifsearchengine.data.local.GifsRoomDbHelperImpl;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;
import tarun.example.com.gifsearchengine.data.model.room.UserRatedGif;
import tarun.example.com.gifsearchengine.data.remote.giphy.GiphyRestClient;
import tarun.example.com.gifsearchengine.data.remote.firebase.FirebaseDbHelper;
import tarun.example.com.gifsearchengine.data.remote.firebase.FirebaseDbHelperImpl;

/**
 * The implementation class corresponding to the {@link DataManager} interface where all methods
 * related to data calls are defined.
 */
public class DataManagerImpl implements DataManager, GifsRoomDbHelperImpl.getRatedGifByIdAsyncTask.GetRatedGifAsyncResponseListener {

    private GiphyRestClient restClient;
    private FirebaseDbHelper firebaseDbHelper;
    private GifsRoomDbHelper gifsRoomDbHelper;
    private GetRatedGifByIdResponseListener responseCallback;

    public DataManagerImpl() {
        restClient = GiphyRestClient.getRestClient();
        firebaseDbHelper = new FirebaseDbHelperImpl();
    }

    /**
     * Parameterized constructor with Application context and listener callback to get back results.
     * @param appContext Application context
     * @param responseCallback Listener callback to get back results.
     */
    public DataManagerImpl(Context appContext, GetRatedGifByIdResponseListener responseCallback) {
        this();
        gifsRoomDbHelper = new GifsRoomDbHelperImpl(appContext);
        this.responseCallback = responseCallback;
    }

    /**
     * Get trending gif results starting from beginning of results.
     * @param callback Callback where response is to be received.
     */
    @Override
    public void getTrendingGifs(Callback callback) {
        restClient.getTrendingGifs(0, callback);
    }

    /**
     * Get trending gif results starting from an offset rather than from beginning.
     * @param offset Offset value from where results should be retrieved.
     * @param callback Callback where response is to be received.
     */
    @Override
    public void getTrendingGifs(int offset, Callback callback) {
        restClient.getTrendingGifs(offset, callback);
    }

    /**
     * Get searched gif results according to the searchTerm starting from beginning of results.
     * @param searchTerm Term to be searched for gifs.
     * @param callback Callback where search results are to be received.
     */
    @Override
    public void getSearchedGifs(String searchTerm, Callback callback) {
        restClient.getSearchedGifs(searchTerm, 0, callback);
    }

    /**
     * Get searched gif results according to the searchTerm starting from an offset rather than from beginning.
     * @param searchTerm Term to be searched for gifs.
     * @param offset Offset value from where results should be retrieved.
     * @param callback Callback where search results are to be received.
     */
    @Override
    public void getSearchedGifs(String searchTerm, int  offset, Callback callback) {
        restClient.getSearchedGifs(searchTerm, offset, callback);
    }

    /**
     * Add a new gif object or update an existing one in the firebase database.
     * @param firebaseGif Firebase gif object to be stored.
     */
    @Override
    public void addOrUpdateGif(FirebaseGif firebaseGif) {
        firebaseDbHelper.addOrUpdateGif(firebaseGif);
    }

    /**
     * Get the list of all ranked gifs from the firebase database.
     * @param listener
     */
    @Override
    public void getRankedGifsFromFirebase(ValueEventListener listener) {
        firebaseDbHelper.getRankedGifsFromFirebase(listener);
    }

    /**
     * Get the list of all ranked gifs from the firebase database.
     * @param listener
     */
    @Override
    public void getRankedGifsFromFirebase(ChildEventListener listener) {
        firebaseDbHelper.getRankedGifsFromFirebase(listener);
    }

    /**
     * Fetch the rated gif object stored in local db by id.
     * @param id Gif ID to search for the gif in db.
     */
    @Override
    public void fetchRatedGifById(String id) {
        gifsRoomDbHelper.fetchRatedGifById(id, this);
    }

    /**
     * Insert the gif object in local db.
     * @param ratedGif Gif object to be inserted into db
     */
    @Override
    public void insertRatedGif(UserRatedGif ratedGif) {
        gifsRoomDbHelper.insertRatedGif(ratedGif);
    }

    /**
     * Update the existing gif object in local db.
     * @param ratedGif Gif object to be updated in db.
     */
    @Override
    public void updateRatedGif(UserRatedGif ratedGif) {
        gifsRoomDbHelper.updateRatedGif(ratedGif);
    }

    @Override
    public void responseReceived(UserRatedGif userRatedGif) {
        responseCallback.responseReceived(userRatedGif);
    }

    // Listener callback interface to send back the results.
    public interface GetRatedGifByIdResponseListener {
        void responseReceived(UserRatedGif userRatedGif);
    }
}
