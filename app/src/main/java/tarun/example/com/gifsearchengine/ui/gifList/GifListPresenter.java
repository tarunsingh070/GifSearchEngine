package tarun.example.com.gifsearchengine.ui.gifList;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import tarun.example.com.gifsearchengine.data.Constants;
import tarun.example.com.gifsearchengine.data.DataManager;
import tarun.example.com.gifsearchengine.data.DataManagerImpl;
import tarun.example.com.gifsearchengine.data.remote.giphy.GifsDataSourceFactory;
import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;

/**
 * A presenter class which would setup the {@link GifsDataSourceFactory}
 * to fetch data from Giphy api and feed it to {@link tarun.example.com.gifsearchengine.ui.gifList.GifsDataSourceListAdapter}
 * in order to support pagination while fetching data and displaying to the user.
 */
public class GifListPresenter implements GifListContract.Presenter {

    private static final String TAG = GifListPresenter.class.getSimpleName();

    private static final String KEY_QUERY = "GifListFragment.query";
    private static final String KEY_SELECTED_SORT_BY_OPTION = "GifListFragment.sortBy";

    private int sortBySelectedOptionPosition;
    private GifListContract.View view;

    // List of adapter gif items created from gif items received through FirebaseDatabase Api.
    private final List<AdapterGifItem> rankedGifItems;

    private final DataManager dataManager;

    // The object defining configurations for the data source fetching the gifs data in a paginated fashion.
    private final PagedList.Config pagedListConfig =
            new PagedList.Config.Builder().setEnablePlaceholders(false)
                    .setInitialLoadSizeHint(Constants.LOADING_PAGE_SIZE)
                    .setEnablePlaceholders(false)
                    .setPageSize(Constants.LOADING_PAGE_SIZE).build();

    private String recentSearchQuery ="";

    /**
     * Parameterised constructor that gets called when GifDetailsFragment is being re-stored
     * from a saved instance state.
     * @param savedInstance Bundle object containing the saved state to help for restoration.
     */
    public GifListPresenter(Bundle savedInstance) {
        // If fragment is being re-created and has a saved state available, then pass it to the presenter for restoration.
        if (savedInstance != null) {
            recentSearchQuery = savedInstance.getString(KEY_QUERY);
            sortBySelectedOptionPosition = savedInstance.getInt(KEY_SELECTED_SORT_BY_OPTION);
        }
        dataManager = new DataManagerImpl();
        rankedGifItems = new ArrayList<>();
    }

    @Override
    public void takeView(GifListContract.View view) {
        this.view = view;
        // If rankedGifItems are not empty, that means fragment is being re-opened with an existing
        // saved state, so only fetch the updated firebase items, but don't fetch items from Giphy API to display
        // , instead retain the existing list of items being displayed.
        if (!rankedGifItems.isEmpty()) {
            updateToolbarInfo();
        } else {
            prepareDataToDisplay();
        }
        fetchRankedGifsRealtimeDataFromFirebase();
    }

    @Override
    public void dropView() {
        this.view = null;
    }

