package tarun.example.com.gifsearchengine.data.remote.giphy;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tarun.example.com.gifsearchengine.data.DataManager;
import tarun.example.com.gifsearchengine.data.DataManagerImpl;
import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.data.model.giphy.Gif;
import tarun.example.com.gifsearchengine.data.model.NetworkState;
import tarun.example.com.gifsearchengine.data.model.giphy.ResponseGifs;
import tarun.example.com.gifsearchengine.ui.gifList.GifListFragment;

/**
 * An extension to {@link PositionalDataSource} specialized to provide list of {@link Gif}
 * from Giphy API using {@link DataManager}
 * This class is responsible for fetching the gifs in a paginated fashion and feed the data back to the
 * adapter via callbacks.
 */
public class GifsDataSource extends PositionalDataSource<AdapterGifItem> {

    private static final String TAG = GifsDataSource.class.getSimpleName();

    private final MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private final MutableLiveData<NetworkState> initialLoading = new MutableLiveData<>();
    private final DataManager dataManager;
    private String searchTerm;
    private int sortBySelectedOptionPosition;

    // List of the most recent gif items fetched from Giphy Api.
    private List<AdapterGifItem> newUnrankedGifItems;

    // List of adapter gif items created from gif items received from FirebaseDatabase.
    private List<AdapterGifItem> rankedGifItems;

