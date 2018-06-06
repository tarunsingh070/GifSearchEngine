package tarun.example.com.gifsearchengine.ui.gifDetails;

import tarun.example.com.gifsearchengine.data.model.AdapterGifItem;
import tarun.example.com.gifsearchengine.ui.BasePresenter;

/**
 * A contract interface that defines what methods the {@link GifDetailsFragment} and the {@link GifDetailsPresenter}
 * must implement in order to communicate with each other.
 */
public class GifDetailsContract {

    interface View {

        void loadGif(String url);

        void populateGifDetails(String averageRating);

        void showInvalidRatingErrorMessage();

        void setActivityTitle(String title);

    }

    interface Presenter extends BasePresenter<View> {

        void rateGif(AdapterGifItem gif, int rating);

        void onResumeCalled();

    }

}
