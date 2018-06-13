package tarun.example.com.gifsearchengine.ui.gifDetails;

import android.text.TextUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import tarun.example.com.gifsearchengine.data.DataManager;
import tarun.example.com.gifsearchengine.data.DataManagerImpl;
import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;

/**
 * A presenter class which would fetch data from data sources through model classes and feed it to the
 * view ({@link GifDetailsFragment}) to be displayed.
 */
public class GifDetailsPresenter implements GifDetailsContract.Presenter {

    private GifDetailsContract.View view;
    private DataManager dataManager;
    private AdapterGifItem gif;

    public GifDetailsPresenter() {
        dataManager = new DataManagerImpl();
    }

    public GifDetailsPresenter(AdapterGifItem gif) {
        this();
        this.gif = gif;
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
        String gifUrl = gif.getFullGif().getUrl();
        if (!TextUtils.isEmpty(gifUrl)) {
            view.loadGif(gifUrl);
        }

        // Calculate the size to be displayed in kb.
        gif.getFullGif().setSize(String.valueOf(Integer.parseInt(gif.getFullGif().getSize()) / 1024));

        // Set username as unknown if uploader is not available for this gif.
        if (TextUtils.isEmpty(gif.getUserName())) {
            gif.setUserName("Unknown");
        }

        // Send rating as "Not Rated" if current rating is 0 for this gif, otherwise send the current average rating.
        if (gif.getAverageRating() > 0) {
            view.populateGifDetails(String.valueOf(gif.getAverageRating()));
        } else {
            view.populateGifDetails("Not Rated");
        }

    }

    /**
     * Rate the Gif item passed in as argument.
     * @param adapterGifItem Gif to be rated.
     * @param rating Rating submitted by user.
     */
    @Override
    public void rateGif(AdapterGifItem adapterGifItem, int rating) {
        // Check if rating is greater than 0, else show an invalid rating error toast.
        if (rating > 0) {
            // Increment the rating count by 1, find the new average rating and then write the updated object to Firebase DB.
            int newRatingCount = adapterGifItem.getRatingCount() + 1;
            float newAverageRating = ((adapterGifItem.getAverageRating() * adapterGifItem.getRatingCount()) + rating)/newRatingCount;
            newAverageRating = getRoundedRating(newAverageRating);

            // Update the gif item.
            gif.setAverageRating(newAverageRating);
            gif.setRatingCount(newRatingCount);

            dataManager.addOrUpdateGif(new FirebaseGif(adapterGifItem.getId(), newAverageRating, newRatingCount, adapterGifItem.getPreviewUrl()));

            // Reload gif rating.
            refreshRatingInUI();
        } else {
            view.showInvalidRatingErrorMessage();
        }
    }

    /**
     * Round the rating to one decimal place.
     */
    private float getRoundedRating(float averageRating) {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return Float.valueOf(df.format(averageRating));
    }

    /**
     * Refresh rating data in UI.
     */
    private void refreshRatingInUI() {
        // Send rating as "Not Rated" if current rating is 0 for this gif, otherwise send the current average rating.
        if (gif.getAverageRating() > 0) {
            view.populateGifDetails(String.valueOf(gif.getAverageRating()));
        } else {
            view.populateGifDetails("Not Rated");
        }
    }

    /**
     * Update the activity title when view class is resumed.
     */
    @Override
    public void onResumeCalled() {
        // If gif title is not available, set default Activity title, otherwise use Gif title.
        if (TextUtils.isEmpty(gif.getTitle())) {
            view.setDefaultActivityTitle();
        } else {
            view.setActivityTitle(gif.getTitle());
        }
    }
}
