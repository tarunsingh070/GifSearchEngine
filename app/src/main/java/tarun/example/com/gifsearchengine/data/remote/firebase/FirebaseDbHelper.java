package tarun.example.com.gifsearchengine.data.remote.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;

import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;

/**
 * A helper interface to declare all network calls to FirebaseDb.
 */
public interface FirebaseDbHelper {

    void addOrUpdateGif(FirebaseGif firebaseGif);

    void getRankedGifsFromFirebase(ValueEventListener listener);

    void getRankedGifsFromFirebase(ChildEventListener listener);

}
