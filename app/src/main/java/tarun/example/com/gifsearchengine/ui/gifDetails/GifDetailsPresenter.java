package tarun.example.com.gifsearchengine.ui.gifDetails;

import android.text.TextUtils;

import tarun.example.com.gifsearchengine.data.DataManager;
import tarun.example.com.gifsearchengine.data.DataManagerImpl;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;
import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.data.model.room.UserRatedGif;
import tarun.example.com.gifsearchengine.util.NumberUtil;

/**
 * A presenter class which would fetch data from data sources through model classes and feed it to the
 * view ({@link GifDetailsFragment}) to be displayed.
 */
public class GifDetailsPresenter implements GifDetailsContract.Presenter, DataManagerImpl.GetRatedGifByIdResponseListener {

    private GifDetailsContract.View view;
    private DataManager dataManager;
    private AdapterGifItem gif;
    private UserRatedGif existingUserRatedGif;

    public GifDetailsPresenter(AdapterGifItem gif) {
        this.gif = gif;
    }

    @Override
    public void takeView(GifDetailsContract.View view) {
        this.view = view;
        dataManager = new DataManagerImpl(view.getApplicationContext(), this);
        loadUsersPreviouslyRatedGifData();
        loadData();
    }

    @Override
    public void dropView() {
        view = null;
    }

    /**
     * Load the previously rated gif data from the local db for the currently loaded gif.
     */
    private void loadUsersPreviouslyRatedGifData() {
        dataManager.fetchRatedGifById(gif.getId());
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

        view.populateGifDetails();
    }

    @Override
    public void ratingButtonClicked() {
        if (existingUserRatedGif != null) {
            view.showRatingDialog(existingUserRatedGif.getRatingGiven());
        } else {
            view.showRatingDialog();
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

            // Condition to check if this gif has been rated by the current user sometime before as well.
            boolean isExistingUserRatedGif = existingUserRatedGif != null && existingUserRatedGif.getRatingGiven() > 0;

            updateFirebaseDatabase(isExistingUserRatedGif, adapterGifItem, rating);

            updateLocalDatabase(isExistingUserRatedGif, adapterGifItem, rating);

        } else {
            view.showInvalidRatingErrorMessage();
        }
    }

    /**
     * This method updates the data related to the currently rated gif in firebase db.
     * @param isExistingUserRatedGif Condition to check if this gif has been rated by the current user sometime before as well.
     * @param adapterGifItem The gif item containing info to be stored in firebase db.
     * @param rating Rating submitted by user.
     */
    private void updateFirebaseDatabase(boolean isExistingUserRatedGif, AdapterGifItem adapterGifItem, int rating) {
        int newRatingCount = adapterGifItem.getRatingCount();
        float totalRatingStarsTillNow = adapterGifItem.getAverageRating() * adapterGifItem.getRatingCount();

        if (isExistingUserRatedGif) {
            int oldRating = existingUserRatedGif.getRatingGiven();
            int newRating = rating;
            // If the old rating given by the user and the new one are the same, then no need to
            // update anything and simply return.
            if (oldRating == newRating) {
                return;
            }

            // Since this gif has previously been rated too and now user is trying to update the rating given before,
            // so decrement the previously given rating from the total and add the new rating given to the total.
            totalRatingStarsTillNow = totalRatingStarsTillNow - oldRating + newRating;
        } else {
            // Since this gif hasn't been rated before, so add the rating given by user to the existing total star counts.
            totalRatingStarsTillNow += rating;
            // Increment the total ratings count by 1, else only average rating needs to be calculated
            // and updated in firebase while the number of total ratings remain the same.
            newRatingCount++;
        }

        // Find the new average rating and then write the updated object to Firebase DB.
        float newAverageRating = totalRatingStarsTillNow/newRatingCount;
        newAverageRating = NumberUtil.getRoundedToOneDecimalPlace(newAverageRating);

        // Update the gif item.
        gif.setAverageRating(newAverageRating);
        gif.setRatingCount(newRatingCount);

        dataManager.addOrUpdateGif(new FirebaseGif(adapterGifItem.getId(), newAverageRating, newRatingCount, adapterGifItem.getPreviewUrl()));

        // Reload gif rating.
        refreshRatingInUI();
    }

    /**
     * This method updates the data related to the currently rated gif in local db (Room).
     * @param isExistingUserRatedGif Condition to check if this gif has been rated by the current user sometime before as well.
     * @param adapterGifItem The gif item containing info to be stored in firebase db.
     * @param rating Rating submitted by user.
     */
    private void updateLocalDatabase(boolean isExistingUserRatedGif, AdapterGifItem adapterGifItem, int rating) {
        // Add rated gif in local database.
        UserRatedGif userRatedGif = new UserRatedGif(adapterGifItem.getId(), rating);

        // If the gif being rated has previously been rated as well, then update it in db, else add a new entry.
        if (isExistingUserRatedGif) {
            dataManager.updateRatedGif(userRatedGif);
            // Update the existing local gif item for further use
            existingUserRatedGif.setRatingGiven(rating);
        } else {
            dataManager.insertRatedGif(userRatedGif);
            existingUserRatedGif = userRatedGif;
        }
    }

    /**
     * Refresh rating data in UI.
     */
    private void refreshRatingInUI() {
        view.populateGifDetails();
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

    @Override
    public void responseReceived(UserRatedGif userRatedGif) {
        existingUserRatedGif = userRatedGif;
    }
}
