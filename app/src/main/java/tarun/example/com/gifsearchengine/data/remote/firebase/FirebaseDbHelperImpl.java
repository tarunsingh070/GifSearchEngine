package tarun.example.com.gifsearchengine.data.remote.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tarun.example.com.gifsearchengine.data.Constants;
import tarun.example.com.gifsearchengine.data.model.firebase.FirebaseGif;

/**
 * The implementation class corresponding to the {@link FirebaseDbHelper} interface where all methods
 * related to FirebaseDB network calls are defined.
 */
public class FirebaseDbHelperImpl implements FirebaseDbHelper {

    @Override
    public void addOrUpdateGif(FirebaseGif firebaseGif) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.PATH_GIFS)
                .child(firebaseGif.getId())
                .setValue(firebaseGif);
    }

    @Override
    public void getRankedGifsFromFirebase(ValueEventListener listener) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_GIFS);

        ref.addListenerForSingleValueEvent(listener);
    }

    @Override
    public void getRankedGifsFromFirebase(ChildEventListener listener) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_GIFS);

        ref.addChildEventListener(listener);
    }
}