package tarun.example.com.gifsearchengine.ui.gifList;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.text.TextUtils;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

    private int sortBySelectedOptionPosition;
    private GifListContract.View view;

    // List of adapter gif items created from gif items received through FirebaseDatabase Api.
    private List<AdapterGifItem> rankedGifItems;

    private DataManager dataManager;

    // The object defining configurations for the data source fetching the gifs data in a paginated fashion.
    private PagedList.Config pagedListConfig =
            new PagedList.Config.Builder().setEnablePlaceholders(false)
                    .setInitialLoadSizeHint(Constants.LOADING_PAGE_SIZE)
                    .setEnablePlaceholders(false)
                    .setPageSize(Constants.LOADING_PAGE_SIZE).build();

    private String searchQuery;

    GifListPresenter() {
        dataManager = new DataManagerImpl();
        rankedGifItems = new ArrayList<>();
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
     * Fetch the real-time list of ranked Gifs from Firebase database.
     */
    private void fetchRankedGifsRealtimeDataFromFirebase() {
        // Check for network connectivity before proceeding.
        if (!view.isNetworkConnectivityAvailable()) {
            view.showOrHideRetryButton(true);
            view.showNetworkConnectivityError();
            return;
        }

        dataManager.getRankedGifsFromFirebase(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (view == null || !view.isViewVisible()) {
                    return;
                }

                // Fetch trending gifs only after the data from firebase is available.
                loadPaginatedGifResults();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Fetch trending gifs anyways.
                loadPaginatedGifResults();
            }
        });

        // Clear the previous items before re-populating data.
        rankedGifItems.clear();
        dataManager.getRankedGifsFromFirebase(new ChildEventListener() {
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
     * Method to handle changes in search queries.
     * @param query The query string to be searched.
     */
    @Override
    public void searchQueryChanged(String query) {
        // If view is no longer available, then don't proceed any further.
        if (view == null || !view.isViewVisible()) {
            return;
        }

        // update the searchQuery field.
        searchQuery = query;
        loadPaginatedGifResults();
    }

    /**
     * This method is responsible for initiating the loading of gifs (both Trending as well as search based).
     */
    private void loadPaginatedGifResults() {
        // Check for network connectivity before proceeding.
        if (!view.isNetworkConnectivityAvailable()) {
            view.showOrHideRetryButton(true);
            view.showNetworkConnectivityError();
            return;
        }

        /*
        While loading Trending gifs, the search query being passed here would be an empty string.
        Also send the reference of rankedGifItems list so that as it gets updated here in realtime from firebase db,
        the GifsDataSource will always have the updated list for use.
         */
        GifsDataSourceFactory gifsDataSourceFactory = new GifsDataSourceFactory(searchQuery, rankedGifItems, sortBySelectedOptionPosition);
        LiveData<PagedList<AdapterGifItem>> gifItems = new LivePagedListBuilder<>(gifsDataSourceFactory, pagedListConfig).build();

        view.registerDataSourceFactoryUpdate(gifsDataSourceFactory.getGifsDataSourceMutableLiveData());
        view.bindGifsListAdapterData(gifItems);

        // If search query is empty, hide the sortby dropdown menu, otherwise show it.
        if (!TextUtils.isEmpty(searchQuery)) {
            view.setSortingDropDownVisibility(true);
        } else {
            view.setSortingDropDownVisibility(false);
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
            view.showOrHideRetryButton(false);
            fetchRankedGifsRealtimeDataFromFirebase();
        } else {
            view.showNetworkConnectivityError();
        }
    }
}