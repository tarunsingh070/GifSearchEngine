package tarun.example.com.gifsearchengine.data.model.giphy;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model class for storing the complete information we receive when requesting for a set of gifs.
 */
public class ResponseGifs {

    @SerializedName("data")
    List<Gif> gifs;

    Pagination pagination;

    public List<Gif> getGifs() {
        return gifs;
    }

    public void setGifs(List<Gif> gifs) {
        this.gifs = gifs;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
