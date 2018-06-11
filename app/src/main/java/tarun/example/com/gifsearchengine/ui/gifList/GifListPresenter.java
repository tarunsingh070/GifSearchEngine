package tarun.example.com.gifsearchengine.ui.gifList;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tarun.example.com.gifsearchengine.data.Constants;
import tarun.example.com.gifsearchengine.data.DataManager;
import tarun.example.com.gifsearchengine.data.DataManagerImpl;
import tarun.example.com.gifsearchengine.data.model.AdapterGifItem;
import tarun.example.com.gifsearchengine.data.model.Gif;
import tarun.example.com.gifsearchengine.data.model.ResponseGifs;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;

/**
 * A presenter class which would fetch data from data sources through model classes and feed it to the
 * view ({@link GifListFragment}) to be displayed.
 */
public class GifListPresenter implements GifListContract.Presenter {

    private static final String TAG = GifListPresenter.class.getSimpleName();

    private int sortBySelectedOptionPosition;
    private GifListContract.View view;
    private DataManager dataManager;
    private GsonBuilder gsonBuilder;
    private ResponseGifs responseGifs;

    // List of adapter gif items created from gif items received through Giphy Api.
    private List<AdapterGifItem> unRankedGifItems;

    // List of adapter gif items created from gif items received through FirebaseDatabase Api.
    private List<AdapterGifItem> rankedGifItems;

    // Final list of adapter gif that will be displayed through the adapter.
    private List<AdapterGifItem> finalizedGifItems;

    GifListPresenter() {
        dataManager = new DataManagerImpl();
        gsonBuilder = new GsonBuilder();
        unRankedGifItems = new ArrayList<>();
        rankedGifItems = new ArrayList<>();
        finalizedGifItems = new ArrayList<>();
    }

    @Override
    public void takeView(GifListContract.View view) {
        this.view = view;
        fetchRankedGifsRealtimeDataFromFirebase();
    }

    @Override
    public void dropView() {
        this.view = null;
    }

