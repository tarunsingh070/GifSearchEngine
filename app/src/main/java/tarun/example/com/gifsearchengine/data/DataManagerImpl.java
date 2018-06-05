package tarun.example.com.gifsearchengine.data;

import okhttp3.Callback;
import tarun.example.com.gifsearchengine.data.remote.GiphyRestClient;

/**
 * The implementation class corresponding to the {@link DataManager} interface where all methods
 * related to data calls are defined.
 */
public class DataManagerImpl implements DataManager {

    private GiphyRestClient restClient;

    public DataManagerImpl() {
        restClient = GiphyRestClient.getRestClient();
    }

    @Override
    public void getTrendingGifs(Callback callback) {
        restClient.getTrendingGifs(callback);
    }

}
