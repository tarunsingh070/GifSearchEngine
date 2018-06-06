package tarun.example.com.gifsearchengine.ui.gifList;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import java.util.List;

import tarun.example.com.gifsearchengine.R;
import tarun.example.com.gifsearchengine.data.Constants;
import tarun.example.com.gifsearchengine.data.model.AdapterGifItem;
import tarun.example.com.gifsearchengine.ui.gifDetails.GifDetailsFragment;
import tarun.example.com.gifsearchengine.utils.KeyboardUtils;

/**
 * This fragment defines the UI to show the Gifs in a grid view format.
 */
public class GifListFragment extends Fragment implements GifListContract.View, GifsListAdapter.ItemClickListener {

    public static final String TAG = GifListFragment.class.getSimpleName();
    public static final int SPINNER_OPTION_RELEVANCE_POSITION = 0;
    public static final int SPINNER_OPTION_RANKING_POSITION = 1;
    private static final int NO_OF_COLUMNS = 3;

    private OnGifListClickedListener mListener;
    private GifListContract.Presenter presenter;
    private RecyclerView gifsRecyclerView;
    private MenuItem searchMenuItem;
    private MenuItem sortMenuItem;

    SearchView searchView;

    private GifsListAdapter gifsListAdapter;

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
        getActivity().setTitle(Constants.ACTIVITY_TITLE_TRENDING);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gif_list, container, false);
        gifsRecyclerView = rootView.findViewById(R.id.rv_gifs);

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
        searchMenuItem = menu.findItem( R.id.action_search);
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

    @Override
    public void setActivityTitle(String title) {
        getActivity().setTitle(title);
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

    @Override
    public void showProgress() {
        //Todo: Show progress bar.
    }

    @Override
    public void hideProgress() {
        //Todo: Hide progress bar.
    }

    @Override
    public FragmentActivity getFragmentActivity() {
        return getActivity();
    }

    /**
     * Update and refresh the recycler view and adapter as per the data received.
     * @param gifItems The latest set of Gif items to be displayed to user.
     */
    @Override
    public void updateGifsListAdapterData(List<AdapterGifItem> gifItems) {
        gifsListAdapter.setGifs(gifItems);
        gifsListAdapter.notifyDataSetChanged();
    }

    /**
     * Setup the recycler view to display Gifs in a grid view format.
     */
    private void setupGifsRecyclerView() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), NO_OF_COLUMNS);

        gifsListAdapter = new GifsListAdapter(getContext(), this);
        gifsRecyclerView.setLayoutManager(gridLayoutManager);
        gifsRecyclerView.setAdapter(gifsListAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dropView();
    }

    @Override
    public void onItemClick(AdapterGifItem gif) {
        mListener.onGifClicked(gif);
    }

    /**
     * This interface must be implemented by HomeActivity as it contains this
     * fragment to allow an interaction with gif list to be communicated
     * to the activity and then to the {@link GifDetailsFragment}.
     */
    public interface OnGifListClickedListener {
        void onGifClicked(AdapterGifItem gif);
    }

}
