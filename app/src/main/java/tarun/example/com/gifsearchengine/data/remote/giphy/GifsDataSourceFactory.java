package tarun.example.com.gifsearchengine.data.remote.giphy;


import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import java.util.List;

import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;

/**
 * Factory class for Creating {@link GifsDataSource} which would help facilitate the fetching of gifs with support of pagination.
 */
public class GifsDataSourceFactory extends DataSource.Factory<Integer, AdapterGifItem> {

    private MutableLiveData<GifsDataSource> gifsDataSourceMutableLiveData = new MutableLiveData<>();
    private String searchTerm;
    private List<AdapterGifItem> rankedGifItems;
    private int sortBySelectedOptionPosition;

    public GifsDataSourceFactory(String searchTerm, List<AdapterGifItem> rankedGifItems, int sortBySelectedOptionPosition) {
        this.searchTerm = searchTerm;
        this.rankedGifItems = rankedGifItems;
        this.sortBySelectedOptionPosition = sortBySelectedOptionPosition;
    }

    @Override
    public DataSource<Integer, AdapterGifItem> create() {
        GifsDataSource gifsDataSource = new GifsDataSource(searchTerm, rankedGifItems, sortBySelectedOptionPosition);
        gifsDataSourceMutableLiveData.postValue(gifsDataSource);
        return gifsDataSource;
    }

    public MutableLiveData<GifsDataSource> getGifsDataSourceMutableLiveData() {
        return gifsDataSourceMutableLiveData;
    }
}
