package tarun.example.com.gifsearchengine.ui.gifList;

import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
        fetchTrendingGifs();
        fetchRankedGifsFromFirebase();
    }

    @Override
    public void dropView() {
        this.view = null;
    }

    /**
     * Fetch the list of ranked Gifs from Firebase database.
     */
    private void fetchRankedGifsFromFirebase() {
        dataManager.getRankedGifsFromFirebase(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!view.isViewVisible()) {
                    return;
                }

                List<FirebaseGif> rankedFirebaseGifs = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    FirebaseGif firebaseGif = snapshot.getValue(FirebaseGif.class);
                    rankedFirebaseGifs.add(firebaseGif);
                }

                // Create adapterGifItems to be used for adapter from each firebaseGif object received.
                rankedGifItems.clear();
                for (FirebaseGif firebaseGif: rankedFirebaseGifs) {
                    AdapterGifItem adapterGifItem = new AdapterGifItem(firebaseGif.getId(), firebaseGif.getPreviewUrl()
                            , firebaseGif.getAverageRating(), firebaseGif.getRatingCount());
                    rankedGifItems.add(adapterGifItem);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Do nothing.
            }
        });
    }

    /**
     * Fetch the list of trending Gifs from Giphy api and update title of activity.
     */
    private void fetchTrendingGifs() {
        dataManager.getTrendingGifs(getTrendingGifsCallback());
        view.setActivityTitle(Constants.ACTIVITY_TITLE_TRENDING);
    }

    /**
     * Creates and returns a new {@link Callback} object to receive the trending gifs data.
     * @return
     */
    private Callback getTrendingGifsCallback() {
        view.showProgress();
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!view.isViewVisible()) {
                    return;
                }

                // Todo: Handle error.

                view.hideProgress();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!view.isViewVisible()) {
                    return;
                }

                view.hideProgress();

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
                            // Because ranking doesn't apply to trending gifs.
                            finalizedGifItems.addAll(unRankedGifItems);
                            updateGifsRecyclerView();
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
            AdapterGifItem gifItem = new AdapterGifItem(gif.getId(), gif.getUserName(), gif.getImportDate()
                    ,gif.getTitle(), gif.getImages().getPreviewGif().getUrl(), gif.getImages().getFullGif());
            unRankedGifItems.add(gifItem);
        }
    }

    /**
     * Creates and returns a new {@link Callback} object to receive the searched gifs data.
     * @return
     */
    private Callback getSearchedGifsCallback() {
        view.showProgress();
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!view.isViewVisible()) {
                    return;
                }

                view.hideProgress();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!view.isViewVisible()) {
                    return;
                }

                view.hideProgress();

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
                            updateGifsRecyclerView();
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

        // If the currently selected sort option is by Ranking, then sort the list as per each Gif's rating.
        if (sortBySelectedOptionPosition == GifListFragment.SPINNER_OPTION_RANKING_POSITION) {
            Collections.sort(finalizedGifItems);
        }
    }

    /**
     * Method to handle changes in search queries.
     * @param query
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
        updateGifsRecyclerView();
    }

}