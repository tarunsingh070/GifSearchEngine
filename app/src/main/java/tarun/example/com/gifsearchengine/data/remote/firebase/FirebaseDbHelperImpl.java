package tarun.example.com.gifsearchengine.data.remote.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import tarun.example.com.gifsearchengine.data.Constants;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;

public class FirebaseDbHelperImpl implements FirebaseDbHelper {

    @Override
    public Task<Void> addOrUpdateGif(FirebaseGif firebaseGif) {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.GIFS)
                .child(firebaseGif.getId())
                .setValue(firebaseGif);
    }
}