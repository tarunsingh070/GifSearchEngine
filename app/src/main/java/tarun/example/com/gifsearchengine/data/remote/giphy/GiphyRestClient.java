package tarun.example.com.gifsearchengine.data.remote.giphy;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import tarun.example.com.gifsearchengine.data.Constants;

/**
 * A simple Rest client class which would help make Rest calls to various Giphy APIs.
 */
public class GiphyRestClient {

    private static final String TAG = GiphyRestClient.class.getSimpleName();

    private static final String BASE_URL = "api.giphy.com";
    private static final String URL_TRENDING_GIFS = "v1/gifs/trending";
    private static final String URL_SEARCH_GIFS = "v1/gifs/search";
    private static final String GIPHY_API_KEY = "tKSHo2xJBooBR7H2o7AYdl4hu6YRF7Wf";

    private final OkHttpClient client = new OkHttpClient();

    /**
     * Factory method to return an instance of Giphy Rest Client.
     */
    public static GiphyRestClient getRestClient() {
        return new GiphyRestClient();
    }

    /**
     * This method creates the base url for all giphy api calls with configurations and query params
     * common for all api calls.
     * @return Returns a configured {@link HttpUrl.Builder} object which can further be configured.
     */
    private HttpUrl.Builder getBaseUrlBuilder() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(BASE_URL)
                .addQueryParameter("limit", String.valueOf(Constants.LOADING_PAGE_SIZE))
                .addEncodedQueryParameter("api_key", GIPHY_API_KEY);
    }

    /**
     * Get trending gif results starting from the offset value passed in as argument.
     * @param offset Offset value from where results should be retrieved.
     * @param callback Callback where response is to be received.
     */
    public void getTrendingGifs(int offset, Callback callback) {
        HttpUrl httpUrl = getBaseUrlBuilder()
                .addPathSegments(URL_TRENDING_GIFS)
                .addQueryParameter("offset", String.valueOf(offset))
                .build();

        makeGetRequest(httpUrl, callback);
    }

    /**
     * Get searched gif results according to the searchTerm starting from the offset value passed in as argument.
     * @param searchTerm Term to be searched for gifs.
     * @param offset Offset value from where results should be retrieved.
     * @param callback Callback where search results are to be received.
     */
    public void getSearchedGifs(String searchTerm, int offset, Callback callback) {
        HttpUrl httpUrl = getBaseUrlBuilder()
                .addPathSegments(URL_SEARCH_GIFS)
                .addQueryParameter("q", searchTerm)
                .addQueryParameter("offset", String.valueOf(offset))
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
