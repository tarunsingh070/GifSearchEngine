package tarun.example.com.gifsearchengine.ui.gifList;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import tarun.example.com.gifsearchengine.R;
import tarun.example.com.gifsearchengine.data.Constants;
import tarun.example.com.gifsearchengine.data.remote.giphy.GifsDataSource;
import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.data.model.NetworkState;
import tarun.example.com.gifsearchengine.ui.gifDetails.GifDetailsFragment;
import tarun.example.com.gifsearchengine.util.HttpUtil;
import tarun.example.com.gifsearchengine.util.KeyboardUtils;
import tarun.example.com.gifsearchengine.util.ProgressBarUtils;

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

    SearchView searchView;
    Button retryButton;

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
        presenter = new GifListPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_trending);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gif_list, container, false);
        gifsRecyclerView = rootView.findViewById(R.id.rv_gifs);
        retryButton = rootView.findViewById(R.id.button_retry);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.retryButtonClicked();
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupGifsRecyclerView();
        presenter.takeView(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_gif_list, menu);

        // Get searchView and add a text listener on it to perform search.
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(getSearchTextListener());

        sortMenuItem = menu.findItem(R.id.spinner_sort);
        setupSortingSpinner();
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
                KeyboardUtils.hideKeyboard(getContext(), getActivity());
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                presenter.searchQueryChanged(s);
                return false;
            }
        };
    }

    @Override
    public void setSortingDropDownVisibility(boolean visibility) {
        sortMenuItem.setVisible(visibility);
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
                    ProgressBarUtils.showProgressBarCenter(getActivity());
                } else {
                    ProgressBarUtils.hideProgressBarCenter(getActivity());
                }
            }
        });

        gifsDataSource.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                // FixMe: Find a way to show this loader only after initial loading.
                if (networkState == NetworkState.LOADING && gifsDataSourceListAdapter.getItemCount() > Constants.LOADING_PAGE_SIZE) {
                    ProgressBarUtils.showProgressBarBottom(getActivity());
                } else {
                    ProgressBarUtils.hideProgressBarBottom(getActivity());
                }
            }
        });
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
        gifsDataSourceListAdapter = new GifsDataSourceListAdapter(this);
        gifsRecyclerView.setLayoutManager(gridLayoutManager);
        gifsRecyclerView.setAdapter(gifsDataSourceListAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dropView();
    }

    @Override
    public void showOrHideRetryButton(boolean shouldShow) {
        int visibility = shouldShow ? View.VISIBLE : View.GONE;
        retryButton.setVisibility(visibility);
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
        showErrorMessage(getString(R.string.no_connection_message));
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
