package tarun.example.com.gifsearchengine.data;

import com.google.android.gms.tasks.Task;

import okhttp3.Callback;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;
import tarun.example.com.gifsearchengine.data.remote.giphy.GiphyRestClient;
import tarun.example.com.gifsearchengine.data.remote.firebase.FirebaseDbHelper;
import tarun.example.com.gifsearchengine.data.remote.firebase.FirebaseDbHelperImpl;

/**
 * The implementation class corresponding to the {@link DataManager} interface where all methods
 * related to data calls are defined.
 */
public class DataManagerImpl implements DataManager {

    private GiphyRestClient restClient;
    private FirebaseDbHelper firebaseDbHelper;

    public DataManagerImpl() {
        restClient = GiphyRestClient.getRestClient();
        firebaseDbHelper = new FirebaseDbHelperImpl();
    }

    @Override
    public void getTrendingGifs(Callback callback) {
        restClient.getTrendingGifs(callback);
    }

    @Override
    public Task<Void> addOrUpdateGif(FirebaseGif firebaseGif) {
        return firebaseDbHelper.addOrUpdateGif(firebaseGif);
    }

}
