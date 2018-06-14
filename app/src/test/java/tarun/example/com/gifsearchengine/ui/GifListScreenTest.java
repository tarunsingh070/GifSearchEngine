package tarun.example.com.gifsearchengine.ui;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import tarun.example.com.gifsearchengine.GifTestUtil;
import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.ui.gifList.GifListFragment;
import tarun.example.com.gifsearchengine.ui.gifList.GifListPresenter;

import static junit.framework.Assert.assertNotNull;

// Todo: Write unit tests to test GifListFragment view.

@RunWith(RobolectricTestRunner.class)
// Fixme: Robolectric is throwing runtime exception due to "@integer/google_play_services_version"
// not being found when the @Config line below is uncommented.

//@Config(constants = BuildConfig.class)
public class GifListScreenTest {

    private GifListFragment gifListView;

    GifsActivity activity;

    @Mock
    GifListPresenter presenter;

    AdapterGifItem gif;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        gif = GifTestUtil.getTestAdapterGifItem();
        gifListView = GifListFragment.newInstance();
        activity = Robolectric.buildActivity(GifsActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void checkActivityNotNull() {
        assertNotNull(activity);
    }

}
