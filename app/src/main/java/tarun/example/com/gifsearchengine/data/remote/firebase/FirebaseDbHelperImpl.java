package tarun.example.com.gifsearchengine.data.remote.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import tarun.example.com.gifsearchengine.data.Constants;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;

/**
 * The implementation class corresponding to the {@link FirebaseDbHelper} interface where all methods
 * related to FirebaseDB network calls are defined.
 */
public class FirebaseDbHelperImpl implements FirebaseDbHelper {

    @Override
    public Task<Void> addOrUpdateGif(FirebaseGif firebaseGif) {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.PATH_GIFS)
                .child(firebaseGif.getId())
                .setValue(firebaseGif);
    }
}