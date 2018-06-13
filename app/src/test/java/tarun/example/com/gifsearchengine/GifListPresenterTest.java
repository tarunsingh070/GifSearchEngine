//package tarun.example.com.gifsearchengine;
//
//import android.content.Intent;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;
//import org.robolectric.shadows.ShadowApplication;
//
//import tarun.example.com.gifsearchengine.data.DataManager;
//import tarun.example.com.gifsearchengine.data.DataManagerImpl;
//import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
//import tarun.example.com.gifsearchengine.data.model.giphy.FullGif;
//import tarun.example.com.gifsearchengine.ui.GifsActivity;
//import tarun.example.com.gifsearchengine.ui.gifDetails.GifDetailsFragment;
//import tarun.example.com.gifsearchengine.ui.gifList.GifListContract;
//import tarun.example.com.gifsearchengine.ui.gifList.GifListFragment;
//import tarun.example.com.gifsearchengine.ui.gifList.GifListPresenter;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.verify;
//import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;
//
///**
// * Example local unit test, which will execute on the development machine (host).
// *
// * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class)
//public class GifListPresenterTest {
//
//    @Mock
//    private DataManager dataManager;
//
//    @Mock
//    private DataManagerImpl dataManagerImpl;
//
//    @Mock
//    private GifListContract.View gifListView;
//
//    @Mock
//    private GifDetailsFragment mockedGifDetailsFragment;
//
//    private GifListPresenter gifListPresenter;
//
//    @Before
//    public void setupGifListPresenter() {
//        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
//        // inject the mocks in the test the initMocks method needs to be called.
//        MockitoAnnotations.initMocks(this);
//
//        // Get a reference to the class under test
//        gifListPresenter = new GifListPresenter();
//    }
//
//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }
//
//    @Test
//    public void clickOnFab_ShowsAddsNoteUi() {
////        // When adding a new note
////        gifListPresenter.searchQueryChanged("");
////
////        // Then add note UI is shown
////        verify(dataManager).getTrendingGifs(null);
//
////        gifListPresenter.takeView(gifListView);
////        gifListPresenter.getSearchedGifsCallback();
////        verify(gifListView).showProgress();
//
//    }
//
//    @Test
//    public void clickingLogin_shouldStartLoginActivity() {
////        GifsActivity activity = Robolectric.setupActivity(GifsActivity.class);
//        FullGif fullGif = new FullGif("www.google.com", "100", "100", "5000");
//        AdapterGifItem adapterGifItem = new AdapterGifItem("dummyid", "John", "2018-05-17 15:40:50"
//                , "Cat Gif", "www.imdb.com", fullGif);
//        GifDetailsFragment gifDetailsFragment = GifDetailsFragment.newInstance(adapterGifItem);
//        startFragment(gifDetailsFragment);
//        gifDetailsFragment.getView().findViewById(R.id.button_rate).performClick();
//        verify(mockedGifDetailsFragment).showRatingDialog();
//
////        Intent expectedIntent = new Intent(activity, LoginActivity.class);
////        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
////        assertEquals(expectedIntent.getComponent(), actual.getComponent());
//    }
//}