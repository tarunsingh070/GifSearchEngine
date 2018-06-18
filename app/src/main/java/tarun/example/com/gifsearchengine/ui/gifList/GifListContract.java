package tarun.example.com.gifsearchengine.ui.gifList;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.os.Bundle;

import tarun.example.com.gifsearchengine.data.remote.giphy.GifsDataSource;
import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.ui.BasePresenter;

/**
 * A contract interface that defines what methods the {@link GifListFragment} and the {@link GifListPresenter}
 * must implement in order to communicate with each other.
 */
interface GifListContract {

    interface View {

        boolean isViewVisible();

        void bindGifsListAdapterData(LiveData<PagedList<AdapterGifItem>> pagedList);

        void setSortingDropDownVisibility(boolean visibility, int selectedPosition);

        void registerDataSourceFactoryUpdate(MutableLiveData<GifsDataSource> gifsDataSourceMutableLiveData);

        boolean isNetworkConnectivityAvailable();

        void showNetworkConnectivityError();

        void showErrorMessage(String message);

        void setDefaultActivityTitle();

        void setActivityTitle(String title);

        void showProgressBar();

        void hideProgressBar();

        void clearAdapterData();

    }

    interface Presenter extends BasePresenter<View> {

        void searchQueryChanged(String query);

        void sortByOptionUpdated(int position);

        void retryButtonClicked();

        void saveState(Bundle outState);

        void onErrorOccurred(Exception exception);

    }

}
