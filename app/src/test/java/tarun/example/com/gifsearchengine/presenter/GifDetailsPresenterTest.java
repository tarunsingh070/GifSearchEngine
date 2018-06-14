package tarun.example.com.gifsearchengine.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import tarun.example.com.gifsearchengine.GifTestUtil;
import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.data.model.room.UserRatedGif;
import tarun.example.com.gifsearchengine.ui.gifDetails.GifDetailsFragment;
import tarun.example.com.gifsearchengine.ui.gifDetails.GifDetailsPresenter;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;

/**
 * Using only JUnit and Mockito frameworks here to create unit tests to test {@link GifDetailsPresenter} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class GifDetailsPresenterTest {

    @Mock
    GifDetailsFragment view;

    AdapterGifItem gif;

    GifDetailsPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        gif = GifTestUtil.getTestAdapterGifItem();
        presenter = new GifDetailsPresenter(gif);
    }

    private void presenterTakeView() {
        try {
            presenter.takeView(view);
        } catch (Exception e) {
            // Do nothing as we're catching the exception occurring due to inability to mock Room Database classes.
        }
    }

    @Test
    public void loadDataWithNonEmptyGifUrlAndUsernameTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        presenterTakeView();

        // Using reflection to invoke private loadData method because invoking it through the public takeView()
        // method isn't possible due to inability to mock Room Database classes.
        Method loadDataMethod = GifDetailsPresenter.class.getDeclaredMethod("loadData", new Class[0]);
        loadDataMethod.setAccessible(true);
        loadDataMethod.invoke(presenter);

        verify(view).loadGif(gif.getFullGif().getUrl());
        assertEquals(gif.getUserName(), "Oksana Smilska");
        verify(view).populateGifDetails();
    }

    @Test
    public void loadDataWithEmptyGifUrlAndUsernameTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        presenterTakeView();

        gif.getFullGif().setUrl(null);
        gif.setUserName(null);

        Method loadDataMethod = GifDetailsPresenter.class.getDeclaredMethod("loadData", new Class[0]);
        loadDataMethod.setAccessible(true);
        loadDataMethod.invoke(presenter);

        // Ensure that loadGif method is not called when gif url is null or empty.
        verify(view, times(0)).loadGif(gif.getFullGif().getUrl());
        // Verify that username is set to "Unknown" when username is unavailable.
        assertEquals(gif.getUserName(), "Unknown");
        verify(view).populateGifDetails();
    }

    @Test
    public void prefilledRatingDialogInvokedWhenExistingRatingAvailableTest() {
        presenterTakeView();

        // Mock a previously rated gif being received from db.
        UserRatedGif userRatedGif = new UserRatedGif("Fdy1pYtlhotclXBHdf", 3);
        presenter.responseReceived(userRatedGif);

        // Click rating button.
        presenter.ratingButtonClicked();

        verify(view).showRatingDialog(3);
    }

    @Test
    public void nonPrefilledRatingDialogInvokedWhenExistingRatingUnavailableTest() {
        presenterTakeView();

        // Click rating button.
        presenter.ratingButtonClicked();

        verify(view).showRatingDialog();
    }

    @Test
    public void invalidRatingMessageDisplayTest() {
        presenterTakeView();

        // Call rate gif method when user sets rating to 0 in rating bar and tries to submit it.
        presenter.rateGif(gif, 0);

        verify(view).showInvalidRatingErrorMessage();
    }

    @Test
    public void setNonEmptyGifTitleAsActivityTitleOnResumeTest() {
        presenterTakeView();

        presenter.onResumeCalled();

        verify(view).setActivityTitle(gif.getTitle());
    }

    @Test
    public void setDefaultTitleAsActivityTitleWhenGifTitleEmptyOnResumeTest() {
        presenterTakeView();

        gif.setTitle(null);
        presenter.onResumeCalled();

        verify(view).setDefaultActivityTitle();
    }

}
