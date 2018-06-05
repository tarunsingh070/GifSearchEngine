package tarun.example.com.gifsearchengine.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class for storing information related to a full sized gif object.
 */
public class FullGif implements Parcelable {

    private String url;
    private String width;
    private String height;
    private String size;

    protected FullGif(Parcel in) {
        url = in.readString();
        width = in.readString();
        height = in.readString();
        size = in.readString();
    }

    public static final Creator<FullGif> CREATOR = new Creator<FullGif>() {
        @Override
        public FullGif createFromParcel(Parcel in) {
            return new FullGif(in);
        }

        @Override
        public FullGif[] newArray(int size) {
            return new FullGif[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(width);
        dest.writeString(height);
        dest.writeString(size);
    }
}
