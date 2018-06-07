package tarun.example.com.gifsearchengine.ui.gifList;

import android.support.v4.app.FragmentActivity;

import java.util.List;

import tarun.example.com.gifsearchengine.data.model.AdapterGifItem;
import tarun.example.com.gifsearchengine.ui.BasePresenter;

/**
 * A contract interface that defines what methods the {@link GifListFragment} and the {@link GifListPresenter}
 * must implement in order to communicate with each other.
 */
public interface GifListContract {

    interface View {

        FragmentActivity getFragmentActivity();

        boolean isViewVisible();

        void updateGifsListAdapterData(List<AdapterGifItem> gifs);

        void setSortingDropDownVisibility(boolean visibility);

        void setActivityTitle(String title);

        void showErrorMessage(Exception exception);

    }

    interface Presenter extends BasePresenter<View> {

        void searchQueryChanged(String query);

        void sortByOptionUpdated(int position);

    }

}
