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
import tarun.example.com.gifsearchengine.ui.gifDetails.GifDetailsFragment;
import tarun.example.com.gifsearchengine.ui.gifDetails.GifDetailsPresenter;

import static junit.framework.Assert.assertNotNull;

// Todo: Write unit tests to test GifDetailsFragment view.

@RunWith(RobolectricTestRunner.class)
// Fixme: Robolectric is throwing runtime exception due to "@integer/google_play_services_version"
// not being found when the @Config line below is uncommented.

//@Config(constants = BuildConfig.class)
public class GifDetailsScreenTest {

    private GifDetailsFragment gifDetailsView;

    GifsActivity activity;

    @Mock
    GifDetailsPresenter presenter;

    AdapterGifItem gif;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        gif = GifTestUtil.getTestAdapterGifItem();
        gifDetailsView = GifDetailsFragment.newInstance(gif);
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
