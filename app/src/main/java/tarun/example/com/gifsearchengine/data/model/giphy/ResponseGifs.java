package tarun.example.com.gifsearchengine.data.model.giphy;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model class for storing the complete information we receive when requesting for a set of gifs.
 */
public class ResponseGifs {

    @SerializedName("data")
    private List<Gif> gifs;

    public List<Gif> getGifs() {
        return gifs;
    }

    public void setGifs(List<Gif> gifs) {
        this.gifs = gifs;
    }
}
