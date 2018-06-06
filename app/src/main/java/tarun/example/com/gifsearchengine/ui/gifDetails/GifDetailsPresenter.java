package tarun.example.com.gifsearchengine.ui.gifDetails;

import android.text.TextUtils;

import tarun.example.com.gifsearchengine.data.DataManager;
import tarun.example.com.gifsearchengine.data.DataManagerImpl;
import tarun.example.com.gifsearchengine.data.model.AdapterGifItem;
import tarun.example.com.gifsearchengine.data.model.FullGif;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;

/**
 * A presenter class which would fetch data from data sources through model classes and feed it to the
 * view ({@link GifDetailsFragment}) to be displayed.
 */
public class GifDetailsPresenter implements GifDetailsContract.Presenter {

    private GifDetailsContract.View view;
    private DataManager dataManager;
    private FullGif fullGif;

    public GifDetailsPresenter() {
        dataManager = new DataManagerImpl();
    }

    public GifDetailsPresenter(FullGif fullGif) {
        this();
        this.fullGif = fullGif;
    }

    @Override
    public void takeView(GifDetailsContract.View view) {
        this.view = view;
        loadData();
    }

    @Override
    public void dropView() {
        view = null;
    }

    /**
     * Loads the gif and its related data.
     */
    private void loadData() {
        if (!TextUtils.isEmpty(fullGif.getUrl())) {
            view.loadGif(fullGif.getUrl());
        }

        view.populateGifDetails();
    }

    @Override
    public void onGifRated(AdapterGifItem gif, int rating) {
        // Check if rating is greater than 0, else show an invalid rating error toast.
        if (rating > 0) {
            // Increment the rating count by 1, find the new average rating and then write the updated object to Firebase DB.
            int newRatingCount = gif.getRatingCount() + 1;
            float newAverageRating = (gif.getAverageRating() + rating)/newRatingCount;
            dataManager.addOrUpdateGif(new FirebaseGif(gif.getId(), newAverageRating, newRatingCount, gif.getPreviewUrl()));
        } else {
            view.showInvalidRatingErrorMessage();
        }
    }
}
