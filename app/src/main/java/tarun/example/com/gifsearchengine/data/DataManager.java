package tarun.example.com.gifsearchengine.data;

import com.google.android.gms.tasks.Task;

import okhttp3.Callback;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;

/**
 * An interface which would act as the one and only interface for managing all sorts of data calls (local as well as remote)
 * ,irrespective of the data source.
 */
public interface DataManager {

    void getTrendingGifs(Callback callback);

    Task<Void> addOrUpdateGif(FirebaseGif firebaseGif);

}
