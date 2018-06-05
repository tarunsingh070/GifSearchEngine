package tarun.example.com.gifsearchengine.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class for storing all relevant information related to a gif object to be displayed.
 */
public class AdapterGifItem implements Parcelable {

    private String id;

    private String userName;

    private String importDate;

    private String title;

    private String previewUrl;

    private FullGif fullGif;

    // Will be used when ranking will be implemented
    private float averageRating;

    // Will be used when ranking will be implemented
    private int ratingCount;

    public AdapterGifItem(String id, String userName, String importDate, String title
            , String previewUrl, FullGif fullGif) {
        this.id = id;
        this.userName = userName;
        this.importDate = importDate;
        this.title = title;
        this.previewUrl = previewUrl;
        this.fullGif = fullGif;
    }

    protected AdapterGifItem(Parcel in) {
        id = in.readString();
        userName = in.readString();
        importDate = in.readString();
        title = in.readString();
        previewUrl = in.readString();
        fullGif = in.readParcelable(FullGif.class.getClassLoader());
        averageRating = in.readFloat();
        ratingCount = in.readInt();
    }

    public static final Creator<AdapterGifItem> CREATOR = new Creator<AdapterGifItem>() {
        @Override
        public AdapterGifItem createFromParcel(Parcel in) {
            return new AdapterGifItem(in);
        }

        @Override
        public AdapterGifItem[] newArray(int size) {
            return new AdapterGifItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public FullGif getFullGif() {
        return fullGif;
    }

    public void setFullGif(FullGif fullGif) {
        this.fullGif = fullGif;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userName);
        dest.writeString(importDate);
        dest.writeString(title);
        dest.writeString(previewUrl);
        dest.writeParcelable(fullGif, flags);
        dest.writeFloat(averageRating);
        dest.writeInt(ratingCount);
    }

}
