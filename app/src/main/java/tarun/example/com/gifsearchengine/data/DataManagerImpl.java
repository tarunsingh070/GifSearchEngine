package tarun.example.com.gifsearchengine.data;

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

}