    /**
     * Fetch the real-time data containing list of ranked Gifs from Firebase database.
     */
    private void fetchRankedGifsRealtimeDataFromFirebase() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_GIFS);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Fetch trending gifs once all the data from firebase has been fetched.
                Log.d(TAG, "Fetched all " + dataSnapshot.getChildrenCount() + " items from firebase");
                fetchTrendingGifs();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error occurred while fetching items from firebase db", databaseError.toException());
                // Fetch trending gifs anyways.
                fetchTrendingGifs();
            }
        });

        // Clear the previous items before re-populating data.
        rankedGifItems.clear();
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebaseGif firebaseGif = dataSnapshot.getValue(FirebaseGif.class);
                // Add item only if URL is non-empty.
                if (!TextUtils.isEmpty(firebaseGif.getPreviewUrl())) {
                    addInRankedGifItems(firebaseGif);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FirebaseGif firebaseGif = dataSnapshot.getValue(FirebaseGif.class);
                // When an item is updated in firebase db, update the local item as well.
                updateRankedGifItems(firebaseGif);
                updateFinalizedGifItems(firebaseGif);
                setGifItemsOrdering();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // This scenario will never happen.
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // Do nothing
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Do nothing.
            }
        });
    }

    /**
     * Add the firebase item into the list of ranked gif items.
     * @param firebaseGif Firebase gif item to be added.
     */
    private void addInRankedGifItems(FirebaseGif firebaseGif) {
        // Create an AdapterGifItem instance to be used in adapter from this firebaseGif item received.
        AdapterGifItem adapterGifItem = new AdapterGifItem(firebaseGif.getId(), firebaseGif.getPreviewUrl()
                , firebaseGif.getAverageRating(), firebaseGif.getRatingCount());

        rankedGifItems.add(adapterGifItem);
    }

    /**
     * Update an existing gif item in the list of ranked gif items when an item is updated in firebase db.
     * @param firebaseGif Updated firebase gif item.
     */
    private void updateRankedGifItems(FirebaseGif firebaseGif) {
        for (AdapterGifItem adapterGifItem : rankedGifItems) {
            if (TextUtils.equals(firebaseGif.getId(), adapterGifItem.getId())) {
                // Since Gif ID and Url will always remain same for a gif so, Average rating and
                // rating count are the only fields that can be updated in firebase db.
                adapterGifItem.setAverageRating(firebaseGif.getAverageRating());
                adapterGifItem.setRatingCount(firebaseGif.getRatingCount());
            }
        }
    }

    /**
     * Update an existing gif item being shown when an item is updated in firebase db.
     * @param firebaseGif Updated firebase gif item.
     */
    private void updateFinalizedGifItems(FirebaseGif firebaseGif) {
        for (AdapterGifItem adapterGifItem : finalizedGifItems) {
            if (TextUtils.equals(firebaseGif.getId(), adapterGifItem.getId())) {
                // Since Gif ID and Url will always remain same for a gif so, Average rating and
                // rating count are the only fields that can be updated in firebase db.
                adapterGifItem.setAverageRating(firebaseGif.getAverageRating());
                adapterGifItem.setRatingCount(firebaseGif.getRatingCount());
            }
        }
    }

    /**
     * Fetch the list of trending Gifs from Giphy api and update title of activity.
     */
    private void fetchTrendingGifs() {
        // Reset the sorting option to sort by relevance when Trending gifs are searched so that when
        // user searches for gifs later, then sorting is set back to based on relevance by default.
        sortBySelectedOptionPosition = GifListFragment.SPINNER_OPTION_RELEVANCE_POSITION;

        dataManager.getTrendingGifs(getTrendingGifsCallback());
        view.setActivityTitle(Constants.ACTIVITY_TITLE_TRENDING);
    }

    /**
     * Creates and returns a new {@link Callback} object to receive the trending gifs data.
     * @return
     */
    private Callback getTrendingGifsCallback() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (view == null || !view.isViewVisible()) {
                    return;
                }

                view.showErrorMessage(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (view == null || !view.isViewVisible()) {
                    return;
                }

                /*
                If response is successful and returns a non-empty data, then use Gson library to get
                an instance of ResponseGifs object, create gif items to be used for adapter and
                update the recycler view data set.
                 */
                if (response.isSuccessful() && response.body() != null) {

                    String responseJson = response.body().string();

                    if (!TextUtils.isEmpty(responseJson)) {
                        responseGifs = gsonBuilder.create()
                                .fromJson(responseJson, ResponseGifs.class);

                        if (responseGifs != null) {
                            populateUnrankedGifItems();
                            populateFinalizedSearchedGifItems();
                        }
                    }
                }
            }
        };
    }

    /**
     * Populate the items for adapter by creating an object of {@link AdapterGifItem}
     * for each {@link Gif} item received in response.
     */
    private void populateUnrankedGifItems() {
        unRankedGifItems.clear();
        for (Gif gif: responseGifs.getGifs()) {
            String previewUrl = gif.getImages().getPreviewGif().getUrl();
            String fullUrl = gif.getImages().getFullGif().getUrl();
            // Add item only if URLs are non-empty.
            if (!(TextUtils.isEmpty(previewUrl) || TextUtils.isEmpty(fullUrl))){
                AdapterGifItem gifItem = new AdapterGifItem(gif.getId(), gif.getUserName(), gif.getImportDate()
                        , gif.getTitle(), gif.getImages().getPreviewGif().getUrl(), gif.getImages().getFullGif());
                unRankedGifItems.add(gifItem);
            }
        }
    }

    /**
     * Creates and returns a new {@link Callback} object to receive the searched gifs data.
     * @return
     */
    private Callback getSearchedGifsCallback() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (view == null || !view.isViewVisible()) {
                    return;
                }

                view.showErrorMessage(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (view == null || !view.isViewVisible()) {
                    return;
                }

                /*
                If response is successful and returns a non-empty data, then use Gson library to get
                an instance of ResponseGifs object, create gif items to be used for adapter and
                update the recycler view data set.
                 */
                if (response.isSuccessful() && response.body() != null) {

                    String responseJson = response.body().string();

                    if (!TextUtils.isEmpty(responseJson)) {
                        responseGifs = gsonBuilder.create()
                                .fromJson(responseJson, ResponseGifs.class);

                        if (responseGifs != null) {
                            populateUnrankedGifItems();
                            populateFinalizedSearchedGifItems();
                        }
                    }
                }
            }
        };
    }

    /**
     * Update the GifsRecyclerView explicitly on the UI thread as per the latest data available
     * because this method is being called from a background thread (Callback thread).
     */
    private void updateGifsRecyclerView() {
        // Verify if the view is still attached.
        if (view == null || !view.isViewVisible()) {
            return;
        }

        view.getFragmentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.updateGifsListAdapterData(finalizedGifItems);
            }
        });
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
            LinkedHashMap<String, AdapterGifItem> unRankedGifItemsMap = new LinkedHashMap<>();
            for (AdapterGifItem adapterGifItem : unRankedGifItems) {
                unRankedGifItemsMap.put(adapterGifItem.getId(), adapterGifItem);
            }

            // Iterate through each ranked gif item and if it exists in the list of unranked items too,
            // then update the rating info of that object in unranked items list as per the ranked gif item
            // received from firebase db.
            for (AdapterGifItem rankedGifItem : rankedGifItems) {
                if (unRankedGifItemsMap.containsKey(rankedGifItem.getId())) {
                    AdapterGifItem gifItem = unRankedGifItemsMap.get(rankedGifItem.getId());

                    gifItem.setRatingCount(rankedGifItem.getRatingCount());
                    gifItem.setAverageRating(rankedGifItem.getAverageRating());
                }
            }
            // Add all updated unranked items into the list of finalized gif items for adapter.
            finalizedGifItems.addAll(unRankedGifItemsMap.values());
        } else {
            // No ranked items were available, so simply add all unranked items into the final list,
            // since no updation of ratings info is required.
            finalizedGifItems.addAll(unRankedGifItems);
        }

        setGifItemsOrdering();
    }

    /**
     * Order the gif items based on the sorting option selected before being displayed to the user.
     */
    private void setGifItemsOrdering() {
        // If the currently selected sort option is by Ranking, then sort the list as per each Gif's rating.
        if (sortBySelectedOptionPosition == GifListFragment.SPINNER_OPTION_RANKING_POSITION) {
            Collections.sort(finalizedGifItems);
        }
        updateGifsRecyclerView();
    }

    /**
     * Method to handle changes in search queries.
     * @param query The query string to be searched.
     */
    @Override
    public void searchQueryChanged(String query) {
        // If view is no longer available, then don't proceed any further.
        if (view == null || !view.isViewVisible()) {
            return;
        }

        /*
        If search query is not empty, then search for gifs as per the query, show the sortby dropdown
         and update the activity title.
        If search query is empty, then fetch trending gifs, hide the sortby dropdown and update the activity title.
         */
        if (!TextUtils.isEmpty(query)) {
            dataManager.getSearchedGifs(query, getSearchedGifsCallback());
            view.setSortingDropDownVisibility(true);
            view.setActivityTitle(Constants.ACTIVITY_TITLE_RESULTS);
        } else {
            dataManager.getTrendingGifs(getTrendingGifsCallback());
            view.setSortingDropDownVisibility(false);
            view.setActivityTitle(Constants.ACTIVITY_TITLE_TRENDING);
        }
    }

    /**
     * This method re-populates the final list of Gifs to be shown according to the sort by option selected
     * and updates the recycler view.
     * @param position Position of sort by option selected.
     */
    @Override
    public void sortByOptionUpdated(int position) {
        sortBySelectedOptionPosition = position;
        populateFinalizedSearchedGifItems();
    }

}