    /**
     * Fetch the real-time list of ranked Gifs from Firebase database.
     */
    private void fetchRankedGifsRealtimeDataFromFirebase() {
        // Check for network connectivity before proceeding.
        if (!view.isNetworkConnectivityAvailable()) {
            view.showNetworkConnectivityError();
            return;
        }

        // Clear the previous items before re-populating data.
        rankedGifItems.clear();
        dataManager.getRankedGifsFromFirebase(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebaseGif firebaseGif = dataSnapshot.getValue(FirebaseGif.class);
                // Add item only if URL is non-empty.
                if (firebaseGif != null && !TextUtils.isEmpty(firebaseGif.getPreviewUrl())) {
                    addInRankedGifItems(firebaseGif);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FirebaseGif firebaseGif = dataSnapshot.getValue(FirebaseGif.class);
                // When an item is updated in firebase db, update the local item as well.
                updateRankedGifItems(firebaseGif);
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
     * Prepare all the data (firebase plus giphy api) to be displayed to the user.
     */
    private void prepareDataToDisplay() {
        // Check for network connectivity before proceeding.
        if (!view.isNetworkConnectivityAvailable()) {
            view.showNetworkConnectivityError();
            return;
        }

        // Fetch the data from Giphy Api once firebase data is available.
        view.showProgressBar();
        dataManager.getRankedGifsFromFirebase(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (view != null && view.isViewVisible()) {
                    view.hideProgressBar();

                    // Fetch trending gifs only after the data from firebase is available.
                    loadPaginatedGifResults();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (view != null && view.isViewVisible()) {
                    view.hideProgressBar();
                    // Fetch trending gifs anyways.
                    loadPaginatedGifResults();
                }
            }
        });
    }

    /**
     * Method to handle changes in search queries.
     * @param newQuery The query string to be searched.
     */
    @Override
    public void searchQueryChanged(String newQuery) {
        // If view is no longer available, then don't proceed any further.
        if (view == null || !view.isViewVisible()) {
            return;
        }

        // update the recentSearchQuery field if search query changed from last query and load results.
        if (!TextUtils.equals(newQuery, recentSearchQuery)) {
            // If the new query submitted is an empty string and the most recent query searched has
            // a length of 2 or more characters, then it is most likely that the user pressed the
            // cross button on search field to clear contents and search for some new term. In this scenario
            // we do not want to automatically load the trending gifs due to empty query being submitted
            // when user taps the cross button in search bar and instead would wait for user to type
            // a new query with atleast one character.
            if (recentSearchQuery.length() > 1 && TextUtils.isEmpty(newQuery)) {
                return;
            }
            recentSearchQuery = newQuery;
            loadPaginatedGifResults();
            view.clearAdapterData();
        }
    }

    /**
     * This method is responsible for initiating the loading of gifs (both Trending as well as search based).
     */
    private void loadPaginatedGifResults() {
        // Check for network connectivity before proceeding.
        if (!view.isNetworkConnectivityAvailable()) {
            view.showNetworkConnectivityError();
            return;
        }

        /*
        While loading Trending gifs, the search query being passed here would be an empty string.
        Also send the reference of rankedGifItems list so that as it gets updated here in realtime from firebase db,
        the GifsDataSource will always have the updated list for use.
         */
        GifsDataSourceFactory gifsDataSourceFactory = new GifsDataSourceFactory(recentSearchQuery, rankedGifItems, sortBySelectedOptionPosition);
        LiveData<PagedList<AdapterGifItem>> gifItems = new LivePagedListBuilder<>(gifsDataSourceFactory, pagedListConfig).build();

        view.registerDataSourceFactoryUpdate(gifsDataSourceFactory.getGifsDataSourceMutableLiveData());
        view.bindGifsListAdapterData(gifItems);

        updateToolbarInfo();
    }

    /**
     * Update the info displayed in toolbar.
     */
    private void updateToolbarInfo() {
        updateSortingDropDownVisiblity();
        if (!TextUtils.isEmpty(recentSearchQuery)) {
            view.setActivityTitle(recentSearchQuery);
        } else {
            view.setDefaultActivityTitle();
        }
    }

    /**
     * If search query is empty, hide the sortby dropdown menu, otherwise show it.
     */
    private void updateSortingDropDownVisiblity() {
        if (!TextUtils.isEmpty(recentSearchQuery)) {
            view.setSortingDropDownVisibility(true, sortBySelectedOptionPosition);
        } else {
            view.setSortingDropDownVisibility(false, sortBySelectedOptionPosition);
        }
    }

    /**
     * This method re-populates the final list of Gifs to be shown according to the sort by option selected
     * and updates the recycler view.
     * @param position Position of sort by option selected.
     */
    @Override
    public void sortByOptionUpdated(int position) {
        if (sortBySelectedOptionPosition != position) {
            sortBySelectedOptionPosition = position;
            loadPaginatedGifResults();
        }
    }

    /**
     * When retry button is clicked, check for network connectivity and load items if network available,
     * otherwise simply show no network error.
     */
    @Override
    public void retryButtonClicked() {
        if (view.isNetworkConnectivityAvailable()) {
            fetchRankedGifsRealtimeDataFromFirebase();
            prepareDataToDisplay();
        } else {
            view.showNetworkConnectivityError();
        }
    }

    /**
     * Method to save the presenter state for restoration later before current fragment instance gets destroyed.
     * @param outState Bundle object to save state into.
     */
    @Override
    public void saveState(Bundle outState) {
        outState.putString(KEY_QUERY, recentSearchQuery);
        outState.putInt(KEY_SELECTED_SORT_BY_OPTION, sortBySelectedOptionPosition);
    }

    @Override
    public void onErrorOccurred(Exception exception) {
        // UnknownHostException indicated a network connection issue.
        if (exception instanceof UnknownHostException) {
            view.showNetworkConnectivityError();
        } else {
            view.showErrorMessage(exception.getMessage());
        }
    }
}