package tarun.example.com.gifsearchengine.presenter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import tarun.example.com.gifsearchengine.ui.gifList.GifListFragment;
import tarun.example.com.gifsearchengine.ui.gifList.GifListPresenter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Using only JUnit and Mockito frameworks here to create unit tests to test {@link GifListPresenter} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class GifListPresenterTest {

    @Mock
    private GifListFragment view;

    private GifListPresenter presenter;

    @Before
    public void setup() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        presenter = new GifListPresenter();
    }

    private void mockNetworkNotAvailable() {
        when(view.isNetworkConnectivityAvailable())
                .thenReturn(false);
    }

    private void mockNetworkAvailable() {
        when(view.isNetworkConnectivityAvailable())
                .thenReturn(true);
    }

    private void mockIsViewVisibleTrue() {
        when(view.isViewVisible())
                .thenReturn(true);
    }

    private void presenterTakeView() {
        try {
            presenter.takeView(view);
        } catch (Error e) {
            // Do nothing as we're catching the exception occurring due to inability to mock Firebase classes.
        }
    }

    @Test
    public void rankedGifsAttemptedToCallWhenNetworkAvailableTest() {
        mockNetworkAvailable();
        presenterTakeView();

        // Since firebase classes cannot be mocked, we can check if an attempt to fetch firebase items must have
        // been made by verifying that the methods to show retry button and connectivity error were not called.
        verify(view, times(0)).showOrHideRetryButton(true);
        verify(view, times(0)).showNetworkConnectivityError();
    }

    @Test
    public void noNetworkSituationHandledWhenNetworkUnavailableTest() {
        mockNetworkNotAvailable();
        presenterTakeView();

        // Verify that the methods to show retry button and connectivity error were called since
        // network connectivity is unavailable.
        verify(view).showOrHideRetryButton(true);
        verify(view).showNetworkConnectivityError();
    }

    @Test
    public void noNetworkSituationHandledWhenSearchQueryChangedTest() {
        mockNetworkNotAvailable();
        presenterTakeView();
        presenter.searchQueryChanged("test");

        // Verify that the methods to show retry button and connectivity error were called when user
        // changed the saerch query since network connectivity is unavailable.
        verify(view).showOrHideRetryButton(true);
        verify(view).showNetworkConnectivityError();
    }

    @Test
    public void searchResultsLoadingTriggeredWithNonEmptyQueryTest() {
        mockNetworkAvailable();
        presenterTakeView();
        mockIsViewVisibleTrue();

        // Verify that the appropriate methods are called when search query is non-empty and network is available.
        presenter.searchQueryChanged("test");
        verify(view).registerDataSourceFactoryUpdate(any(MutableLiveData.class));
        verify(view).bindGifsListAdapterData(any(LiveData.class));
        verify(view).setSortingDropDownVisibility(true);
    }

    @Test
    public void searchResultsLoadingTriggeredWithEmptyQueryTest() {
        mockNetworkAvailable();
        presenterTakeView();
        mockIsViewVisibleTrue();

        // Verify that the appropriate methods are called when search query is empty and network is available.
        presenter.searchQueryChanged("");
        verify(view).registerDataSourceFactoryUpdate(any(MutableLiveData.class));
        verify(view).bindGifsListAdapterData(any(LiveData.class));
        verify(view).setSortingDropDownVisibility(false);
    }

    @Test
    public void retryButtonFunctionalityWhenNetworkIsUnavailableTest() {
        mockNetworkNotAvailable();
        presenterTakeView();

        presenter.retryButtonClicked();
        verify(view, times(2)).showNetworkConnectivityError();
    }

    @Test
    public void retryButtonFunctionalityWhenNetworkIsAvailableTest() {
        mockNetworkAvailable();
        presenterTakeView();

        try {
            presenter.retryButtonClicked();
        } catch (Error e) {
            // Do nothing as we're catching the exception occurring due to inability to mock Firebase classes.
        }
        verify(view).showOrHideRetryButton(false);
    }

    @Test
    public void differentSortByOptionSelectedTest() {
        mockNetworkNotAvailable();
        presenterTakeView();

        // different sort by option selected, so loading of results should be triggered and method to
        // show retry button should be triggered because of no network connectivity.
        presenter.sortByOptionUpdated(0);
        verify(view).showOrHideRetryButton(true);
    }

}