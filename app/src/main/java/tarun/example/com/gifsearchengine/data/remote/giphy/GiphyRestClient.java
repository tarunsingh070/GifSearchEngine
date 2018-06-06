package tarun.example.com.gifsearchengine.data.remote.giphy;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * A simple Rest client class which would help make Rest calls to various Giphy APIs.
 */
public class GiphyRestClient {

    private static final String TAG = GiphyRestClient.class.getSimpleName();

    private static final String BASE_URL = "api.giphy.com";
    private static final String URL_TRENDING_GIFS = "v1/gifs/trending";
    private static final String GIPHY_API_KEY = "tKSHo2xJBooBR7H2o7AYdl4hu6YRF7Wf";
    private static final String RECORDS_PAGE_SIZE = "30";

    private OkHttpClient client = new OkHttpClient();

    /**
     * Factory method to return an instance of Giphy Rest Client.
     */
    public static GiphyRestClient getRestClient() {
        return new GiphyRestClient();
    }

    /**
     * This method creates the base url for all giphy api calls.
     * @return Returns a configured {@link HttpUrl.Builder} object which can further be configured.
     */
    private HttpUrl.Builder getBaseUrlBuilder() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(BASE_URL)
                .addEncodedQueryParameter("api_key", GIPHY_API_KEY);
    }

    /**
     * Method to get the list of Trending gifs with a limit of RECORDS_PAGE_SIZE per result set.
     * @param callback Callback object where the response is desired to be received.
     */
    public void getTrendingGifs(Callback callback) {
        HttpUrl httpUrl = getBaseUrlBuilder()
                .addPathSegments(URL_TRENDING_GIFS)
                .addQueryParameter("limit", RECORDS_PAGE_SIZE)
                .build();

        makeGetRequest(httpUrl, callback);
    }

    /**
     * A helper method to make the final Get request.
     */
    private void makeGetRequest(HttpUrl httpUrl, Callback callback) {
        Request request = new Request.Builder().url(httpUrl).get().build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

}