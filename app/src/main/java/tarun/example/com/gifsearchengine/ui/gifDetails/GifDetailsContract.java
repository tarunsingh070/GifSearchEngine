package tarun.example.com.gifsearchengine.ui.gifDetails;

import android.content.Context;

import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.ui.BasePresenter;

/**
 * A contract interface that defines what methods the {@link GifDetailsFragment} and the {@link GifDetailsPresenter}
 * must implement in order to communicate with each other.
 */
class GifDetailsContract {

    interface View {

        void loadGif(String url);

        void populateGifDetails();

        void showInvalidRatingErrorMessage();

        void setDefaultActivityTitle();

        void setActivityTitle(String title);

        Context getApplicationContext();

        boolean isNetworkConnectivityAvailable();

        void showNetworkConnectivityError();

        void showRatingDialog();

        void showRatingDialog(int existingRating);

    }

    interface Presenter extends BasePresenter<View> {

        void ratingButtonClicked();

        void rateGif(AdapterGifItem gif, int rating);

        void onResumeCalled();

        void retryRatingSubmission(AdapterGifItem adapterGifItem, int rating);

    }

}
