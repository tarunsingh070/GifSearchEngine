package tarun.example.com.gifsearchengine.data.remote.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;

import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;

/**
 * A helper interface to declare all network calls to FirebaseDb.
 */
public interface FirebaseDbHelper {

    Task<Void> addOrUpdateGif(FirebaseGif firebaseGif);

    void getRankedGifsFromFirebase(ValueEventListener listener);

}
