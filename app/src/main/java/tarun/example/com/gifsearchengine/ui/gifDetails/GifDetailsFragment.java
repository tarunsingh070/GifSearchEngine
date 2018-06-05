package tarun.example.com.gifsearchengine.ui.gifDetails;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tarun.example.com.gifsearchengine.R;
import tarun.example.com.gifsearchengine.ui.gifList.GifListFragment;

/**
 * This fragment defines the UI to show the details of the Gif selected from the {@link GifListFragment} page.
 */
public class GifDetailsFragment extends Fragment {

    public GifDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gif_details, container, false);
    }

}
