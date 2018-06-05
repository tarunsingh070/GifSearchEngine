package tarun.example.com.gifsearchengine.ui.gifList;

import android.text.TextUtils;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tarun.example.com.gifsearchengine.data.DataManager;
import tarun.example.com.gifsearchengine.data.DataManagerImpl;
import tarun.example.com.gifsearchengine.data.model.AdapterGifItem;
import tarun.example.com.gifsearchengine.data.model.Gif;
import tarun.example.com.gifsearchengine.data.model.ResponseGifs;

/**
 * A presenter class which would fetch data from data sources through model classes and feed it to the
 * view ({@link GifListFragment}) to be displayed.
 */
public class GifListPresenter implements GifListContract.Presenter {

    private GifListContract.View view;
    private DataManager dataManager;
    private GsonBuilder gsonBuilder;
    private ResponseGifs responseGifs;

    private List<AdapterGifItem> adapterGifItems;

    GifListPresenter() {
        dataManager = new DataManagerImpl();
        gsonBuilder = new GsonBuilder();
        adapterGifItems = new ArrayList<>();
    }

    @Override
    public void takeView(GifListContract.View view) {
        this.view = view;
        fetchTrendingGifs();
    }

    @Override
    public void dropView() {
        this.view = null;
    }

    /**
     * Fetch the list of trending Gifs from Giphy api.
     */
    private void fetchTrendingGifs() {
        dataManager.getTrendingGifs(getTrendingGifsCallback());
    }

    /**
     * Creates and returns a new {@link Callback} object to receive the trending gifs data.
     * @return
     */
    private Callback getTrendingGifsCallback() {
        view.showProgress();
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!view.isViewVisible()) {
                    return;
                }

                // Todo: Handle error.

                view.hideProgress();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!view.isViewVisible()) {
                    return;
                }

                view.hideProgress();

                /*
                If response if successful and returns a non-empty data, then use Gson library to get
                an instance of ResponseGifs object, create gif items to be used for adapater and
                update the recycler view data set.
                 */
                if (response.isSuccessful() && response.body() != null) {

                    String responseJson = response.body().string();

                    if (!TextUtils.isEmpty(responseJson)) {
                        responseGifs = gsonBuilder.create()
                                .fromJson(responseJson, ResponseGifs.class);

                        if (responseGifs != null) {
                            populateAdapterGifItems();
                            updateGifsRecyclerView();
                        }
                    }
                }
            }
        };
    }

    /**
     * Populate the items for adapter by creating an object of {@link AdapterGifItem}
     * for each {@link Gif} item received in response.
     */
    private void populateAdapterGifItems() {
        adapterGifItems.clear();
        for (Gif gif: responseGifs.getGifs()) {
            AdapterGifItem gifItem = new AdapterGifItem(gif.getId(), gif.getUserName(), gif.getImportDate()
                    ,gif.getTitle(), gif.getImages().getPreviewGif().getUrl(), gif.getImages().getFullGif());
            adapterGifItems.add(gifItem);
        }
    }

    /**
     * Update the GifsRecyclerView explicitly on the UI thread as per the latest data available
     * because this method is being called from a background thread (Callback thread).
     */
    private void updateGifsRecyclerView() {
        view.getFragmentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.updateGifsListAdapterData(adapterGifItems);
            }
        });
    }

}