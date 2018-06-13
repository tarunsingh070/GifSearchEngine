package tarun.example.com.gifsearchengine.data.model.giphy;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for storing information related to the preview and full sized gif objects.
 */
public class Images {

    @SerializedName("fixed_width_downsampled")
    private PreviewGif previewGif;

    @SerializedName("fixed_width")
    private FullGif fullGif;

    public PreviewGif getPreviewGif() {
        return previewGif;
    }

    public void setPreviewGif(PreviewGif previewGif) {
        this.previewGif = previewGif;
    }

    public FullGif getFullGif() {
        return fullGif;
    }

    public void setFullGif(FullGif fullGif) {
        this.fullGif = fullGif;
    }

}
