package tarun.example.com.gifsearchengine.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import tarun.example.com.gifsearchengine.R;
import tarun.example.com.gifsearchengine.data.model.AdapterGifItem;
import tarun.example.com.gifsearchengine.ui.gifDetails.GifDetailsFragment;
import tarun.example.com.gifsearchengine.ui.gifList.GifListFragment;

/**
 * The Launcher activity which would contain the different fragments for displaying further screens.
 */
public class GifsActivity extends AppCompatActivity implements GifListFragment.OnGifListClickedListener {

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        manager = getSupportFragmentManager();

        // Create and instance of GifListFragment and add it as soon as Home Activity is launched.
        Fragment gifListFragment = manager.findFragmentById(R.id.fragment_container);

        if (gifListFragment == null) {
            gifListFragment = GifListFragment.newInstance();
            manager.beginTransaction()
                    .add(R.id.fragment_container, gifListFragment, GifListFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onGifClicked(AdapterGifItem gif) {
        Fragment gifDetailsFragment = GifDetailsFragment.newInstance(gif);
        manager.beginTransaction()
                .replace(R.id.fragment_container, gifDetailsFragment, GifDetailsFragment.TAG)
                .addToBackStack(GifDetailsFragment.TAG)
                .commit();
    }
}
