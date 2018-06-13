package tarun.example.com.gifsearchengine.data;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;

import okhttp3.Callback;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;

/**
 * An interface which would act as the one and only interface for managing all sorts of data calls (local as well as remote)
 * ,irrespective of the data source.
 */
public interface DataManager {

    void getTrendingGifs(int offset, Callback callback);

    void getTrendingGifs(Callback callback);

    void getSearchedGifs(String searchTerm, int  offset, Callback callback);

    void getSearchedGifs(String searchTerm, Callback callback);

    void addOrUpdateGif(FirebaseGif firebaseGif);

    void getRankedGifsFromFirebase(ValueEventListener listener);

    void getRankedGifsFromFirebase(ChildEventListener listener);

}
