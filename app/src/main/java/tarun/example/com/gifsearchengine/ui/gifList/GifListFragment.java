package tarun.example.com.gifsearchengine.ui.gifList;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tarun.example.com.gifsearchengine.R;
import tarun.example.com.gifsearchengine.data.Constants;
import tarun.example.com.gifsearchengine.data.model.AdapterGifItem;
import tarun.example.com.gifsearchengine.ui.gifDetails.GifDetailsFragment;

/**
 * This fragment defines the UI to show the Gifs in a grid view format.
 */
public class GifListFragment extends Fragment implements GifListContract.View, GifsListAdapter.ItemClickListener {

    public static final String TAG = GifListFragment.class.getSimpleName();
    private static final int NO_OF_COLUMNS = 3;

    private OnGifListClickedListener mListener;
    private GifListContract.Presenter presenter;
    private RecyclerView gifsRecyclerView;

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
