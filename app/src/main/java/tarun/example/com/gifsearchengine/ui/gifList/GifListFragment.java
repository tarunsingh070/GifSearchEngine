package tarun.example.com.gifsearchengine.ui.gifList;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tarun.example.com.gifsearchengine.R;

/**
 * This fragment defines the UI to show the Gifs in a grid view format.
 */
public class GifListFragment extends Fragment {

    public GifListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gif_list, container, false);
    }

}