    // Final list of adapter gif items that will be displayed through the adapter.
    private List<AdapterGifItem> finalizedGifItems;

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return initialLoading;
    }

    private GifsDataSource() {
        dataManager = new DataManagerImpl();
        newUnrankedGifItems = new ArrayList<>();
        rankedGifItems = new ArrayList<>();
        finalizedGifItems = new ArrayList<>();
    }

    GifsDataSource(String searchTerm, List<AdapterGifItem> rankedGifItems, int sortBySelectedOptionPosition) {
        this();
        this.searchTerm = searchTerm;
        this.rankedGifItems = rankedGifItems;
        this.sortBySelectedOptionPosition = sortBySelectedOptionPosition;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull final LoadInitialCallback<AdapterGifItem> callback) {
        // For loading initial data.
        setLoadingStates(true, NetworkState.LOADING);
        fetchInitialResults(callback);
    }

    /**
     * Fetch the initial set of results.
     * If the search term is empty, then fetch the Trending gifs, otherwise fetch the gifs based on the search term.
     */
    private void fetchInitialResults(@NonNull final LoadInitialCallback<AdapterGifItem> callback) {
        if (!TextUtils.isEmpty(searchTerm)) {
            dataManager.getSearchedGifs(searchTerm, getInitialResultsCallback(callback));
        } else {
            dataManager.getTrendingGifs(getInitialResultsCallback(callback));
        }
    }

    /**
     * Creates and returns a callback object to process the initial set of gifs data received in response.
     * @param callback The {@link android.arch.paging.PositionalDataSource.LoadInitialCallback} callback
     * object to be used to post the processed gifs data to be displayed.
     */
    private Callback getInitialResultsCallback(@NonNull final LoadInitialCallback<AdapterGifItem> callback) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Api call to retrieve Initial set of gifs failed.", e);
                setLoadingStates(true, NetworkState.FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    populateNewUnrankedGifAdapterItems(response.body().string());
                    populateFinalizedSearchedGifItems();

                    // Post the processed data via callback.
                    callback.onResult(new ArrayList<>(finalizedGifItems), 0);
                    setLoadingStates(true, NetworkState.LOADED);
                } else {
                    Log.e(TAG, "Api call to retrieve Initial set of gifs completed unsuccessfully due to : " + response.message());
                    setLoadingStates(true, NetworkState.FAILED);
                }
            }
        };
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull final LoadRangeCallback<AdapterGifItem> callback) {
        // For loading paged data.
        setLoadingStates(false, NetworkState.LOADING);
        fetchRangeResults(params.startPosition, callback);
    }

    /**
     * Fetch the paged data based on offset as the user scrolls down the list of gifs.
     * If the search term is empty, then fetch the next set of Trending gifs, otherwise
     * fetch the next set of gifs based on the search term.
     * @param offset The offset value from where the next set of gifs are to be fetched.
     */
    private void fetchRangeResults(int offset, @NonNull final LoadRangeCallback<AdapterGifItem> callback) {
        if (!TextUtils.isEmpty(searchTerm)) {
            dataManager.getSearchedGifs(searchTerm, offset, getRangeResultsCallback(callback));
        } else {
            dataManager.getTrendingGifs(offset, getRangeResultsCallback(callback));
        }
    }

    /**
     * Creates and returns a callback object to process the paginated gifs data received in response.
     * @param callback The {@link android.arch.paging.PositionalDataSource.LoadRangeCallback} callback
     * object to be used to post the processed gifs data to be displayed.
     */
    private Callback getRangeResultsCallback(@NonNull final LoadRangeCallback<AdapterGifItem> callback) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Api call to retrieve paginated gifs failed.", e);
                setLoadingStates(false, NetworkState.FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    populateNewUnrankedGifAdapterItems(response.body().string());
                    populateFinalizedSearchedGifItems();
                    callback.onResult(new ArrayList<>(finalizedGifItems));
                    setLoadingStates(false, NetworkState.LOADED);
                } else {
                    Log.e(TAG, "Api call to retrieve paginated gifs completed unsuccessfully due to : " + response.message());
                    setLoadingStates(false, NetworkState.FAILED);
                }
            }
        };
    }

    /**
     * Prepare the items for adapter by creating an object of {@link AdapterGifItem}
     * for each {@link Gif} item received in response.
     */
    private void populateNewUnrankedGifAdapterItems(String response) {
        newUnrankedGifItems.clear();

        List<Gif> gifs = getGifsFromResponse(response);

        for (Gif gif: gifs) {
            String previewUrl = gif.getImages().getPreviewGif().getUrl();
            String fullUrl = gif.getImages().getFullGif().getUrl();
            // Add item only if URLs are non-empty.
            if (!(TextUtils.isEmpty(previewUrl) || TextUtils.isEmpty(fullUrl))){
                AdapterGifItem gifItem = new AdapterGifItem(gif.getId(), gif.getUserName(), gif.getImportDate()
                        , gif.getTitle(), gif.getImages().getPreviewGif().getUrl(), gif.getImages().getFullGif());
                newUnrankedGifItems.add(gifItem);
            }
        }
    }

    /**
     * Populate the final list of items for adapter according to the sort option currently selected.
     */
    private void populateFinalizedSearchedGifItems() {
        finalizedGifItems.clear();

        // If there are some non-zero ranked gifs received from firebase db, then use their rating info
        // to replace the default rating info in all unranked items to be shown on the details page.
        if (rankedGifItems != null && rankedGifItems.size() > 0) {
            // Create a map from the list of unranked gif items so they can be easily searched and modified.
            LinkedHashMap<String, AdapterGifItem> newUnrankedGifItemsMap = new LinkedHashMap<>();
            for (AdapterGifItem adapterGifItem : this.newUnrankedGifItems) {
                newUnrankedGifItemsMap.put(adapterGifItem.getId(), adapterGifItem);
            }

            // Iterate through each ranked gif item and if it exists in the list of unranked items too,
            // then update the rating info of that object in unranked items list as per the ranked gif item
            // received from firebase db.
            for (AdapterGifItem rankedGifItem : rankedGifItems) {
                if (newUnrankedGifItemsMap.containsKey(rankedGifItem.getId())) {
                    AdapterGifItem gifItem = newUnrankedGifItemsMap.get(rankedGifItem.getId());

                    gifItem.setRatingCount(rankedGifItem.getRatingCount());
                    gifItem.setAverageRating(rankedGifItem.getAverageRating());
                }
            }
            // Add all updated unranked items into the list of finalized gif items for adapter.
            finalizedGifItems.addAll(newUnrankedGifItemsMap.values());
        } else {
            // No ranked items were available, so simply add all unranked items into the final list,
            // since no updation of ratings info is required.
            finalizedGifItems.addAll(this.newUnrankedGifItems);
        }

        setGifItemsOrdering();
    }

    /**
     * Process the JSON string response received and convert it into a list of Gifs.
     * @param response The JSON string response received.
     * @return List of {@link Gif} objects created from the JSON response.
     */
    private List<Gif> getGifsFromResponse(String response) {
        List<Gif> gifs = new ArrayList<>();
        if (TextUtils.isEmpty(response)) {
            return gifs;
        }
        // Use Gson library to convert JSON data into an instance of ResponseGifs which would contain the list of Gifs.
        ResponseGifs responseGifs = new GsonBuilder().create()
                .fromJson(response, ResponseGifs.class);

        if (responseGifs.getGifs() != null) {
            gifs.addAll(responseGifs.getGifs());
        }
        return gifs;
    }

    /**
     * Set the Loading states based on the loading condition and state passed in.
     * @param isInitialLoading Flag to indicate if the state is being set while loading initial data set
     * or subsequent data sets.
     * @param loadingState The state of loading to be set.
     */
    private void setLoadingStates(boolean isInitialLoading, NetworkState loadingState) {
        // Posting of value for initial loading is required only while loading the initial set of data.
        if (isInitialLoading) {
            initialLoading.postValue(loadingState);
        }
        networkState.postValue(loadingState);
    }

    /**
     * Order the gif items based on the sorting option selected before being displayed to the user.
     */
    private void setGifItemsOrdering() {
        // If the currently selected sort option is by Ranking, then sort the list as per each Gif's rating.
        if (sortBySelectedOptionPosition == GifListFragment.SPINNER_OPTION_RANKING_POSITION) {
            Collections.sort(finalizedGifItems);
        }
    }
}
