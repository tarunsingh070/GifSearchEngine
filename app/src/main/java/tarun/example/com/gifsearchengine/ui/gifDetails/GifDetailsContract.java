package tarun.example.com.gifsearchengine.ui.gifDetails;

import tarun.example.com.gifsearchengine.data.model.AdapterGifItem;
import tarun.example.com.gifsearchengine.ui.BasePresenter;

public class GifDetailsContract {

    interface View {

        void loadGif(String url);

        void populateGifDetails();

        void showInvalidRatingErrorMessage();

    }

    interface Presenter extends BasePresenter<View> {

        void onGifRated(AdapterGifItem gif, int rating);

    }

}