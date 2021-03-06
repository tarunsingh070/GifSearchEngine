package tarun.example.com.gifsearchengine.ui.gifList;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import tarun.example.com.gifsearchengine.R;
import tarun.example.com.gifsearchengine.data.Constants;
import tarun.example.com.gifsearchengine.data.remote.giphy.GifsDataSource;
import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.data.model.NetworkState;
import tarun.example.com.gifsearchengine.ui.gifDetails.GifDetailsFragment;
import tarun.example.com.gifsearchengine.util.HttpUtil;
import tarun.example.com.gifsearchengine.util.KeyboardUtil;
import tarun.example.com.gifsearchengine.util.ProgressBarUtil;

/**
 * This fragment defines the UI to show the Gifs in a grid view format.
 */
public class GifListFragment extends Fragment implements GifListContract.View, GifsDataSourceListAdapter.ItemClickListener {

    public static final String TAG = GifListFragment.class.getSimpleName();
    public static final int SPINNER_OPTION_RELEVANCE_POSITION = 0;
    public static final int SPINNER_OPTION_RANKING_POSITION = 1;
    private static final int NO_OF_COLUMNS = 3;

    private OnGifListClickedListener mListener;
    private GifListContract.Presenter presenter;
    private RecyclerView gifsRecyclerView;
    private MenuItem sortMenuItem;
    private GifsDataSourceListAdapter gifsDataSourceListAdapter;

    private View emptyView;

    public GifListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment GifListFragment.
     */
    public static GifListFragment newInstance() {
        return new GifListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // If fragment is being re-created and has a saved state available, then pass it to the presenter for restoration.
        presenter = new GifListPresenter(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gif_list, container, false);
        gifsRecyclerView = rootView.findViewById(R.id.rv_gifs);
        emptyView = rootView.findViewById(R.id.layout_empty);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupGifsRecyclerView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.saveState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_gif_list, menu);

        // Get searchView and add a text listener on it to perform search.
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(getSearchTextListener());

        sortMenuItem = menu.findItem(R.id.spinner_sort);
        setupSortingSpinner();

        presenter.takeView(this);
    }

    /**
     * Method to setup the sorting spinner.
     */
    private void setupSortingSpinner() {
        Spinner sortingSpinner = (Spinner) sortMenuItem.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortingSpinner.setAdapter(adapter);

        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.sortByOptionUpdated(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
    }

    /**
     * This method defines and returns QueryTextListener instance for handling queries when Search View widget is accessed.
     * @return
     */
    private SearchView.OnQueryTextListener getSearchTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtil.hideKeyboard(getContext(), getActivity());
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                presenter.searchQueryChanged(s);
                return false;
            }
        };
    }

    /**
     * Clears the adapter data.
     */
    @Override
    public void clearAdapterData() {
        gifsDataSourceListAdapter.submitList(null);
    }

    @Override
    public void setSortingDropDownVisibility(boolean visibility, int selectedPosition) {
        sortMenuItem.setVisible(visibility);
        ((Spinner) sortMenuItem.getActionView()).setSelection(selectedPosition);
    }

    /**
     * Register the data source factory live data by setting an observer to observe any changes
     * in the live data (list of gifs) made from data source.
     * @param gifsDataSourceMutableLiveData The Gifs Live data list to be observed for changes.
     */
    @Override
    public void registerDataSourceFactoryUpdate(MutableLiveData<GifsDataSource> gifsDataSourceMutableLiveData) {
        gifsDataSourceMutableLiveData.observe(this, new Observer<GifsDataSource>() {
            @Override
            public void onChanged(@Nullable GifsDataSource gifsDataSource) {
                registerGifDataSourceObservers(gifsDataSource);
            }
        });
    }

    /**
     * Register observers to observe the various states of Initial loading and Range loading.
     */
    private void registerGifDataSourceObservers(GifsDataSource gifsDataSource) {
        gifsDataSource.getInitialLoading().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState == NetworkState.LOADING) {
                    showProgressBar();
                } else {
                    hideProgressBar();
                    showEmptyPlaceholder(gifsDataSourceListAdapter.getItemCount() == 0);
                }
            }
        });

        gifsDataSource.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState == NetworkState.LOADING && gifsDataSourceListAdapter.getItemCount() > Constants.LOADING_PAGE_SIZE) {
                    ProgressBarUtil.showProgressBarBottom(getActivity());
                } else {
                    ProgressBarUtil.hideProgressBarBottom(getActivity());
                }
            }
        });

        gifsDataSource.getErrorDetails().observe(this, new Observer<Exception>() {
            @Override
            public void onChanged(@Nullable Exception exception) {
                presenter.onErrorOccurred(exception);
            }
        });
    }

    private void showEmptyPlaceholder(boolean shouldShow) {
        emptyView.setVisibility(shouldShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showProgressBar() {
        ProgressBarUtil.showProgressBarCenter(getActivity());
    }

    @Override
    public void hideProgressBar() {
        ProgressBarUtil.hideProgressBarCenter(getActivity());
    }

    @Override
    public boolean isViewVisible() {
        return isVisible();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGifListClickedListener) {
            mListener = (OnGifListClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGifListClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Bind the {@link PagedList} to be displayed to the recycler view.
     * @param gifsPagedList The list of Gif items to be observed and displayed to the user.
     */
    @Override
    public void bindGifsListAdapterData(LiveData<PagedList<AdapterGifItem>> gifsPagedList) {
        gifsPagedList.observe(this, new Observer<PagedList<AdapterGifItem>>() {
            @Override
            public void onChanged(@Nullable PagedList<AdapterGifItem> pagedList) {
                gifsDataSourceListAdapter.submitList(pagedList);
            }
        });
    }

    /**
     * Setup the recycler view to display Gifs in a grid view format.
     */
    private void setupGifsRecyclerView() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), NO_OF_COLUMNS);
        // If coming back from details fragment, show the gifs list in the same state as it was in
        // when user left the list screen.
        if (gifsDataSourceListAdapter == null) {
            gifsDataSourceListAdapter = new GifsDataSourceListAdapter(this);
        }
        gifsRecyclerView.setLayoutManager(gridLayoutManager);
        gifsRecyclerView.setAdapter(gifsDataSourceListAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dropView();
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean isNetworkConnectivityAvailable() {
        return HttpUtil.isNetworkAvailable(getContext());
    }

    @Override
    public void showNetworkConnectivityError() {
        if (getView() != null) {
            Snackbar networkIssueSnackBar = Snackbar.make(getView(), R.string.no_connection_message, Snackbar.LENGTH_INDEFINITE);
            networkIssueSnackBar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.retryButtonClicked();
                }
            });
            networkIssueSnackBar.show();
        }
    }

    @Override
    public void setDefaultActivityTitle() {
        getActivity().setTitle(R.string.title_trending);
    }

    @Override
    public void setActivityTitle(String title) {
        getActivity().setTitle(title);
    }

    @Override
    public void onItemClick(AdapterGifItem gif) {
        mListener.onGifClicked(gif);
    }

    /**
     * This interface must be implemented by GifsActivity as it contains this
     * fragment to allow an interaction with gif list to be communicated
     * to the activity and then to the {@link GifDetailsFragment}.
     */
    public interface OnGifListClickedListener {
        void onGifClicked(AdapterGifItem gif);
    }
}
