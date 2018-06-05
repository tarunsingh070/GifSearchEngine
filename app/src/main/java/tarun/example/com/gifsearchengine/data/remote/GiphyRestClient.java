package tarun.example.com.gifsearchengine.data.remote;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * A simple Rest client class which would help make Rest calls to various Giphy APIs.
 */
public class GiphyRestClient {

    private static final String TAG = GiphyRestClient.class.getSimpleName();

    private static final String BASE_URL = "api.giphy.com";
    private static final String GIPHY_API_KEY = "tKSHo2xJBooBR7H2o7AYdl4hu6YRF7Wf";

    private OkHttpClient client = new OkHttpClient();

    /**
     * Factory method to return an instance of Giphy Rest Client.
     */
    public static GiphyRestClient getRestClient() {
        return new GiphyRestClient();
    }

    /**
     * This method creates the base url for all giphy api calls.
     * @return
     */
    private HttpUrl.Builder getBaseUrlBuilder() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(BASE_URL)
                .addEncodedQueryParameter("api_key", GIPHY_API_KEY);
    }

}
