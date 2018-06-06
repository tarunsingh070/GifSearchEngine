package tarun.example.com.gifsearchengine.data.model.firebase;

public class FirebaseGif {

    private String id;

    private float averageRating;

    private int ratingCount;

    private String previewUrl;

    public FirebaseGif() {
    }

    public FirebaseGif(String id, float averageRating, int ratingCount, String previewUrl) {
        this.id = id;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.previewUrl = previewUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FirebaseGif && id.equals(((FirebaseGif) obj).id);
    }

}
